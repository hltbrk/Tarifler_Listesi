package javaapplication2;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TarifListeleme extends JFrame {

    private JPanel checkboxPanel2;
    private JButton listeleButton;
    private JTable yemekTable2;
    private JScrollPane scrollPane5;
    private DefaultTableModel tableModel5;

    private JFrame parentFrame;  // Ana pencere referansı

    public TarifListeleme(JFrame parentFrame) {
        this.parentFrame = parentFrame;  // Ana pencereyi saklıyoruz
        setTitle("Yemek Tarifi Listesi");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Checkbox paneli
        checkboxPanel2 = new JPanel();
        checkboxPanel2.setLayout(new BoxLayout(checkboxPanel2, BoxLayout.Y_AXIS));

        // Checkbox paneline scroll ekle
        JScrollPane checkboxScrollPane = new JScrollPane(checkboxPanel2);
        checkboxScrollPane.setPreferredSize(new Dimension(200, 600));
        add(checkboxScrollPane, BorderLayout.WEST);

        // Veritabanından malzeme isimlerini al ve checkbox listesi oluştur
        List<JCheckBox> malzemeCheckBoxList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/yemek", "root", "1234")) {
            String query = "SELECT malzeme_adi FROM malzeme";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                JCheckBox checkBox = new JCheckBox(rs.getString("malzeme_adi"));
                malzemeCheckBoxList.add(checkBox);
                checkboxPanel2.add(checkBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Listele butonu
        listeleButton = new JButton("Listele");
        add(listeleButton, BorderLayout.SOUTH);

        // Tablonun hazırlanması - Ek sütunlar eklendi
        String[] columnNames = {"Tarif ID", "Ad", "Tür", "Süre", "Tarif", "Eşleşme Yüzdesi", "Toplam Miktar"};
        tableModel5 = new DefaultTableModel(columnNames, 0);
        yemekTable2 = new JTable(tableModel5);

        // Tarif ID sütununu gizle
        yemekTable2.getColumnModel().getColumn(0).setMinWidth(0);
        yemekTable2.getColumnModel().getColumn(0).setMaxWidth(0);

        // Eşleşme yüzdesi için progress bar render
        yemekTable2.getColumnModel().getColumn(5).setCellRenderer(new ProgressBarRenderer());

        // Toplam miktar için özelleştirilmiş render
        yemekTable2.getColumnModel().getColumn(6).setCellRenderer(new DecimalRenderer());

        scrollPane5 = new JScrollPane(yemekTable2);
        add(scrollPane5, BorderLayout.CENTER);

        // Listeleme işlemi için action listener
        listeleButton.addActionListener(e -> {
            List<String> selectedMalzemeler = new ArrayList<>();
            for (JCheckBox checkBox : malzemeCheckBoxList) {
                if (checkBox.isSelected()) {
                    selectedMalzemeler.add(checkBox.getText());
                }
            }

            if (!selectedMalzemeler.isEmpty()) {
                listeleTarifler(selectedMalzemeler);
            }
        });

        // Pencere kapandığında ana pencereyi tekrar göster
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                parentFrame.setVisible(true);  // Ana pencereyi göster
            }
        });

        setVisible(true);
    }

    private void listeleTarifler(List<String> selectedMalzemeler) {
        tableModel5.setRowCount(0);  // Önce tabloyu temizle
        String malzemePlaceholders = String.join(",", selectedMalzemeler.stream().map(m -> "?").toArray(String[]::new));

        String query = "SELECT t.tarif_id, t.tarif_adi, t.kategori, t.hazirlama_suresi, t.talimatlar, " +
                "SUM(CASE WHEN m.malzeme_birimi = 'adet' THEN tm.malzeme_miktar / 10 ELSE tm.malzeme_miktar END) AS toplam_kullanilan_malzeme_miktari, " +
                "(SUM(CASE WHEN m.malzeme_birimi = 'adet' THEN tm.malzeme_miktar / 10 ELSE tm.malzeme_miktar END) / " +
                "(SELECT SUM(CASE WHEN m2.malzeme_birimi = 'adet' THEN tm2.malzeme_miktar / 10 ELSE tm2.malzeme_miktar END) " +
                "FROM tarif_malzeme tm2 " +
                "JOIN malzeme m2 ON tm2.malzeme_id = m2.malzeme_id " +
                "WHERE tm2.tarif_id = t.tarif_id)) * 100 AS yuzde_eslesme " +
                "FROM tarifler t " +
                "JOIN tarif_malzeme tm ON t.tarif_id = tm.tarif_id " +
                "JOIN malzeme m ON tm.malzeme_id = m.malzeme_id " +
                "WHERE m.malzeme_adi IN (" + malzemePlaceholders + ") " +
                "GROUP BY t.tarif_id " +
                "HAVING COUNT(DISTINCT m.malzeme_adi) = ? " +
                "ORDER BY yuzde_eslesme DESC";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/yemek", "root", "1234")) {
            PreparedStatement pstmt = connection.prepareStatement(query);

            // Seçilen malzemeleri parametre olarak ekle
            for (int i = 0; i < selectedMalzemeler.size(); i++) {
                pstmt.setString(i + 1, selectedMalzemeler.get(i));
            }

            // HAVING koşulu için seçilen malzeme sayısını ayarla
            pstmt.setInt(selectedMalzemeler.size() + 1, selectedMalzemeler.size());

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int tarifId = rs.getInt("tarif_id");
                String ad = rs.getString("tarif_adi");
                String tur = rs.getString("kategori");
                int sure = rs.getInt("hazirlama_suresi");
                String tarif = rs.getString("talimatlar");
                double yuzdeEslesme = rs.getDouble("yuzde_eslesme");
                double toplamKullanilanMiktar = rs.getDouble("toplam_kullanilan_malzeme_miktari");

                // Değerleri iki ondalık basamağa yuvarla
                toplamKullanilanMiktar = Math.round(toplamKullanilanMiktar * 100.0) / 100.0;
                yuzdeEslesme = Math.round(yuzdeEslesme * 100.0) / 100.0;

                // Satırı tabloya ekle
                tableModel5.addRow(new Object[]{tarifId, ad, tur, sure, tarif, yuzdeEslesme, toplamKullanilanMiktar});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // ProgressBar için özel render
    private static class ProgressBarRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            double yuzde = (double) value;
            JProgressBar progressBar = new JProgressBar(0, 100);
            progressBar.setValue((int) yuzde);
            progressBar.setStringPainted(true);
            return progressBar;
        }
    }

    // Ondalık sayıları doğru formatta göstermek için özel render
    private static class DecimalRenderer extends DefaultTableCellRenderer {
        private final DecimalFormat decimalFormat = new DecimalFormat("0.##");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            if (value instanceof Number) {
                double number = ((Number) value).doubleValue();
                // Sayıyı 1'in altındaysa "0.##" formatında göster
                value = decimalFormat.format(number);
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}
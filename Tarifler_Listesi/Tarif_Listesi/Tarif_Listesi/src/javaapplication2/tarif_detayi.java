/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package javaapplication2;

import java.sql.*;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.HashMap;

public class tarif_detayi extends javax.swing.JFrame {

    private JPanel malzemePanel;
    private ArrayList<JCheckBox> malzemeCheckBoxList;
    private HashMap<JCheckBox, JTextField> malzemeMiktarMap; // Her checkbox için miktar alanı
    private int tarifId; // Seçilen tarifin ID'si

    public tarif_detayi(int tarifId) {
        this.tarifId = tarifId; // Constructor'dan gelen tarif_id'yi sakla
        initComponents();
        malzemeMiktarMap = new HashMap<>();
        malzemeleriYukle(); // Form yüklendiğinde malzemeleri getir
    }

    private void malzemeleriYukle() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            String url = "jdbc:mysql://localhost:3306/yemek"; // Veritabanı URL'si
            String user = "root"; // Veritabanı kullanıcı adı
            String password = "1234"; // Veritabanı şifresi

            connection = DriverManager.getConnection(url, user, password);

            // Tüm malzemeleri ve tarifte kullanılanları getiren sorgu
            String sql = "SELECT m.malzeme_id, m.malzeme_adi, tm.malzeme_miktar " +
                    "FROM malzeme m " +
                    "LEFT JOIN tarif_malzeme tm ON m.malzeme_id = tm.malzeme_id AND tm.tarif_id = ?";

            statement = connection.prepareStatement(sql);
            statement.setInt(1, tarifId);  // tarifId, jTable'den alınmış olan tarifin ID'si

            resultSet = statement.executeQuery();

            malzemeCheckBoxList = new ArrayList<>(); // Checkbox'ları saklamak için liste
            malzemePanel.removeAll(); // Paneli temizle

            while (resultSet.next()) {
                String malzemeAdi = resultSet.getString("malzeme_adi");
                JCheckBox checkBox = new JCheckBox(malzemeAdi); // Her malzeme için checkbox
                malzemeCheckBoxList.add(checkBox); // Checkbox'ı listeye ekle

                // Yanına miktar girişi için JTextField
                JTextField miktarGirdi = new JTextField(5); // 5 karakterlik genişlik
                malzemeMiktarMap.put(checkBox, miktarGirdi); // Checkbox ile miktarı eşleştir

                // Eğer malzeme tarifte kullanıldıysa checkbox'ı işaretle ve miktar alanını doldur
                double malzemeMiktar = resultSet.getDouble("malzeme_miktar");
                if (malzemeMiktar > 0) {
                    checkBox.setSelected(true);
                    miktarGirdi.setText(String.valueOf(malzemeMiktar));
                    miktarGirdi.setVisible(true); // Miktar girişini göster
                } else {
                    miktarGirdi.setVisible(false); // Malzeme tarifte kullanılmadıysa gizli kalacak
                }

                // Checkbox dinleyicisi
                checkBox.addItemListener(e -> {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        miktarGirdi.setVisible(true); // Seçilince miktar girişi görünür olur
                    } else {
                        miktarGirdi.setVisible(false); // Seçim kaldırıldığında gizlenir
                    }
                    malzemePanel.revalidate(); // Paneli güncelle
                    malzemePanel.repaint(); // Paneli yeniden çiz
                });

                malzemePanel.add(checkBox); // Panelde checkbox göster
                malzemePanel.add(miktarGirdi); // Panelde miktar girişi göster
            }

            malzemePanel.revalidate(); // Paneli güncelle
            malzemePanel.repaint(); // Paneli yeniden çiz

        } catch (SQLException exception) {
            exception.printStackTrace();
            JOptionPane.showMessageDialog(null, "Veritabanı hatası: " + exception.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }




    @SuppressWarnings("unchecked")
    private void initComponents() {
        malzemeleri_degistir = new javax.swing.JButton();
        malzemePanel = new JPanel();
        malzemePanel.setLayout(new GridLayout(0, 2)); // Checkbox ve miktar için 2 sütun

        JScrollPane malzemeScrollPane = new JScrollPane(malzemePanel);



        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(malzemeScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(malzemeleri_degistir, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addContainerGap(20, Short.MAX_VALUE))

        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(malzemeScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(malzemeleri_degistir)
                                .addContainerGap(20, Short.MAX_VALUE))
        );
malzemeleri_degistir.setText("Degistir");
        malzemeleri_degistir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                malzemeleri_degistirActionPerformed(evt);
            }
        });
        pack();
    }

    private void malzemeleri_degistirActionPerformed(java.awt.event.ActionEvent evt) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        DbHelper db = new DbHelper();

        try {
            // Veritabanı bağlantısı
            String url = "jdbc:mysql://localhost:3306/yemek";
            String user = "root";
            String password = "1234";
            connection = DriverManager.getConnection(url, user, password);

            // İlk olarak, mevcut tarifin malzemelerini sil
            String deleteSql = "DELETE FROM tarif_malzeme WHERE tarif_id = ?";
            statement = connection.prepareStatement(deleteSql);
            statement.setInt(1, tarifId);
            statement.executeUpdate();
            statement.close();

            // Seçilen malzemeler için tarif-malzeme ilişkisini ekle
            for (JCheckBox checkBox : malzemeCheckBoxList) {
                if (checkBox.isSelected()) {
                    String selectedMalzeme = checkBox.getText();
                    String miktar = malzemeMiktarMap.get(checkBox).getText(); // Girilen miktarı al

                    // Malzeme ID'sini bulmak için sorgu yap
                    String malzemeSql = "SELECT malzeme_id FROM malzeme WHERE malzeme_adi = ?";
                    PreparedStatement malzemeStatement = connection.prepareStatement(malzemeSql);
                    malzemeStatement.setString(1, selectedMalzeme);

                    ResultSet malzemeResultSet = malzemeStatement.executeQuery();
                    if (malzemeResultSet.next()) {
                        int malzemeId = malzemeResultSet.getInt("malzeme_id");

                        // Tarif-Malzeme ilişkisinin eklenmesi (miktar dahil)
                        String tarifMalzemeSql = "INSERT INTO tarif_malzeme (tarif_id, malzeme_id, malzeme_miktar) VALUES (?, ?, ?)";
                        PreparedStatement tarifMalzemeStatement = connection.prepareStatement(tarifMalzemeSql);
                        tarifMalzemeStatement.setInt(1, tarifId);
                        tarifMalzemeStatement.setInt(2, malzemeId);
                        tarifMalzemeStatement.setFloat(3, Float.parseFloat(miktar)); // Miktarı float olarak kaydet

                        tarifMalzemeStatement.executeUpdate();
                        tarifMalzemeStatement.close();
                    }
                    malzemeResultSet.close();
                    malzemeStatement.close();
                }
            }
            JOptionPane.showMessageDialog(null, "Malzemeler başarıyla güncellendi.");

            // Tarif listesini güncelle ve bu pencereyi kapat
            dispose();
            Tarif_Listesi_3 tarif_listesi_3 = new Tarif_Listesi_3();
            tarif_listesi_3.verileriGetir();
            new Tarif_Listesi_3().setVisible(true);
            dispose();

        } catch (SQLException exception) {
            db.ShowError(exception);
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(() -> {
            new tarif_detayi(1).setVisible(true); // Örnek olarak tarifId 1 gönderiliyor
        });
    }

    private javax.swing.JButton malzemeleri_degistir;
}


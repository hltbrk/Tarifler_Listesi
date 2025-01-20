/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package javaapplication2;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement; // Bu satırın var olduğundan emin olun
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.DefaultListModel; // Bu satırı ekleyin
import javax.swing.JList; // Bu satırı ekleyin
import javax.swing.JScrollPane; // Bu satırı ekleyin
import java.sql.*; // SQL için gerekli import
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

import java.awt.event.ItemEvent;
import java.util.HashMap;

public class ekleme_ekrani_2 extends javax.swing.JFrame {

    private JPanel malzemePanel;
    private ArrayList<JCheckBox> malzemeCheckBoxList;
    private HashMap<JCheckBox, JTextField> malzemeMiktarMap; // Her checkbox için miktar alanı

    public ekleme_ekrani_2() {
        initComponents();
        malzemeMiktarMap = new HashMap<>(); // Checkbox ve miktar alanlarını eşleştirecek harita
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
            String sql = "SELECT malzeme_adi FROM malzeme";

            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            malzemeCheckBoxList = new ArrayList<>(); // Checkbox'ları saklamak için liste
            malzemePanel.removeAll(); // Paneli temizle

            while (resultSet.next()) {
                String malzeme = resultSet.getString("malzeme_adi");
                JCheckBox checkBox = new JCheckBox(malzeme); // Her malzeme için checkbox
                malzemeCheckBoxList.add(checkBox); // Checkbox'ı listeye ekle

                // Yanına miktar girişi için JTextField
                JTextField miktarGirdi = new JTextField(5); // 5 karakterlik genişlik
                miktarGirdi.setVisible(false); // Başta gizli olacak
                malzemeMiktarMap.put(checkBox, miktarGirdi); // Checkbox ile miktarı eşleştir

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

        popupMenu1 = new java.awt.PopupMenu();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        ad_girdi = new javax.swing.JTextField();
        sure_girdi = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tarif_girdi = new javax.swing.JTextArea();
        tur_girdi = new javax.swing.JComboBox<>();
        sql_ekle = new javax.swing.JButton();
        malzeme_ekle = new javax.swing.JButton();

        malzemePanel = new JPanel();
        malzemePanel.setLayout(new GridLayout(0, 2)); // Checkbox ve miktar için 2 sütun

        JScrollPane malzemeScrollPane = new JScrollPane(malzemePanel);

        popupMenu1.setLabel("popupMenu1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel2.setText("ADI");
        jLabel3.setText("KATEGORİ");
        jLabel4.setText("HAZIRLAMA SURESİ");
        jLabel5.setText("TARİF");

        ad_girdi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ad_girdiActionPerformed(evt);
            }
        });

        geri_button = new javax.swing.JButton(); // Geri butonu oluşturma
        geri_button.setText("Geri");
        geri_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                geri_buttonActionPerformed(evt); // Geri butonuna tıklanınca çalışacak metot
            }
        });

        tarif_girdi.setColumns(20);
        tarif_girdi.setRows(50);
        jScrollPane1.setViewportView(tarif_girdi);

        tur_girdi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ana Yemek", "Çorba", "Arasıcak", "Tatlı","İçecek" }));
        tur_girdi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tur_girdiActionPerformed(evt);
            }
        });

        sql_ekle.setText("Ekle");
        sql_ekle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sql_ekleActionPerformed(evt);
            }
        });
        malzeme_ekle.setText(" Malzeme Ekle");
        malzeme_ekle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                malzeme_ekleActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(jLabel2)
                                                        .addComponent(ad_girdi, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel3)
                                                        .addComponent(tur_girdi, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel4)
                                                        .addComponent(sure_girdi, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel5)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(20, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(malzemeScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                                .addComponent(malzeme_ekle, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addComponent(sql_ekle, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20))
                        .addComponent(geri_button, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE) // Geri butonunu yerleştirin


        );

        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(jLabel3)
                                        .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(ad_girdi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(tur_girdi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(sure_girdi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel5)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(malzemeScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(malzeme_ekle)
                                        .addComponent(sql_ekle))
                                .addGap(18, 18, 18)
                                .addComponent(geri_button) // Geri butonunu ekle
                                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
    }

    private void ad_girdiActionPerformed(java.awt.event.ActionEvent evt) {
    }
    private void geri_buttonActionPerformed(java.awt.event.ActionEvent evt) {
        // Geri butonuna tıklanınca yapılacak işlem
        new Tarif_Listesi_3().setVisible(true); // Önceki ekranı aç
        dispose(); // Mevcut ekranı kapat
    }
    private void tur_girdiActionPerformed(java.awt.event.ActionEvent evt) {
    }
    private void malzeme_ekleActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
        new malzeme_ekle().setVisible(true);
    }
    private void sql_ekleActionPerformed(java.awt.event.ActionEvent evt) {

        java.sql.Connection connection = null;
        DbHelper db = new DbHelper();
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        try {
            // Veritabanı bağlantısı
            String url = "jdbc:mysql://localhost:3306/yemek";
            String user = "root";
            String password = "1234";

            connection = DriverManager.getConnection(url, user, password);

            // Yeni tarif eklemesi için SQL sorgusu
            String sql = "INSERT INTO tarifler (tarif_adi, kategori, hazirlama_suresi, talimatlar) VALUES (?, ?, ?, ?)";
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Parametrelerin ayarlanması
            statement.setString(1, ad_girdi.getText());
            statement.setString(2, (String) tur_girdi.getSelectedItem());
            statement.setString(3, sure_girdi.getText());
            statement.setString(4, tarif_girdi.getText());

            // Sorguyu çalıştır
            statement.executeUpdate();

            // Eklenen tarifin ID'sini al
            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int tarifId = generatedKeys.getInt(1); // Eklenen tarifin ID'si

                // Seçilen malzemeler için ilişkili tabloya ekleme işlemi
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
                JOptionPane.showMessageDialog(null, "Kayıt başarılı ve malzemeler ilişkilendirildi.");

            } else {
                JOptionPane.showMessageDialog(null, "Tarif eklenirken bir hata oluştu.");
            }

            Tarif_Listesi_3 tarif_listesi_3 = new Tarif_Listesi_3();
            tarif_listesi_3.verileriGetir();
            new Tarif_Listesi_3().setVisible(true);
            dispose();

        } catch (SQLException exception) {
            // Duplicate hatası kontrolü
            if (exception.getErrorCode() == 1062) { // MySQL'de duplicate key hatası kodu
                JOptionPane.showMessageDialog(null, "Bu tarif zaten mevcut, lütfen farklı bir tarif adı deneyin.", "Hata", JOptionPane.ERROR_MESSAGE);
            } else {
                db.ShowError(exception); // Diğer SQL hataları için varsayılan hata mesajı
            }
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(() -> {
            new ekleme_ekrani_2().setVisible(true);
        });
    }

    private java.awt.PopupMenu popupMenu1;
    private javax.swing.JTextField ad_girdi;
    private javax.swing.JTextArea tarif_girdi;
    private javax.swing.JButton sql_ekle;
    private javax.swing.JButton malzeme_ekle;

    private javax.swing.JButton geri_button;


    private javax.swing.JTextField sure_girdi;
    private javax.swing.JComboBox<String> tur_girdi;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
}

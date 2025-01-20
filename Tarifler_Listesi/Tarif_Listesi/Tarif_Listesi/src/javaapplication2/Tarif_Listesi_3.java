/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package javaapplication2;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.util.Map;
import javax.swing.border.EmptyBorder;

public class Tarif_Listesi_3 extends javax.swing.JFrame {


    private int selectedRow;
    private JPanel malzemePanel;
    private JPanel checkboxPanel2;
    private JButton listeleButton;
    private JTable yemekTable2;
    private JScrollPane scrollPane5;
    private DefaultTableModel tableModel5;

    // Sınıf seviyesinde JComboBox tanımlıyoruz
    private JComboBox<String> filtrelemeComboBox;






    private Map<Integer, Float> eksikMaliyetCache = new HashMap<>();

    public Tarif_Listesi_3() {
        initComponents();
        verileriGetir("SELECT * FROM tarifler", "");
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        setupKeyListener();
    }

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {
        int selectedRow = jTable1.getSelectedRow(); // Tıklanan satırı bul
        if (selectedRow != -1) { // Bir satır seçiliyse
            int tarifId1 = Integer.parseInt(jTable1.getValueAt(selectedRow, 0).toString());

            // Fotoğraf yolunu veritabanından al ve göster
            String fotoPath = null;
            String queryFotoPath = "SELECT foto_path FROM tarifler WHERE tarif_id = " + tarifId1;

            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/yemek", "root", "1234");
                 Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(queryFotoPath)) {

                if (rs.next()) {
                    fotoPath = rs.getString("foto_path");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Fotoğrafın var olup olmadığını kontrol et ve göster
            if (fotoPath != null && !fotoPath.isEmpty()) {
                gosterFotograf(fotoPath);
            } else {
                System.err.println("Fotoğraf yolu bulunamadı veya boş.");
                yemekFotoLabel.setIcon(null); // Fotoğraf yoksa boş bırak
            }

            // Diğer alanlar dolduruluyor
            ad_degistir.setText(jTable1.getValueAt(selectedRow, 1).toString());
            tur_degistir.setSelectedItem(jTable1.getValueAt(selectedRow, 2).toString());
            int hazirlamaSuresi = Integer.parseInt(jTable1.getValueAt(selectedRow, 3).toString());
            sure_degistir.setValue(hazirlamaSuresi);
            tarif_degistir.setText(jTable1.getValueAt(selectedRow, 4).toString());

            // Eksik maliyeti hesapla (CustomTableCellRenderer üzerinden)
            CustomTableCellRenderer renderer = new CustomTableCellRenderer();
            float eksikMaliyet = renderer.tarifYapilabilirMi(tarifId1);

            // Toplam maliyeti hesapla
            double toplamMaliyet = 0.0;
            String queryToplamMaliyet = "SELECT SUM(tm.malzeme_miktar * m.birim_fiyat) AS toplam_maliyet " +
                    "FROM tarif_malzeme tm " +
                    "JOIN malzeme m ON tm.malzeme_id = m.malzeme_id " +
                    "WHERE tm.tarif_id = " + tarifId1;

            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/yemek", "root", "1234");
                 Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(queryToplamMaliyet)) {

                if (rs.next()) {
                    toplamMaliyet = rs.getDouble("toplam_maliyet");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Toplam maliyeti yuvarla ve göster
            double yuvarlanmisMaliyet = Math.round(toplamMaliyet * 100.0) / 100.0;
            maliyet.setText(String.format("%.2f", yuvarlanmisMaliyet)); // Toplam maliyeti TextField'da göster

            // Eksik maliyeti güncelle (sadece göster)
            maliyet_goster.setText(String.valueOf(eksikMaliyet));
            jTable1.setValueAt(String.valueOf(eksikMaliyet), selectedRow, 5); // Eksik maliyeti tabloya yansıt

            // Diğer işlemler...
            tarif_detayi tarifDetayi = new tarif_detayi(tarifId1); // tarif_id'yi parametre olarak gönder
            tarifDetayi.setVisible(true);
        }
    }

    // Fotoğrafı yükleyen ve gösteren yardımcı metot
    private void gosterFotograf(String fotoPath) {

        File imageFile = new File(fotoPath);
        if (imageFile.exists()) {
            System.out.println("Fotoğraf dosyası bulundu: " + imageFile.getAbsolutePath());
        } else {
            System.err.println("Fotoğraf dosyası bulunamadı: " + fotoPath);
        }

        if (imageFile.exists()) { // Fotoğraf dosyası gerçekten var mı?
            ImageIcon imageIcon = new ImageIcon(imageFile.getAbsolutePath());
            Image img = imageIcon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH); // Ölçeklendir
            yemekFotoLabel.setIcon(new ImageIcon(img));
            System.out.println("Fotoğraf başarıyla yüklendi.");
        } else {
            System.err.println("Fotoğraf dosyası bulunamadı: " + fotoPath);
            yemekFotoLabel.setIcon(null); // Fotoğraf yoksa boş bırak
        }
    }







    private void filtrele() {
        String secim = (String) filtrelemeComboBox.getSelectedItem();
        String aramaMetni = arama_cubugu.getText();
        String query;





        if (aramaMetni.isEmpty()) {
            switch (secim) {
                case "Karışık":
                    query = "SELECT * FROM tarifler";
                    verileriGetir(query,aramaMetni);
                    break;
                case "A'dan Z'ye":
                    query = "SELECT * FROM tarifler ORDER BY tarif_adi ASC";
                    verileriGetir(query,aramaMetni);
                    break;
                case "Z'den A'ya":
                    query = "SELECT * FROM tarifler ORDER BY tarif_adi DESC";
                    verileriGetir(query,aramaMetni);
                    break;
                case "Süre (artan)":
                    query = "SELECT * FROM tarifler ORDER BY hazirlama_suresi ASC";
                    verileriGetir(query,aramaMetni);
                    break;
                case "Süre (azalan)":
                    query = "SELECT * FROM tarifler ORDER BY hazirlama_suresi DESC";
                    verileriGetir(query,aramaMetni);
                    break;
                case "Ana Yemek":
                    query = "SELECT * FROM tarifler WHERE kategori = 'Ana Yemek'";
                    verileriGetir(query,aramaMetni);
                    break;
                case "Tatlı":
                    query = "SELECT * FROM tarifler WHERE kategori = 'Tatlı'";
                    verileriGetir(query,aramaMetni);
                    break;
                case "Çorba":
                    query = "SELECT * FROM tarifler WHERE kategori = 'Çorba'";
                    verileriGetir(query,aramaMetni);
                    break;
                case "Ara Sıcak":
                    query = "SELECT * FROM tarifler WHERE kategori = 'Ara Sıcak'";
                    verileriGetir(query,aramaMetni);
                    break;
                case "İçecek":
                    query = "SELECT * FROM tarifler WHERE kategori = 'İçecek'";
                    verileriGetir(query,aramaMetni);
                    break;


                case "Malzeme (azdan çoğa)":
                    query = "SELECT t.*, COUNT(tm.tarif_id) AS malzeme_sayisi " +
                            "FROM tarifler t " +
                            "JOIN tarif_malzeme tm ON t.tarif_id = tm.tarif_id " +
                            "GROUP BY t.tarif_id " +
                            "ORDER BY malzeme_sayisi ASC";
                    verileriGetir(query,aramaMetni);
                    break;
                case "Maliyet (azdan çoğa)":
                    query = "SELECT t.*, SUM(tm.malzeme_miktar * m.birim_fiyat) AS toplam_maliyet " +
                            "FROM tarifler t " +
                            "JOIN tarif_malzeme tm ON t.tarif_id = tm.tarif_id " +
                            "JOIN malzeme m ON tm.malzeme_id = m.malzeme_id " +
                            "GROUP BY t.tarif_id " +
                            "ORDER BY toplam_maliyet ASC";
                    verileriGetir(query,aramaMetni);
                    break;

                default:
                    query = "SELECT * FROM tarifler";
                    verileriGetir(query,aramaMetni);
                    break;

            }
        } else {
            // Arama metni varsa, sorgulara "LIKE" ifadesi ekleyelim
            switch (secim) {
                case "Karışık":
                    query = "SELECT * FROM tarifler WHERE tarif_adi LIKE '%" + aramaMetni + "%'";
                    verileriGetir(query, aramaMetni);
                    break;
                case "A'dan Z'ye":
                    query = "SELECT * FROM tarifler WHERE tarif_adi LIKE '%" + aramaMetni + "%' ORDER BY tarif_adi ASC";
                    verileriGetir(query, aramaMetni);
                    break;
                case "Z'den A'ya":
                    query = "SELECT * FROM tarifler WHERE tarif_adi LIKE '%" + aramaMetni + "%' ORDER BY tarif_adi DESC";
                    verileriGetir(query, aramaMetni);
                    break;
                case "Süre (artan)":
                    query = "SELECT * FROM tarifler WHERE tarif_adi LIKE '%" + aramaMetni + "%' ORDER BY hazirlama_suresi ASC";
                    verileriGetir(query, aramaMetni);
                    break;
                case "Süre (azalan)":
                    query = "SELECT * FROM tarifler WHERE tarif_adi LIKE '%" + aramaMetni + "%' ORDER BY hazirlama_suresi DESC";
                    verileriGetir(query, aramaMetni);
                    break;
                case "Ana Yemek":
                    query = "SELECT * FROM tarifler WHERE kategori = 'Ana Yemek' AND tarif_adi LIKE '%" + aramaMetni + "%'";
                    verileriGetir(query, aramaMetni);
                    break;
                case "Tatlı":
                    query = "SELECT * FROM tarifler WHERE kategori = 'Tatlı' AND tarif_adi LIKE '%" + aramaMetni + "%'";
                    verileriGetir(query, aramaMetni);
                    break;
                case "Çorba":
                    query = "SELECT * FROM tarifler WHERE kategori = 'Çorba' AND tarif_adi LIKE '%" + aramaMetni + "%'";
                    verileriGetir(query, aramaMetni);
                    break;
                case "Ara Sıcak":
                    query = "SELECT * FROM tarifler WHERE kategori = 'Ara Sıcak' AND tarif_adi LIKE '%" + aramaMetni + "%'";
                    verileriGetir(query, aramaMetni);
                    break;
                case "İçecek":
                    query = "SELECT * FROM tarifler WHERE kategori = 'İçecek' AND tarif_adi LIKE '%" + aramaMetni + "%'";
                    verileriGetir(query, aramaMetni);
                    break;
                case "Malzeme (azdan çoğa)":
                    query = "SELECT t.*, COUNT(tm.tarif_id) AS malzeme_sayisi " +
                            "FROM tarifler t " +
                            "JOIN tarif_malzeme tm ON t.tarif_id = tm.tarif_id " +
                            "WHERE t.tarif_adi LIKE '%" + aramaMetni + "%' " +
                            "GROUP BY t.tarif_id " +
                            "ORDER BY malzeme_sayisi ASC";
                    verileriGetir(query, aramaMetni);
                    break;
                case "Maliyet (azdan çoğa)":
                    query = "SELECT t.*, SUM(tm.malzeme_miktar * m.birim_fiyat) AS toplam_maliyet " +
                            "FROM tarifler t " +
                            "JOIN tarif_malzeme tm ON t.tarif_id = tm.tarif_id " +
                            "JOIN malzeme m ON tm.malzeme_id = m.malzeme_id " +
                            "WHERE t.tarif_adi LIKE '%" + aramaMetni + "%' " +
                            "GROUP BY t.tarif_id " +
                            "ORDER BY toplam_maliyet ASC";
                    verileriGetir(query, aramaMetni);
                    break;
                default:
                    query = "SELECT * FROM tarifler WHERE tarif_adi LIKE '%" + aramaMetni + "%'";
                    verileriGetir(query, aramaMetni); // Sorguyu tabloya gönder
                    break;
            }
        }

    }




    public void verileriGetir() {

        verileriGetir("",""); // Varsayılan olarak boş bir arama metni ile çağır
    }

    public void verileriGetir(String query,String aramaMetni) {
        DefaultTableModel tableModel = (DefaultTableModel) jTable1.getModel();
        tableModel.setRowCount(0);

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/yemek", "root", "1234");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {

                String[] data = {
                        rs.getString("tarif_id"),
                        rs.getString("tarif_adi"),
                        rs.getString("kategori"),
                        rs.getString("hazirlama_suresi"),
                        rs.getString("talimatlar")

                };
                tableModel.addRow(data);
            }

            jTable1.getColumnModel().getColumn(0).setMinWidth(0);
            jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(0).setWidth(0);
            jTable1.getColumnModel().getColumn(5).setMinWidth(0);
            jTable1.getColumnModel().getColumn(5).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(5).setWidth(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
// <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        // Constructor veya initComponent içinde JLabel'in başlangıç ayarları
        yemekFotoLabel= new JLabel();
        yemekFotoLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        yemekFotoLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        yemekFotoLabel.setPreferredSize(new java.awt.Dimension(150, 100));
        popupMenu1 = new java.awt.PopupMenu();
        jFileChooser1 = new javax.swing.JFileChooser();
        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        ekleme_butonu = new javax.swing.JButton();
        malzemelerle_sec = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        degistir = new javax.swing.JButton();

        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        ad_degistir = new javax.swing.JTextField();
        maliyet_goster = new javax.swing.JTextField();
        tarif_degistir = new javax.swing.JTextArea();
        maliyet = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        tur_degistir = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        sure_degistir = new javax.swing.JSpinner();
        arama_cubugu = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        popupMenu1.setLabel("popupMenu1");
        JPanel eksikMaliyetPanel = new JPanel();
        JLabel eksikMaliyetLabel = new JLabel("Eksik malzeme bilgisi burada görünecek");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        // Buton ve bileşenlerin renk ve font ayarları
        Font yeniFont = new Font("Arial", Font.BOLD, 14);

        ekleme_butonu.setText("EKLE");
        ekleme_butonu.setBackground(Color.darkGray);
        ekleme_butonu.setForeground(Color.WHITE);
        ekleme_butonu.setFont(yeniFont);
        ekleme_butonu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ekleme_butonuActionPerformed(evt);
            }
        });

        malzemelerle_sec.setText("Ne yapiyim?");
        malzemelerle_sec.setBackground(Color.ORANGE);
        malzemelerle_sec.setForeground(Color.BLACK);
        malzemelerle_sec.setFont(yeniFont);
        malzemelerle_sec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                malzemelerle_secActionPerformed(evt);
            }
        });

        jButton2.setText("ÇIKAR");
        jButton2.setBackground(Color.RED);
        jButton2.setForeground(Color.WHITE);
        jButton2.setFont(yeniFont);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        degistir.setText("DÜZENLE");
        degistir.setBackground(Color.GREEN);
        degistir.setForeground(Color.BLACK);
        degistir.setFont(yeniFont);
        degistir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                degistirActionPerformed(evt);
            }
        });

        // JTextField renklendirmesi
        ad_degistir.setBackground(Color.LIGHT_GRAY);
        ad_degistir.setForeground(Color.BLACK);
        ad_degistir.setFont(yeniFont);

        maliyet_goster.setBackground(Color.LIGHT_GRAY);
        maliyet_goster.setForeground(Color.BLACK);
        maliyet_goster.setFont(yeniFont);

        maliyet.setBackground(Color.green);
        maliyet.setForeground(Color.BLACK);
        maliyet.setFont(yeniFont);

        // JTextArea renklendirmesi
        tarif_degistir.setColumns(20);
        tarif_degistir.setRows(6);
        tarif_degistir.setLineWrap(true);
        tarif_degistir.setWrapStyleWord(true);
        tarif_degistir.setBackground(Color.LIGHT_GRAY);
        tarif_degistir.setForeground(Color.BLACK);
        tarif_degistir.setFont(yeniFont);

        JScrollPane tarifScrollPane = new JScrollPane(tarif_degistir, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // JLabel'lerin renk ayarları
        jLabel1.setText("AD");
        jLabel1.setForeground(Color.DARK_GRAY);
        jLabel1.setFont(yeniFont);

        jLabel2.setText("TÜR");
        jLabel2.setForeground(Color.DARK_GRAY);
        jLabel2.setFont(yeniFont);

        jLabel3.setText("Tarif");
        jLabel3.setForeground(Color.DARK_GRAY);
        jLabel3.setFont(yeniFont);

        jLabel4.setText("Hazırlama Süresi");
        jLabel4.setForeground(Color.DARK_GRAY);
        jLabel4.setFont(yeniFont);

        jLabel6.setText("Yemek İsmi Ara:");
        jLabel6.setForeground(Color.DARK_GRAY);
        jLabel6.setFont(yeniFont);

        jLabel7.setText("Eksik Maliyet");
        jLabel7.setForeground(Color.DARK_GRAY);
        jLabel7.setFont(yeniFont);

        jLabel8.setText("Toplam Maliyet");
        jLabel8.setForeground(Color.DARK_GRAY);
        jLabel8.setFont(yeniFont);

        // JComboBox ve arama çubuğunun renklendirilmesi
        tur_degistir.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seçilmedi", "Ana Yemek", "Çorba", "Ara Sıcak", "Tatlı","İçecek" }));
        tur_degistir.setBackground(Color.WHITE);
        tur_degistir.setForeground(Color.BLACK);
        tur_degistir.setFont(yeniFont);

        arama_cubugu.setBackground(Color.LIGHT_GRAY);
        arama_cubugu.setForeground(Color.BLACK);
        arama_cubugu.setFont(yeniFont);

        // Filtreleme ComboBox'unu renklendiriyoruz
        filtrelemeComboBox = new JComboBox<>(new String[]{
                "Karışık", "A'dan Z'ye", "Z'den A'ya", "Süre (artan)", "Süre (azalan)",
                "Ana Yemek", "Tatlı", "Çorba", "Ara Sıcak","İçecek", "Malzeme (azdan çoğa)", "Maliyet (azdan çoğa)"
        });
        filtrelemeComboBox.setBackground(Color.WHITE);
        filtrelemeComboBox.setForeground(Color.BLACK);
        filtrelemeComboBox.setFont(yeniFont);

        filtrelemeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filtrele(); // Filtreleme işlemi tetikleniyor
            }
        });

        // JTable default renderer kullanımı için CustomTableCellRenderer zaten eklenmişti
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null}
                },
                new String[]{
                        "ID", "Ad", "Tür", "Süre", "Tarif", "Maliyet"
                }
        ));

        jTable1.setDefaultRenderer(Object.class, new CustomTableCellRenderer());
        jScrollPane2.setViewportView(jTable1);

        // layout ayarları içerisine ekleyin
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel1)
                                        .addComponent(ad_degistir, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel2)
                                        .addComponent(tur_degistir, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel4)
                                        .addComponent(sure_degistir, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel3)
                                        .addComponent(tarifScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel7)
                                        .addComponent(maliyet_goster, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel8)
                                        .addComponent(maliyet, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(filtrelemeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(arama_cubugu, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel6)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(ekleme_butonu, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(degistir, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(malzemelerle_sec, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(10, 10, 10) // Yatay merkezleme için boşluk
                                                .addComponent(yemekFotoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))) // Fotoğraf boyutu artırıldı
                                .addContainerGap())
        );

        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10) // Üst boşluk azaltıldı
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(ad_degistir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(10, 10, 10) // Boşluk azaltıldı
                                                .addComponent(jLabel2)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(tur_degistir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(10, 10, 10)
                                                .addComponent(jLabel4)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(sure_degistir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(10, 10, 10)
                                                .addComponent(jLabel3)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(tarifScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE) // Boyut azaltıldı
                                                .addGap(10, 10, 10)
                                                .addComponent(jLabel7)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(maliyet_goster, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(10, 10, 10)
                                                .addComponent(jLabel8)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(maliyet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(10, 10, 10)
                                                .addComponent(filtrelemeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(10, 10, 10)
                                                .addComponent(jLabel6)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(arama_cubugu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(10, 10, 10)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(ekleme_butonu)
                                                        .addComponent(degistir))
                                                .addGap(10, 10, 10)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(malzemelerle_sec)
                                                        .addComponent(jButton2)))
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)) // Tablo yüksekliği azaltıldı
                                .addGap(10, 10, 10)
                                .addComponent(yemekFotoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );

        pack();


    }



    private void ekleme_butonuActionPerformed(java.awt.event.ActionEvent evt) {

        // Ekleme_Ekrani sınıfını oluştur ve göster

        ekleme_ekrani_2 eklemeEkrani = new ekleme_ekrani_2();
        eklemeEkrani.setVisible(true); // Yeni pencereyi göster
        dispose();
    }
    private void setupKeyListener() {
        arama_cubugu.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filtrele(); // Her tuş basımında filtrele metodunu çalıştır
            }
        });
    }
    private void malzemelerle_secActionPerformed(java.awt.event.ActionEvent evt) {

        TarifListeleme tarifListeleme = new TarifListeleme(this);
        tarifListeleme.setVisible(true); // Yeni pencereyi göster
        dispose();
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        //cıkar
        selectedRow = jTable1.getSelectedRow(); // Sınıf seviyesindeki selectedRow'u güncelle

        if (selectedRow != -1) { // Bir tarif seçildiyse
            try {
                // Veritabanı bağlantısı
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/yemek", "root", "1234");
                Statement stmt = con.createStatement();

                // JSpinner'dan int değeri almak
                int hazirSuresi = (Integer) sure_degistir.getValue();

                // tarif_id'yi doğru bir şekilde almak için
                int tarifId = Integer.parseInt(jTable1.getValueAt(selectedRow, 0).toString());

                // Sorguyu oluştur
                String deleteTarifQuery = "DELETE FROM tarifler WHERE tarif_id = " + tarifId;
                stmt.executeUpdate(deleteTarifQuery);

                String deleteMalzemeQuery = "DELETE FROM tarif_malzeme WHERE tarif_id = " + tarifId;
                stmt.executeUpdate(deleteMalzemeQuery);

                verileriGetir("SELECT * FROM tarifler","");


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Lütfen bir tarif seçin.");
        }
    }
    private void degistirActionPerformed(java.awt.event.ActionEvent evt) {
        selectedRow = jTable1.getSelectedRow(); // Sınıf seviyesindeki selectedRow'u güncelle
        if (selectedRow != -1) { // Bir tarif seçildiyse
            try {
                // Veritabanı bağlantısı
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/yemek", "root", "1234");
                Statement stmt = con.createStatement();

                int hazirSuresi = (Integer) sure_degistir.getValue();
                int tarifId = Integer.parseInt(jTable1.getValueAt(selectedRow, 0).toString());
                String yeniTarifAdi = ad_degistir.getText();

                // Aynı isimde bir tarif olup olmadığını kontrol et
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM tarifler WHERE tarif_adi = '" + yeniTarifAdi + "' AND tarif_id != " + tarifId);
                rs.next();
                int count = rs.getInt(1);

                if (count == 0) {
                    // Sorguyu oluştur ve güncelle
                    String updateQuery = "UPDATE tarifler SET "
                            + "tarif_adi = '" + yeniTarifAdi + "', "
                            + "kategori = '" + tur_degistir.getSelectedItem().toString() + "', "
                            + "hazirlama_suresi = " + hazirSuresi + ", "
                            + "talimatlar = '" + tarif_degistir.getText() + "'"
                            + " WHERE tarif_id = " + tarifId;

                    stmt.executeUpdate(updateQuery);
                    verileriGetir("SELECT * FROM tarifler", "");
                } else {
                    System.out.println("Aynı isimde bir tarif zaten mevcut.");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Lütfen bir tarif seçin.");
        }
    }


    // Variables declaration - do not modify
    private javax.swing.JLabel yemekFotoLabel;
    private javax.swing.JTextField ad_degistir;
    private javax.swing.JTextField maliyet_goster;
    private javax.swing.JTextField maliyet;
    private javax.swing.JTextField arama_cubugu;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton degistir;
    private javax.swing.JButton malzemelerle_sec;
    private javax.swing.JButton ekleme_butonu;
    private javax.swing.JButton jButton2;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private  javax.swing.JLabel jLabel7;
    private  javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private java.awt.PopupMenu popupMenu1;
    private javax.swing.JSpinner sure_degistir;
    private javax.swing.JTextArea tarif_degistir;
    private javax.swing.JComboBox<String> tur_degistir;
    // End of variables declaration
}


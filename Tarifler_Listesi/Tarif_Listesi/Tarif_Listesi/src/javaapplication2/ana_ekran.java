package javaapplication2;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class ana_ekran extends javax.swing.JFrame {

    private ImagePanel imagePanel;  // Arka plan resmini çizdirmek için özel panel

    public ana_ekran() {
        imagePanel = new ImagePanel(); // Arka plan resmi için özel paneli başlatıyoruz
        this.setContentPane(imagePanel); // İçerik panelini arka plan paneli ile değiştiriyoruz
        initComponents();
    }

    // Arka plan resmini çizdirmek için özel JPanel sınıfı
    class ImagePanel extends JPanel {
        private Image backgroundImage;

        public ImagePanel() {
            // Resmin doğru yolunu kontrol edin.
            backgroundImage = new ImageIcon("C:\\Users\\Huawei\\Desktop\\biziko\\biz.jpg").getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Resmi panelin boyutuna göre ayarla ve çiz
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void initComponents() {

        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("Baslat");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(250, 250, 250)  // Yatayda ortalama için
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE) // Buton genişliği 200
                                .addContainerGap(250, Short.MAX_VALUE)) // Boşluk dengeleme
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(150, 150, 150) // Dikeyde ortalama için
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(206, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        // "Listele" butonuna basıldığında Tarif_Listesi penceresi açılacak
        new Tarif_Listesi_3().setVisible(true);
        dispose();
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ana_ekran().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    // End of variables declaration//GEN-END:variables
}
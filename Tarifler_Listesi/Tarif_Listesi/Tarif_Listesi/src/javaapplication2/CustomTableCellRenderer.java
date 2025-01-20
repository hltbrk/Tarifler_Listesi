package javaapplication2;

import java.awt.Color;
import java.awt.Component;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CustomTableCellRenderer extends DefaultTableCellRenderer {


    private Map<Integer, Float> eksikMaliyetCache = new HashMap<>();

    public float tarifYapilabilirMi(int tarifId) {
        // Önbellekte (cache) varsa doğrudan al, yoksa hesapla ve önbelleğe ekle
        return eksikMaliyetCache.computeIfAbsent(tarifId, id -> {
            float toplamEksikMaliyet = 0.0f;

            String query = "SELECT tarif_malzeme.malzeme_miktar, malzeme.toplam_miktar, malzeme.birim_fiyat " +
                    "FROM tarif_malzeme " +
                    "INNER JOIN malzeme ON tarif_malzeme.malzeme_id = malzeme.malzeme_id " +
                    "WHERE tarif_malzeme.tarif_id = " + tarifId;

            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/yemek", "root", "1234");
                 Statement stmt = con.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    float malzemeMiktar = rs.getFloat("malzeme_miktar");
                    float toplamMiktar = rs.getFloat("toplam_miktar");
                    float birimFiyat = rs.getFloat("birim_fiyat");

                    if (toplamMiktar < malzemeMiktar) {
                        float eksikMiktar = malzemeMiktar - toplamMiktar;
                        toplamEksikMaliyet += eksikMiktar * birimFiyat;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return -1f;
            }

            return toplamEksikMaliyet;
        });
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);


        // Tablo modelindeki tarif_id'yi al
        int tarifId = Integer.parseInt(table.getModel().getValueAt(row, 0).toString()); // 0. sütun (tarif_id)

        // Bu tarifin yapılabilir olup olmadığını ve eksik malzeme tutarını kontrol et
        float eksikMaliyet = tarifYapilabilirMi(tarifId);


        // Eksik malzeme varsa kırmızı arka plan ver ve eksik tutarı göster
        if (eksikMaliyet > 0) {
            c.setBackground(Color.decode("#EE3A3A"));
            System.out.println("Tarif ID: " + tarifId + " - Eksik malzemelerin toplam maliyeti: " + eksikMaliyet + " TL");
        } else if (eksikMaliyet == 0) {
            // Eğer tüm malzemeler yeterliyse yeşil arka plan ver
            c.setBackground(Color.decode("#00AA00"));
        } else {
            // Eğer bir hata olursa gri renge boyayabiliriz
            c.setBackground(Color.GRAY);
        }

        // Eğer satır seçildiyse, seçili arka plan rengini koru
        if (isSelected) {
            c.setBackground(table.getSelectionBackground());
        }

        return c;
    }
}

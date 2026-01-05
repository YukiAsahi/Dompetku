package Proyek.Model;

import java.time.LocalDateTime;
public class CatatanUang {
    private int id;
    private int dompetId;
    private int userId;
    private String jenisCatatan;
    private int kategoriId;
    private double nominal;
    private String catatan;
    private LocalDateTime tanggalCatat;

    private String kategoriNama;
    private String dompetNama;
    private Integer dompetTujuanId;

    public CatatanUang() {
    }

    public CatatanUang(int dompetId, int userId, String jenisCatatan, double nominal) {
        this.setDompetId(dompetId);
        this.setUserId(userId);
        this.setJenisCatatan(jenisCatatan);
        this.setNominal(nominal);
        this.setTanggalCatat(LocalDateTime.now());
    }
    public void setIdUser(int userId) {
        this.userId = userId;
    }

    public void setIdDompet(int dompetId) {
        this.dompetId = dompetId;
    }

    public void setIdKategori(int kategoriId) {
        this.kategoriId = kategoriId;
    }

    public void setNamaKategori(String nama) {
        this.kategoriNama = nama;
    }

    public void setNamaDompet(String nama) {
        this.dompetNama = nama;
    }

    public CatatanUang(int id, int dompetId, int userId, String jenisCatatan, int kategoriId, double nominal,
            String catatan, LocalDateTime tanggalCatat) {
        this.setId(id);
        this.setDompetId(dompetId);
        this.setUserId(userId);
        this.setJenisCatatan(jenisCatatan);
        this.setKategoriId(kategoriId);
        this.setNominal(nominal);
        this.setCatatan(catatan);
        this.setTanggalCatat(tanggalCatat);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDompetId() {
        return dompetId;
    }

    public void setDompetId(int dompetId) {
        this.dompetId = dompetId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getJenisCatatan() {
        return jenisCatatan;
    }

    public void setJenisCatatan(String jenisCatatan) {
        this.jenisCatatan = jenisCatatan;
    }

    public int getKategoriId() {
        return kategoriId;
    }

    public void setKategoriId(int kategoriId) {
        this.kategoriId = kategoriId;
    }

    public double getNominal() {
        return nominal;
    }

    public void setNominal(double nominal) {
        this.nominal = nominal;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public LocalDateTime getTanggalCatat() {
        return tanggalCatat;
    }

    public void setTanggalCatat(LocalDateTime tanggalCatat) {
        this.tanggalCatat = tanggalCatat;
    }

    public String getKategoriNama() {
        return kategoriNama;
    }

    public void setKategoriNama(String kategoriNama) {
        this.kategoriNama = kategoriNama;
    }

    public String getDompetNama() {
        return dompetNama;
    }

    public void setDompetNama(String dompetNama) {
        this.dompetNama = dompetNama;
    }

    public Integer getDompetTujuanId() {
        return dompetTujuanId;
    }

    public void setDompetTujuanId(Integer dompetTujuanId) {
        this.dompetTujuanId = dompetTujuanId;
    }

    public boolean isPemasukan() {
        return "Pemasukan".equalsIgnoreCase(jenisCatatan);
    }

    public boolean isPengeluaran() {
        return "Pengeluaran".equalsIgnoreCase(jenisCatatan);
    }

    public boolean isTransfer() {
        return "Transfer".equalsIgnoreCase(jenisCatatan);
    }

    @Override
    public String toString() {
        return tanggalCatat + " - " + jenisCatatan + ": Rp " + String.format("%.2f", nominal);
    }
}



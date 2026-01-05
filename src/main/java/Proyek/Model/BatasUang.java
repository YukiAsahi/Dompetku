package Proyek.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
public class BatasUang {
    private int id;
    private int userId;
    private int kategoriId;
    private double maxUang;
    private LocalDate mulaiDari;
    private LocalDate selesaiSampai;
    private LocalDateTime tanggalBikin;

    private String jenis;
    private double progressTabungan;
    private int dompetId;

    private String kategoriNama;
    private double currentSpending;
    private String namaDompet;
    private double saldoDompet;

    public BatasUang(int userId, int kategoriId, double maxUang) {
        this.setUserId(userId);
        this.setKategoriId(kategoriId);
        this.setMaxUang(maxUang);
        this.setJenis("BUDGETING");
        this.setTanggalBikin(LocalDateTime.now());
    }

    public BatasUang(int userId, int kategoriId, double maxUang, int dompetId) {
        this.setUserId(userId);
        this.setKategoriId(kategoriId);
        this.setMaxUang(maxUang);
        this.setDompetId(dompetId);
        this.setJenis("GOALS");
        this.setProgressTabungan(0);
        this.setTanggalBikin(LocalDateTime.now());
    }

    public BatasUang(int id, int userId, int kategoriId, double maxUang, LocalDate mulaiDari,
            LocalDate selesaiSampai, LocalDateTime tanggalBikin, String jenis,
            double progressTabungan, int dompetId) {
        this.setId(id);
        this.setUserId(userId);
        this.setKategoriId(kategoriId);
        this.setMaxUang(maxUang);
        this.setMulaiDari(mulaiDari);
        this.setSelesaiSampai(selesaiSampai);
        this.setTanggalBikin(tanggalBikin);
        this.setJenis(jenis);
        this.setProgressTabungan(progressTabungan);
        this.setDompetId(dompetId);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getKategoriId() {
        return kategoriId;
    }

    public void setKategoriId(int kategoriId) {
        this.kategoriId = kategoriId;
    }

    public double getMaxUang() {
        return maxUang;
    }

    public void setMaxUang(double maxUang) {
        this.maxUang = maxUang;
    }

    public LocalDate getMulaiDari() {
        return mulaiDari;
    }

    public void setMulaiDari(LocalDate mulaiDari) {
        this.mulaiDari = mulaiDari;
    }

    public LocalDate getSelesaiSampai() {
        return selesaiSampai;
    }

    public void setSelesaiSampai(LocalDate selesaiSampai) {
        this.selesaiSampai = selesaiSampai;
    }

    public LocalDateTime getTanggalBikin() {
        return tanggalBikin;
    }

    public void setTanggalBikin(LocalDateTime tanggalBikin) {
        this.tanggalBikin = tanggalBikin;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public double getProgressTabungan() {
        return progressTabungan;
    }

    public void setProgressTabungan(double progressTabungan) {
        this.progressTabungan = progressTabungan;
    }

    public int getDompetId() {
        return dompetId;
    }

    public void setDompetId(int dompetId) {
        this.dompetId = dompetId;
    }

    public String getKategoriNama() {
        return kategoriNama;
    }

    public void setKategoriNama(String kategoriNama) {
        this.kategoriNama = kategoriNama;
    }

    public double getCurrentSpending() {
        return currentSpending;
    }

    public void setCurrentSpending(double currentSpending) {
        this.currentSpending = currentSpending;
    }

    public String getNamaDompet() {
        return namaDompet;
    }

    public void setNamaDompet(String namaDompet) {
        this.namaDompet = namaDompet;
    }

    public double getSaldoDompet() {
        return saldoDompet;
    }

    public void setSaldoDompet(double saldoDompet) {
        this.saldoDompet = saldoDompet;
    }

    public boolean isBudgeting() {
        return "BUDGETING".equals(jenis);
    }

    public double sisaBudget() {
        return maxUang - currentSpending;
    }

    public double hitungPersenTerpakai() {
        if (maxUang <= 0)
            return 0;
        return (currentSpending / maxUang) * 100;
    }

    public boolean isMelebihi() {
        return currentSpending > maxUang;
    }

    public boolean isGoals() {
        return "GOALS".equals(jenis);
    }

    public double hitungPersenProgress() {
        if (maxUang <= 0)
            return 0;
        return (progressTabungan / maxUang) * 100;
    }

    public double hitungProgressAktual() {
        return Math.min(progressTabungan, saldoDompet);
    }

    public double hitungPersenProgressAktual() {
        if (maxUang <= 0)
            return 0;
        return (hitungProgressAktual() / maxUang) * 100;
    }

    public boolean goalsTercapai() {
        return progressTabungan >= maxUang;
    }

    public boolean punyaDeadline() {
        return selesaiSampai != null;
    }

    @Override
    public String toString() {
        if (isBudgeting()) {
            return kategoriNama + " - Limit: Rp " + String.format("%,.0f", maxUang);
        } else {
            return kategoriNama + " - Target: Rp " + String.format("%,.0f", maxUang);
        }
    }
}



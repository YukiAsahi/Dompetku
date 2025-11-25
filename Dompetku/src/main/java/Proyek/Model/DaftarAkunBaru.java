package Proyek.Model;

import java.time.LocalDateTime;

public class DaftarAkunBaru {
    private int idDaftar;
    private String namaAkun;
    private String sandi;
    private String namaLengkap;
    private String email;
    private String tipeDaftar;
    private String statusDaftar;
    private String alasanTolak;
    private Integer idAdmin;
    private LocalDateTime tanggalDaftar;
    private LocalDateTime tanggalProses;
    
    // Constructor untuk insert baru
    public DaftarAkunBaru(String namaAkun, String sandi, String namaLengkap, String email, String tipeDaftar) {
        this.namaAkun = namaAkun;
        this.sandi = sandi;
        this.namaLengkap = namaLengkap;
        this.email = email;
        this.tipeDaftar = tipeDaftar;
        this.statusDaftar = "Pending";
        this.tanggalDaftar = LocalDateTime.now();
    }
    
    // Constructor full
    public DaftarAkunBaru(int idDaftar, String namaAkun, String sandi, String namaLengkap, String email, 
                         String tipeDaftar, String statusDaftar, String alasanTolak, Integer idAdmin,
                         LocalDateTime tanggalDaftar, LocalDateTime tanggalProses) {
        this.idDaftar = idDaftar;
        this.namaAkun = namaAkun;
        this.sandi = sandi;
        this.namaLengkap = namaLengkap;
        this.email = email;
        this.tipeDaftar = tipeDaftar;
        this.statusDaftar = statusDaftar;
        this.alasanTolak = alasanTolak;
        this.idAdmin = idAdmin;
        this.tanggalDaftar = tanggalDaftar;
        this.tanggalProses = tanggalProses;
    }
    
    // Getters & Setters
    public int getIdDaftar() { return idDaftar; }
    public void setIdDaftar(int idDaftar) { this.idDaftar = idDaftar; }
    
    public String getNamaAkun() { return namaAkun; }
    public void setNamaAkun(String namaAkun) { this.namaAkun = namaAkun; }
    
    public String getSandi() { return sandi; }
    public void setSandi(String sandi) { this.sandi = sandi; }
    
    public String getNamaLengkap() { return namaLengkap; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTipeDaftar() { return tipeDaftar; }
    public void setTipeDaftar(String tipeDaftar) { this.tipeDaftar = tipeDaftar; }
    
    public String getStatusDaftar() { return statusDaftar; }
    public void setStatusDaftar(String statusDaftar) { this.statusDaftar = statusDaftar; }
    
    public String getAlasanTolak() { return alasanTolak; }
    public void setAlasanTolak(String alasanTolak) { this.alasanTolak = alasanTolak; }
    
    public Integer getIdAdmin() { return idAdmin; }
    public void setIdAdmin(Integer idAdmin) { this.idAdmin = idAdmin; }
    
    public LocalDateTime getTanggalDaftar() { return tanggalDaftar; }
    public void setTanggalDaftar(LocalDateTime tanggalDaftar) { this.tanggalDaftar = tanggalDaftar; }
    
    public LocalDateTime getTanggalProses() { return tanggalProses; }
    public void setTanggalProses(LocalDateTime tanggalProses) { this.tanggalProses = tanggalProses; }
    
    // Helper methods
    public boolean isPending() { return "Pending".equals(statusDaftar); }
    public boolean isApproved() { return "Approved".equals(statusDaftar); }
    public boolean isRejected() { return "Rejected".equals(statusDaftar); }
    
    @Override
    public String toString() {
        return namaAkun + " (" + tipeDaftar + ") - " + statusDaftar;
    }
}

package Proyek.Model;

import java.time.LocalDateTime;

public class Pengguna {
    private int idUser;
    private String namaAkun;
    private String sandi;
    private String namaLengkap;
    private String email;
    private String tipeAkun;
    private boolean aktif;
    private String kodeLink;
    private LocalDateTime tanggalBuat;
    private LocalDateTime loginTerakhir;
    
    // Constructor untuk login
    public Pengguna(String namaAkun, String sandi, String namaLengkap, String email, String tipeAkun) {
        this.namaAkun = namaAkun;
        this.sandi = sandi;
        this.namaLengkap = namaLengkap;
        this.email = email;
        this.tipeAkun = tipeAkun;
        this.aktif = true;
        this.tanggalBuat = LocalDateTime.now();
    }
    
    // Constructor full
    public Pengguna(int idUser, String namaAkun, String sandi, String namaLengkap, String email, 
                    String tipeAkun, boolean aktif, String kodeLink, LocalDateTime tanggalBuat, LocalDateTime loginTerakhir) {
        this.idUser = idUser;
        this.namaAkun = namaAkun;
        this.sandi = sandi;
        this.namaLengkap = namaLengkap;
        this.email = email;
        this.tipeAkun = tipeAkun;
        this.aktif = aktif;
        this.kodeLink = kodeLink;
        this.tanggalBuat = tanggalBuat;
        this.loginTerakhir = loginTerakhir;
    }
    
    // Getters & Setters
    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }
    
    public String getNamaAkun() { return namaAkun; }
    public void setNamaAkun(String namaAkun) { this.namaAkun = namaAkun; }
    
    public String getSandi() { return sandi; }
    public void setSandi(String sandi) { this.sandi = sandi; }
    
    public String getNamaLengkap() { return namaLengkap; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTipeAkun() { return tipeAkun; }
    public void setTipeAkun(String tipeAkun) { this.tipeAkun = tipeAkun; }
    
    public boolean isAktif() { return aktif; }
    public void setAktif(boolean aktif) { this.aktif = aktif; }
    
    public String getKodeLink() { return kodeLink; }
    public void setKodeLink(String kodeLink) { this.kodeLink = kodeLink; }
    
    public LocalDateTime getTanggalBuat() { return tanggalBuat; }
    public void setTanggalBuat(LocalDateTime tanggalBuat) { this.tanggalBuat = tanggalBuat; }
    
    public LocalDateTime getLoginTerakhir() { return loginTerakhir; }
    public void setLoginTerakhir(LocalDateTime loginTerakhir) { this.loginTerakhir = loginTerakhir; }
    
    // Helper methods
    public boolean isMahasiswa() { return "Mahasiswa".equals(tipeAkun); }
    public boolean isOrangTua() { return "OrangTua".equals(tipeAkun); }
    public boolean isAdmin() { return "Admin".equals(tipeAkun); }
    
    @Override
    public String toString() {
        return namaAkun + " (" + tipeAkun + ") - " + namaLengkap;
    }
}

package Proyek.Model;

import java.time.LocalDateTime;
public class Pengguna {

    private int idUser;
    private String namaAkun;
    private String sandi;
    private String namaLengkap;
    private String email;
    private String tipeAkun;
    private String aktif;
    private LocalDateTime tanggalDaftar;
    private LocalDateTime loginTerakhir;
    private byte[] foto;
    private String kodeOrangTua;

    public Pengguna() {
        this.setAktif("belum aktif");
        this.setTanggalDaftar(LocalDateTime.now());
    }

    public Pengguna(int idUser, String namaAkun, String sandi, String namaLengkap, String email,
            String tipeAkun, String aktif, LocalDateTime tanggalDaftar, LocalDateTime loginTerakhir, byte[] foto) {
        this.setIdUser(idUser);
        this.setNamaAkun(namaAkun);
        this.setSandi(sandi);
        this.setNamaLengkap(namaLengkap);
        this.setEmail(email);
        this.setTipeAkun(tipeAkun);
        this.setAktif(aktif);
        this.setTanggalDaftar(tanggalDaftar);
        this.setLoginTerakhir(loginTerakhir);
        this.setFoto(foto);
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getNamaAkun() {
        return namaAkun;
    }

    public void setNamaAkun(String namaAkun) {
        this.namaAkun = namaAkun;
    }

    public String getSandi() {
        return sandi;
    }

    public void setSandi(String sandi) {
        this.sandi = sandi;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipeAkun() {
        return tipeAkun;
    }

    public void setTipeAkun(String tipeAkun) {
        this.tipeAkun = tipeAkun;
    }

    public String getAktif() {
        return aktif;
    }

    public void setAktif(String aktif) {
        this.aktif = aktif;
    }

    public boolean isAktif() {
        return "aktif".equals(this.aktif);
    }

    public LocalDateTime getTanggalDaftar() {
        return tanggalDaftar;
    }

    public void setTanggalDaftar(LocalDateTime tanggalBuat) {
        this.tanggalDaftar = tanggalBuat;
    }

    public LocalDateTime getLoginTerakhir() {
        return loginTerakhir;
    }

    public void setLoginTerakhir(LocalDateTime loginTerakhir) {
        this.loginTerakhir = loginTerakhir;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public boolean isMahasiswa() {
        return "Mahasiswa".equals(tipeAkun);
    }

    public boolean isOrangTua() {
        return "OrangTua".equals(tipeAkun);
    }

    public boolean isAdmin() {
        return "Admin".equals(tipeAkun);
    }

    public String getKodeOrangTua() {
        return kodeOrangTua;
    }

    public void setKodeOrangTua(String kodeOrangTua) {
        this.kodeOrangTua = kodeOrangTua;
    }

    @Override
    public String toString() {
        return namaAkun + " (" + tipeAkun + ") - " + namaLengkap;
    }
}



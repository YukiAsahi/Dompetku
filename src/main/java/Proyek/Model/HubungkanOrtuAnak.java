package Proyek.Model;

import java.time.LocalDateTime;
public class HubungkanOrtuAnak {
    private int idLink;
    private int idOrtu;
    private int idAnak;
    private String kodeAkses;
    private int aktif;
    private LocalDateTime tanggalLink;
    private String namaOrtu;
    private String namaAnak;
    private String emailOrtu;
    private String emailAnak;
    private double totalSaldoAnak;
    private int jumlahDompetAnak;

    public HubungkanOrtuAnak() {
    }

    public HubungkanOrtuAnak(int idAnak, String kodeAkses) {
        this.idAnak = idAnak;
        this.kodeAkses = kodeAkses;
        this.aktif = 0;
        this.tanggalLink = LocalDateTime.now();
    }

    public HubungkanOrtuAnak(int idLink, int idOrtu, int idAnak, String kodeAkses, int aktif,
            LocalDateTime tanggalLink) {
        this.idLink = idLink;
        this.idOrtu = idOrtu;
        this.idAnak = idAnak;
        this.kodeAkses = kodeAkses;
        this.aktif = aktif;
        this.tanggalLink = tanggalLink;
    }

    public int getIdLink() {
        return idLink;
    }

    public void setIdLink(int idLink) {
        this.idLink = idLink;
    }

    public int getIdOrtu() {
        return idOrtu;
    }

    public void setIdOrtu(int idOrtu) {
        this.idOrtu = idOrtu;
    }

    public int getIdAnak() {
        return idAnak;
    }

    public void setIdAnak(int idAnak) {
        this.idAnak = idAnak;
    }

    public String getKodeAkses() {
        return kodeAkses;
    }

    public void setKodeAkses(String kodeAkses) {
        this.kodeAkses = kodeAkses;
    }

    public int getAktif() {
        return aktif;
    }

    public void setAktif(int aktif) {
        this.aktif = aktif;
    }

    public LocalDateTime getTanggalLink() {
        return tanggalLink;
    }

    public void setTanggalLink(LocalDateTime tanggalLink) {
        this.tanggalLink = tanggalLink;
    }

    public String getNamaOrtu() {
        return namaOrtu;
    }

    public void setNamaOrtu(String namaOrtu) {
        this.namaOrtu = namaOrtu;
    }

    public String getNamaAnak() {
        return namaAnak;
    }

    public void setNamaAnak(String namaAnak) {
        this.namaAnak = namaAnak;
    }

    public String getEmailOrtu() {
        return emailOrtu;
    }

    public void setEmailOrtu(String emailOrtu) {
        this.emailOrtu = emailOrtu;
    }

    public String getEmailAnak() {
        return emailAnak;
    }

    public void setEmailAnak(String emailAnak) {
        this.emailAnak = emailAnak;
    }

    public double getTotalSaldoAnak() {
        return totalSaldoAnak;
    }

    public void setTotalSaldoAnak(double totalSaldoAnak) {
        this.totalSaldoAnak = totalSaldoAnak;
    }

    public int getJumlahDompetAnak() {
        return jumlahDompetAnak;
    }

    public void setJumlahDompetAnak(int jumlahDompetAnak) {
        this.jumlahDompetAnak = jumlahDompetAnak;
    }

    public boolean isAktif() {
        return aktif == 1;
    }

    @Override
    public String toString() {
        return "Link: " + namaOrtu + " → " + namaAnak + " [" + (aktif == 1 ? "Aktif" : "Pending") + "]";
    }
}



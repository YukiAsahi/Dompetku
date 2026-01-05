package Proyek.Model;

import java.time.LocalDateTime;

public class Kategori {
    private int id;
    private int idPengguna; // Added: link to user
    private String namaKategori;
    private String jenisKategori;
    private byte[] ikon;
    private boolean aktif;
    private LocalDateTime tanggalTambah;

    public Kategori(String namaKategori, String jenisKategori, int idPengguna) {
        this.setNamaKategori(namaKategori);
        this.setJenisKategori(jenisKategori);
        this.setIdPengguna(idPengguna);
        this.setAktif(true);
        this.setTanggalTambah(LocalDateTime.now());
    }

    // Constructor tanpa idPengguna untuk backward compatibility (kategori default)
    public Kategori(String namaKategori, String jenisKategori) {
        this.setNamaKategori(namaKategori);
        this.setJenisKategori(jenisKategori);
        this.setIdPengguna(0); // 0 = kategori default/global
        this.setAktif(true);
        this.setTanggalTambah(LocalDateTime.now());
    }

    public Kategori(int id, int idPengguna, String namaKategori, String jenisKategori,
            byte[] ikon, boolean aktif, LocalDateTime tanggalTambah) {
        this.setId(id);
        this.setIdPengguna(idPengguna);
        this.setNamaKategori(namaKategori);
        this.setJenisKategori(jenisKategori);
        this.setIkon(ikon);
        this.setAktif(aktif);
        this.setTanggalTambah(tanggalTambah);
    }

    // Old constructor for backward compatibility
    public Kategori(int id, String namaKategori, String jenisKategori,
            byte[] ikon, boolean aktif, LocalDateTime tanggalTambah) {
        this.setId(id);
        this.setIdPengguna(0);
        this.setNamaKategori(namaKategori);
        this.setJenisKategori(jenisKategori);
        this.setIkon(ikon);
        this.setAktif(aktif);
        this.setTanggalTambah(tanggalTambah);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPengguna() {
        return idPengguna;
    }

    public void setIdPengguna(int idPengguna) {
        this.idPengguna = idPengguna;
    }

    public String getNamaKategori() {
        return namaKategori;
    }

    public void setNamaKategori(String namaKategori) {
        this.namaKategori = namaKategori;
    }

    public String getJenisKategori() {
        return jenisKategori;
    }

    public void setJenisKategori(String jenisKategori) {
        this.jenisKategori = jenisKategori;
    }

    public byte[] getIkon() {
        return ikon;
    }

    public void setIkon(byte[] ikon) {
        this.ikon = ikon;
    }

    public boolean isAktif() {
        return aktif;
    }

    public void setAktif(boolean aktif) {
        this.aktif = aktif;
    }

    public LocalDateTime getTanggalTambah() {
        return tanggalTambah;
    }

    public void setTanggalTambah(LocalDateTime tanggalTambah) {
        this.tanggalTambah = tanggalTambah;
    }

    public boolean isPemasukan() {
        return "Pemasukan".equalsIgnoreCase(jenisKategori);
    }

    public boolean isPengeluaran() {
        return "Pengeluaran".equalsIgnoreCase(jenisKategori);
    }

    public boolean isKategoriDefault() {
        return idPengguna == 0;
    }

    @Override
    public String toString() {
        return namaKategori + " (" + jenisKategori + ")";
    }
}

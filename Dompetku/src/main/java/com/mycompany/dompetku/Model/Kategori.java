/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.dompetku.Model;

import java.time.LocalDateTime;

public class Kategori {
    private int id;
    private String namaKategori;
    private String jenisKategori;      
    private String ikon;
    private boolean aktif;
    private LocalDateTime tanggalTambah;
    
    public Kategori(String namaKategori, String jenisKategori) {
        this.namaKategori = namaKategori;
        this.jenisKategori = jenisKategori;
        this.aktif = true;
        this.tanggalTambah = LocalDateTime.now();
    }
    
    
    public Kategori(int id, String namaKategori, String jenisKategori,
                   String ikon, boolean aktif, LocalDateTime tanggalTambah) {
        this.id = id;
        this.namaKategori = namaKategori;
        this.jenisKategori = jenisKategori;
        this.ikon = ikon;
        this.aktif = aktif;
        this.tanggalTambah = tanggalTambah;
    }
    
    public int getId() {
        return id; 
    }
    public void setId(int id) {
        this.id = id; 
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
    public String getIkon() {
        return ikon; 
    }
    public void setIkon(String ikon) {
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
    
    public boolean isIncome() {
        return "INCOME".equals(jenisKategori); 
    }
    public boolean isExpense() {
        return "EXPENSE".equals(jenisKategori); 
    }
    
    @Override
    public String toString() {
        return namaKategori + " (" + jenisKategori + ")";
    }
}

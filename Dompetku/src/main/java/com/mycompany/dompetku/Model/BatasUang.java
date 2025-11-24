/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.dompetku.Model;

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
    
    private String kategoriNama;
    private double currentSpending;
    
    public BatasUang(int userId, int kategoriId, double maxUang) {
        this.userId = userId;
        this.kategoriId = kategoriId;
        this.maxUang = maxUang;
        this.tanggalBikin = LocalDateTime.now();
    }
    
    public BatasUang(int id, int userId, int kategoriId, double maxUang, LocalDate mulaiDari, LocalDate selesaiSampai, LocalDateTime tanggalBikin) {
        this.id = id;
        this.userId = userId;
        this.kategoriId = kategoriId;
        this.maxUang = maxUang;
        this.mulaiDari = mulaiDari;
        this.selesaiSampai = selesaiSampai;
        this.tanggalBikin = tanggalBikin;
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
    
    public double getRemainingBudget() {
        return maxUang - currentSpending; 
    }
    public double getBudgetUsagePercent() {
        return (currentSpending / maxUang) * 100; 
    }
    public boolean isExceeded() {
        return currentSpending > maxUang; 
    }
    
    @Override
    public String toString() {
        return kategoriNama + " - Limit: Rp " + String.format("%.2f", maxUang);
    }
}


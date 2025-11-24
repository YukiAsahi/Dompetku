/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.dompetku.Model;

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
    
    public CatatanUang(int dompetId, int userId, String jenisCatatan, double nominal) {
        this.dompetId = dompetId;
        this.userId = userId;
        this.jenisCatatan = jenisCatatan;
        this.nominal = nominal;
        this.tanggalCatat = LocalDateTime.now();
    }
    
    public CatatanUang(int id, int dompetId, int userId, String jenisCatatan, int kategoriId, double nominal, String catatan, LocalDateTime tanggalCatat) {
        this.id = id;
        this.dompetId = dompetId;
        this.userId = userId;
        this.jenisCatatan = jenisCatatan;
        this.kategoriId = kategoriId;
        this.nominal = nominal;
        this.catatan = catatan;
        this.tanggalCatat = tanggalCatat;
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
    
    public boolean isIncome() {
        return "INCOME".equals(jenisCatatan); 
    }
    public boolean isExpense() {
        return "EXPENSE".equals(jenisCatatan); 
    }
    public boolean isTransfer() {
        return "TRANSFER".equals(jenisCatatan); 
    }
    
    @Override
    public String toString() {
        return tanggalCatat + " - " + jenisCatatan + ": Rp " + String.format("%.2f", nominal);
    }
}


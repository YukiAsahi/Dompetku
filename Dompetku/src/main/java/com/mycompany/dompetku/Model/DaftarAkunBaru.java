/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.dompetku.Model;

import java.time.LocalDateTime;

public class DaftarAkunBaru {
    private int id;
    private String namaAkun;
    private String email;
    private String sandi;
    private String namaLengkap;
    private String tipeDaftar; 
    private String statusDaftar;
    private String alasanTolak;
    private LocalDateTime tanggalDaftar;
    private LocalDateTime tanggalProses;
    private Integer idAdmin;
    
    public DaftarAkunBaru(String namaAkun, String email, String sandi, String namaLengkap, String tipeDaftar) {
        this.namaAkun = namaAkun;
        this.email = email;
        this.sandi = sandi;
        this.namaLengkap = namaLengkap;
        this.tipeDaftar = tipeDaftar;
        this.statusDaftar = "PENDING";
        this.tanggalDaftar = LocalDateTime.now();
    }
    
    
    public DaftarAkunBaru(int id, String namaAkun, String email, String sandi, String namaLengkap, String tipeDaftar, String statusDaftar, String alasanTolak, LocalDateTime tanggalDaftar, LocalDateTime tanggalProses, Integer idAdmin) {
        this.id = id;
        this.namaAkun = namaAkun;
        this.email = email;
        this.sandi = sandi;
        this.namaLengkap = namaLengkap;
        this.tipeDaftar = tipeDaftar;
        this.statusDaftar = statusDaftar;
        this.alasanTolak = alasanTolak;
        this.tanggalDaftar = tanggalDaftar;
        this.tanggalProses = tanggalProses;
        this.idAdmin = idAdmin;
    }
    
    public int getId() {
        return id; 
    }
    public void setId(int id) {
        this.id = id; 
    }
    public String getNamaAkun() {
        return namaAkun; 
    }
    public void setNamaAkun(String namaAkun) {
        this.namaAkun = namaAkun; 
    }
    public String getEmail() {
        return email; 
    }
    public void setEmail(String email) {
        this.email = email; 
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
    public String getTipeDaftar() {
        return tipeDaftar; 
    }
    public void setTipeDaftar(String tipeDaftar) {
        this.tipeDaftar = tipeDaftar; 
    }
    public String getStatusDaftar() {
        return statusDaftar; 
    }
    public void setStatusDaftar(String statusDaftar) {
        this.statusDaftar = statusDaftar; 
    }
    public String getAlasanTolak() {
        return alasanTolak; 
    }
    public void setAlasanTolak(String alasanTolak) {
        this.alasanTolak = alasanTolak; 
    }
    public LocalDateTime getTanggalDaftar() {
        return tanggalDaftar; 
    }
    public void setTanggalDaftar(LocalDateTime tanggalDaftar) {
        this.tanggalDaftar = tanggalDaftar; 
    }
    public LocalDateTime getTanggalProses() {
        return tanggalProses; 
    }
    public void setTanggalProses(LocalDateTime tanggalProses) {
        this.tanggalProses = tanggalProses; 
    }
    public Integer getIdAdmin() {
        return idAdmin; 
    }
    public void setIdAdmin(Integer idAdmin) {
        this.idAdmin = idAdmin; 
    }
    
    public boolean isPending() {
        return "PENDING".equals(statusDaftar); 
    }
    public boolean isApproved() {
        return "APPROVED".equals(statusDaftar); 
    }
    public boolean isRejected() {
        return "REJECTED".equals(statusDaftar); 
    }
    
    @Override
    public String toString() {
        return namaAkun + " (" + tipeDaftar + ") - " + statusDaftar;
    }
}

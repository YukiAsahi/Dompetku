/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.dompetku.Model;

import java.time.LocalDateTime;

public class Pengguna {
    private int id;
    private String username;
    private String password;
    private String email;
    private String namaLengkap;
    private String tipeUser;          
    private boolean aktif;
    private LocalDateTime createdDate;
    
    public Pengguna(String username, String password, String email, String namaLengkap, String tipeUser) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.namaLengkap = namaLengkap;
        this.tipeUser = tipeUser;
        this.aktif = true;
        this.createdDate = LocalDateTime.now();
    }
    
    public Pengguna(int id, String username, String password, String email, String namaLengkap, String tipeUser, boolean aktif, LocalDateTime createdDate) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.namaLengkap = namaLengkap;
        this.tipeUser = tipeUser;
        this.aktif = aktif;
        this.createdDate = createdDate;
    }
    
    public int getId(){
        return id; 
    }
    public void setId(int id) {
        this.id = id; 
    }
    public String getUsername() {
        return username; 
    }
    public void setUsername(String username) {
        this.username = username; 
    }
    public String getPassword() {
        return password; 
    }
    public void setPassword(String password) {
        this.password = password; 
    }
    public String getEmail() {
        return email; 
    }
    public void setEmail(String email) {
        this.email = email; 
    }
    public String getNamaLengkap() {
        return namaLengkap; 
    }
    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap; 
    }
    public String getTipeUser() {
        return tipeUser; 
    }
    public void setTipeUser(String tipeUser) {
        this.tipeUser = tipeUser; 
    }
    public boolean isAktif() {
        return aktif; 
    }
    public void setAktif(boolean aktif) {
        this.aktif = aktif; 
    }
    public LocalDateTime getCreatedDate() {
        return createdDate; 
    }
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate; 
    }
    
    public boolean isMahasiswa() {
        return "MAHASISWA".equals(tipeUser); 
    }
    public boolean isOrangtua() {
        return "ORANG_TUA".equals(tipeUser); 
    }
    public boolean isAdmin() {
        return "ADMIN".equals(tipeUser); 
    }
    
    @Override
    public String toString() {
        return username + " (" + tipeUser + ") - " + namaLengkap;
    }
}


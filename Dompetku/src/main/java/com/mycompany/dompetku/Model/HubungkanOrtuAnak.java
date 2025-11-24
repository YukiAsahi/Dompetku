/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.dompetku.Model;

import java.time.LocalDateTime;

public class HubungkanOrtuAnak {
    private int id;
    private int idOrtu;
    private int idAnak;
    private String kodeAkses;
    private String statusLink;         
    private LocalDateTime tanggalRequest;
    private LocalDateTime tanggalTerima;
    
    
    private String namaOrtu;
    private String namaAnak;
    private String emailOrtu;
    private String emailAnak;
    private double totalSaldoAnak;
    private int jumlahDompetAnak;
    
    
    public HubungkanOrtuAnak(int idOrtu, int idAnak) {
        this.idOrtu = idOrtu;
        this.idAnak = idAnak;
        this.statusLink = "PENDING";
        this.tanggalRequest = LocalDateTime.now();
    }
    
    
    public HubungkanOrtuAnak(int id, int idOrtu, int idAnak, String kodeAkses, String statusLink, LocalDateTime tanggalRequest,LocalDateTime tanggalTerima) {
        this.id = id;
        this.idOrtu = idOrtu;
        this.idAnak = idAnak;
        this.kodeAkses = kodeAkses;
        this.statusLink = statusLink;
        this.tanggalRequest = tanggalRequest;
        this.tanggalTerima = tanggalTerima;
    }
    
    public int getId() {
        return id; 
    }
    public void setId(int id) {
        this.id = id; 
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
    public String getStatusLink() {
        return statusLink; 
    }
    public void setStatusLink(String statusLink) {
        this.statusLink = statusLink; 
    }
    public LocalDateTime getTanggalRequest() {
        return tanggalRequest; 
    }
    public void setTanggalRequest(LocalDateTime tanggalRequest) {
        this.tanggalRequest = tanggalRequest; 
    }
    public LocalDateTime getTanggalTerima() {
        return tanggalTerima; 
    }
    public void setTanggalTerima(LocalDateTime tanggalTerima) {
        this.tanggalTerima = tanggalTerima; 
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
    
    public boolean isPending() {
        return "PENDING".equals(statusLink); 
    }
    public boolean isAccepted() {
        return "ACCEPTED".equals(statusLink); 
    }
    public boolean isRejected() {
        return "REJECTED".equals(statusLink); 
    }
    
    @Override
    public String toString() {
        return "Link: " + namaOrtu + " → " + namaAnak + " [" + statusLink + "]";
    }
}


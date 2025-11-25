/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Proyek.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class KartuLaporan {
    private int id;
    private int userId;
    private LocalDate awalPeriode;
    private LocalDate akhirPeriode;
    private LocalDateTime createdDate;
    
    private double totalIncome;
    private double totalExpense;
    private double netBalance;
    private int jumlahTransaksi;
    private int jumlahDompet;
    
    public KartuLaporan(int userId) {
        this.userId = userId;
        this.createdDate = LocalDateTime.now();
    }
    
    public KartuLaporan(int id, int userId, LocalDate awalPeriode, LocalDate akhirPeriode, LocalDateTime createdDate) {
        this.id = id;
        this.userId = userId;
        this.awalPeriode = awalPeriode;
        this.akhirPeriode = akhirPeriode;
        this.createdDate = createdDate;
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
    public LocalDate getAwalPeriode() {
        return awalPeriode; 
    }
    public void setAwalPeriode(LocalDate awalPeriode) {
        this.awalPeriode = awalPeriode; 
    }
    public LocalDate getAkhirPeriode() {
        return akhirPeriode; 
    }
    public void setAkhirPeriode(LocalDate akhirPeriode) {
        this.akhirPeriode = akhirPeriode; 
    }
    public LocalDateTime getCreatedDate() {
        return createdDate; 
    }
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate; 
    }
    public double getTotalIncome() {
        return totalIncome; 
    }
    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome; 
    }
    public double getTotalExpense() {
        return totalExpense; 
    }
    public void setTotalExpense(double totalExpense) {
        this.totalExpense = totalExpense; 
    }
    public double getNetBalance() {
        return netBalance; 
    }
    public void setNetBalance(double netBalance) {
        this.netBalance = netBalance; 
    }
    public int getJumlahTransaksi() {
        return jumlahTransaksi; 
    }
    public void setJumlahTransaksi(int jumlahTransaksi) {
        this.jumlahTransaksi = jumlahTransaksi; 
    }
    public int getJumlahDompet() {
        return jumlahDompet; 
    }
    public void setJumlahDompet(int jumlahDompet) {
        this.jumlahDompet = jumlahDompet; 
    }
    
    public void calculateNetBalance() {
        this.netBalance = totalIncome - totalExpense;
    }
    
    public double getSavingsRate() {
        if (totalIncome == 0) return 0;
        return (netBalance / totalIncome) * 100;
    }
    
    public String getPeriodeString() {
        return awalPeriode + " s/d " + akhirPeriode;
    }
    
    @Override
    public String toString() {
        return "Report [" + getPeriodeString() + "] - Income: Rp " + 
               String.format("%.2f", totalIncome) + " | Expense: Rp " +
               String.format("%.2f", totalExpense);
    }
}


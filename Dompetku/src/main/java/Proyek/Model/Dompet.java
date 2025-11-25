/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Proyek.Model;

import java.time.LocalDateTime;

public class Dompet {
    private int id;
    private int userId;
    private String namaDompet;
    private String jenisDompet;        
    private double saldoSekarang;
    private Double targetSaldo;
    private LocalDateTime deadlineTabung;
    private LocalDateTime dibukupKapan;
    
    private double totalIncome;
    private double totalExpense;
    
    
    public Dompet(int userId, String namaDompet, String jenisDompet, double saldoSekarang) {
        this.userId = userId;
        this.namaDompet = namaDompet;
        this.jenisDompet = jenisDompet;
        this.saldoSekarang = saldoSekarang;
        this.dibukupKapan = LocalDateTime.now();
    }
    
    
    public Dompet(int id, int userId, String namaDompet, String jenisDompet, double saldoSekarang, Double targetSaldo, LocalDateTime deadlineTabung, LocalDateTime dibukupKapan) {
        this.id = id;
        this.userId = userId;
        this.namaDompet = namaDompet;
        this.jenisDompet = jenisDompet;
        this.saldoSekarang = saldoSekarang;
        this.targetSaldo = targetSaldo;
        this.deadlineTabung = deadlineTabung;
        this.dibukupKapan = dibukupKapan;
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
    public String getNamaDompet() {
        return namaDompet; 
    }
    public void setNamaDompet(String namaDompet) {
        this.namaDompet = namaDompet; 
    }
    public String getJenisDompet() {
        return jenisDompet; 
    }
    public void setJenisDompet(String jenisDompet) {
        this.jenisDompet = jenisDompet; 
    }
    public double getSaldoSekarang() {
        return saldoSekarang; 
    }
    public void setSaldoSekarang(double saldoSekarang) {
        this.saldoSekarang = saldoSekarang; 
    }
    public Double getTargetSaldo() {
        return targetSaldo; 
    }
    public void setTargetSaldo(Double targetSaldo) {
        this.targetSaldo = targetSaldo; 
    }
    public LocalDateTime getDeadlineTabung() {
        return deadlineTabung; 
    }
    public void setDeadlineTabung(LocalDateTime deadlineTabung) {
        this.deadlineTabung = deadlineTabung; 
    }
    public LocalDateTime getDibukupKapan() {
        return dibukupKapan; 
    }
    public void setDibukupKapan(LocalDateTime dibukupKapan) {
        this.dibukupKapan = dibukupKapan; 
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
    
    public boolean isMain() {
        return "MAIN".equals(jenisDompet); 
    }
    public boolean isSavings() {
        return "SAVINGS".equals(jenisDompet); 
    }
    public boolean hasSavingsGoal() {
        return targetSaldo != null && targetSaldo > 0; 
    }
    public double getSavingsProgress() {
        if (!hasSavingsGoal()) return 0;
        return (saldoSekarang / targetSaldo) * 100;
    }
    
    @Override
    public String toString() {
        return namaDompet + " (" + jenisDompet + ") - Rp " + String.format("%.2f", saldoSekarang);
    }
}


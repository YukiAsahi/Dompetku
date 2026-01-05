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
    private byte[] icon;
    private boolean utamaEnggak;

    private double totalIncome;
    private double totalExpense;

    public Dompet(int userId, String namaDompet, String jenisDompet, double saldoSekarang) {
        this.setUserId(userId);
        this.setNamaDompet(namaDompet);
        this.setJenisDompet(jenisDompet);
        this.setSaldoSekarang(saldoSekarang);
        this.setDibukupKapan(LocalDateTime.now());
    }

    public Dompet(int id, int userId, String namaDompet, String jenisDompet, double saldoSekarang, Double targetSaldo,
            LocalDateTime deadlineTabung, LocalDateTime dibukupKapan) {
        this.setId(id);
        this.setUserId(userId);
        this.setNamaDompet(namaDompet);
        this.setJenisDompet(jenisDompet);
        this.setSaldoSekarang(saldoSekarang);
        this.setTargetSaldo(targetSaldo);
        this.setDeadlineTabung(deadlineTabung);
        this.setDibukupKapan(dibukupKapan);
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

    public byte[] getIcon() {
        return icon;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }

    public boolean isUtamaEnggak() {
        return utamaEnggak;
    }

    public void setUtamaEnggak(boolean utamaEnggak) {
        this.utamaEnggak = utamaEnggak;
    }

    public boolean isUtama() {
        return utamaEnggak;
    }

    public boolean isTabungan() {
        return !utamaEnggak;
    }

    public boolean punyaTargetTabungan() {
        return targetSaldo != null && targetSaldo > 0;
    }

    public double hitungProgressTabungan() {
        if (!punyaTargetTabungan())
            return 0;
        return (saldoSekarang / targetSaldo) * 100;
    }

    @Override
    public String toString() {
        return namaDompet + " (" + jenisDompet + ") - Rp " + String.format("%.2f", saldoSekarang);
    }
}



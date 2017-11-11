package com.example.kemik.bonusball.Entities;

/**
 * Created by kemik on 29/10/2017.
 */

public class Entrant {

    private long entrantId;
    private String entrantName;
    private int lineNumber;
    private String paymentStatus;
    private long drawId;

    public Entrant() {
    }

    public Entrant(long entrantId, String entrantName, int lineNumber, String paymentStatus,
                   long drawId) {
        this.entrantId = entrantId;
        this.entrantName = entrantName;
        this.lineNumber = lineNumber;
        this.paymentStatus = paymentStatus;
        this.drawId = drawId;
    }

    public long getEntrantId() {
        return entrantId;
    }

    public void setEntrantId(long entrantId) {
        this.entrantId = entrantId;
    }

    public String getEntrantName() {
        return entrantName;
    }

    public void setEntrantName(String entrantName) {
        this.entrantName = entrantName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public long getDrawId() {
        return drawId;
    }

    public void setDrawId(long drawId) {
        this.drawId = drawId;
    }

    @Override
    public String toString() {
        return "Entrant{" +
                "entrantId=" + entrantId +
                ", entrantName='" + entrantName + '\'' +
                ", lineNumber=" + lineNumber +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", drawId=" + drawId +
                '}';
    }
}

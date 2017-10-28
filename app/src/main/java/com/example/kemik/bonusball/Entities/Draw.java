package com.example.kemik.bonusball.Entities;

/**
 * Created by kemik on 26/10/2017.
 */

public class Draw {

    private long drawId;
    private String drawName;
    private String entrantName;
    private int number;
    private double drawValue;
    private double ticketValue;
    private long startDate;
    private int winner;
    private String paymentStatus;

    public Draw() {}

    public Draw(long drawId, String drawName, String entrantName, int number, double drawValue,
                double ticketValue, long startDate, int winner, String paymentStatus) {
        this.drawId = drawId;
        this.drawName = drawName;
        this.entrantName = entrantName;
        this.number = number;
        this.drawValue = drawValue;
        this.ticketValue = ticketValue;
        this.startDate = startDate;
        this.winner = winner;
        this.paymentStatus = paymentStatus;
    }

    public long getDrawId() {
        return drawId;
    }

    public void setDrawId(long drawId) {
        this.drawId = drawId;
    }

    public String getDrawName() {
        return drawName;
    }

    public void setDrawName(String drawName) {
        this.drawName = drawName;
    }

    public String getEntrantName() {
        return entrantName;
    }

    public void setEntrantName(String entrantName) {
        this.entrantName = entrantName;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public double getDrawValue() {
        return drawValue;
    }

    public void setDrawValue(double drawValue) {
        this.drawValue = drawValue;
    }

    public double getTicketValue() {
        return ticketValue;
    }

    public void setTicketValue(double ticketValue) {
        this.ticketValue = ticketValue;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    @Override
    public String toString() {
        return "Draw{" +
                "drawId=" + drawId +
                ", drawName='" + drawName + '\'' +
                ", entrantName='" + entrantName + '\'' +
                ", number=" + number +
                ", drawValue=" + drawValue +
                ", ticketValue=" + ticketValue +
                ", startDate=" + startDate +
                ", winner=" + winner +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}

package com.example.kemik.bonusball.Entities;

/**
 * Created by kemik on 26/10/2017.
 */

public class Draw {

    private long drawId;
    private String drawName;
    private double drawValue;
    private double ticketValue;
    private long startDate;
    private int winner;

    public Draw() {}

    public Draw(long drawId, String drawName, double drawValue, double ticketValue, long startDate,
                int winner) {
        this.drawId = drawId;
        this.drawName = drawName;
        this.drawValue = drawValue;
        this.ticketValue = ticketValue;
        this.startDate = startDate;
        this.winner = winner;
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

    @Override
    public String toString() {
        return "Draw{" +
                "drawId=" + drawId +
                ", drawName='" + drawName + '\'' +
                ", drawValue=" + drawValue +
                ", ticketValue=" + ticketValue +
                ", startDate=" + startDate +
                ", winner=" + winner +
                '}';
    }
}

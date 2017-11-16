package com.example.kemik.bonusball;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.kemik.bonusball.Entities.Draw;
import com.example.kemik.bonusball.Entities.Entrant;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bonusBall.db";
    private static final int DATABASE_VERSION = 1;

    /**
     * Tables
     */
    private static final String DRAW_TABLE = "draw_table";
    private static final String ENTRANT_TABLE = "entrant_table";

    /**
     * Columns
     */
    // Draw
    private static final String COLUMN_DRAW_ID = "drawId";
    private static final String COLUMN_DRAW_NAME = "drawName";
    private static final String COLUMN_DRAW_VALUE = "drawValue";
    private static final String COLUMN_TICKET_VALUE = "ticketValue";
    private static final String COLUMN_START_DATE = "startDate";
    private static final String COLUMN_WINNER = "winner";
    // Entrant
    private static final String COLUMN_ENTRANT_ID = "entrantId";
    private static final String COLUMN_LINE_NUMBER = "lineNumber";
    private static final String COLUMN_ENTRANT_NAME = "entrantName";
    private static final String COLUMN_PAYMENT_STATUS = "paymentStatus";
    private static final String COLUMN_DRAW = "draw";

    /**
     * Create Tables
     */
    private static final String CREATE_DRAW_TABLE =
            "CREATE TABLE IF NOT EXISTS " + DRAW_TABLE + "(" +
                    COLUMN_DRAW_ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_DRAW_NAME + " VARCHAR(50), " +
                    COLUMN_DRAW_VALUE + " REAL, " +
                    COLUMN_TICKET_VALUE + " REAL, " +
                    COLUMN_START_DATE + " INTEGER, " +
                    COLUMN_WINNER + " INTEGER);";

    private static final String CREATE_ENTRANT_TABLE =
            "CREATE TABLE IF NOT EXISTS " + ENTRANT_TABLE + "(" +
                    COLUMN_ENTRANT_ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_LINE_NUMBER + " INTEGER, " +
                    COLUMN_ENTRANT_NAME + " VARCHAR(50), " +
                    COLUMN_PAYMENT_STATUS + " VARCHAR(50), " +
                    COLUMN_DRAW + " INTEGER, " +
                    " FOREIGN KEY (" + COLUMN_DRAW + ") REFERENCES " + DRAW_TABLE + "(" + COLUMN_DRAW_ID + "));";
    /**
     * Constructor
     *
     * @param context
     */
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("z! ===== ===== ===== ===== =====");
        System.out.println("z! DBHelper - onCreate()...");
        db.execSQL(CREATE_DRAW_TABLE);
        db.execSQL(CREATE_ENTRANT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("z! DBHelper - onUpgrade()...");
        db.execSQL("DROP TABLE IF EXISTS " + ENTRANT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DRAW_TABLE);
        onCreate(db);
    }

    /**
     * Create a new Draw
     * Also creates 59 numbered lines in an Entrant Table with the drawId
     */
    public void createNewDraw(String drawName, double drawValue, double ticketValue, long startDate) {
        System.out.println("z! ===== ===== ===== ===== =====");
        System.out.println("z! DBHelper - createNewDraw()...");

        SQLiteDatabase db = getWritableDatabase();

        ContentValues drawValues = new ContentValues();
        drawValues.put(COLUMN_DRAW_NAME, drawName);
        drawValues.put(COLUMN_DRAW_VALUE, drawValue);
        drawValues.put(COLUMN_TICKET_VALUE, ticketValue);
        drawValues.put(COLUMN_START_DATE, startDate);
        long drawId = db.insert(DRAW_TABLE, null, drawValues);

        // Populate this Entrant Table with 59 numbered lines with the appropriate drawId
        ContentValues entrantValues = new ContentValues();
        for (int i = 1; i < 60; i++) {
            entrantValues.put(COLUMN_LINE_NUMBER, i);
            entrantValues.put(COLUMN_DRAW, drawId);
            db.insert(ENTRANT_TABLE, null, entrantValues);
        }

        db.close();
    }

    /**
     * Select all details of all draws, ordered by newest first
     */
    public ArrayList<Draw> getDraws() {
        System.out.println("z! ===== ===== ===== ===== =====");
        System.out.println("z! DBHelper - getDraws()...");
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Draw> draws = new ArrayList<>();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DRAW_TABLE + " ORDER BY " + COLUMN_DRAW_ID + " DESC",
                null
        );

        if (cursor.moveToFirst()) {
            System.out.println("z! DBHelper - getDraws - moveToFirst");
            do {
                Draw draw = new Draw();
                draw.setDrawId(cursor.getLong(cursor.getColumnIndex(COLUMN_DRAW_ID)));
                draw.setDrawName(cursor.getString(cursor.getColumnIndex(COLUMN_DRAW_NAME)));
                draw.setDrawValue(cursor.getDouble(cursor.getColumnIndex(COLUMN_DRAW_VALUE)));
                draw.setTicketValue(cursor.getDouble(cursor.getColumnIndex(COLUMN_TICKET_VALUE)));
                draw.setStartDate(cursor.getLong(cursor.getColumnIndex(COLUMN_START_DATE)));
                draws.add(draw);
                System.out.println("z! DBHelper - getDraws - drawsNames:" + draw.getDrawName());
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return draws;
    }

    /**
     * Get a single draw from its ID
     *
     * @param drawId
     * @return
     */
    public Draw getDrawById(long drawId) {
        System.out.println("z! ===== ===== ===== ===== =====");
        System.out.println("z! DBHelper - getDrawById()...");

        SQLiteDatabase db = getReadableDatabase();
        Draw draw = new Draw();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DRAW_TABLE + " dt " +
                        " JOIN " + ENTRANT_TABLE + " et " +
                        " WHERE dt." + COLUMN_DRAW_ID + " = " + drawId,
                null
        );

        if (cursor.moveToFirst()) {
            draw.setDrawId(cursor.getLong(cursor.getColumnIndex(COLUMN_DRAW_ID)));
            draw.setDrawName(cursor.getString(cursor.getColumnIndex(COLUMN_DRAW_NAME)));
            draw.setDrawValue(cursor.getDouble(cursor.getColumnIndex(COLUMN_DRAW_VALUE)));
            draw.setTicketValue(cursor.getDouble(cursor.getColumnIndex(COLUMN_TICKET_VALUE)));
            draw.setStartDate(cursor.getLong(cursor.getColumnIndex(COLUMN_START_DATE)));
        }
        cursor.close();
        db.close();

        return draw;
    }

    /**
     * Update Draw details using the drawId
     *
     * @param drawName
     * @param drawValue
     * @param ticketValue
     * @param startDate
     * @param drawId
     */
    public void updateDraw(String drawName, double drawValue, double ticketValue, long startDate, long drawId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_DRAW_NAME, drawName);
        values.put(COLUMN_DRAW_VALUE, drawValue);
        values.put(COLUMN_TICKET_VALUE, ticketValue);
        values.put(COLUMN_START_DATE, startDate);
        db.update(DRAW_TABLE, values, COLUMN_DRAW_ID + " = " + drawId, null);

        db.close();
    }

    /**
     * Get a single entrants details from their ID
     *
     * @param drawId
     * @return
     */
    public ArrayList<Entrant> getEntrantsByDrawId(long drawId) {
        System.out.println("z! ===== ===== ===== ===== =====");
        System.out.println("z! DBHelper - getEntrantsByDrawId()...");

        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Entrant> entrants = new ArrayList<>();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + ENTRANT_TABLE +
                        " WHERE " + COLUMN_DRAW + " = " + drawId + ";",
                null
        );

        if (cursor.moveToFirst()) {
            do {
                Entrant entrant = new Entrant();
                entrant.setEntrantId(cursor.getLong(cursor.getColumnIndex(COLUMN_ENTRANT_ID)));
                entrant.setEntrantName(cursor.getString(cursor.getColumnIndex(COLUMN_ENTRANT_NAME)));
                entrant.setLineNumber(cursor.getInt(cursor.getColumnIndex(COLUMN_LINE_NUMBER)));
                entrant.setPaymentStatus(cursor.getString(cursor.getColumnIndex(COLUMN_PAYMENT_STATUS)));
                entrant.setDrawId(cursor.getLong(cursor.getColumnIndex(COLUMN_DRAW)));
                entrants.add(entrant);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return entrants;
    }

    /**
     * Update the row using the lineNumber and drawId as a reference
     * with the entrants name
     *
     * @param name
     * @param lineNumber
     */
    public void updateNameToChosenNumber(String name, int lineNumber, long drawId) {
        System.out.println("z! ===== ===== ===== ===== =====");
        System.out.println("z! DBHelper - updateNameToChosenNumber()...");
        System.out.println("z! DBHelper - updateNameToChosenNumber() - name: " + name);
        System.out.println("z! DBHelper - updateNameToChosenNumber() - lineNumber: " + lineNumber);
        System.out.println("z! DBHelper - updateNameToChosenNumber() - drawId: " + drawId);

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_ENTRANT_NAME, name);
        db.update(
                ENTRANT_TABLE,
                values,
                COLUMN_LINE_NUMBER + " = " + lineNumber +
                        " AND " + COLUMN_DRAW + " = " + drawId,
                null);

        db.close();
    }

    /**
     * Get the amount of available numbers for this draw by returning the
     *
     * @param drawId
     * @return
     */
    public int getAvailableAmountOfNumbers(long drawId) {
        SQLiteDatabase db = getReadableDatabase();
        int counter = 0;
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_ENTRANT_NAME + " FROM " + ENTRANT_TABLE +
                        " WHERE " + COLUMN_ENTRANT_NAME + " IS NULL " +
                        "AND " + COLUMN_DRAW + " = " + drawId, null);

        if (cursor.moveToFirst()) {
            do {
                counter++;
            } while (cursor.moveToNext());
        }

        return counter;
    }

    /**
     * Return an array of the remaining numbers for a draw,
     * checking for null entrantName entries against the drawId
     * @param drawId
     * @return
     */
    public ArrayList<Integer> getRemainingNumbers(long drawId) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Integer> remainingNumbers = new ArrayList<>();

        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_LINE_NUMBER + " FROM " + ENTRANT_TABLE +
                        " WHERE " + COLUMN_ENTRANT_NAME + " IS NULL " +
                        "AND " + COLUMN_DRAW + " = " + drawId, null);

        if (cursor.moveToFirst()) {
            do {
                remainingNumbers.add(cursor.getInt(cursor.getColumnIndex(COLUMN_LINE_NUMBER)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return remainingNumbers;
    }

    /**
     * Delete Draw and its associated Entrants by drawId
     * @param drawId
     */
    public void deleteDraw(long drawId) {
        System.out.println("z! ===== ===== ===== ===== =====");
        System.out.println("z! DBHelper - deleteDraw()...");
        SQLiteDatabase db = getWritableDatabase();
        db.delete(ENTRANT_TABLE, COLUMN_DRAW + " = " + drawId, null);
        db.delete(DRAW_TABLE, COLUMN_DRAW_ID + " = " + drawId, null);

        db.close();
    }

    /**
     * Drop all tables
     * @param db
     */
    public void dropTables(SQLiteDatabase db) {
        System.out.println("z! ===== ===== ===== ===== =====");
        System.out.println("z! DBHelper - dropTables()...");
        db.execSQL("DROP TABLE IF EXISTS " + DRAW_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ENTRANT_TABLE);
    }

    public void changePaymentStatus(int lineNumber, long drawId, String status) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_PAYMENT_STATUS, status);
        db.update(ENTRANT_TABLE, values, COLUMN_LINE_NUMBER + " = " + lineNumber + " AND " + COLUMN_DRAW + " = " + drawId, null);

        db.close();
    }
}

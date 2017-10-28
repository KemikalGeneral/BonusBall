package com.example.kemik.bonusball;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.kemik.bonusball.Entities.Draw;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bonusBall.db";
    private static final int DATABASE_VERSION = 1;

    /**
     * Tables
     */
    private static final String DRAW_TABLE = "draw_table";

    /**
     * Columns
     */
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DRAW_NAME = "drawName";
    private static final String COLUMN_ENTRANT_NAME = "entrantName";
    private static final String COLUMN_NUMBER = "number";
    private static final String COLUMN_DRAW_VALUE = "drawValue";
    private static final String COLUMN_TICKET_VALUE = "ticketValue";
    private static final String COLUMN_START_DATE = "startDate";
    private static final String COLUMN_PAYMENT_STATUS = "paymentStatus";
    private static final String COLUMN_WINNER = "winner";

    /**
     * Create Tables
     */
    private static final String CREATE_DRAW_TABLE =
            "CREATE TABLE IF NOT EXISTS " + DRAW_TABLE + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_DRAW_NAME + " VARCHAR(50), " +
                    COLUMN_ENTRANT_NAME + " VARCHAR(50), " +
                    COLUMN_NUMBER + " INTEGER, " +
                    COLUMN_DRAW_VALUE + " REAL, " +
                    COLUMN_TICKET_VALUE + " REAL, " +
                    COLUMN_START_DATE + " INTEGER, " +
                    COLUMN_WINNER + " INTEGER," +
                    COLUMN_PAYMENT_STATUS + " VARCHAR(50));";

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
        System.out.println("z! DBHelper - onCreate()...");
        db.execSQL(CREATE_DRAW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("z! DBHelper - onUpgrade()...");
        db.execSQL("DROP TABLE IF EXISTS " + DRAW_TABLE);
        onCreate(db);
    }

    /**
     * Create a new Draw
     */
    public void createNewDraw(String drawName, double drawValue, double ticketValue, long startDate) {
        System.out.println("z! DBHelper - createNewDraw()...");
        System.out.println("z! DBHelper - createNewDraw - drawName: " + drawName);
        System.out.println("z! DBHelper - createNewDraw - drawValue: " + drawValue);
        System.out.println("z! DBHelper - createNewDraw - ticketValue: " + ticketValue);
        System.out.println("z! DBHelper - createNewDraw - drawStartDate: " + startDate);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_DRAW_NAME, drawName);
        values.put(COLUMN_DRAW_VALUE, drawValue);
        values.put(COLUMN_TICKET_VALUE, ticketValue);
        values.put(COLUMN_START_DATE, startDate);

        long id = db.insert(DRAW_TABLE, null, values);
        System.out.println("z! DBHelper - createNewDraw - id: " + id);
    }

    /**
     * Select details of draw
     */
    public ArrayList<Draw> getDraws() {
        System.out.println("z! DBHelper - getDraws()...");
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Draw> draws = new ArrayList<>();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DRAW_TABLE,
                null
        );

        if (cursor.moveToFirst()) {
            System.out.println("z! DBHelper - getDraws - moveToFirst");
            do {
                Draw draw = new Draw();
                draw.setDrawId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                draw.setDrawName(cursor.getString(cursor.getColumnIndex(COLUMN_DRAW_NAME)));
                draw.setDrawValue(cursor.getDouble(cursor.getColumnIndex(COLUMN_DRAW_VALUE)));
                draw.setTicketValue(cursor.getDouble(cursor.getColumnIndex(COLUMN_TICKET_VALUE)));
                draw.setStartDate(cursor.getLong(cursor.getColumnIndex(COLUMN_START_DATE)));
                draws.add(draw);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return draws;
    }

    /**
     * Drop table
     * @param db
     */
    public void dropTables(SQLiteDatabase db) {
        System.out.println("z! DBHelper - dropTables()...");
        db.execSQL("DROP TABLE IF EXISTS " + DRAW_TABLE);
    }
}

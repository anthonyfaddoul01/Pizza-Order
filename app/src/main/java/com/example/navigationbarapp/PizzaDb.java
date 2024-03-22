package com.example.navigationbarapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.util.List;

public class PizzaDb extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "pizza_cart.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_CART = "cart";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PIZZA_NAME = "pizza_name";
    public static final String COLUMN_SIZE = "size";
    public static final String COLUMN_TOPPINGS = "toppings";
    public static final String COLUMN_TOTAL_PRICE = "total_price";

    private static final String CREATE_TABLE_CART =
            "CREATE TABLE " + TABLE_CART + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PIZZA_NAME + " TEXT, " +
                    COLUMN_SIZE + " TEXT, " +
                    COLUMN_TOPPINGS + " TEXT, " +
                    COLUMN_TOTAL_PRICE + " REAL);";

    public PizzaDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CART);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        onCreate(db);
    }

    public long insertPizzaOrder(String pizzaName, String size, String toppings, double totalPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PIZZA_NAME, pizzaName);
        values.put(COLUMN_SIZE, size);
        values.put(COLUMN_TOPPINGS, toppings);
        values.put(COLUMN_TOTAL_PRICE, totalPrice);

        return db.insert(TABLE_CART, null, values);
    }

    public Cursor getAllPizzaOrders() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID, COLUMN_PIZZA_NAME, COLUMN_SIZE, COLUMN_TOPPINGS, COLUMN_TOTAL_PRICE};
        return db.query(TABLE_CART, columns, null, null, null, null, null);
    }

    public void removePizzaOrders(List<Long> orderIds) {
        SQLiteDatabase db = this.getWritableDatabase();
        String ids = TextUtils.join(",", orderIds);

        String whereClause = COLUMN_ID + " IN (" + ids + ")";
        db.delete(TABLE_CART, whereClause, null);

        db.close();
    }
    public double calculateTotalPrice() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_TOTAL_PRICE};
        Cursor cursor = db.query(TABLE_CART, columns, null, null, null, null, null);

        double totalPrice = 0;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                double itemPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_PRICE));
                totalPrice += itemPrice;
            } while (cursor.moveToNext());

            cursor.close();
        }

        return totalPrice;
    }
}

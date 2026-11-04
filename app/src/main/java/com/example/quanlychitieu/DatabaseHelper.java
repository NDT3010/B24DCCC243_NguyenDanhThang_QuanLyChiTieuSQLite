package com.example.quanlychitieu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "expense_manager.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL)");

        db.execSQL("CREATE TABLE expenses (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "amount INTEGER NOT NULL, " +
                "category TEXT NOT NULL, " +
                "date TEXT NOT NULL, " +
                "note TEXT)");

        db.execSQL("INSERT INTO users (username, password) VALUES ('admin', '123456')");

        db.execSQL("INSERT INTO expenses (name, amount, category, date) VALUES ('Ăn sáng', 30000, 'Ăn uống', '2026-10-13')");
        db.execSQL("INSERT INTO expenses (name, amount, category, date) VALUES ('Đổ xăng', 50000, 'Đi lại', '2026-10-12')");
        db.execSQL("INSERT INTO expenses (name, amount, category, date) VALUES ('Mua sách', 120000, 'Học tập', '2026-10-11')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS expenses");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ? AND password = ?",
                new String[]{username, password});
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    public long insertExpense(Expense expense) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", expense.getName());
        values.put("amount", expense.getAmount());
        values.put("category", expense.getCategory());
        values.put("date", expense.getDate());
        values.put("note", expense.getNote());
        long id = db.insert("expenses", null, values);
        return id;
    }

    public int updateExpense(Expense expense) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", expense.getName());
        values.put("amount", expense.getAmount());
        values.put("category", expense.getCategory());
        values.put("date", expense.getDate());
        values.put("note", expense.getNote());
        return db.update("expenses", values, "id = ?", new String[]{String.valueOf(expense.getId())});
    }

    public int deleteExpense(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete("expenses", "id = ?", new String[]{String.valueOf(id)});
    }

    public Expense getExpenseById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM expenses WHERE id = ?", new String[]{String.valueOf(id)});
        Expense expense = null;
        if (cursor.moveToFirst()) {
            expense = new Expense();
            expense.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            expense.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            expense.setAmount(cursor.getInt(cursor.getColumnIndexOrThrow("amount")));
            expense.setCategory(cursor.getString(cursor.getColumnIndexOrThrow("category")));
            expense.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
            expense.setNote(cursor.getString(cursor.getColumnIndexOrThrow("note")));
        }
        cursor.close();
        return expense;
    }

    public List<Expense> getExpenses(String keyword, String category) {
        List<Expense> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM expenses WHERE name LIKE ?";
        List<String> args = new ArrayList<>();
        args.add("%" + keyword + "%");

        if (category != null && !category.equals("Tất cả danh mục")) {
            query += " AND category = ?";
            args.add(category);
        }

        query += " ORDER BY date DESC, id DESC";

        Cursor cursor = db.rawQuery(query, args.toArray(new String[0]));
        while (cursor.moveToNext()) {
            Expense expense = new Expense();
            expense.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            expense.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            expense.setAmount(cursor.getInt(cursor.getColumnIndexOrThrow("amount")));
            expense.setCategory(cursor.getString(cursor.getColumnIndexOrThrow("category")));
            expense.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
            expense.setNote(cursor.getString(cursor.getColumnIndexOrThrow("note")));
            list.add(expense);
        }
        cursor.close();
        return list;
    }

    public long getTotal(String keyword, String category) {
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT SUM(amount) FROM expenses WHERE name LIKE ?";
        List<String> args = new ArrayList<>();
        args.add("%" + keyword + "%");

        if (category != null && !category.equals("Tất cả danh mục")) {
            query += " AND category = ?";
            args.add(category);
        }

        Cursor cursor = db.rawQuery(query, args.toArray(new String[0]));
        long total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getLong(0);
        }
        cursor.close();
        return total;
    }

    public Map<String, Long> getTotalByCategory() {
        Map<String, Long> map = new HashMap<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT category, SUM(amount) FROM expenses GROUP BY category", null);
        while (cursor.moveToNext()) {
            map.put(cursor.getString(0), cursor.getLong(1));
        }
        cursor.close();
        return map;
    }
}

package de.calculatorapp.database;

import java.util.ArrayList;
import java.util.Collection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DatabaseConnection extends SQLiteOpenHelper {

    public static class Backlog implements BaseColumns {
        public static final String TABLE_NAME = "backlog";
        public static final String COLUMN_NAME_VARIABLE = "variable";
        public static final String COLUMN_NAME_CALCULATION = "calculation";
        public static final String COLUMN_NAME_RESULT = "result";
    }

    public static final String DATABASE_NAME = "calculator.db";
    private static final String VARCHAR_TYPE = " VARCHAR(300)";
    private static final String NOT_NULL = " NOT NULL";
    private static final String COMMA_SEP = ",";

    public static final String SQL_CREATE_BACKLOG_TABLE = "CREATE TABLE " + Backlog.TABLE_NAME + " ("
            + Backlog.COLUMN_NAME_VARIABLE + VARCHAR_TYPE + COMMA_SEP + Backlog.COLUMN_NAME_CALCULATION + VARCHAR_TYPE
            + NOT_NULL + COMMA_SEP + Backlog.COLUMN_NAME_RESULT + VARCHAR_TYPE + NOT_NULL + ");";

    private static final String SQL_DELETE_BACKLOG = "DROP TABLE IF EXISTS " + Backlog.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;

    public DatabaseConnection(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public String getVariableResultByName(String variable) {
        String[] projection = { Backlog.COLUMN_NAME_RESULT };

        SQLiteDatabase readableDatabase = this.getReadableDatabase();
        String whereClause = Backlog.COLUMN_NAME_VARIABLE + "= ?";
        String[] whereArgs = new String[] { variable };
        Cursor query = readableDatabase.query(Backlog.TABLE_NAME, projection, whereClause, whereArgs, null, null, null);
        if (query.moveToNext()) {
            return query.getString(0);
        }
        return "";
    }

    public ArrayList<String> getAllCalculationsAsHtml() {
        String[] projection = { "rowid", Backlog.COLUMN_NAME_VARIABLE, Backlog.COLUMN_NAME_CALCULATION,
                Backlog.COLUMN_NAME_RESULT };
        String sortOrder = "rowid" + " DESC";

        Cursor query = this.getReadableDatabase().query(Backlog.TABLE_NAME, projection, null, null, null, null,
                sortOrder);

        ArrayList<String> calculationList = new ArrayList<String>();
        while (query.moveToNext()) {
            if (query.getString(1) != null) {
                calculationList.add("<h3>" + query.getString(1) + "=" + query.getString(2) + "</h3><p>"
                        + query.getString(1) + "=" + query.getString(3) + "</p>");
            } else {
                calculationList.add("<h3>" + query.getString(2) + "</h3><p>" + query.getString(3) + "</p>");
            }
        }
        return calculationList;
    }

    public Collection<? extends String> getAllVariablesAsHtml() {
        String[] projection = { "rowid", Backlog.COLUMN_NAME_VARIABLE, Backlog.COLUMN_NAME_CALCULATION,
                Backlog.COLUMN_NAME_RESULT };
        String sortOrder = "rowid" + " DESC";

        Cursor query = this.getReadableDatabase().query(Backlog.TABLE_NAME, projection, null, null, null, null,
                sortOrder);

        ArrayList<String> calculationList = new ArrayList<String>();
        while (query.moveToNext()) {
            if (query.getString(1) != null) {
                calculationList.add("<h3>" + query.getString(1) + "=" + query.getString(2) + "</h3><p>"
                        + query.getString(1) + "=" + query.getString(3) + "</p>");
            }
        }
        return calculationList;
    }

    public void deleteBacklogEntries() {
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        writableDatabase.execSQL(SQL_DELETE_BACKLOG);
        writableDatabase.execSQL(SQL_CREATE_BACKLOG_TABLE);
        writableDatabase.close();
    }

    public void deleteVariables() {
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        writableDatabase.delete(Backlog.TABLE_NAME, Backlog.COLUMN_NAME_VARIABLE + "<>''", null);
        writableDatabase.close();
    }

    public long insertCalculation(String calculation, String result, String variable) {
        SQLiteDatabase writableDb = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Backlog.COLUMN_NAME_VARIABLE, variable);
        values.put(Backlog.COLUMN_NAME_CALCULATION, calculation);
        values.put(Backlog.COLUMN_NAME_RESULT, result);
        long rowID = writableDb.insert(Backlog.TABLE_NAME, null, values);
        writableDb.close();
        return rowID;
    }

    public boolean isVariableExisting(String variable) {
        if (variable == null)
            return false;

        String[] projection = { Backlog.COLUMN_NAME_VARIABLE };

        SQLiteDatabase readableDatabase = this.getReadableDatabase();
        String whereClause = Backlog.COLUMN_NAME_VARIABLE + "= ?";
        String[] whereArgs = new String[] { variable };
        Cursor query = readableDatabase.query(Backlog.TABLE_NAME, projection, whereClause, whereArgs, null, null, null);
        if (query.moveToNext()) {
            return (query.getString(0).equals(variable));
        }
        return false;
    }

    public void updateVariable(String calculation, String result, String variable) {
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        writableDatabase.delete(Backlog.TABLE_NAME, Backlog.COLUMN_NAME_VARIABLE + "='" + variable + "'", null);
        writableDatabase.close();
        insertCalculation(calculation, result, variable);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_BACKLOG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

package com.example.playernk;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.UUID;

public class DB {

    //    public ArrayList<Change> changesForResponse = new ArrayList<>();
    public ArrayList<String> changesRefs = new ArrayList<>();

    public String emptyRef;

    private static final String DB_NAME = "PLNKDB";
    private static final int DB_VERSION = 1;

    private final Context mCtx;

    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public Boolean opened;

    public boolean requested;
    public String error;

//    private TextView tvExchangeInGrogress;

    public DB(Context ctx) {

        mCtx = ctx;

//        emptyRef =  mCtx.getString(R.string.emptyRef);

        opened = false;

        requested = false;
        error = "";


//        try {
//
//            tvExchangeInGrogress = (TextView) ((Activity) mCtx).findViewById(R.id.tvExchangeInGrogress);
//        }catch (Exception e){}


//        changesForResponse = new ArrayList<>();
        changesRefs = new ArrayList<>();
    }


    public static String getDbConstant(Context mCtx, String constName) {
        DB db = new DB(mCtx);

        db.open();

        String appId = db.getConstant(constName);

        db.close();
        return appId;
    }



    // открыть подключение
    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();

        opened = true;

    }

    // закрыть подключение
    public void close() {
        if (mDBHelper!=null) mDBHelper.close();

        opened = false;
    }

    public String getRequestUserProg() {

        open();

        String url = "request/" + getConstant("user_id") + "/" + getConstant("prog_id");

        close();

        return url;
    }



    public Cursor rawQuery(String query, String[] args){

        return mDB.rawQuery(query, args);

    }

    // получить все данные из таблицы DB_TABLE
    public Cursor getAllData(String db_name) {
        return mDB.query(db_name, null, null, null, null, null, null);
    }

    public Cursor getAllData(String db_name, String orderBy) {
        return mDB.query(db_name, null, null, null, null, null, orderBy);
    }

    public Cursor getAllDataByRef(String db_name, String ref) {
        return mDB.query(db_name, null, "ref = ?", new String[] { ref }, null, null, null, null);
    }

    public Cursor getAllDataByFilter(String db_name, String selection, String[] selectionArgs, String orderBy) {
        return mDB.query(db_name, null, selection, selectionArgs, null, null, orderBy);
    }

    public Cursor getAllDataByOwner(String db_name, String owner) {
        return mDB.query(db_name, null, "owner = ?", new String[] { owner }, null, null, null);
    }

    // добавить запись в DB_TABLE
    public void addRec(String db_name, ContentValues cv) {
        Long inserted = mDB.insert(db_name, null, cv);
    }

    // удалить запись из DB_TABLE
    public void delRec(String db_name, long id) {
        mDB.delete(db_name, "_id = " + String.valueOf(id), null);
    }

    public void delRecByRef(String db_name, String ref) {
        mDB.delete(db_name, "ref = ?", new String[] { ref });
    }

    public void delRecByFilter(String db_name, String whereClause, String[] where) {
        mDB.delete(db_name, whereClause, where);
    }

    //    public Cursor getAllDataByFilter(String db_name, String selection, String[] selectionArgs, String orderBy) {
//        return mDB.query(db_name, null, selection, selectionArgs, null, null, orderBy);
//    }
//
    public String getExternalRef(String ref) {

        String external = null;

        Cursor cc = getAllDataByFilter("refs", "internal = ? or external = ?", new String[] { ref, ref }, null);

        if (cc.moveToFirst()) {

            do {

                for (String name : cc.getColumnNames()) {

                    switch (name) {

                        case "external": {
                            external = getStringFromCursor(cc, name);
                            break;
                        }

                    }
                }


            } while (cc.moveToNext());
        }

        cc.close();

        return external;

    }

    public int update(String table,
                      ContentValues values,
                      String whereClause,
                      String[] whereArgs) {
        return mDB.update(table,  values,  whereClause, whereArgs);
    }

    public long insert(String table,
                       String nullColumnHack,
                       ContentValues values) {
        return mDB.insertOrThrow(table, nullColumnHack, values);
    }

    public Cursor query(String table,
                        String[] columns,
                        String selection,
                        String[] selectionArgs,
                        String groupBy,
                        String having,
                        String orderBy) {
        return mDB.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public boolean updateConstant(String name, String value) {

        ContentValues cv = new ContentValues();

        cv.put("value", value);
        // обновляем по id
        int updCount = mDB.update("constants", cv, "name = ?", new String[] { name });

        if (updCount == 0) {
            cv.put("name", name);
            cv.put("value", value);

            updCount = (int) mDB.insert("constants", null, cv);
        }

        return updCount == 1;

    }

    public String getConstant(String name) {

        Cursor c = mDB.query("constants", null, "name = ?", new String[] { name }, null, null, null);

        String result = null;
        // ставим позицию курсора на первую строку выборки
        // если в выборке нет строк, вернется false
        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int valueColIndex = c.getColumnIndex("value");

            do {
                // получаем значения по номерам столбцов и пишем все в лог
                result = c.getString(valueColIndex);
                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false -
                // выходим из цикла
            } while (c.moveToNext());
        } ;

        c.close();

        return result;

    }

    public ContentValues getRecById(String table, Integer id) {

        Cursor c = mDB.query(table, null, "_id = ?", new String[] { String.valueOf(id) }, null, null, null);

        ContentValues cv = new ContentValues();

        if (c.moveToFirst()) {

            for (String column: c.getColumnNames()) {

                int curColumnIndex = c.getColumnIndex(column);
                int curType = c.getType(curColumnIndex);

                if(curType == 1) {
                    cv.put(column, c.getLong(curColumnIndex));
                }
                else if(curType == 2) {
                    cv.put(column, c.getFloat(curColumnIndex));
                }
                else if(curType == 3) {
                    cv.put(column, c.getString(curColumnIndex));
                }
            }

        } ;

        c.close();

        return cv;

    }

    public ContentValues getRecByRef(String table, String ref) {

        Cursor c = mDB.query(table, null, "ref = ?", new String[] { ref }, null, null, null);

        ContentValues cv = new ContentValues();

        if (c.moveToFirst()) {

            for (String column: c.getColumnNames()) {

                int curColumnIndex = c.getColumnIndex(column);
                int curType = c.getType(curColumnIndex);

                if(curType == 1) {
                    cv.put(column, c.getLong(curColumnIndex));
                }
                else if(curType == 2) {
                    cv.put(column, c.getFloat(curColumnIndex));
                }
                else if(curType == 3) {
                    cv.put(column, c.getString(curColumnIndex));
                }
            }

        } ;

        c.close();

        return cv;

    }

    public ContentValues getRecByName(String table, String name) {

        Cursor c = mDB.query(table, null, "name = ?", new String[] { name }, null, null, null);

        ContentValues cv = new ContentValues();

        if (c.moveToFirst()) {

            for (String column: c.getColumnNames()) {

                int curColumnIndex = c.getColumnIndex(column);
                int curType = c.getType(curColumnIndex);

                if(curType == 1) {
                    cv.put(column, c.getLong(curColumnIndex));
                }
                else if(curType == 2) {
                    cv.put(column, c.getFloat(curColumnIndex));
                }
                else if(curType == 3) {
                    cv.put(column, c.getString(curColumnIndex));
                }
            }

        } ;

        c.close();

        return cv;

    }

    public ContentValues getRecByFilter(String table, String selection, String[] where) {

        Cursor c = mDB.query(table, null, selection, where, null, null, null);

        ContentValues cv = new ContentValues();

        if (c.moveToFirst()) {

            for (String column: c.getColumnNames()) {

                int curColumnIndex = c.getColumnIndex(column);
                int curType = c.getType(curColumnIndex);

                if(curType == 1) {
                    cv.put(column, c.getLong(curColumnIndex));
                }
                else if(curType == 2) {
                    cv.put(column, c.getFloat(curColumnIndex));
                }
                else if(curType == 3) {
                    cv.put(column, c.getString(curColumnIndex));
                }
            }

        } ;

        c.close();

        return cv;

    }

    public boolean updateRecord(String table, ContentValues cv) {

        int updCount = mDB.update(table, cv, "ref = ?", new String[] { cv.getAsString("ref") });

        if (updCount == 0) {

            updCount = (int) mDB.insert(table, null, cv);
        }

        return true;

    }

    public boolean updateChanges(String ref, String type, String name){

        ContentValues cvch = new ContentValues();
        cvch.put("ref", ref);
        cvch.put("type", type);
        cvch.put("name", name);

        updateRecord("changes", cvch);

        return true;

    }

    public static int getIntFromCursor(Cursor cursor, String column){

        int idInd = cursor.getColumnIndex(column);
        return cursor.getInt(idInd);

    }

    public static String getStringFromCursor(Cursor cursor, String column){

        int idInd = cursor.getColumnIndex(column);
        return cursor.getString(idInd);

    }

    public String getStringByRef(String table, String ref, String props, String def) {

        String result = def;
        if(ref != null && !ref.isEmpty() && !ref.equals(emptyRef)){

            ContentValues cvref = getRecByRef(table, ref);

            result = cvref.getAsString(props);
        }
        return result;
    }

    // класс по созданию и управлению БД
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        // создаем и заполняем БД
        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL("create table constants ("
                    + "_id integer primary key autoincrement,"
                    + "name text,"
                    + "value text" + ");");








        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            if (oldVersion == 1 && newVersion == 2) {


            }
        }
    }

    public static void onStart(Context mCtx){

        DB db = new DB(mCtx);

        db.open();

        String appId = db.getConstant("appId");
        if (appId == null) {

            appId = UUID.randomUUID().toString();
            db.updateConstant("appId", appId);

        }

        String random = db.getConstant("random");
        if (random == null) {

            random = "false";
            db.updateConstant("random", random);

        }

        String newOnly = db.getConstant("newOnly");
        if (newOnly == null) {

            newOnly = "false";
            db.updateConstant("newOnly", newOnly);

        }

        String favorites = db.getConstant("favorites");
        if (favorites == null) {

            favorites = "false";
            db.updateConstant("favorites", favorites);

        }

        String aquare = db.getConstant("aquare");
        if (aquare == null) {

            aquare = "false";
            db.updateConstant("aquare", aquare);

        }

        String loop = db.getConstant("loop");
        if (loop == null) {

            loop = "false";
            db.updateConstant("loop", loop);

        }

        String aquareStart = db.getConstant("aquareStart");
        if (aquareStart == null) {

            aquareStart = "30";
            db.updateConstant("aquareStart", aquareStart);

        }

        String aquareRange = db.getConstant("aquareRange");
        if (aquareRange == null) {

            aquareRange = "60";
            db.updateConstant("aquareRange", aquareRange);

        }

        String selectedStyles = db.getConstant("selectedStyles");
        if (selectedStyles == null) {

            selectedStyles = "";
            db.updateConstant("selectedStyles", selectedStyles);

        }

        String userName = db.getConstant("userName");
        if (userName == null) {

            userName = "";
            db.updateConstant("userName", userName);
            db.updateConstant("userId", userName);

        }

        db.close();

    }

    public static Boolean isSettingsExist(Context mCtx){

        DB db = new DB(mCtx);

        db.open();

        String warehouseId = db.getConstant("warehouseId");

        db.close();


        return warehouseId != null;

    }

    public static Bundle getSettings(Context mCtx){

        Bundle result = new Bundle();

        DB db = new DB(mCtx);

        db.open();

        result.putString("warehouseId", db.getConstant("warehouseId"));
        result.putString("warehouseDescription", db.getConstant("warehouseDescription"));

        db.close();


        return result;

    }

}



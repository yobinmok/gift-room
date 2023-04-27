package com.example.capstone_giftcon.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class DbOpenHelper(private val mCtx: Context) {
    private var mDBHelper: DatabaseHelper? = null

    private inner class DatabaseHelper(
        context: Context?,
        name: String?,
        factory: CursorFactory?,
        version: Int
    ) : SQLiteOpenHelper(context, name, factory, version) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(DataBases.CreateDB._CREATE0)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS " + DataBases.CreateDB._TABLENAME0)
            onCreate(db)
        }
    }

    @Throws(SQLException::class)
    fun open(): DbOpenHelper {
        mDBHelper = DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION)
        mDB = mDBHelper!!.writableDatabase
        return this
    }

    fun create() {
        mDBHelper!!.onCreate(mDB!!)
    }

    fun close() {
        mDB!!.close()
    }

    // Insert DB
    fun insertColumn(icon: ByteArray?, name: String?): Long {
        val values = ContentValues()
        values.put(DataBases.CreateDB.ICON, icon)
        values.put(DataBases.CreateDB.NAME, name)
        return mDB!!.insert(DataBases.CreateDB._TABLENAME0, null, values)
        //        SQLiteStatement p = mDB.compileStatement("INSERT INTO imgTable values (?);");
//        p.bindBlob(1,icon);
//        p.execute();
    }

    // Update DB
    //    public boolean updateColumn(long id, String userid, String name, long num){
    //        ContentValues values = new ContentValues();
    //        values.put(DataBases.CreateDB.IMGID, userid);
    //        values.put(DataBases.CreateDB.NAME, name);
    //        values.put(DataBases.CreateDB.NUM, num);
    //        return mDB.update(DataBases.CreateDB._TABLENAME0, values, "_id=" + id, null) > 0;
    //    }
    // Delete All
    fun deleteAllColumns() {
        mDB!!.delete(DataBases.CreateDB._TABLENAME0, null, null)
    }

    // Delete DB
    fun deleteColumn(id: Long): Boolean {
        return mDB!!.delete(DataBases.CreateDB._TABLENAME0, "_id=$id", null) > 0
    }

    // Select DB
    fun selectColumns(): Cursor {
        return mDB!!.query(DataBases.CreateDB._TABLENAME0, null, null, null, null, null, null)
    }

    fun select(id: Int): ByteArray {
        val c = mDB!!.rawQuery(
            "SELECT icon FROM imgTable WHERE " + BaseColumns._ID + " = " + id + ";",
            null
        )
        c.moveToNext()
        return c.getBlob(0)
        //        Log.d("select check!!!!!!!!!!!!!!", String.valueOf(c));
//        Log.d("select check!!!!!!!!!!!!!!!!", String.valueOf(c.getBlob(0)));
        //return c;
    }

    fun selectId(): Int {
        val c = mDB!!.rawQuery("SELECT " + BaseColumns._ID + " FROM imgTable;", null)
        //return c.getBlob(0);
        return c.count
    }

    // sort by column
    fun sortColumn(sort: String): Cursor {
        return mDB!!.rawQuery("SELECT * FROM imgTable ORDER BY $sort;", null)
    }

    companion object {
        private const val DATABASE_NAME = "InnerDatabase(SQLite).db"
        private const val DATABASE_VERSION = 1
        var mDB: SQLiteDatabase? = null
    }
}
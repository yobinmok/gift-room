package com.example.capstone_giftcon.database

import android.provider.BaseColumns

class DataBases {
    object CreateDB : BaseColumns {
        const val ICON = "icon"
        const val NAME = "img_name"
        const val _TABLENAME0 = "imgTable"
        const val _TABLENAME1 = "profileTable"
        const val _CREATE0 = ("create table if not exists " + _TABLENAME0 + "("
                + BaseColumns._ID + " integer primary key autoincrement, "
                + ICON + " BLOB not null,"
                + NAME + " text not null);")
        const val _CREATE1 = ("create table if not exists " + _TABLENAME1 + "("
                + BaseColumns._ID + " integer primary key autoincrement, "
                + ICON + " BLOB not null,"
                + NAME + " text not null);")
    }
}
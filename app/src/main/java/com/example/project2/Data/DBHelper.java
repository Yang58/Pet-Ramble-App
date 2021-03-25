package com.example.project2.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

    // 생성자 - db 파일 생성
    public DBHelper(Context context){
        super(context, "info", null, 1);
    }

    // 처음 만들때 호출 - 테이블 생성 등 초기 처리.
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE info (name TEXT, age INTEGER, pname TEXT, page INTEGER,pkind TEXT, finish INTEGER);");
    }

    // db 업그레이드 필요시 호출 (version 값에 따라 반응함)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS info");
        onCreate(db);

    }
}

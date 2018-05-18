package com.app.snapsearch.snapsearch;

/**
 * Created by varunviswanathan on 5/18/18.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SnapLab {
    private static SnapLab mSnapLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static SnapLab get(Context context){
        if(mSnapLab = null){
            mSnapLab = new SnapLab(context);
        }

        return mSnapLab;
    }

    private SnapLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new something(mContext)
                .getWritableDatabase();

    }
}

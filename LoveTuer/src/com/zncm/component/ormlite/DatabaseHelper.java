package com.zncm.component.ormlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;

/**
 * Database helper class used to manage the creation and upgrading of your
 * database. This class also usually provides the DAOs used by the other
 * classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    // name of the database file for your application -- change to something
    // appropriate for your app
    private static final String DATABASE_NAME = "babylovemath.db";
    // any time you make changes to your database objects, you may have to
    // increase the database version


    private static final int DATABASE_VERSION = 1;


    // the DAO object we use to access the SimpleData table
//    private RuntimeExceptionDao<QuestionData, Integer> questionRuntimeDao = null;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is first created. Usually you should
     * call createTable statements here to create the tables that will store
     * your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
//            TableUtils.createTableIfNotExists(connectionSource, QuestionData.class);

        } catch (Exception e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }

    }

    /**
     * This is called when your application is upgraded and it has a higher
     * version number. This allows you to adjust the various data to match the
     * new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            // TableUtils.dropTable(connectionSource, PictureData.class, false);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (Exception e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

//    public RuntimeExceptionDao<QuestionData, Integer> getQuestionDataDao() {
//        if (questionRuntimeDao == null) {
//            questionRuntimeDao = getRuntimeExceptionDao(QuestionData.class);
//        }
//        return questionRuntimeDao;
//    }
//
//
//
//    @Override
//    public void close() {
//        super.close();
//        questionRuntimeDao = null;
//    }
}

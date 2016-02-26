package com.clothapp.resources;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by jack1 on 25/02/2016.
 */
public class CustomContentProvider extends ContentProvider {
    static final String PROVIDER_NAME = ".resources.Categorie";
    static final String URL = "content://" + PROVIDER_NAME + "/vestiti";
    public static final Uri CONTENT_URI = Uri.parse(URL);


    private static HashMap<String, String> CLOTHES_PROJECTION_MAP;

    public static final String _ID = "_id";
    public static final String NAME = "name";

    static final int CLOTHES = 1;
    static final int CLOTH_ID = 2;
    static final int CLEAR = 3;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "vestiti", CLOTHES);
        uriMatcher.addURI(PROVIDER_NAME, "vestiti/#", CLOTH_ID);
        uriMatcher.addURI(PROVIDER_NAME, "clear", CLEAR);
    }
    /**
     * Database specific constant declarations
     */
    private SQLiteDatabase db;
    static final String DATABASE_NAME = "Categories";
    static final String CLOTHES_TABLE_NAME = "clothes";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE =
            " CREATE TABLE " + CLOTHES_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " name TEXT NOT NULL);";
    private DatabaseHelper dbHelper;

    /**
     * Helper class that actually creates and manages
     * the provider's underlying data repository.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " +  CLOTHES_TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new DatabaseHelper(context);

        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */
        db = dbHelper.getWritableDatabase();
        return (db == null)? false:true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String query = uri.getLastPathSegment().toLowerCase();
        System.out.println("content:" + query);
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(CLOTHES_TABLE_NAME);
        System.out.println(qb.getTables());


        if (sortOrder == null || sortOrder == ""){
            /**
             * By default sort on student names
             */
            sortOrder = NAME;
        }

        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);

        System.out.println("cursor"+c.getColumnNames().toString());
        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            /**
             * Get all student records
             */
            case CLOTHES:
                return "vnd.android.cursor.dir/vnd.resources/vestiti";

            /**
             * Get a particular student
             */
            case CLOTH_ID:
                return "vnd.android.cursor.item/vnd.resources/vestiti";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /**
         * Add a new student record
         */
        long rowID = db.insert(	CLOTHES_TABLE_NAME, "", values);

        /**
         * If record is added successfully
         */

        if (rowID > 0)
        {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        db.execSQL("DELETE FROM "+CLOTHES_TABLE_NAME);
        switch (uriMatcher.match(uri)){
            case CLOTHES:
                count = db.delete(CLOTHES_TABLE_NAME, selection, selectionArgs);
                break;

            case CLOTH_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete( CLOTHES_TABLE_NAME, _ID +  " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case CLEAR:
                count=db.delete(CLOTHES_TABLE_NAME, null,null);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}

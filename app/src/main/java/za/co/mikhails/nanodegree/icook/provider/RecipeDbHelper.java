package za.co.mikhails.nanodegree.icook.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import za.co.mikhails.nanodegree.icook.provider.RecipeContract.SearchResultEntry;

public class RecipeDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "recipe.db";

    public RecipeDbHelper(Context context) {
        this(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public RecipeDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version, null);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + SearchResultEntry.TABLE_NAME + " (" +
                SearchResultEntry._ID + " INTEGER PRIMARY KEY," +
                SearchResultEntry.COLUMN_ID + " INTEGER UNIQUE NOT NULL, " +
                SearchResultEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                SearchResultEntry.COLUMN_IMAGE + " TEXT, " +
                SearchResultEntry.COLUMN_IMAGE_TYPE + " TEXT, " +
                SearchResultEntry.COLUMN_IMAGE_BASE_URL + " TEXT, " +
                SearchResultEntry.COLUMN_CALORIES + " TEXT, " +
                SearchResultEntry.COLUMN_PROTEIN + " TEXT, " +
                SearchResultEntry.COLUMN_FAT + " TEXT, " +
                SearchResultEntry.COLUMN_CARBS + " TEXT " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SearchResultEntry.TABLE_NAME);
        onCreate(db);
    }
}

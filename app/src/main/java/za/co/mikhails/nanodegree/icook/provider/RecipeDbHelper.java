package za.co.mikhails.nanodegree.icook.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import za.co.mikhails.nanodegree.icook.provider.RecipeContract.IngredientEntry;
import za.co.mikhails.nanodegree.icook.provider.RecipeContract.RecipeEntry;
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
        final String SQL_CREATE_SEARCH_RESULT_TABLE = "CREATE TABLE " + SearchResultEntry.TABLE_NAME + " (" +
                SearchResultEntry.COLUMN_ID + " INTEGER PRIMARY KEY," +
                SearchResultEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                SearchResultEntry.COLUMN_IMAGE + " TEXT, " +
                SearchResultEntry.COLUMN_IMAGE_TYPE + " TEXT, " +
                SearchResultEntry.COLUMN_IMAGE_BASE_URL + " TEXT, " +
                SearchResultEntry.COLUMN_CALORIES + " TEXT, " +
                SearchResultEntry.COLUMN_PROTEIN + " TEXT, " +
                SearchResultEntry.COLUMN_FAT + " TEXT, " +
                SearchResultEntry.COLUMN_CARBS + " TEXT " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_SEARCH_RESULT_TABLE);

        final String SQL_CREATE_RECIPE_TABLE = "CREATE TABLE " + RecipeEntry.TABLE_NAME + " (" +
                RecipeEntry.COLUMN_ID + " INTEGER PRIMARY KEY," +
                RecipeEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                RecipeEntry.COLUMN_DESCRIPTION + " TEXT, " +
                RecipeEntry.COLUMN_IMAGE_URL + " TEXT, " +
                RecipeEntry.COLUMN_READY_IN + " TEXT, " +
                RecipeEntry.COLUMN_PERCENT_PROTEIN + " TEXT, " +
                RecipeEntry.COLUMN_PERCENT_FAT + " TEXT, " +
                RecipeEntry.COLUMN_PERCENT_CARBS + " TEXT " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_RECIPE_TABLE);

        final String SQL_CREATE_INGREDIENT_TABLE = "CREATE TABLE " + IngredientEntry.TABLE_NAME + " (" +
                IngredientEntry.COLUMN_ID + " INTEGER PRIMARY KEY," +
                IngredientEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                IngredientEntry.COLUMN_IMAGE + " TEXT, " +
                IngredientEntry.COLUMN_AMOUNT + " TEXT, " +
                IngredientEntry.COLUMN_UNIT + " TEXT " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_INGREDIENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SearchResultEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RecipeEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + IngredientEntry.TABLE_NAME);
        onCreate(db);
    }
}

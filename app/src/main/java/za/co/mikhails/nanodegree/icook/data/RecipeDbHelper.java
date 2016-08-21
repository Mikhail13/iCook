package za.co.mikhails.nanodegree.icook.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import za.co.mikhails.nanodegree.icook.data.RecipeContract.FavoritesEntry;
import za.co.mikhails.nanodegree.icook.data.RecipeContract.IngredientEntry;
import za.co.mikhails.nanodegree.icook.data.RecipeContract.InstructionsEntry;
import za.co.mikhails.nanodegree.icook.data.RecipeContract.RecipeEntry;
import za.co.mikhails.nanodegree.icook.data.RecipeContract.SearchResultEntry;
import za.co.mikhails.nanodegree.icook.data.RecipeContract.ShoppingListEntry;

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
                SearchResultEntry.COLUMN_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SearchResultEntry.COLUMN_ID + " INTEGER NOT NULL," +
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
                IngredientEntry.COLUMN_RECIPE_ID + " INTEGER NOT NULL, " +
                IngredientEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                IngredientEntry.COLUMN_IMAGE + " TEXT, " +
                IngredientEntry.COLUMN_AMOUNT + " TEXT, " +
                IngredientEntry.COLUMN_UNIT + " TEXT " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_INGREDIENT_TABLE);

        final String SQL_CREATE_INSTRUCTIONS_TABLE = "CREATE TABLE " + InstructionsEntry.TABLE_NAME + " (" +
                InstructionsEntry.COLUMN_ID + " INTEGER PRIMARY KEY," +
                InstructionsEntry.COLUMN_RECIPE_ID + " INTEGER NOT NULL, " +
                InstructionsEntry.COLUMN_NAME + " TEXT, " +
                InstructionsEntry.COLUMN_NUMBER + " TEXT, " +
                InstructionsEntry.COLUMN_STEP + " TEXT " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_INSTRUCTIONS_TABLE);

        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + FavoritesEntry.TABLE_NAME + " (" +
                FavoritesEntry.COLUMN_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavoritesEntry.COLUMN_ID + " INTEGER NOT NULL," +
                FavoritesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_IMAGE + " TEXT, " +
                FavoritesEntry.COLUMN_IMAGE_TYPE + " TEXT, " +
                FavoritesEntry.COLUMN_IMAGE_BASE_URL + " TEXT, " +
                FavoritesEntry.COLUMN_CALORIES + " TEXT, " +
                FavoritesEntry.COLUMN_PROTEIN + " TEXT, " +
                FavoritesEntry.COLUMN_FAT + " TEXT, " +
                FavoritesEntry.COLUMN_CARBS + " TEXT " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);

        final String SQL_CREATE_SHOPPING_LIST_TABLE = "CREATE TABLE " + ShoppingListEntry.TABLE_NAME + " (" +
                ShoppingListEntry.COLUMN_ID + " INTEGER PRIMARY KEY," +
                ShoppingListEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                ShoppingListEntry.COLUMN_IMAGE + " TEXT, " +
                ShoppingListEntry.COLUMN_AMOUNT + " TEXT, " +
                ShoppingListEntry.COLUMN_UNIT + " TEXT, " +
                ShoppingListEntry.COLUMN_CHECKED + " INTEGER " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_SHOPPING_LIST_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SearchResultEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RecipeEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + IngredientEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + InstructionsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + InstructionsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ShoppingListEntry.TABLE_NAME);
        onCreate(db);
    }
}

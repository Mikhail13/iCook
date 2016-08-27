package za.co.mikhails.nanodegree.icook.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import za.co.mikhails.nanodegree.icook.data.RecipeContract.FavoritesEntry;
import za.co.mikhails.nanodegree.icook.data.RecipeContract.IngredientEntry;
import za.co.mikhails.nanodegree.icook.data.RecipeContract.InstructionsEntry;
import za.co.mikhails.nanodegree.icook.data.RecipeContract.RecipeEntry;
import za.co.mikhails.nanodegree.icook.data.RecipeContract.SearchResultEntry;
import za.co.mikhails.nanodegree.icook.data.RecipeContract.ShoppingListEntry;

public class RecipeContentProvider extends ContentProvider {

    private static final int SEARCH_RESULT = 100;
    private static final int RECIPE = 200;
    private static final int RECIPE_DETAILS = 300;
    private static final int INGREDIENT = 400;
    private static final int INGREDIENT_LIST = 500;
    private static final int INSTRUCTIONS = 600;
    private static final int FAVORITES = 700;
    private static final int SHOPPING_LIST = 800;

    protected UriMatcher uriMatcher;
    private RecipeDbHelper dbHelper;

    public RecipeContentProvider() {
    }

    @Override
    public boolean onCreate() {
        dbHelper = new RecipeDbHelper(getContext());
        uriMatcher = buildUriMatcher();
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case SEARCH_RESULT:
                return SearchResultEntry.CONTENT_TYPE;
            case RECIPE_DETAILS:
            case RECIPE:
                return RecipeEntry.CONTENT_TYPE;
            case INGREDIENT:
                return IngredientEntry.CONTENT_TYPE;
            case SHOPPING_LIST:
                return ShoppingListEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (uriMatcher.match(uri)) {
            case SEARCH_RESULT:
                retCursor = dbHelper.getReadableDatabase().query(SearchResultEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case FAVORITES:
                retCursor = dbHelper.getReadableDatabase().query(FavoritesEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case RECIPE_DETAILS:
                retCursor = dbHelper.getReadableDatabase().query(RecipeEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case INGREDIENT:
            case INGREDIENT_LIST:
                retCursor = dbHelper.getReadableDatabase().query(IngredientEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case INSTRUCTIONS:
                retCursor = dbHelper.getReadableDatabase().query(InstructionsEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SHOPPING_LIST:
                retCursor = dbHelper.getReadableDatabase().query(ShoppingListEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Context context = getContext();
        if (context != null) {
            retCursor.setNotificationUri(context.getContentResolver(), uri);
        }
        return retCursor;
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;
        long itemId;

        switch (match) {
            case SEARCH_RESULT:
                itemId = db.insert(SearchResultEntry.TABLE_NAME, null, values);
                if (itemId > 0) {
                    returnUri = SearchResultEntry.buildResultUri(itemId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case FAVORITES:
                itemId = db.insert(FavoritesEntry.TABLE_NAME, null, values);
                if (itemId > 0) {
                    returnUri = FavoritesEntry.buildResultUri(itemId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case RECIPE_DETAILS:
                itemId = db.insertWithOnConflict(RecipeEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (itemId > 0) {
                    returnUri = RecipeEntry.buildResultUri(itemId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case INGREDIENT:
                itemId = db.insertWithOnConflict(IngredientEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (itemId > 0) {
                    returnUri = IngredientEntry.buildResultUri(itemId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case SHOPPING_LIST:
                itemId = db.insertWithOnConflict(ShoppingListEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (itemId > 0) {
                    returnUri = ShoppingListEntry.buildResultUri(itemId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }
        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        int result;
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        switch (match) {
            case SEARCH_RESULT:
                db.beginTransaction();
                int returnRecipeCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(SearchResultEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_IGNORE);
                        if (_id != -1) {
                            returnRecipeCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                result = returnRecipeCount;
                break;
            case INGREDIENT:
                db.beginTransaction();
                int returnIngredientCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(IngredientEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnIngredientCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                result = returnIngredientCount;
                break;
            case INSTRUCTIONS:
                db.beginTransaction();
                int returnInstructionsCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(InstructionsEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnInstructionsCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                result = returnInstructionsCount;
                break;
            default:
                result = super.bulkInsert(uri, values);
        }
        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }
        return result;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int updated;
        switch (match) {
            case RECIPE_DETAILS:
                updated = db.update(RecipeEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case SHOPPING_LIST:
                updated = db.update(ShoppingListEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Context context = getContext();
        if (updated > 0 && context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }
        return updated;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsDeleted;
        if (null == selection) selection = "1";
        switch (match) {
            case SEARCH_RESULT:
                rowsDeleted = db.delete(SearchResultEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INSTRUCTIONS:
                rowsDeleted = db.delete(InstructionsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITES:
                rowsDeleted = db.delete(FavoritesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SHOPPING_LIST:
                rowsDeleted = db.delete(ShoppingListEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Context context = getContext();
        if (rowsDeleted > 0 && context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    protected UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RecipeContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, RecipeContract.PATH_SEARCH_RESULT, SEARCH_RESULT);
        matcher.addURI(authority, RecipeContract.PATH_RECIPE_DETAILS, RECIPE);
        matcher.addURI(authority, RecipeContract.PATH_RECIPE, RECIPE_DETAILS);
        matcher.addURI(authority, RecipeContract.PATH_INGREDIENT, INGREDIENT);
        matcher.addURI(authority, RecipeContract.PATH_INGREDIENT_LIST, INGREDIENT_LIST);
        matcher.addURI(authority, RecipeContract.PATH_INSTRUCTIONS, INSTRUCTIONS);
        matcher.addURI(authority, RecipeContract.PATH_FAVORITES, FAVORITES);
        matcher.addURI(authority, RecipeContract.PATH_SHOPPING_LIST, SHOPPING_LIST);

        return matcher;
    }
}

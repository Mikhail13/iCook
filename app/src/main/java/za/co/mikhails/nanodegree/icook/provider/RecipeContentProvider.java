package za.co.mikhails.nanodegree.icook.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import za.co.mikhails.nanodegree.icook.provider.RecipeContract.RecipeEntry;
import za.co.mikhails.nanodegree.icook.provider.RecipeContract.SearchResultEntry;

public class RecipeContentProvider extends ContentProvider {
    private static final int SEARCH_RESULT = 100;
    private static final int RECIPE = 200;
    private static final int RECIPE_DETAILS = 300;

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
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case SEARCH_RESULT:
                return SearchResultEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (uriMatcher.match(uri)) {
            case SEARCH_RESULT:
                retCursor = dbHelper.getReadableDatabase().query(SearchResultEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case RECIPE_DETAILS:
                retCursor = dbHelper.getReadableDatabase().query(RecipeEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
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
            case RECIPE:
                itemId = db.insert(RecipeEntry.TABLE_NAME, null, values);
                if (itemId > 0) {
                    returnUri = RecipeEntry.buildResultUri(itemId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int result = 0;
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
            default:
                result = super.bulkInsert(uri, values);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsDeleted;
        if (null == selection) selection = "1";
        switch (match) {
            case SEARCH_RESULT:
                rowsDeleted = db.delete(SearchResultEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    protected UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RecipeContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, RecipeContract.PATH_SEARCH_RESULT, SEARCH_RESULT);
        matcher.addURI(authority, RecipeContract.PATH_RECIPE_DETAILS, RECIPE);
        matcher.addURI(authority, RecipeContract.PATH_RECIPE_DETAILS + "/#", RECIPE_DETAILS);

        return matcher;
    }
}

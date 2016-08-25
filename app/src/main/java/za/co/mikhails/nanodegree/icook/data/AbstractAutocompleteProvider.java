package za.co.mikhails.nanodegree.icook.data;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import za.co.mikhails.nanodegree.icook.spoonacular.SpoonacularApi;

public abstract class AbstractAutocompleteProvider extends ContentProvider {
    private static final String TAG = AutocompleteSuggestionProvider.class.getSimpleName();

    private static final String ID_COLUMN = "_id";
    private static final int DEFAULT_LIMIT = 10;

    protected SpoonacularApi spoonacularApi;

    public AbstractAutocompleteProvider() {
    }

    @Override
    public boolean onCreate() {
        spoonacularApi = new SpoonacularApi();

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String query = uri.getLastPathSegment();

        if (!SearchManager.SUGGEST_URI_PATH_QUERY.equals(query)) {

            String limitParam = uri.getQueryParameter(SearchManager.SUGGEST_PARAMETER_LIMIT);
            int limit = DEFAULT_LIMIT;
            if (limitParam != null && limitParam.length() > 0) {
                try {
                    limit = Integer.parseInt(limitParam);
                } catch (NumberFormatException e) {
                    Log.d(TAG, "Unable to parse query limit: " + limitParam);
                }
            }

            ListCursor listCursor = new ListCursor(new String[]{ID_COLUMN, SearchManager.SUGGEST_COLUMN_TEXT_1});
            return getPopulateCursor(query, limit, listCursor);
        }
        return null;
    }

    protected abstract ListCursor getPopulateCursor(String query, int limit, ListCursor listCursor);

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}

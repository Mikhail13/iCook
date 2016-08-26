package za.co.mikhails.nanodegree.icook.data;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import za.co.mikhails.nanodegree.icook.spoonacular.SpoonacularApi;

public class AutocompleteContentProvider extends ContentProvider {
    private static final String TAG = AutocompleteContentProvider.class.getSimpleName();

    private static final String CONTENT_AUTHORITY = "za.co.mikhails.nanodegree.icook.provider.autocomplete";
    private static final String PATH_SUGGESTION = "search_suggest_query";
    private static final String SUGGESTION_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUGGESTION;

    private static final String PATH_INGREDIENTS = "ingredients";
    private static final String INGREDIENTS_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INGREDIENTS;

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final Uri INGREDIENTS_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_INGREDIENTS).build();

    private static final String ID_COLUMN = "_id";
    private static final int DEFAULT_LIMIT = 10;

    private static final int SUGGESTION = 100;
    private static final int INGREDIENTS = 200;

    protected SpoonacularApi spoonacularApi;
    protected UriMatcher uriMatcher;

    public AutocompleteContentProvider() {
    }

    @Override
    public boolean onCreate() {
        spoonacularApi = new SpoonacularApi();
        uriMatcher = buildUriMatcher();
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

            ListCursor listCursor;
            switch (uriMatcher.match(uri)) {
                case SUGGESTION:
                    listCursor = new ListCursor(new String[]{ID_COLUMN, SearchManager.SUGGEST_COLUMN_TEXT_1});
                    return spoonacularApi.requestAutocompleteSuggestions(getContext(), listCursor, query, limit);
                case INGREDIENTS:
                    listCursor = new ListCursor(new String[]{ID_COLUMN, SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_ICON_1});
                    return spoonacularApi.requestAutocompleteIngredients(getContext(), listCursor, query, limit);
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case SUGGESTION:
                return SUGGESTION_TYPE;
            case INGREDIENTS:
                return INGREDIENTS_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

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

    protected UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(CONTENT_AUTHORITY, PATH_SUGGESTION + "/*", SUGGESTION);
        matcher.addURI(CONTENT_AUTHORITY, PATH_INGREDIENTS + "/*", INGREDIENTS);

        return matcher;
    }
}

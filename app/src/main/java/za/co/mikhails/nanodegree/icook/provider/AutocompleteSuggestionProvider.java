package za.co.mikhails.nanodegree.icook.provider;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.squareup.picasso.Picasso;

import za.co.mikhails.nanodegree.icook.R;
import za.co.mikhails.nanodegree.icook.spoonacular.RestApi;

public class AutocompleteSuggestionProvider extends ContentProvider implements AddRowListener {
    private static final String CONTENT_AUTHORITY = "za.co.mikhails.nanodegree.icook.provider.autocomplete";
    private static final String PATH_AUTOCOMPLETE = "autocomplete";
    private static final String AUTOCOMPLETE_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_AUTOCOMPLETE;
    private static final String ID_COLUMN = "_id";

    private RestApi restApi;
    private final ListCursor listCursor = new ListCursor(new String[]{
            ID_COLUMN,
            SearchManager.SUGGEST_COLUMN_TEXT_1});
//            ,SearchManager.SUGGEST_COLUMN_ICON_1});

    public AutocompleteSuggestionProvider() {
    }

    @Override
    public boolean onCreate() {
        restApi = new RestApi();

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String query = uri.getLastPathSegment();
        if (!SearchManager.SUGGEST_URI_PATH_QUERY.equals(query)) {
            listCursor.clear();
            restApi.requestAutocompleteSuggestions(getContext(), listCursor, query, 10);
            return listCursor;
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return AUTOCOMPLETE_TYPE;
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

    @Override
    public void addRow(String[] columnValues) {
        Picasso.with(getContext()).load(getContext().getString(R.string.ingredients_url));
    }
}

package za.co.mikhails.nanodegree.icook.data;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;

import za.co.mikhails.nanodegree.icook.data.RecipeContract.SearchResultEntry;

public class SearchResultLoader extends CursorLoader {

    public static CursorLoader newLoaderInstance(Context context) {
        return new SearchResultLoader(context, SearchResultEntry.buildUri());
    }

    private SearchResultLoader(Context context, Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, SearchResultEntry.COLUMN_KEY);
    }

    public interface Query {
        String[] PROJECTION = {
                SearchResultEntry.COLUMN_KEY,
                SearchResultEntry.COLUMN_ID,
                SearchResultEntry.COLUMN_TITLE,
                SearchResultEntry.COLUMN_IMAGE,
                SearchResultEntry.COLUMN_IMAGE_TYPE,
                SearchResultEntry.COLUMN_IMAGE_BASE_URL,
                SearchResultEntry.COLUMN_CALORIES,
                SearchResultEntry.COLUMN_PROTEIN,
                SearchResultEntry.COLUMN_FAT,
                SearchResultEntry.COLUMN_CARBS
        };

        int ID = 1;
        int TITLE = 2;
        int IMAGE = 3;
        int IMAGE_TYPE = 4;
        int IMAGE_BASE_URL = 5;
        int CALORIES = 6;
        int PROTEIN = 7;
        int FAT = 8;
        int CARBS = 9;
    }
}

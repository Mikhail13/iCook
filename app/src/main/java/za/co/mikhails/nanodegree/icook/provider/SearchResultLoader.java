package za.co.mikhails.nanodegree.icook.provider;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;

public class SearchResultLoader extends CursorLoader {

    public static CursorLoader newLoaderInstance(Context context) {
        return new SearchResultLoader(context, RecipeContract.SearchResultEntry.buildUri());
    }

    private SearchResultLoader(Context context, Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, null);
    }

    public interface Query {
        String[] PROJECTION = {
                RecipeContract.SearchResultEntry._ID,
                RecipeContract.SearchResultEntry.COLUMN_RECIPE_ID,
                RecipeContract.SearchResultEntry.COLUMN_TITLE,
                RecipeContract.SearchResultEntry.COLUMN_IMAGE,
                RecipeContract.SearchResultEntry.COLUMN_IMAGE_TYPE,
                RecipeContract.SearchResultEntry.COLUMN_IMAGE_BASE_URL,
                RecipeContract.SearchResultEntry.COLUMN_CALORIES,
                RecipeContract.SearchResultEntry.COLUMN_PROTEIN,
                RecipeContract.SearchResultEntry.COLUMN_FAT,
                RecipeContract.SearchResultEntry.COLUMN_CARBS
        };

        int _ID = 0;
        int RECIPE_ID = 1;
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

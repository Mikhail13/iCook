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
                RecipeContract.SearchResultEntry.COLUMN_ID,
                RecipeContract.SearchResultEntry.COLUMN_TITLE,
                RecipeContract.SearchResultEntry.COLUMN_IMAGE,
                RecipeContract.SearchResultEntry.COLUMN_IMAGE_TYPE,
                RecipeContract.SearchResultEntry.COLUMN_IMAGE_BASE_URL,
                RecipeContract.SearchResultEntry.COLUMN_CALORIES,
                RecipeContract.SearchResultEntry.COLUMN_PROTEIN,
                RecipeContract.SearchResultEntry.COLUMN_FAT,
                RecipeContract.SearchResultEntry.COLUMN_CARBS
        };

        int ID = 0;
        int TITLE = 1;
        int IMAGE = 2;
        int IMAGE_TYPE = 3;
        int IMAGE_BASE_URL = 4;
        int CALORIES = 5;
        int PROTEIN = 6;
        int FAT = 7;
        int CARBS = 8;
    }
}

package za.co.mikhails.nanodegree.icook.provider;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import za.co.mikhails.nanodegree.icook.provider.RecipeContract.RecipeEntry;

public class RecipeDetailsLoader extends CursorLoader {

    public static RecipeDetailsLoader newInstanceForItemId(Context context, String recipeId) {
        String selection = RecipeEntry.COLUMN_RECIPE_ID + "=?";
        String[] selectionArgs = {recipeId};
        return new RecipeDetailsLoader(context, RecipeEntry.buildItemUri(recipeId), selection, selectionArgs);
    }

    private RecipeDetailsLoader(Context context, Uri uri, String selection, String[] selectionArgs) {
        super(context, uri, Query.PROJECTION, null, null, null);
//        super(context, uri, Query.PROJECTION, selection, selectionArgs, null);
    }

    public interface Query {
        String[] PROJECTION = {
                RecipeEntry._ID,
                RecipeEntry.COLUMN_RECIPE_ID,
                RecipeEntry.COLUMN_TITLE,
                RecipeEntry.COLUMN_DESCRIPTION,
                RecipeEntry.COLUMN_IMAGE_URL
        };

        int _ID = 0;
        int RECIPE_ID = 1;
        int TITLE = 2;
        int DESCRIPTION = 4;
        int IMAGE_URL = 4;
    }
}

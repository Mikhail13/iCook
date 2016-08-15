package za.co.mikhails.nanodegree.icook.provider;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import za.co.mikhails.nanodegree.icook.provider.RecipeContract.RecipeEntry;

public class RecipeDetailsLoader extends CursorLoader {

    public static RecipeDetailsLoader newInstanceForItemId(Context context, long recipeId) {
        String selection = RecipeEntry.COLUMN_ID + "=?";
        String[] selectionArgs = {String.valueOf(recipeId)};
        return new RecipeDetailsLoader(context, RecipeEntry.buildItemUri(recipeId), selection, selectionArgs);
    }

    private RecipeDetailsLoader(Context context, Uri uri, String selection, String[] selectionArgs) {
        super(context, uri, Query.PROJECTION, selection, selectionArgs, null);
    }

    public interface Query {
        String[] PROJECTION = {
                RecipeEntry.COLUMN_ID,
                RecipeEntry.COLUMN_TITLE,
                RecipeEntry.COLUMN_DESCRIPTION,
                RecipeEntry.COLUMN_IMAGE_URL
        };

        int ID = 0;
        int TITLE = 1;
        int DESCRIPTION = 2;
        int IMAGE_URL = 3;
    }
}

package za.co.mikhails.nanodegree.icook.data;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import za.co.mikhails.nanodegree.icook.data.RecipeContract.RecipeEntry;

public class RecipeDetailsLoader extends CursorLoader {

    public static RecipeDetailsLoader newInstanceForRecipeId(Context context, long recipeId) {
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
                RecipeEntry.COLUMN_IMAGE_URL,
                RecipeEntry.COLUMN_READY_IN,
                RecipeEntry.COLUMN_PERCENT_PROTEIN,
                RecipeEntry.COLUMN_PERCENT_FAT,
                RecipeEntry.COLUMN_PERCENT_CARBS
        };

        int ID = 0;
        int TITLE = 1;
        int DESCRIPTION = 2;
        int IMAGE_URL = 3;
        int READY_IN = 4;
        int PERCENT_PROTEIN = 5;
        int PERCENT_FAT = 6;
        int PERCENT_CARBS = 7;
    }
}

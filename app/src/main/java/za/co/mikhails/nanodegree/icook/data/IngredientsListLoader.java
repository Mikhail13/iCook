package za.co.mikhails.nanodegree.icook.data;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import za.co.mikhails.nanodegree.icook.data.RecipeContract.IngredientEntry;

public class IngredientsListLoader extends CursorLoader {

    private IngredientsListLoader(Context context, Uri uri, String selection, String[] selectionArgs) {
        super(context, uri, Query.PROJECTION, selection, selectionArgs, null);
    }

    public static IngredientsListLoader newInstanceForRecipeId(Context context, long recipeId) {
        String selection = IngredientEntry.COLUMN_RECIPE_ID + "=?";
        String[] selectionArgs = {String.valueOf(recipeId)};
        return new IngredientsListLoader(context, IngredientEntry.CONTENT_LIST_URI, selection, selectionArgs);
    }

    public interface Query {
        String[] PROJECTION = {
                IngredientEntry.COLUMN_ID,
                IngredientEntry.COLUMN_RECIPE_ID,
                IngredientEntry.COLUMN_NAME,
                IngredientEntry.COLUMN_IMAGE,
                IngredientEntry.COLUMN_AMOUNT,
                IngredientEntry.COLUMN_UNIT
        };

        int ID = 0;
        int RECIPE_ID = 1;
        int NAME = 2;
        int IMAGE = 3;
        int AMOUNT = 4;
        int UNIT = 5;
    }
}

package za.co.mikhails.nanodegree.icook.data;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import za.co.mikhails.nanodegree.icook.data.RecipeContract.InstructionsEntry;

public class InstructionsListLoader extends CursorLoader {

    public static InstructionsListLoader newInstanceForRecipeId(Context context, long recipeId) {
        String selection = InstructionsEntry.COLUMN_RECIPE_ID + "=?";
        String[] selectionArgs = {String.valueOf(recipeId)};
        String orderBy = InstructionsEntry.COLUMN_NAME + ",ABS(" + InstructionsEntry.COLUMN_NUMBER + ")";
        return new InstructionsListLoader(context, InstructionsEntry.CONTENT_LIST_URI, selection, selectionArgs, orderBy);
    }

    private InstructionsListLoader(Context context, Uri uri, String selection, String[] selectionArgs, String orderBy) {
        super(context, uri, Query.PROJECTION, selection, selectionArgs, orderBy);
    }

    public interface Query {
        String[] PROJECTION = {
                InstructionsEntry.COLUMN_ID,
                InstructionsEntry.COLUMN_RECIPE_ID,
                InstructionsEntry.COLUMN_NAME,
                InstructionsEntry.COLUMN_NUMBER,
                InstructionsEntry.COLUMN_STEP
        };

        int ID = 0;
        int RECIPE_ID = 1;
        int NAME = 2;
        int NUMBER = 3;
        int STEP = 4;
    }
}

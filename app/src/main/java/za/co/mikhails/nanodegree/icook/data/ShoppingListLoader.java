package za.co.mikhails.nanodegree.icook.data;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import za.co.mikhails.nanodegree.icook.data.RecipeContract.ShoppingListEntry;

public class ShoppingListLoader extends CursorLoader {

    private ShoppingListLoader(Context context, Uri uri, String selection, String[] selectionArgs) {
        super(context, uri, Query.PROJECTION, selection, selectionArgs, null);
    }

    public interface Query {

        String[] PROJECTION = {
                ShoppingListEntry.COLUMN_ID,
                ShoppingListEntry.COLUMN_NAME,
                ShoppingListEntry.COLUMN_IMAGE,
                ShoppingListEntry.COLUMN_AMOUNT,
                ShoppingListEntry.COLUMN_UNIT,
                ShoppingListEntry.COLUMN_CHECKED
        };

        int ID = 0;
        int NAME = 1;
        int IMAGE = 2;
        int AMOUNT = 3;
        int UNIT = 4;
        int CHECKED = 5;
    }
}

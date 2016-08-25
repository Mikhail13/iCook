package za.co.mikhails.nanodegree.icook.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.Nullable;

public class AutocompleteIngredientsProvider extends AbstractAutocompleteProvider {
    private static final String CONTENT_AUTHORITY = "za.co.mikhails.nanodegree.icook.provider.ingredients";
    private static final String PATH_AUTOCOMPLETE = "ingredients";
    public static final String AUTOCOMPLETE_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_AUTOCOMPLETE;

    protected ListCursor getPopulateCursor(String query, int limit, ListCursor listCursor) {
        return spoonacularApi.requestAutocompleteIngredients(getContext(), listCursor, query, limit);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return AUTOCOMPLETE_TYPE;
    }
}

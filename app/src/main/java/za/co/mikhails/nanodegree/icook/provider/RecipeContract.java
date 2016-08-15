package za.co.mikhails.nanodegree.icook.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class RecipeContract {

    public static final String CONTENT_AUTHORITY = "za.co.mikhails.nanodegree.icook.provider.recipe";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String FAVORITES_CONTENT_AUTHORITY = "za.co.mikhails.nanodegree.icook.provider.favorites";
    public static final Uri FAVORITES_BASE_CONTENT_URI = Uri.parse("content://" + FAVORITES_CONTENT_AUTHORITY);

    public static final String PATH_SEARCH_RESULT = "search_result";
    public static final String PATH_RECIPE_DETAILS = "details";
    public static final String PATH_RECIPE = PATH_RECIPE_DETAILS + "/#";
    public static final String PATH_INGREDIENT = "ingredient";

    public static final class SearchResultEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SEARCH_RESULT).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SEARCH_RESULT;

        public static final String TABLE_NAME = "search_result";

        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_IMAGE_TYPE = "image_type";
        public static final String COLUMN_IMAGE_BASE_URL = "image_base_url";
        public static final String COLUMN_CALORIES = "calories";
        public static final String COLUMN_PROTEIN = "protein";
        public static final String COLUMN_FAT = "fat";
        public static final String COLUMN_CARBS = "carbs";

        public static Uri buildResultUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildUri() {
            Uri baseContentUri = RecipeContract.BASE_CONTENT_URI;
            Uri.Builder builder = baseContentUri.buildUpon().appendPath(RecipeContract.PATH_SEARCH_RESULT);
            return builder.build();
        }
    }

    public static final class RecipeEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPE_DETAILS).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPE_DETAILS;

        public static final String TABLE_NAME = "recipe";

        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_READY_IN = "ready_in";
        public static final String COLUMN_PERCENT_PROTEIN = "percent_protein";
        public static final String COLUMN_PERCENT_FAT = "percent_fat";
        public static final String COLUMN_PERCENT_CARBS = "percent_carbs";

        public static Uri buildResultUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildItemUri(long recipeId) {
            return ContentUris.withAppendedId(CONTENT_URI, recipeId);
        }
    }

    public static final class IngredientEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_INGREDIENT).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INGREDIENT;

        public static final String TABLE_NAME = "ingredient";

        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_UNIT = "unit";

        public static Uri buildResultUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildItemUri(long recipeId) {
            return ContentUris.withAppendedId(CONTENT_URI, recipeId);
        }
    }

}

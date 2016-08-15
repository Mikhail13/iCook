package za.co.mikhails.nanodegree.icook.spoonacular;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import za.co.mikhails.nanodegree.icook.R;
import za.co.mikhails.nanodegree.icook.provider.ListCursor;
import za.co.mikhails.nanodegree.icook.provider.RecipeContract;

public class SpoonacularApi {
    private static final String TAG = SpoonacularApi.class.getSimpleName();

    public static final String PARAM_TYPE = "type";
    public static final String PARAM_CUISINE = "cuisine";
    public static final String PARAM_DIET = "diet";
    public static final String PARAM_INTOLERANCES = "intolerances";
    public static final String PARAM_MIN_CALORIES = "minCalories";
    public static final String PARAM_MAX_CALORIES = "maxCalories";
    public static final String PARAM_MIN_CARBS = "minCarbs";
    public static final String PARAM_MAX_CARBS = "maxCarbs";
    public static final String PARAM_MIN_FAT = "minFat";
    public static final String PARAM_MAX_FAT = "maxFat";
    public static final String PARAM_MIN_PROTEIN = "minProtein";
    public static final String PARAM_MAX_PROTEIN = "maxProtein";
    public static final String PARAM_INCLUDE_INGREDIENTS = "includeIngredients";
    public static final String PARAM_EXCLUDE_INGREDIENTS = "excludeIngredients";

    static final String RECIPE_VALUES = "recipeValues";
    static final String INGREDIENT_LIST = "ingredientList";

    public ListCursor requestAutocompleteSuggestions(Context context, ListCursor listCursor, String query, int limit) {
        HttpURLConnection urlConnection = null;
        JsonReader reader = null;
        try {
            Uri builtUri = Uri.parse(context.getString(R.string.autocomplete_url)).buildUpon()
                    .appendQueryParameter("number", String.valueOf(limit))
                    .appendQueryParameter("query", query.trim())
                    .build();

            URL url = new URL(builtUri.toString());
            Log.d(TAG, "URL: " + url.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("X-Mashape-Key", context.getString(R.string.mashape_key));
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));

            if (reader.hasNext()) {
                reader.beginArray();
                while (reader.hasNext()) {
                    reader.beginObject();

                    String[] columnValues = new String[2];
                    while (reader.hasNext()) {
                        String name = reader.nextName();
                        if (name.equals("id")) {
                            columnValues[0] = reader.nextString();
                        } else if (name.equals("title")) {
                            columnValues[1] = reader.nextString();
                        } else {
                            reader.skipValue();
                        }
                    }
                    listCursor.addRow(columnValues);
                    reader.endObject();
                }
                reader.endArray();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }
        return listCursor;
    }

    public ContentValues requestRecipeSummary(Context context, long recipeId) {
        ContentValues result = null;

        if (recipeId != -1) {
            HttpURLConnection urlConnection = null;
            JsonReader reader = null;
            try {
                Uri builtUri = Uri.parse(context.getString(R.string.recipe_summary_url)).buildUpon()
                        .appendPath(String.valueOf(recipeId))
                        .appendPath("summary")
//                        .appendPath("information")
//                        .appendQueryParameter("includeNutrition", "false")
                        .build();

                URL url = new URL(builtUri.toString());
                Log.d(TAG, "URL: " + url.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("X-Mashape-Key", context.getString(R.string.mashape_key));
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
                result = parseRecipeSummaryValues(reader);

            } catch (Exception e) {
                Log.e(TAG, "Error ", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }
        }
        return result;
    }

//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//            StringBuilder out = new StringBuilder();
//            String line;
//            while ((line = bufferedReader.readLine()) != null) {
//                out.append(line);
//            }
//            Log.d(TAG, "requestQuickSearch: " + out.toString());
//            bufferedReader.close();

    private ContentValues parseRecipeSummaryValues(JsonReader reader) throws IOException {
        ContentValues resultValues = new ContentValues();
        if (reader.hasNext()) {

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("id")) {
                    resultValues.put(RecipeContract.RecipeEntry.COLUMN_ID, reader.nextLong());
                } else if (name.equals("title")) {
                    resultValues.put(RecipeContract.RecipeEntry.COLUMN_TITLE, reader.nextString());
                } else if (name.equals("summary")) {
                    resultValues.put(RecipeContract.RecipeEntry.COLUMN_DESCRIPTION, reader.nextString());
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        }
        return resultValues;
    }

    public Map<String, Object> requestRecipeDetails(Context context, long recipeId) {
        Map<String, Object> result = null;

        if (recipeId != -1) {
            HttpURLConnection urlConnection = null;
            JsonReader reader = null;
            try {
                Uri builtUri = Uri.parse(context.getString(R.string.recipe_summary_url)).buildUpon()
                        .appendPath(String.valueOf(recipeId))
                        .appendPath("information")
                        .appendQueryParameter("includeNutrition", "true")
                        .build();

                URL url = new URL(builtUri.toString());
                Log.d(TAG, "URL: " + url.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("X-Mashape-Key", context.getString(R.string.mashape_key));
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
                result = parseRecipeDetailsValues(reader);

            } catch (Exception e) {
                Log.e(TAG, "Error ", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }
        }
        return result;
    }

    private Map<String, Object> parseRecipeDetailsValues(JsonReader reader) throws IOException {
        ContentValues recipeValues = new ContentValues();
        List<ContentValues> ingredientList = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        result.put(RECIPE_VALUES, recipeValues);
        result.put(INGREDIENT_LIST, ingredientList);

        if (reader.hasNext()) {

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("id")) {
                    recipeValues.put(RecipeContract.RecipeEntry.COLUMN_ID, reader.nextLong());
                } else if (name.equals("title")) {
                    recipeValues.put(RecipeContract.RecipeEntry.COLUMN_TITLE, reader.nextString());
                } else if (name.equals("readyInMinutes")) {
                    recipeValues.put(RecipeContract.RecipeEntry.COLUMN_READY_IN, reader.nextString());
                } else if (name.equals("image")) {
                    recipeValues.put(RecipeContract.RecipeEntry.COLUMN_IMAGE_URL, reader.nextString());
                } else if (name.equals("caloricBreakdown")) {

                    reader.beginObject();
                    while (reader.hasNext()) {
                        String percent = reader.nextName();
                        if (percent.equals("percentProtein")) {
                            recipeValues.put(RecipeContract.RecipeEntry.COLUMN_PERCENT_PROTEIN, reader.nextString());
                        } else if (percent.equals("percentFat")) {
                            recipeValues.put(RecipeContract.RecipeEntry.COLUMN_PERCENT_FAT, reader.nextString());
                        } else if (percent.equals("percentCarbs")) {
                            recipeValues.put(RecipeContract.RecipeEntry.COLUMN_PERCENT_CARBS, reader.nextString());
                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();

                } else if (name.equals("extendedIngredients")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        ingredientList.add(parseIngredientValues(reader));
                    }
                    reader.endArray();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        }
        return result;
    }


    @NonNull
    private ContentValues parseIngredientValues(JsonReader reader) throws IOException {
        reader.beginObject();

        ContentValues resultValues = new ContentValues();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                resultValues.put(RecipeContract.IngredientEntry.COLUMN_ID, reader.nextLong());
            } else if (name.equals("name")) {
                resultValues.put(RecipeContract.IngredientEntry.COLUMN_NAME, reader.nextString());
            } else if (name.equals("image")) {
                resultValues.put(RecipeContract.IngredientEntry.COLUMN_IMAGE, reader.nextString());
            } else if (name.equals("amount")) {
                resultValues.put(RecipeContract.IngredientEntry.COLUMN_AMOUNT, reader.nextString());
            } else if (name.equals("unit")) {
                resultValues.put(RecipeContract.IngredientEntry.COLUMN_UNIT, reader.nextString());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return resultValues;
    }

    public List<ContentValues> requestQuickSearch(Context context, String query, int limit) {
        List<ContentValues> result = null;

        if (query != null && query.trim().length() > 0) {
            HttpURLConnection urlConnection = null;
            JsonReader reader = null;
            try {
                Uri builtUri = Uri.parse(context.getString(R.string.search_url)).buildUpon()
                        .appendQueryParameter("limitLicense", "false")
                        .appendQueryParameter("number", String.valueOf(limit))
                        .appendQueryParameter("offset", "0")
                        .appendQueryParameter("ranking", "1")
                        .appendQueryParameter("minCalories", "0")
                        .appendQueryParameter("maxCalories", "15000")
                        .appendQueryParameter("includeIngredients", "all")
                        .appendQueryParameter("query", query.trim())
                        .build();

                URL url = new URL(builtUri.toString());
                Log.d(TAG, "URL: " + url.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("X-Mashape-Key", context.getString(R.string.mashape_key));
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
                result = parseResultValues(reader);

            } catch (Exception e) {
                Log.e(TAG, "Error ", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }
        }
        return result;
    }

    public List<ContentValues> requestAdvancedSearch(Context context, Bundle bundle, int limit) {
        List<ContentValues> result = null;

        if (bundle != null) {
            HttpURLConnection urlConnection = null;
            JsonReader reader = null;
            try {
                Uri.Builder builder = Uri.parse(context.getString(R.string.search_url)).buildUpon()
                        .appendQueryParameter("limitLicense", "false")
                        .appendQueryParameter("number", String.valueOf(limit))
                        .appendQueryParameter("offset", "0")
                        .appendQueryParameter("ranking", "1");
                appendQueryParameter(builder, PARAM_TYPE, bundle);
                appendQueryParameter(builder, PARAM_CUISINE, bundle);
                appendQueryParameter(builder, PARAM_DIET, bundle);
                appendQueryParameter(builder, PARAM_INTOLERANCES, bundle);
                appendQueryParameter(builder, PARAM_MIN_CALORIES, bundle);
                appendQueryParameter(builder, PARAM_MAX_CALORIES, bundle);
                appendQueryParameter(builder, PARAM_MIN_CARBS, bundle);
                appendQueryParameter(builder, PARAM_MAX_CARBS, bundle);
                appendQueryParameter(builder, PARAM_MIN_FAT, bundle);
                appendQueryParameter(builder, PARAM_MAX_FAT, bundle);
                appendQueryParameter(builder, PARAM_MIN_PROTEIN, bundle);
                appendQueryParameter(builder, PARAM_MAX_PROTEIN, bundle);
                appendQueryParameter(builder, PARAM_INCLUDE_INGREDIENTS, bundle);
                appendQueryParameter(builder, PARAM_EXCLUDE_INGREDIENTS, bundle);

                URL url = new URL(builder.build().toString());
                Log.d(TAG, "URL: " + url.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("X-Mashape-Key", context.getString(R.string.mashape_key));
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
                result = parseResultValues(reader);

            } catch (Exception e) {
                Log.e(TAG, "Error ", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }
        }
        return result;
    }

    private void appendQueryParameter(Uri.Builder builder, String paramName, Bundle bundle) {
        if (bundle.containsKey(paramName)) {
            builder.appendQueryParameter(paramName, bundle.getString(paramName));
        }
    }

    private List<ContentValues> parseResultValues(JsonReader reader) throws IOException {
        List<ContentValues> resultList = new ArrayList();
        String baseUrl = null;
        while (reader.hasNext()) {

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("results") && reader.peek() != JsonToken.NULL) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        resultList.add(parseContentValues(reader));
                    }
                    reader.endArray();
                } else if (name.equals("baseUri")) {
                    baseUrl = reader.nextString();
                } else {
                    reader.skipValue();
                }
            }
            if (baseUrl != null) {
                for (ContentValues contentValues : resultList) {
                    contentValues.put(RecipeContract.SearchResultEntry.COLUMN_IMAGE_BASE_URL, baseUrl);
                }
            }
        }
        return resultList;
    }

    @NonNull
    private ContentValues parseContentValues(JsonReader reader) throws IOException {
        reader.beginObject();

        ContentValues resultValues = new ContentValues();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                resultValues.put(RecipeContract.SearchResultEntry.COLUMN_ID, reader.nextLong());
            } else if (name.equals("title")) {
                resultValues.put(RecipeContract.SearchResultEntry.COLUMN_TITLE, reader.nextString());
            } else if (name.equals("image")) {
                resultValues.put(RecipeContract.SearchResultEntry.COLUMN_IMAGE, reader.nextString());
            } else if (name.equals("imageType")) {
                resultValues.put(RecipeContract.SearchResultEntry.COLUMN_IMAGE_TYPE, reader.nextString());
            } else if (name.equals("calories")) {
                resultValues.put(RecipeContract.SearchResultEntry.COLUMN_CALORIES, reader.nextString());
            } else if (name.equals("protein")) {
                resultValues.put(RecipeContract.SearchResultEntry.COLUMN_PROTEIN, reader.nextString());
            } else if (name.equals("fat")) {
                resultValues.put(RecipeContract.SearchResultEntry.COLUMN_FAT, reader.nextString());
            } else if (name.equals("carbs")) {
                resultValues.put(RecipeContract.SearchResultEntry.COLUMN_CARBS, reader.nextString());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return resultValues;
    }

}

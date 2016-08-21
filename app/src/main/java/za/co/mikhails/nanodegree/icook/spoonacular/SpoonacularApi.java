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
import java.util.List;

import za.co.mikhails.nanodegree.icook.R;
import za.co.mikhails.nanodegree.icook.data.ListCursor;
import za.co.mikhails.nanodegree.icook.data.RecipeContract;

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

    private static final int PARSE_RECIPE_SEARCH_RESULT = 1;
    private static final int PARSE_RECIPE_DETAILS = 2;
    private static final int PARSE_RECIPE_SUMMARY = 3;
    private static final int PARSE_RECIPE_INSTRUCTIONS = 4;

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
                        switch (name) {
                            case "id":
                                columnValues[0] = reader.nextString();
                                break;
                            case "title":
                                columnValues[1] = reader.nextString();
                                break;
                            default:
                                reader.skipValue();
                                break;
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

    List<ContentValues> requestRecipeDetails(Context context, long recipeId) {
        Uri builtUri = Uri.parse(context.getString(R.string.recipe_info_url)).buildUpon()
                .appendPath(String.valueOf(recipeId))
                .appendPath("information")
                .appendQueryParameter("includeNutrition", "true")
                .build();

        return requestSpoonacularData(builtUri,
                context.getString(R.string.mashape_key), PARSE_RECIPE_DETAILS);
    }

    List<ContentValues> requestRecipeSummary(Context context, long recipeId) {
        Uri builtUri = Uri.parse(context.getString(R.string.recipe_info_url)).buildUpon()
                .appendPath(String.valueOf(recipeId))
                .appendPath("summary")
//                        .appendPath("information")
//                        .appendQueryParameter("includeNutrition", "false")
                .build();

        return requestSpoonacularData(builtUri,
                context.getString(R.string.mashape_key), PARSE_RECIPE_SUMMARY);
    }

    List<ContentValues> requestRecipeInstructions(Context context, long recipeId) {
        Uri builtUri = Uri.parse(context.getString(R.string.recipe_info_url)).buildUpon()
                .appendPath(String.valueOf(recipeId))
                .appendPath("analyzedInstructions")
                .appendQueryParameter("stepBreakdown", "true")
                .build();

        return requestSpoonacularData(builtUri,
                context.getString(R.string.mashape_key), PARSE_RECIPE_INSTRUCTIONS);
    }

    List<ContentValues> requestQuickSearch(Context context, String query, int offset, int number) {
        Uri builtUri = Uri.parse(context.getString(R.string.search_url)).buildUpon()
                .appendQueryParameter("limitLicense", "false")
                .appendQueryParameter("number", String.valueOf(number))
                .appendQueryParameter("offset", String.valueOf(offset))
                .appendQueryParameter("ranking", "1")
                .appendQueryParameter("minCalories", "0")
                .appendQueryParameter("maxCalories", "15000")
                .appendQueryParameter("includeIngredients", "all")
                .appendQueryParameter("query", query.trim())
                .build();

        return requestSpoonacularData(builtUri, context.getString(R.string.mashape_key), PARSE_RECIPE_SEARCH_RESULT);
    }

    List<ContentValues> requestAdvancedSearch(Context context, Bundle bundle, int offset, int number) {
        Uri.Builder builder = Uri.parse(context.getString(R.string.search_url)).buildUpon()
                .appendQueryParameter("limitLicense", "false")
                .appendQueryParameter("number", String.valueOf(number))
                .appendQueryParameter("offset", String.valueOf(offset))
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

        return requestSpoonacularData(builder.build(), context.getString(R.string.mashape_key), PARSE_RECIPE_SEARCH_RESULT);
    }

    private void appendQueryParameter(Uri.Builder builder, String paramName, Bundle bundle) {
        if (bundle.containsKey(paramName)) {
            builder.appendQueryParameter(paramName, bundle.getString(paramName));
        }
    }

    private List<ContentValues> parseRecipeSummaryValues(JsonReader reader) throws IOException {
        List<ContentValues> resultList = new ArrayList<>();
        ContentValues resultValues = new ContentValues();
        resultList.add(resultValues);

        if (reader.hasNext()) {

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case "id":
                        resultValues.put(RecipeContract.RecipeEntry.COLUMN_ID, reader.nextLong());
                        break;
                    case "title":
                        resultValues.put(RecipeContract.RecipeEntry.COLUMN_TITLE, reader.nextString());
                        break;
                    case "summary":
                        resultValues.put(RecipeContract.RecipeEntry.COLUMN_DESCRIPTION, reader.nextString());
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
            reader.endObject();
        }
        return resultList;
    }

    private List<ContentValues> parseRecipeDetailsValues(JsonReader reader) throws IOException {
        List<ContentValues> resultList = new ArrayList<>();
        ContentValues recipeValues = new ContentValues();
        resultList.add(recipeValues);

        if (reader.hasNext()) {

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case "id":
                        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_ID, reader.nextLong());
                        break;
                    case "title":
                        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_TITLE, reader.nextString());
                        break;
                    case "readyInMinutes":
                        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_READY_IN, reader.nextString());
                        break;
                    case "image":
                        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_IMAGE_URL, reader.nextString());
                        break;
                    case "caloricBreakdown":

                        reader.beginObject();
                        while (reader.hasNext()) {
                            String percent = reader.nextName();
                            switch (percent) {
                                case "percentProtein":
                                    recipeValues.put(RecipeContract.RecipeEntry.COLUMN_PERCENT_PROTEIN, reader.nextString());
                                    break;
                                case "percentFat":
                                    recipeValues.put(RecipeContract.RecipeEntry.COLUMN_PERCENT_FAT, reader.nextString());
                                    break;
                                case "percentCarbs":
                                    recipeValues.put(RecipeContract.RecipeEntry.COLUMN_PERCENT_CARBS, reader.nextString());
                                    break;
                                default:
                                    reader.skipValue();
                                    break;
                            }
                        }
                        reader.endObject();

                        break;
                    case "extendedIngredients":
                        reader.beginArray();
                        while (reader.hasNext()) {
                            resultList.add(parseIngredientValues(reader));
                        }
                        reader.endArray();
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
            reader.endObject();
        }
        return resultList;
    }

    private List<ContentValues> parseRecipeInstructionsValues(JsonReader reader) throws IOException {
        List<ContentValues> resultList = new ArrayList<>();

        if (reader.hasNext()) {
            reader.beginArray();

            while (reader.hasNext()) {
                reader.beginObject();
                String itemName = null;
                List<ContentValues> steps = new ArrayList<>();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    switch (name) {
                        case "name":
                            itemName = reader.nextString();
                            break;
                        case "steps":
                            reader.beginArray();
                            while (reader.hasNext()) {
                                reader.beginObject();
                                ContentValues step = new ContentValues();
                                while (reader.hasNext()) {
                                    String attribute = reader.nextName();
                                    switch (attribute) {
                                        case "number":
                                            step.put(RecipeContract.InstructionsEntry.COLUMN_NUMBER, reader.nextString());
                                            break;
                                        case "step":
                                            step.put(RecipeContract.InstructionsEntry.COLUMN_STEP, reader.nextString());
                                            break;
                                        default:
                                            reader.skipValue();
                                            break;
                                    }
                                }
                                reader.endObject();
                                steps.add(step);
                            }
                            reader.endArray();
                            break;
                        default:
                            reader.skipValue();
                            break;
                    }
                }
                reader.endObject();
                for (ContentValues step : steps) {
                    step.put(RecipeContract.InstructionsEntry.COLUMN_NAME, itemName);
                    resultList.add(step);
                }
            }
            reader.endArray();
        }
        return resultList;
    }

    @NonNull
    private ContentValues parseIngredientValues(JsonReader reader) throws IOException {
        reader.beginObject();

        ContentValues resultValues = new ContentValues();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "id":
                    resultValues.put(RecipeContract.IngredientEntry.COLUMN_ID, reader.nextLong());
                    break;
                case "name":
                    resultValues.put(RecipeContract.IngredientEntry.COLUMN_NAME, reader.nextString());
                    break;
                case "image":
                    resultValues.put(RecipeContract.IngredientEntry.COLUMN_IMAGE, reader.nextString());
                    break;
                case "amount":
                    resultValues.put(RecipeContract.IngredientEntry.COLUMN_AMOUNT, reader.nextString());
                    break;
                case "unit":
                    resultValues.put(RecipeContract.IngredientEntry.COLUMN_UNIT, reader.nextString());
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        return resultValues;
    }

    private List<ContentValues> parseSearchResultValues(JsonReader reader) throws IOException {
        List<ContentValues> resultList = new ArrayList<>();
        String baseUrl = null;
        while (reader.hasNext()) {

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("results") && reader.peek() != JsonToken.NULL) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        resultList.add(parseSearchContentValues(reader));
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
    private ContentValues parseSearchContentValues(JsonReader reader) throws IOException {
        reader.beginObject();

        ContentValues resultValues = new ContentValues();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "id":
                    resultValues.put(RecipeContract.SearchResultEntry.COLUMN_ID, reader.nextLong());
                    break;
                case "title":
                    resultValues.put(RecipeContract.SearchResultEntry.COLUMN_TITLE, reader.nextString());
                    break;
                case "image":
                    resultValues.put(RecipeContract.SearchResultEntry.COLUMN_IMAGE, reader.nextString());
                    break;
                case "imageType":
                    resultValues.put(RecipeContract.SearchResultEntry.COLUMN_IMAGE_TYPE, reader.nextString());
                    break;
                case "calories":
                    resultValues.put(RecipeContract.SearchResultEntry.COLUMN_CALORIES, reader.nextString());
                    break;
                case "protein":
                    resultValues.put(RecipeContract.SearchResultEntry.COLUMN_PROTEIN, reader.nextString());
                    break;
                case "fat":
                    resultValues.put(RecipeContract.SearchResultEntry.COLUMN_FAT, reader.nextString());
                    break;
                case "carbs":
                    resultValues.put(RecipeContract.SearchResultEntry.COLUMN_CARBS, reader.nextString());
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        return resultValues;
    }


    private List<ContentValues> requestSpoonacularData(Uri builtUri, String mashapeKey, int dataType) {
        List<ContentValues> result = null;

        HttpURLConnection urlConnection = null;
        JsonReader reader = null;
        try {

            URL url = new URL(builtUri.toString());
            Log.d(TAG, "URL: " + url.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("X-Mashape-Key", mashapeKey);
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();

            reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
            result = parseSpoonacularJson(dataType, reader);

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
        return result;
    }

    private List<ContentValues> parseSpoonacularJson(int dataType, JsonReader reader) throws IOException {
        switch (dataType) {
            case PARSE_RECIPE_SEARCH_RESULT:
                return parseSearchResultValues(reader);
            case PARSE_RECIPE_DETAILS:
                return parseRecipeDetailsValues(reader);
            case PARSE_RECIPE_SUMMARY:
                return parseRecipeSummaryValues(reader);
            case PARSE_RECIPE_INSTRUCTIONS:
                return parseRecipeInstructionsValues(reader);
        }
        throw new UnsupportedOperationException("Unknown data type: " + dataType);
    }

}

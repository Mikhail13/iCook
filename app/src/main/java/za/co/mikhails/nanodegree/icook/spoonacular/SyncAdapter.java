package za.co.mikhails.nanodegree.icook.spoonacular;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
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
import za.co.mikhails.nanodegree.icook.provider.RecipeContract;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = SyncAdapter.class.getSimpleName();

    public static final String NEXT_PAGE = "nextpage";
    public static final String RECIPE = "recipe";
    public static final String QUERY = "query";
    public static final String QUERY_ADVANCED = "query_advanced";

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

    private int mTotalPages = 1;
    private int mLastPage = 0;
    private String baseUrl = null;

    //TODO: load next page on scroll

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        getContext().getContentResolver().delete(RecipeContract.SearchResultEntry.CONTENT_URI, null, null);
        if (extras.containsKey(RECIPE)) {
            requestRecipeDetails(extras.getString(RECIPE));
        } else if (extras.containsKey(QUERY)) {
            requestQuickSearch(extras.getString(QUERY), 10);
        } else if (extras.containsKey(QUERY_ADVANCED)) {
            requestAdvancedSearch(extras, 10);
        }
    }

    public boolean requestRecipeDetails(String recipeId) {
        boolean result = false;

        if (recipeId != null && recipeId.trim().length() > 0) {
            HttpURLConnection urlConnection = null;
            JsonReader reader = null;
            try {
                Uri builtUri = Uri.parse(getContext().getString(R.string.recipe_details_url)).buildUpon()
                        .appendPath(recipeId)
                        .appendPath("summary")
//                        .appendPath("information")
//                        .appendQueryParameter("includeNutrition", "false")
                        .build();

                URL url = new URL(builtUri.toString());
                Log.d(TAG, "URL: " + url.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("X-Mashape-Key", getContext().getString(R.string.mashape_key));
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//            StringBuilder out = new StringBuilder();
//            String line;
//            while ((line = bufferedReader.readLine()) != null) {
//                out.append(line);
//            }
//            Log.d(TAG, "requestQuickSearch: " + out.toString());
//            bufferedReader.close();

                reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));

                parseRecipeSummaryValues(reader);
                result = true;

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


    public boolean requestQuickSearch(String query, int limit) {
        boolean result = false;

        if (query != null && query.trim().length() > 0) {
            HttpURLConnection urlConnection = null;
            JsonReader reader = null;
            try {
                Uri builtUri = Uri.parse(getContext().getString(R.string.search_url)).buildUpon()
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
                urlConnection.setRequestProperty("X-Mashape-Key", getContext().getString(R.string.mashape_key));
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
                parseResultValues(reader);
                result = true;

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

    public boolean requestAdvancedSearch(Bundle bundle, int limit) {
        boolean result = false;

        if (bundle != null) {
            HttpURLConnection urlConnection = null;
            JsonReader reader = null;
            try {
                Uri.Builder builder = Uri.parse(getContext().getString(R.string.search_url)).buildUpon()
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
                urlConnection.setRequestProperty("X-Mashape-Key", getContext().getString(R.string.mashape_key));
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
                parseResultValues(reader);
                result = true;

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

    private void parseRecipeSummaryValues(JsonReader reader) throws IOException {
        if (reader.hasNext()) {

            reader.beginObject();
            ContentValues resultValues = new ContentValues();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("id")) {
                    resultValues.put(RecipeContract.RecipeEntry.COLUMN_RECIPE_ID, reader.nextLong());
                } else if (name.equals("title")) {
                    resultValues.put(RecipeContract.RecipeEntry.COLUMN_TITLE, reader.nextString());
                } else if (name.equals("summary")) {
                    resultValues.put(RecipeContract.RecipeEntry.COLUMN_DESCRIPTION, reader.nextString());
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();

            Uri inserted = insertRecipeDataIntoContentProvider(resultValues);
            Log.d(TAG, "Inserted [" + inserted + "] results");
        }
    }

    private void parseResultValues(JsonReader reader) throws IOException {
        while (reader.hasNext()) {
            List<ContentValues> resultList = new ArrayList();

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
            int inserted = insertSearchResultDataIntoContentProvider(resultList);
            Log.d(TAG, "Inserted [" + inserted + "] results");
        }
    }

    @NonNull
    private ContentValues parseContentValues(JsonReader reader) throws IOException {
        reader.beginObject();

        ContentValues resultValues = new ContentValues();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                resultValues.put(RecipeContract.SearchResultEntry.COLUMN_RECIPE_ID, reader.nextLong());
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

    private Uri insertRecipeDataIntoContentProvider(ContentValues resultList) {
        Uri result = null;
        if (resultList.size() > 0) {
            result = getContext().getContentResolver().insert(
                    RecipeContract.RecipeEntry.CONTENT_URI, resultList);
        }
        return result;
    }

    private int insertSearchResultDataIntoContentProvider(List<ContentValues> resultList) {
        int result = 0;
        if (resultList.size() > 0) {
            result = getContext().getContentResolver().bulkInsert(
                    RecipeContract.SearchResultEntry.CONTENT_URI,
                    resultList.toArray(new ContentValues[resultList.size()]));
        }
        return result;
    }

    public static void syncRecipeDetailsImmediately(Context context, String recipeId) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        if (recipeId != null) {
            bundle.putString(RECIPE, recipeId);
        }
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static void syncRecipeListImmediately(Context context, String query) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        if (query != null) {
            bundle.putString(QUERY, query);
        }
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static void syncRecipeListImmediately(Context context, Bundle query) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        if (query != null) {
            bundle.putBoolean(QUERY_ADVANCED, true);
            bundle.putAll(query);
        }
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        if (null == accountManager.getPassword(newAccount)) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        }
        return newAccount;
    }

    public static void loadNextPageOnScroll(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putBoolean(SyncAdapter.NEXT_PAGE, true);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }
}
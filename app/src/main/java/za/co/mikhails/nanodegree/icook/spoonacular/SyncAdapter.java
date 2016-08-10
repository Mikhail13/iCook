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
    public static final String QUERY = "query";
    private int mTotalPages = 1;
    private int mLastPage = 0;
    private String baseUrl = null;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        getContext().getContentResolver().delete(RecipeContract.SearchResultEntry.CONTENT_URI, null, null);
        requestRecipeSearch(extras.getString(QUERY), 10);
    }

    public boolean requestRecipeSearch(String query, int limit) {
        boolean result = false;

        HttpURLConnection urlConnection = null;
        JsonReader reader = null;
        try {
            Uri builtUri = Uri.parse(getContext().getString(R.string.search_url)).buildUpon()
                    .appendQueryParameter("limitLicense", "false")
                    .appendQueryParameter("number", String.valueOf(limit))
                    .appendQueryParameter("offset", "0")
                    .appendQueryParameter("ranking", "1")
//                    .appendQueryParameter("minCalories", "150")
//                    .appendQueryParameter("maxCalories", "15000")
                    .appendQueryParameter("minProtein", "5")
                    .appendQueryParameter("maxProtein", "100")
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

//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//            StringBuilder out = new StringBuilder();
//            String line;
//            while ((line = bufferedReader.readLine()) != null) {
//                out.append(line);
//            }
//            Log.d(TAG, "requestRecipeSearch: " + out.toString());
//            bufferedReader.close();
//
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
        return result;
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
            int inserted = insertDataIntoContentProvider(resultList);
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

    private int insertDataIntoContentProvider(List<ContentValues> resultList) {
        int result = 0;
        if (resultList.size() > 0) {
            result = getContext().getContentResolver().bulkInsert(
                    RecipeContract.SearchResultEntry.CONTENT_URI,
                    resultList.toArray(new ContentValues[resultList.size()]));
        }
        return result;
    }

    public static void syncRecipeListImmediately(Context context, String query) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        if (query != null) {
            bundle.putString(QUERY, query);
        }
        ContentResolver.requestSync(getSyncAccount(context, query),
                context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context, String sortType) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        if (null == accountManager.getPassword(newAccount)) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context, sortType);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context, String query) {
//        SyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        syncRecipeListImmediately(context, query);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context, null);
    }

    public static void loadNextPageOnScroll(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putBoolean(SyncAdapter.NEXT_PAGE, true);
        ContentResolver.requestSync(getSyncAccount(context, null), context.getString(R.string.content_authority), bundle);
    }
}
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
import android.util.Log;

import java.util.List;
import java.util.Map;

import za.co.mikhails.nanodegree.icook.R;
import za.co.mikhails.nanodegree.icook.provider.RecipeContract.IngredientEntry;
import za.co.mikhails.nanodegree.icook.provider.RecipeContract.RecipeEntry;
import za.co.mikhails.nanodegree.icook.provider.RecipeContract.SearchResultEntry;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = SyncAdapter.class.getSimpleName();

    public static final String NEXT_PAGE = "nextpage";
    public static final String RECIPE_ID = "recipe_id";
    public static final String QUERY = "query";
    public static final String QUERY_ADVANCED = "query_advanced";

    private int mTotalPages = 1;
    private int mLastPage = 0;
    private SpoonacularApi spoonacularApi = new SpoonacularApi();

    //TODO: load next page on scroll

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        if (extras.containsKey(RECIPE_ID)) {

            long recipeId = extras.getLong(RECIPE_ID, -1);
            if (recipeId != -1) {
                ContentValues contentValues = spoonacularApi.requestRecipeSummary(getContext(), recipeId);
                if (contentValues != null) {
                    Uri inserted = insertRecipeDataIntoContentProvider(contentValues);
                    Log.d(TAG, "Inserted uri [" + inserted + "]");
                }

                Map<String, Object> map = spoonacularApi.requestRecipeDetails(getContext(), recipeId);
                List<ContentValues> ingredientList = (List<ContentValues>) map.get(SpoonacularApi.INGREDIENT_LIST);
                ContentValues recipeValues = (ContentValues) map.get(SpoonacularApi.RECIPE_VALUES);

                if (recipeValues != null) {
                    int updated = updateRecipeDataIntoContentProvider(recipeValues);
                    Log.d(TAG, "Updated [" + updated + "] records");
                }

                if (ingredientList != null) {
                    int inserted = insertIngredientsDataIntoContentProvider(ingredientList);
                    Log.d(TAG, "Inserted [" + inserted + "] records");
                }
            }

        } else if (extras.containsKey(QUERY)) {

            getContext().getContentResolver().delete(SearchResultEntry.CONTENT_URI, null, null);
            List<ContentValues> contentValuesList = spoonacularApi.requestQuickSearch(getContext(), extras.getString(QUERY), 10);
            if (contentValuesList != null) {
                int inserted = insertSearchResultDataIntoContentProvider(contentValuesList);
                Log.d(TAG, "Inserted [" + inserted + "] records");
            }

        } else if (extras.containsKey(QUERY_ADVANCED)) {

            getContext().getContentResolver().delete(SearchResultEntry.CONTENT_URI, null, null);
            List<ContentValues> contentValuesList = spoonacularApi.requestAdvancedSearch(getContext(), extras, 10);
            if (contentValuesList != null) {
                int inserted = insertSearchResultDataIntoContentProvider(contentValuesList);
                Log.d(TAG, "Inserted [" + inserted + "] records");
            }

        }
    }

    private Uri insertRecipeDataIntoContentProvider(ContentValues resultList) {
        Uri result = null;
        if (resultList.size() > 0) {
            result = getContext().getContentResolver().insert(
                    RecipeEntry.buildItemUri(resultList.getAsLong(RecipeEntry.COLUMN_ID)), resultList);
        }
        return result;
    }

    private int updateRecipeDataIntoContentProvider(ContentValues resultList) {
        int result = 0;
        if (resultList.size() > 0) {
            result = getContext().getContentResolver().update(
                    RecipeEntry.buildItemUri(resultList.getAsLong(RecipeEntry.COLUMN_ID)), resultList,
                    RecipeEntry.COLUMN_ID + "=?", new String[]{resultList.getAsString(RecipeEntry.COLUMN_ID)});
        }
        return result;
    }

    private int insertIngredientsDataIntoContentProvider(List<ContentValues> resultList) {
        int result = 0;
        if (resultList.size() > 0) {
            result = getContext().getContentResolver().bulkInsert(
                    IngredientEntry.CONTENT_URI,
                    resultList.toArray(new ContentValues[resultList.size()]));
        }
        return result;
    }

    private int insertSearchResultDataIntoContentProvider(List<ContentValues> resultList) {
        int result = 0;
        if (resultList.size() > 0) {
            result = getContext().getContentResolver().bulkInsert(
                    SearchResultEntry.CONTENT_URI,
                    resultList.toArray(new ContentValues[resultList.size()]));
        }
        return result;
    }

    public static void syncRecipeDetailsImmediately(Context context, long recipeId) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putLong(RECIPE_ID, recipeId);

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
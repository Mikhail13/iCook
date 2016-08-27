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

import za.co.mikhails.nanodegree.icook.R;
import za.co.mikhails.nanodegree.icook.data.RecipeContract.IngredientEntry;
import za.co.mikhails.nanodegree.icook.data.RecipeContract.InstructionsEntry;
import za.co.mikhails.nanodegree.icook.data.RecipeContract.RecipeEntry;
import za.co.mikhails.nanodegree.icook.data.RecipeContract.SearchResultEntry;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final int PAGE_SIZE = 20;
    private static final String TAG = SyncAdapter.class.getSimpleName();
    private static final String NEXT_PAGE = "nextpage";
    private static final String RECIPE_ID = "recipe_id";
    private static final String INSTRUCTIONS = "instructions";
    private static final String QUERY = "query";
    private static final String QUERY_ADVANCED = "query_advanced";

    private int offset = 0;
    private boolean noMoreResults = false;
    private Bundle lastSearchExtras;
    private SpoonacularApi spoonacularApi = new SpoonacularApi();

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
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

    public static void syncRecipeInstructionsImmediately(Context context, long recipeId) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putLong(RECIPE_ID, recipeId);
        bundle.putBoolean(INSTRUCTIONS, true);

        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    private static Account getSyncAccount(Context context) {
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

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        if (extras.containsKey(RECIPE_ID)) {

            long recipeId = extras.getLong(RECIPE_ID, -1);
            if (recipeId != -1) {

                if (extras.containsKey(INSTRUCTIONS)) {

                    getContext().getContentResolver().delete(InstructionsEntry.CONTENT_URI, InstructionsEntry.COLUMN_RECIPE_ID + "=?",
                            new String[]{String.valueOf(recipeId)});
                    List<ContentValues> contentValues = spoonacularApi.requestRecipeInstructions(getContext(), recipeId);
                    if (contentValues != null) {
                        int inserted = insertRecipeInstructionsIntoContentProvider(contentValues, recipeId);
                        Log.d(TAG, "Inserted [" + inserted + "] records");
                    }
                } else {

                    List<ContentValues> contentValues = spoonacularApi.requestRecipeSummary(getContext(), recipeId);
                    if (contentValues != null) {
                        Uri inserted = insertRecipeDataIntoContentProvider(contentValues.get(0));
                        Log.d(TAG, "Inserted uri [" + inserted + "]");
                    }

                    List<ContentValues> recipeDetails = spoonacularApi.requestRecipeDetails(getContext(), recipeId);
                    if (recipeDetails != null) {
                        int updated = updateRecipeDataIntoContentProvider(recipeDetails.remove(0));
                        Log.d(TAG, "Updated [" + updated + "] records");

                        int inserted = insertIngredientsDataIntoContentProvider(recipeDetails, recipeId);
                        Log.d(TAG, "Inserted [" + inserted + "] records");
                    }
                }
            }

        } else if (extras.containsKey(QUERY)) {

            offset = 0;
            noMoreResults = false;
            getContext().getContentResolver().delete(SearchResultEntry.CONTENT_URI, null, null);
            lastSearchExtras = extras;
            List<ContentValues> contentValuesList = spoonacularApi.requestQuickSearch(getContext(), extras.getString(QUERY), offset, PAGE_SIZE);
            insertContentValues(contentValuesList);

        } else if (extras.containsKey(QUERY_ADVANCED)) {

            offset = 0;
            noMoreResults = false;
            getContext().getContentResolver().delete(SearchResultEntry.CONTENT_URI, null, null);
            lastSearchExtras = extras;
            List<ContentValues> contentValuesList = spoonacularApi.requestAdvancedSearch(getContext(), extras, offset, PAGE_SIZE);
            insertContentValues(contentValuesList);

        } else if (extras.containsKey(NEXT_PAGE) && lastSearchExtras != null && !noMoreResults) {

            if (lastSearchExtras.containsKey(QUERY)) {
                List<ContentValues> contentValuesList = spoonacularApi.requestQuickSearch(getContext(), lastSearchExtras.getString(QUERY), offset, PAGE_SIZE);
                insertContentValues(contentValuesList);
            } else if (lastSearchExtras.containsKey(QUERY_ADVANCED)) {
                List<ContentValues> contentValuesList = spoonacularApi.requestAdvancedSearch(getContext(), lastSearchExtras, offset, PAGE_SIZE);
                insertContentValues(contentValuesList);
            }
        }
    }

    private void insertContentValues(List<ContentValues> contentValuesList) {
        if (contentValuesList != null) {
            int listSize = contentValuesList.size();
            if (listSize < PAGE_SIZE) {
                noMoreResults = true;
            }
            offset += listSize;
            int inserted = insertSearchResultDataIntoContentProvider(contentValuesList);
            Log.d(TAG, "Inserted [" + inserted + "] records");
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

    private int insertRecipeInstructionsIntoContentProvider(List<ContentValues> resultList, long recipeId) {
        int result = 0;
        if (resultList.size() > 0) {
            for (ContentValues contentValues : resultList) {
                contentValues.put(IngredientEntry.COLUMN_RECIPE_ID, recipeId);
            }
            result = getContext().getContentResolver().bulkInsert(
                    InstructionsEntry.CONTENT_URI,
                    resultList.toArray(new ContentValues[resultList.size()]));
        }
        return result;
    }

    private int insertIngredientsDataIntoContentProvider(List<ContentValues> resultList, long recipeId) {
        int result = 0;
        if (resultList.size() > 0) {
            for (ContentValues contentValues : resultList) {
                contentValues.put(IngredientEntry.COLUMN_RECIPE_ID, recipeId);
            }
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
}
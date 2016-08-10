package za.co.mikhails.nanodegree.icook;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import za.co.mikhails.nanodegree.icook.provider.RecipeContract;
import za.co.mikhails.nanodegree.icook.spoonacular.SyncAdapter;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnSuggestionListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int SEARCH_RESULT_LOADER = 1;
    private static final String SEARCH_QUERY = "search_query";
    private View progressIndicator;
    private ListView searchResult;
    private SearchResultAdapter searchResultAdapter;
    private CursorLoader searchResultLoader;
    private Parcelable restoreListViewState;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Intent intent = getIntent();
//        String action = intent.getAction();
//        if (Intent.ACTION_SEARCH.equals(action)) {
//            String query = intent.getStringExtra(SearchManager.QUERY);
//            searchQuery("potato");
//        } else if (Intent.ACTION_VIEW.equals(action)) {
//            Log.d(TAG, "Intent.ACTION_VIEW: intent.getData(): " + intent.getData());
////            handles a click on a search suggestion; launches activity to show word
////            Intent intentShowLocal = new Intent(this, DetailsLocalActivity.class);
////            intentShowLocal.setData(intent.getData());
////            startActivity(intentShowLocal);
//        }

        progressIndicator = findViewById(R.id.progress_indicator);
        searchResult = (ListView) findViewById(R.id.search_result);
        searchResultAdapter = new SearchResultAdapter(this, null, 0);
        searchResult.setAdapter(searchResultAdapter);

        AdView mAdView = (AdView) findViewById(R.id.ad_banner);
        if (mAdView != null) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
            mAdView.loadAd(adRequest);
        }

        getLoaderManager().restartLoader(SEARCH_RESULT_LOADER, null, this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchQuery(query);
        }
    }

    private void searchQuery(String query) {
        Log.d(TAG, "searchQuery: [" + query + "]");

        SyncAdapter.syncRecipeListImmediately(this, query);

//        Bundle bundle = new Bundle();
//        bundle.putString(SEARCH_QUERY, query);
        getLoaderManager().restartLoader(SEARCH_RESULT_LOADER, null, this);
    }

    public void showProgressIndicator(boolean show) {
        progressIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem menuItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnSuggestionListener(this);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        if (id == SEARCH_RESULT_LOADER) {
            Uri baseContentUri = RecipeContract.BASE_CONTENT_URI;
            Uri.Builder builder = baseContentUri.buildUpon()
                    .appendPath(RecipeContract.PATH_SEARCH_RESULT);
//                    .appendQueryParameter("query", query);
//            ContentUris.appendId(builder, query);
            searchResultLoader = new CursorLoader(this, builder.build(), null, null, null, null);
            return searchResultLoader;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader == searchResultLoader) {
            searchResultAdapter.swapCursor(data);

            // TODO: Save/restore list view state
//            if (restoreListViewState != null) {
//                searchResult.onRestoreInstanceState(restoreListViewState);
//                restoreListViewState = null;
//            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader == searchResultLoader) {
            searchResultAdapter.swapCursor(null);
        }
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return onSuggestion(position);
    }

    @Override
    public boolean onSuggestionClick(int position) {
        return onSuggestion(position);
    }

    private boolean onSuggestion(int position) {
        Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
        String suggestionText = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
        searchView.setQuery(suggestionText, true);
        return true;
    }
}
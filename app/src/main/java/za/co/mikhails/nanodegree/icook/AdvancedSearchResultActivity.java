package za.co.mikhails.nanodegree.icook;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import za.co.mikhails.nanodegree.icook.provider.RecipeContract;
import za.co.mikhails.nanodegree.icook.spoonacular.SyncAdapter;

public class AdvancedSearchResultActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = AdvancedSearchResultActivity.class.getSimpleName();
    public static final String VALUES_BUNDLE = "values_bundle";
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
        setContentView(R.layout.search_result);

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

        progressIndicator = findViewById(R.id.progress_indicator);
        searchResult.setEmptyView(progressIndicator);


        getLoaderManager().restartLoader(SEARCH_RESULT_LOADER, null, this);

        Bundle valuesBundle = getIntent().getBundleExtra(VALUES_BUNDLE);
        SyncAdapter.syncRecipeListImmediately(this, valuesBundle);

//        Bundle bundle = new Bundle();
//        bundle.putString(SEARCH_QUERY, query);
        getLoaderManager().restartLoader(SEARCH_RESULT_LOADER, null, this);

        //TODO: show progress bar
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
}

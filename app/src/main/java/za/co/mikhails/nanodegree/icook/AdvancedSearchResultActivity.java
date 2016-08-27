package za.co.mikhails.nanodegree.icook;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import za.co.mikhails.nanodegree.icook.data.RecipeContract;
import za.co.mikhails.nanodegree.icook.spoonacular.SyncAdapter;

public class AdvancedSearchResultActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    public static final String VALUES_BUNDLE = "values_bundle";
    private static final String TAG = AdvancedSearchResultActivity.class.getSimpleName();
    private static final int SEARCH_RESULT_LOADER = 1;
    private SearchResultAdapter searchResultAdapter;
    private CursorLoader searchResultLoader;
    private boolean twoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

        ListView searchResult = (ListView) findViewById(R.id.list_view);
        if (searchResult != null) {
            searchResultAdapter = new SearchResultAdapter(this, null, 0);
            searchResult.setAdapter(searchResultAdapter);
            searchResult.setOnItemSelectedListener(this);
            searchResult.setOnItemClickListener(this);
            searchResult.setEmptyView(findViewById(R.id.empty_view));
        }

        AdView mAdView = (AdView) findViewById(R.id.ad_banner);
        if (mAdView != null) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
            mAdView.loadAd(adRequest);
        }

        getLoaderManager().restartLoader(SEARCH_RESULT_LOADER, null, this);

        Bundle valuesBundle = getIntent().getBundleExtra(VALUES_BUNDLE);
        SyncAdapter.syncRecipeListImmediately(this, valuesBundle);

        getLoaderManager().restartLoader(SEARCH_RESULT_LOADER, null, this);

        if (findViewById(R.id.recipe_detail_container) != null) {
            twoPane = true;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        if (id == SEARCH_RESULT_LOADER) {
            Uri baseContentUri = RecipeContract.BASE_CONTENT_URI;
            Uri.Builder builder = baseContentUri.buildUpon()
                    .appendPath(RecipeContract.PATH_SEARCH_RESULT);
            searchResultLoader = new CursorLoader(this, builder.build(), null, null, null, null);
            return searchResultLoader;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader == searchResultLoader) {
            searchResultAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader == searchResultLoader) {
            searchResultAdapter.swapCursor(null);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        onItemClick(parent, view, position, id);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        long recipeId = (Long) view.getTag(R.id.RECIPE_ID);
        Log.d(TAG, "onItemClick: " + recipeId);

        if (twoPane) {
            RecipeDetailsFragment fragment = RecipeDetailsFragment.newInstance(recipeId, this.getClass().getSimpleName(), false);
            getSupportFragmentManager().beginTransaction().replace(R.id.recipe_detail_container, fragment).commit();
        } else {
            Intent intent = new Intent(this, RecipeDetailsActivity.class);
            intent.putExtra(RecipeDetailsActivity.RECIPE_ID, recipeId);
            intent.putExtra(RecipeDetailsActivity.NAVIGATE_BACK, this.getClass().getSimpleName());
            startActivity(intent);
        }
    }
}

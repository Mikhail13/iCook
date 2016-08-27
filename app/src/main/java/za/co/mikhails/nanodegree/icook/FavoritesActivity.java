package za.co.mikhails.nanodegree.icook;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import za.co.mikhails.nanodegree.icook.data.RecipeContract;

public class FavoritesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    private static final String TAG = FavoritesActivity.class.getSimpleName();
    private SearchResultAdapter favoritesAdapter;
    private CursorLoader favoritesLoader;
    private boolean twoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

        ListView favoritesListView = (ListView) findViewById(R.id.list_view);
        if (favoritesListView != null) {
            favoritesAdapter = new SearchResultAdapter(this, null, 0);
            favoritesListView.setAdapter(favoritesAdapter);
            favoritesListView.setOnItemSelectedListener(this);
            favoritesListView.setOnItemClickListener(this);
            favoritesListView.setEmptyView(findViewById(R.id.empty_view));
        }

        ImageView emptyImage = (ImageView) findViewById(R.id.empty_image);
        if (emptyImage != null) {
            emptyImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_star_black));
            emptyImage.setAlpha(0.3f);
        }
        TextView emptyText = (TextView) findViewById(R.id.empty_text);
        if (emptyText != null) {
            emptyText.setText(getResources().getString(R.string.search_result_favorites_empty_text));
        }

        AdView mAdView = (AdView) findViewById(R.id.ad_banner);
        if (mAdView != null) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
            mAdView.loadAd(adRequest);
        }

        getLoaderManager().restartLoader(0, null, this);

        if (findViewById(R.id.recipe_detail_container) != null) {
            twoPane = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.favorites_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear_favorites:
                clearFavorites();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void clearFavorites() {
        getContentResolver().delete(RecipeContract.FavoritesEntry.CONTENT_URI, null, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        favoritesLoader = new CursorLoader(this, RecipeContract.FavoritesEntry.buildUri(), null, null, null, null);
        return favoritesLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader == favoritesLoader) {
            favoritesAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader == favoritesLoader) {
            favoritesAdapter.swapCursor(null);
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
            RecipeDetailsFragment fragment = RecipeDetailsFragment.newInstance(recipeId, this.getClass().getSimpleName(), true);
            getSupportFragmentManager().beginTransaction().replace(R.id.recipe_detail_container, fragment).commit();
        } else {
            Intent intent = new Intent(this, RecipeDetailsActivity.class);
            intent.putExtra(RecipeDetailsActivity.RECIPE_ID, recipeId);
            intent.putExtra(RecipeDetailsActivity.NAVIGATE_BACK, this.getClass().getSimpleName());
            intent.putExtra(RecipeDetailsActivity.IS_FAVORITE, true);
            startActivity(intent);
        }
    }
}

package za.co.mikhails.nanodegree.icook;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import za.co.mikhails.nanodegree.icook.data.SearchResultLoader;
import za.co.mikhails.nanodegree.icook.spoonacular.SyncAdapter;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        SearchView.OnSuggestionListener, NavigationView.OnNavigationItemSelectedListener,
        AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

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
        setContentView(R.layout.activity_quick_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        searchResult = (ListView) findViewById(R.id.search_result);
        searchResultAdapter = new SearchResultAdapter(this, null, 0);
        searchResult.setAdapter(searchResultAdapter);
        searchResult.setOnItemSelectedListener(this);
        searchResult.setOnItemClickListener(this);
        searchResult.setOnScrollListener(this);
        searchResult.setEmptyView(findViewById(R.id.empty_view));

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
        getLoaderManager().restartLoader(SEARCH_RESULT_LOADER, null, this);
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
            searchResultLoader = SearchResultLoader.newLoaderInstance(this);
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_advanced_search) {
            startActivity(new Intent(this, AdvancedSearchActivity.class));
        } else if (id == R.id.nav_faforites) {
            startActivity(new Intent(this, FavoritesActivity.class));
        } else if (id == R.id.nav_shopping_list) {
            startActivity(new Intent(this, ShoppingListActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

        Intent intent = new Intent(this, RecipeDetailsActivity.class);
        intent.putExtra(RecipeDetailsActivity.RECIPE_ID, recipeId);
        intent.putExtra(RecipeDetailsActivity.NAVIGATE_BACK, this.getClass().getSimpleName());
        startActivity(intent);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (totalItemCount - visibleItemCount < firstVisibleItem + visibleItemCount) {
            SyncAdapter.loadNextPageOnScroll(this);
        }
    }
}

package za.co.mikhails.nanodegree.icook;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.List;

import za.co.mikhails.nanodegree.icook.spoonacular.AutocompleteItem;
import za.co.mikhails.nanodegree.icook.spoonacular.RestApi;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private View progressIndicator;
    private ListView searchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchQuery(query);
        }

        progressIndicator = findViewById(R.id.progress_indicator);
        searchResult = (ListView) findViewById(R.id.search_result);

        AdView mAdView = (AdView) findViewById(R.id.ad_banner);
        if (mAdView != null) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
            mAdView.loadAd(adRequest);
        }
    }

    private void searchFilter(String query) {
        Log.d(TAG, "searchFilter: [" + query + "]");

        if (query != null && query.trim().length() > 2) {
            List<AutocompleteItem> autocompleteItemList = new RestApi().getAutocompleteList(this, query);
            for (AutocompleteItem item : autocompleteItemList) {
                Log.d(TAG, "autocompleteItem: [" + item.getName() + "," + item.getImage() + "]");
            }
        }
    }

    private void searchQuery(String query) {
        Log.d(TAG, "searchQuery: [" + query + "]");
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
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                searchQuery(query);
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String query) {
//                searchFilter(query);
//                return true;
//            }
//        });

        return true;
    }
}

package za.co.mikhails.nanodegree.icook;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
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

import za.co.mikhails.nanodegree.icook.data.RecipeContract.ShoppingListEntry;

public class ShoppingListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    private static final String TAG = ShoppingListActivity.class.getSimpleName();
    private ListView shoppingListView;
    private ShoppingListAdapter shoppingListAdapter;
    private CursorLoader shoppingListLoader;
    private Parcelable restoreListViewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_list);

        shoppingListView = (ListView) findViewById(R.id.list_view);
        shoppingListAdapter = new ShoppingListAdapter(this, null, 0);
        shoppingListView.setAdapter(shoppingListAdapter);
        shoppingListView.setOnItemSelectedListener(this);
        shoppingListView.setOnItemClickListener(this);
        shoppingListView.setEmptyView(findViewById(R.id.empty_view));

        ImageView emptyImage = (ImageView) findViewById(R.id.empty_image);
        emptyImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_shopping_cart_black));
        emptyImage.setAlpha(0.3f);
        TextView emptyText = (TextView) findViewById(R.id.empty_text);
        emptyText.setText(getResources().getString(R.string.search_result_shopping_list_empty_text));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment = new IngredientInputDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "ingredient");
            }
        });

        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.shopping_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear_shopping_complete:
                clearComplete();
                return true;
            case R.id.action_clear_shopping:
                clearAll();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void clearComplete() {
        String where = ShoppingListEntry.COLUMN_CHECKED + "=1";
        getContentResolver().delete(ShoppingListEntry.CONTENT_URI, where, null);
    }

    private void clearAll() {
        getContentResolver().delete(ShoppingListEntry.CONTENT_URI, null, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        shoppingListLoader = new CursorLoader(this, ShoppingListEntry.CONTENT_URI, null, null, null, null);
        return shoppingListLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader == shoppingListLoader) {

            shoppingListAdapter.swapCursor(data);

            // TODO: Save/restore list view state
//            if (restoreListViewState != null) {
//                shoppingListView.onRestoreInstanceState(restoreListViewState);
//                restoreListViewState = null;
//            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader == shoppingListLoader) {
            shoppingListAdapter.swapCursor(null);
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

        long itemId = (Long) view.getTag(R.id.INGREDIENT_ID);
        Log.d(TAG, "onItemClick: " + itemId);

        boolean checked = (Boolean) view.getTag(R.id.CHECKED);
        String where = ShoppingListEntry.COLUMN_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(itemId)};
        ContentValues contentValues = new ContentValues();
        contentValues.put(ShoppingListEntry.COLUMN_CHECKED, checked ? 0 : 1);
        getContentResolver().update(ShoppingListEntry.CONTENT_URI, contentValues, where, selectionArgs);
    }
}

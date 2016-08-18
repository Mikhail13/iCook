package za.co.mikhails.nanodegree.icook;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import za.co.mikhails.nanodegree.icook.provider.IngredientsListLoader;

public class TabIngredientsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String RECIPE_ID = RecipeDetailsActivity.RECIPE_ID;
    private long recipeId = -1;
    private CursorAdapter cursorAdapter;

    public static Fragment newInstance(long recipeId) {
        Bundle arguments = new Bundle();
        arguments.putLong(RECIPE_ID, recipeId);
        TabIngredientsFragment fragment = new TabIngredientsFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.tab_ingredients_fragment, container, false);

        if (getArguments().containsKey(RECIPE_ID)) {
            recipeId = getArguments().getLong(RECIPE_ID);
        } else if (savedInstanceState != null && savedInstanceState.containsKey(RECIPE_ID)) {
            recipeId = savedInstanceState.getLong(RECIPE_ID);
        }

        cursorAdapter = new IngredientsListAdapter(getContext(), null, 0);
        setListAdapter(cursorAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null && recipeId != -1) {
            outState.putLong(RECIPE_ID, recipeId);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return IngredientsListLoader.newInstanceForRecipeId(getContext(), recipeId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        cursorAdapter.swapCursor(null);
    }
}


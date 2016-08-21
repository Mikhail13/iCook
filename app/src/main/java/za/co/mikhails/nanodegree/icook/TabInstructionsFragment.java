package za.co.mikhails.nanodegree.icook;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import za.co.mikhails.nanodegree.icook.data.InstructionsListLoader;
import za.co.mikhails.nanodegree.icook.spoonacular.SyncAdapter;

public class TabInstructionsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String RECIPE_ID = RecipeDetailsActivity.RECIPE_ID;
    private long recipeId = -1;
    private InstructionsListAdapter cursorAdapter;

    public static Fragment newInstance(long recipeId) {
        Bundle arguments = new Bundle();
        arguments.putLong(RECIPE_ID, recipeId);
        TabInstructionsFragment fragment = new TabInstructionsFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.tab_instructions_fragment, container, false);

        if (getArguments().containsKey(RECIPE_ID)) {
            recipeId = getArguments().getLong(RECIPE_ID);
        } else if (savedInstanceState != null && savedInstanceState.containsKey(RECIPE_ID)) {
            recipeId = savedInstanceState.getLong(RECIPE_ID);
        }

        cursorAdapter = new InstructionsListAdapter(getContext(), null, 0);
        setListAdapter(cursorAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SyncAdapter.syncRecipeInstructionsImmediately(getContext(), recipeId);
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
        return InstructionsListLoader.newInstanceForRecipeId(getContext(), recipeId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor data) {
        Map<Integer, String> namePositions = new HashMap<>();
        String previous = "";
        data.moveToPosition(-1);

        while (data.moveToNext()) {
            String nameValue = data.getString(InstructionsListLoader.Query.NAME);

            if (!previous.equals(nameValue)) {
                namePositions.put(data.getPosition(), nameValue);
                previous = nameValue;
            }
        }
        data.moveToPosition(-1);
        cursorAdapter.setNamePositions(namePositions);

        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        cursorAdapter.swapCursor(null);
    }
}


package za.co.mikhails.nanodegree.icook;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.MessageFormat;

import za.co.mikhails.nanodegree.icook.data.IngredientsListLoader;
import za.co.mikhails.nanodegree.icook.data.RecipeContract;
import za.co.mikhails.nanodegree.icook.data.RecipeContract.FavoritesEntry;
import za.co.mikhails.nanodegree.icook.data.RecipeContract.SearchResultEntry;
import za.co.mikhails.nanodegree.icook.data.RecipeContract.ShoppingListEntry;
import za.co.mikhails.nanodegree.icook.data.RecipeDetailsLoader;
import za.co.mikhails.nanodegree.icook.data.SearchResultLoader;
import za.co.mikhails.nanodegree.icook.spoonacular.SyncAdapter;

public class RecipeDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ADD_TO_FAVORITES = "add_to_favorites";
    private static final String ADD_TO_SHOPPING_LIST = "add_to_shopping_list";

    private static final String SAVED_RECIPE_ID = "saved_recipe_id";
    private static final String SAVED_IS_FAVORITE = "saved_is_favorite";
    private static final String SAVED_NAVIGATE_BACK = "saved_navigate_back";
    private static final String SAVED_SELECTED_TAB = "saved_selected_tab";

    private long recipeId;
    private boolean isFavorite;
    private String navigateBack;
    private CollapsingToolbarLayout toolbarLayout;
    private ImageView imageView;
    private TabSummaryFragment tabSummaryFragment;
    private Fragment tabIngredientsFragment;
    private Fragment tabInstructionsFragment;
    private FloatingActionButton fab;
    private TabLayout tabLayout;
    private int selectedTab = 0;
    private TabLayout.OnTabSelectedListener onTabSelectedListener;
    private String titleText;
    private String summaryText;
    private String toolbarImageUrl;

    public static RecipeDetailsFragment newInstance(long recipeId, String navigateBack, boolean isFavorite) {
        Bundle arguments = new Bundle();
        arguments.putLong(RecipeDetailsActivity.RECIPE_ID, recipeId);
        arguments.putString(RecipeDetailsActivity.NAVIGATE_BACK, navigateBack);
        arguments.putBoolean(RecipeDetailsActivity.IS_FAVORITE, isFavorite);
        RecipeDetailsFragment fragment = new RecipeDetailsFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(RecipeDetailsActivity.RECIPE_ID)) {
            recipeId = getArguments().getLong(RecipeDetailsActivity.RECIPE_ID);
            navigateBack = getArguments().getString(RecipeDetailsActivity.NAVIGATE_BACK);
            isFavorite = getArguments().getBoolean(RecipeDetailsActivity.IS_FAVORITE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_details, container, false);

        if (savedInstanceState != null) {
            recipeId = savedInstanceState.getLong(SAVED_RECIPE_ID);
            isFavorite = savedInstanceState.getBoolean(SAVED_IS_FAVORITE);
            navigateBack = savedInstanceState.getString(SAVED_NAVIGATE_BACK);
        }

        final AppCompatActivity activity = (AppCompatActivity) getActivity();

        final ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
        tabSummaryFragment = new TabSummaryFragment();
        tabIngredientsFragment = TabIngredientsFragment.newInstance(recipeId);
        tabInstructionsFragment = TabInstructionsFragment.newInstance(recipeId);
        FragmentStatePagerAdapter pagerAdapter = new FragmentStatePagerAdapter(getFragmentManager()) {
            private String[] titles = new String[]{"Summary", "Ingredients", "Instructions"};

            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }

            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return tabSummaryFragment;
                    case 1:
                        return tabIngredientsFragment;
                    case 2:
                        return tabInstructionsFragment;
                }
                return null;
            }

            @Override
            public int getCount() {
                return titles.length;
            }
        };
        viewPager.setAdapter(pagerAdapter);
        tabLayout = (TabLayout) activity.findViewById(R.id.tab_layout);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
            onTabSelectedListener = new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {

                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    super.onTabSelected(tab);

                    selectedTab = tab.getPosition();
                    switch (selectedTab) {
                        case 0:
                            if (isFavorite) {
                                fab.setVisibility(View.GONE);
                            } else {
                                fab.setVisibility(View.VISIBLE);
                                fab.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_star_white));
                                fab.setTag(ADD_TO_FAVORITES);
                            }
                            break;
                        case 1:
                            fab.setVisibility(View.VISIBLE);
                            fab.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_shopping_cart_white));
                            fab.setTag(ADD_TO_SHOPPING_LIST);
                            break;
                        case 2:
                            fab.setVisibility(View.GONE);
                            break;
                    }
                }
            };
            tabLayout.addOnTabSelectedListener(onTabSelectedListener);
        }

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view != null && view.getTag() != null && view.getTag() instanceof String) {
                    String title;
                    String toastText = null;
                    switch ((String) view.getTag()) {
                        case ADD_TO_FAVORITES:
                            title = copyRecipeToFavorites(recipeId);
                            if (title != null) {
                                toastText = MessageFormat.format(getString(R.string.toast_add_to_favorites), title);
                            }
                            break;
                        case ADD_TO_SHOPPING_LIST:
                            if (copyIngredientsToList(recipeId)) {
                                toastText = getString(R.string.toast_add_to_shopping_list);
                            }
                            break;
                    }
                    if (toastText != null) {
                        Toast.makeText(getContext(), toastText, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        toolbarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        imageView = (ImageView) activity.findViewById(R.id.toolbar_image);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putLong(SAVED_RECIPE_ID, recipeId);
        savedInstanceState.putBoolean(SAVED_IS_FAVORITE, isFavorite);
        savedInstanceState.putString(SAVED_NAVIGATE_BACK, navigateBack);
        savedInstanceState.putInt(SAVED_SELECTED_TAB, selectedTab);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            selectedTab = savedInstanceState.getInt(SAVED_SELECTED_TAB, 0);
        }
        if (onTabSelectedListener != null && tabLayout != null) {
            onTabSelectedListener.onTabSelected(tabLayout.getTabAt(selectedTab));
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SyncAdapter.syncRecipeDetailsImmediately(getContext(), recipeId);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.recipe_details_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_google_plus:

                return true;
            case R.id.share_recipe:
                if (titleText != null && summaryText != null) {
                    shareRecipe(titleText, summaryText);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareRecipe(String recipeTitle, String recipeSummary) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, recipeTitle);
        share.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(recipeSummary));
        startActivity(Intent.createChooser(share, "Share trailer"));
    }

    private void shareGooglePlus(String recipeTitle, String recipeSummary, String imageUrl) {
//        PlusShare.Builder share = new PlusShare.Builder(this);
//        share.setText("hello everyone!");
//        share.addStream(selectedImage);
//        share.setType(mime);
//        startActivityForResult(share.getIntent(), REQ_START_SHARE);
//
//        Picasso.with(getContext()).load(imageUrl).get()
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return RecipeDetailsLoader.newInstanceForRecipeId(getContext(), recipeId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (isAdded() && cursor != null && cursor.moveToFirst()) {

            if (toolbarLayout != null) {
                titleText = cursor.getString(RecipeDetailsLoader.Query.TITLE);
                toolbarLayout.setTitle(titleText);
            }

            summaryText = cursor.getString(RecipeDetailsLoader.Query.DESCRIPTION);
            tabSummaryFragment.setText(summaryText);

            if (tabIngredientsFragment.isAdded()) {
                tabIngredientsFragment.getLoaderManager().getLoader(0).forceLoad();
            }

            if (imageView != null) {
                toolbarImageUrl = cursor.getString(RecipeDetailsLoader.Query.IMAGE_URL);
                if (toolbarImageUrl != null && toolbarImageUrl.length() > 0) {
                    Picasso.with(getContext()).load(toolbarImageUrl).into(imageView);
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

    private String copyRecipeToFavorites(long recipeId) {
        String title = null;

        String selection = SearchResultEntry.COLUMN_ID + "=?";
        String[] selectionArgs = {String.valueOf(recipeId)};

        Cursor cursor = getContext().getContentResolver().query(
                SearchResultEntry.CONTENT_URI, SearchResultLoader.Query.PROJECTION, selection, selectionArgs, null);
        if (cursor != null && cursor.moveToFirst()) {
            ContentValues contentValues = new ContentValues();
            DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
            contentValues.remove(SearchResultEntry.COLUMN_KEY);
            title = contentValues.getAsString(SearchResultEntry.COLUMN_TITLE);
            getContext().getContentResolver().insert(FavoritesEntry.CONTENT_URI, contentValues);
        }
        return title;
    }

    private boolean copyIngredientsToList(long recipeId) {
        boolean result = false;

        String selection = SearchResultEntry.COLUMN_ID + "=?";
        String[] selectionArgs = {String.valueOf(recipeId)};

        Cursor cursor = getContext().getContentResolver().query(
                RecipeContract.IngredientEntry.CONTENT_URI, IngredientsListLoader.Query.PROJECTION,
                selection, selectionArgs, null);
        if (cursor != null) {
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                ContentValues contentValues = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
                contentValues.remove(RecipeContract.IngredientEntry.COLUMN_RECIPE_ID);
                getContext().getContentResolver().insert(ShoppingListEntry.CONTENT_URI, contentValues);
                result = true;
            }
        }
        return result;
    }

}

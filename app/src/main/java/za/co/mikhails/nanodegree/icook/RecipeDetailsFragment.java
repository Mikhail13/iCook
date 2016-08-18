package za.co.mikhails.nanodegree.icook;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import za.co.mikhails.nanodegree.icook.provider.RecipeDetailsLoader;
import za.co.mikhails.nanodegree.icook.spoonacular.SyncAdapter;

public class RecipeDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = RecipeDetailsFragment.class.getSimpleName();
    private long recipeId;
    private View rootView;
    private ActionBar supportActionBar;
    private CollapsingToolbarLayout toolbarLayout;
    private ImageView imageView;
    private TabSummaryFragment tabSummaryFragment;
    private Fragment tabIngredientsFragment;

    public static Fragment newInstance(long recipeId) {
        Bundle arguments = new Bundle();
        arguments.putLong(RecipeDetailsActivity.RECIPE_ID, recipeId);
        RecipeDetailsFragment fragment = new RecipeDetailsFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(RecipeDetailsActivity.RECIPE_ID)) {
            recipeId = getArguments().getLong(RecipeDetailsActivity.RECIPE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_recipe_details, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        supportActionBar = activity.getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavUtils.navigateUpTo(activity, new Intent(getContext(), MainActivity.class));
            }
        });
        final ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
        tabSummaryFragment = new TabSummaryFragment();
        tabIngredientsFragment = TabIngredientsFragment.newInstance(recipeId);
        FragmentStatePagerAdapter pagerAdapter = new FragmentStatePagerAdapter(getFragmentManager()) {
            private String[] titles = new String[]{"Summary", "Ingredients", "Instructions", "Nutrition"};

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
                        return new TabSummaryFragment();
                    case 3:
                        return new TabSummaryFragment();
                }
                return null;
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        NestedScrollView nestedScrollView = (NestedScrollView) rootView.findViewById(R.id.nested_scroll_view);
        nestedScrollView.setFillViewport(true);

        toolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.toolbar_layout);

        imageView = (ImageView) rootView.findViewById(R.id.toolbar_image);

        return rootView;
    }

    private void bindViews(Cursor cursor) {

        String titleText = cursor.getString(RecipeDetailsLoader.Query.TITLE);
        String desctiptionText = cursor.getString(RecipeDetailsLoader.Query.DESCRIPTION);
        String toolbarImageUrl = cursor.getString(RecipeDetailsLoader.Query.IMAGE_URL);

        toolbarLayout.setTitle(titleText);

//            TextView description = (TextView) rootView.findViewById(R.id.recipe_description);
//            description.setText(Html.fromHtml(desctiptionText));

        tabSummaryFragment.setText(desctiptionText);
        if (tabIngredientsFragment.isAdded()) {
            tabIngredientsFragment.getLoaderManager().getLoader(0).forceLoad();
        }

        if (toolbarImageUrl != null && toolbarImageUrl.length() > 0) {
            Picasso.with(getContext()).load(toolbarImageUrl).into(imageView);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SyncAdapter.syncRecipeDetailsImmediately(getContext(), recipeId);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return RecipeDetailsLoader.newInstanceForRecipeId(getContext(), recipeId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (isAdded() && cursor != null && cursor.moveToFirst()) {
            bindViews(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }
}

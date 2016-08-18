package za.co.mikhails.nanodegree.icook;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import za.co.mikhails.nanodegree.icook.provider.RecipeDetailsLoader;
import za.co.mikhails.nanodegree.icook.spoonacular.SyncAdapter;

public class RecipeDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String RECIPE_ID = "recipe_id";
    private long recipeId;
    private ActionBar supportActionBar;
    private CollapsingToolbarLayout toolbarLayout;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        long recipeId = getIntent().getLongExtra(RECIPE_ID, -1);
//        fragmentTransaction.add(R.id.fragment_container, RecipeDetailsFragment.newInstance(recipeId));
//        fragmentTransaction.commit();
//
//        View view = findViewById(android.R.id.content);
//        view.setAlpha(0);
//        view.animate().alpha(1);

        recipeId = getIntent().getLongExtra(RECIPE_ID, -1);
        onCreateView(getLayoutInflater());

        SyncAdapter.syncRecipeDetailsImmediately(this, recipeId);
        getSupportLoaderManager().initLoader(0, null, this);

    }

    public void onCreateView(LayoutInflater inflater) {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        AppCompatActivity activity = this;
        activity.setSupportActionBar(toolbar);
        supportActionBar = activity.getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavUtils.navigateUpTo(RecipeDetailsActivity.this, new Intent(RecipeDetailsActivity.this, MainActivity.class));
            }
        });
        final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new RecipeDetailsPagerAdapter(this));
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        showToast("One");
                        break;
                    case 1:
                        showToast("Two");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.nested_scroll_view);
        nestedScrollView.setFillViewport(true);

        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        imageView = (ImageView) findViewById(R.id.toolbar_image);

//        rootView.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
//                        .setType("text/plain")
//                        .setText("Some sample text")
//                        .getIntent(), getString(R.string.action_share)));
//            }
//        });

    }

    private void showToast(String text) {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void bindViews(Cursor cursor) {

        String titleText = cursor.getString(RecipeDetailsLoader.Query.TITLE);
        String desctiptionText = cursor.getString(RecipeDetailsLoader.Query.DESCRIPTION);
        String toolbarImageUrl = cursor.getString(RecipeDetailsLoader.Query.IMAGE_URL);

        toolbarLayout.setTitle(titleText);

//            TextView description = (TextView) findViewById(R.id.recipe_description);
//            description.setText(Html.fromHtml(desctiptionText));

        if (toolbarImageUrl != null && toolbarImageUrl.length() > 0) {
            Picasso.with(this).load(toolbarImageUrl).into(imageView);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return RecipeDetailsLoader.newInstanceForItemId(this, recipeId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            bindViews(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }
}

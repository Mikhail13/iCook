package za.co.mikhails.nanodegree.icook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class RecipeDetailsActivity extends AppCompatActivity {

    public static final String RECIPE_ID = "recipe_id";
    public static final String NAVIGATE_BACK = "navigate_back";
    public static final String IS_FAVORITE = "is_favorite";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        long recipeId = getIntent().getLongExtra(RECIPE_ID, -1);
        final String navigateBack = getIntent().getStringExtra(NAVIGATE_BACK);
        boolean isFavorite = getIntent().getBooleanExtra(IS_FAVORITE, false);

        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, RecipeDetailsFragment.newInstance(recipeId, navigateBack, isFavorite));
            fragmentTransaction.commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Class parenClass = navigateBack.equals(RecipeListActivity.class.getSimpleName()) ? RecipeListActivity.class : AdvancedSearchResultActivity.class;
                NavUtils.navigateUpTo(RecipeDetailsActivity.this, new Intent(RecipeDetailsActivity.this, parenClass));
            }
        });

        NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.nested_scroll_view);
        nestedScrollView.setFillViewport(true);

        View view = findViewById(android.R.id.content);
        view.setAlpha(0);
        view.animate().alpha(1);
    }
}

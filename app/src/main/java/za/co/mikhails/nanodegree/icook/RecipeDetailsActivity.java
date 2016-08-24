package za.co.mikhails.nanodegree.icook;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class RecipeDetailsActivity extends AppCompatActivity {

    public static final String RECIPE_ID = "recipe_id";
    public static final String NAVIGATE_BACK = "navigate_back";
    public static final String IS_FAVORITE = "is_favorite";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            long recipeId = getIntent().getLongExtra(RECIPE_ID, -1);
            String navigateBack = getIntent().getStringExtra(NAVIGATE_BACK);
            boolean isFavorite = getIntent().getBooleanExtra(IS_FAVORITE, false);
            fragmentTransaction.add(R.id.fragment_container, RecipeDetailsFragment.newInstance(recipeId, navigateBack, isFavorite));
            fragmentTransaction.commit();
        }

        View view = findViewById(android.R.id.content);
        view.setAlpha(0);
        view.animate().alpha(1);
    }
}

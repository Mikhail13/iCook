package za.co.mikhails.nanodegree.icook;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public class RecipeDetailsActivity extends AppCompatActivity {

    public static final String RECIPE_ID = "recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        String recipeId = getIntent().getStringExtra(RECIPE_ID);
        fragmentTransaction.add(R.id.fragment_container, RecipeDetailsFragment.newInstance(recipeId));
        fragmentTransaction.commit();
    }
}

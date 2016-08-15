package za.co.mikhails.nanodegree.icook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import za.co.mikhails.nanodegree.icook.spoonacular.SyncAdapter;

public class AdvancedSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search);

        setupSingleSelectionEditText(R.id.meal_type, R.array.meal_type_array);
        setupSingleSelectionEditText(R.id.cuisine, R.array.cuisine_array);
        setupSingleSelectionEditText(R.id.diet, R.array.diet_array);
        setupSingleSelectionEditText(R.id.intolerances, R.array.intolerances_array);

        //TODO: multiple choice

    }

    private void setupSingleSelectionEditText(int editTextResId, int arrayResId) {
        final EditText editText = (EditText) findViewById(editTextResId);
        final ListPopupWindow listPopupWindow = new ListPopupWindow(this);
        final String[] stringArray = getResources().getStringArray(arrayResId);
        listPopupWindow.setAdapter(new ArrayAdapter(this, R.layout.list_item_popup_hint, stringArray));
        listPopupWindow.setAnchorView(editText);

        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editText.setText("");
                editText.append(stringArray[position]);
                listPopupWindow.dismiss();
            }
        });
        editText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                listPopupWindow.show();
            }
        });

    }

    private void setupSpinner(int spinnerResId, int arrayResId) {
        Spinner spinner;
        spinner = (Spinner) findViewById(spinnerResId);
        if (spinner != null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    arrayResId, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }
    }

    public void searchSubmit(View view) {
        Bundle valuesBundle = new Bundle();
        saveInputValues(valuesBundle);

        Intent intent = new Intent(this, AdvancedSearchResultActivity.class);
        intent.putExtra(AdvancedSearchResultActivity.VALUES_BUNDLE, valuesBundle);
        startActivity(intent);
    }

    private void saveInputValues(Bundle valuesBundle) {
        addBundleValue(valuesBundle, SyncAdapter.PARAM_TYPE, R.id.meal_type);
        addBundleValue(valuesBundle, SyncAdapter.PARAM_CUISINE, R.id.cuisine);
        addBundleValue(valuesBundle, SyncAdapter.PARAM_DIET, R.id.diet);
        addBundleValue(valuesBundle, SyncAdapter.PARAM_INTOLERANCES, R.id.intolerances);
        addBundleValue(valuesBundle, SyncAdapter.PARAM_MIN_CALORIES, R.id.min_calories);
        addBundleValue(valuesBundle, SyncAdapter.PARAM_MAX_CALORIES, R.id.max_calories);
        addBundleValue(valuesBundle, SyncAdapter.PARAM_MIN_CARBS, R.id.min_carbs);
        addBundleValue(valuesBundle, SyncAdapter.PARAM_MAX_CARBS, R.id.max_carbs);
        addBundleValue(valuesBundle, SyncAdapter.PARAM_MIN_FAT, R.id.min_fat);
        addBundleValue(valuesBundle, SyncAdapter.PARAM_MAX_FAT, R.id.max_fat);
        addBundleValue(valuesBundle, SyncAdapter.PARAM_MIN_PROTEIN, R.id.min_protein);
        addBundleValue(valuesBundle, SyncAdapter.PARAM_MAX_PROTEIN, R.id.max_protein);
        addBundleValue(valuesBundle, SyncAdapter.PARAM_INCLUDE_INGREDIENTS, R.id.include_ingredients);
        addBundleValue(valuesBundle, SyncAdapter.PARAM_EXCLUDE_INGREDIENTS, R.id.exclude_ingredients);
    }

    private void addBundleValue(Bundle bundle, String paramKey, int resId) {
        TextView textView = (TextView) findViewById(resId);
        String value = textView.getText().toString().trim();
        if (value.length() > 0) {
            bundle.putString(paramKey, value);
        }
    }
}

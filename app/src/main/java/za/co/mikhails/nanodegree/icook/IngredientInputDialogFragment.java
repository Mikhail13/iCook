package za.co.mikhails.nanodegree.icook;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.ResourceCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import za.co.mikhails.nanodegree.icook.data.RecipeContract.ShoppingListEntry;

import static za.co.mikhails.nanodegree.icook.data.AutocompleteContentProvider.INGREDIENTS_CONTENT_URI;

public class IngredientInputDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    private AutoCompleteTextView autoCompleteTextView;
    private String selectedId;
    private String selectedName;
    private String selectedImage;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setTitle(R.string.dialog_ingredient_input_title);
        View view = inflater.inflate(R.layout.dialog_ingredient_input, null);
        autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.ingredient_input);
        autoCompleteTextView.setOnItemSelectedListener(this);
        autoCompleteTextView.setOnItemClickListener(this);
        autoCompleteTextView.setAdapter(new ResourceCursorAdapter(getContext(), R.layout.list_item_popup_hint, null, 0) {

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                String id = cursor.getString(0);
                String name = cursor.getString(1);
                String image = cursor.getString(2);

                view.setTag(R.id.INGREDIENT_ID, id);
                view.setTag(R.id.INGREDIENT_NAME, name);
                view.setTag(R.id.INGREDIENT_IMAGE, image);

                ((TextView) view).setText(name);
            }

            @Override
            public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
                Cursor cursor = null;
                String query = constraint == null ? "" : constraint.toString();
                if (query.length() > 2) {
                    Uri uri = INGREDIENTS_CONTENT_URI.buildUpon().appendPath(query).build();
                    cursor = getContext().getContentResolver().query(uri, null, null, null, null);
                }
                return cursor;
            }

        });
        builder.setView(view)
                .setPositiveButton(R.string.add_ingredient, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ContentValues contentValues = new ContentValues();
                        String value = autoCompleteTextView.getText().toString();
                        if (selectedName != null && selectedName.equals(value)) {
                            contentValues.put(ShoppingListEntry.COLUMN_ID, selectedId);
                            contentValues.put(ShoppingListEntry.COLUMN_NAME, selectedName);
                            contentValues.put(ShoppingListEntry.COLUMN_IMAGE, selectedImage);
                        } else {
                            contentValues.put(ShoppingListEntry.COLUMN_NAME, value);
                        }
                        getContext().getContentResolver().insert(ShoppingListEntry.CONTENT_URI, contentValues);
                        IngredientInputDialogFragment.this.getDialog().cancel();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        IngredientInputDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
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
        autoCompleteTextView.setText("");
        selectedId = (String) view.getTag(R.id.INGREDIENT_ID);
        selectedName = (String) view.getTag(R.id.INGREDIENT_NAME);
        selectedImage = (String) view.getTag(R.id.INGREDIENT_IMAGE);
        autoCompleteTextView.append(selectedName);
    }
}

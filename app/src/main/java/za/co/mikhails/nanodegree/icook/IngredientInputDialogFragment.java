package za.co.mikhails.nanodegree.icook;

import android.app.Dialog;
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
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

public class IngredientInputDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();


        builder.setTitle(R.string.dialog_ingredient_input_title);
        View view = inflater.inflate(R.layout.dialog_ingredient_input, null);
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.ingredient_input);
        autoCompleteTextView.setAdapter(new ResourceCursorAdapter(getContext(), R.layout.list_item_popup_hint, null, 0) {

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                ((TextView) view).setText(cursor.getString(1));
            }

            @Override
            public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
                String filter = constraint == null ? "" : constraint.toString();
                Uri uri = Uri.parse("content://za.co.mikhails.nanodegree.icook.provider.ingredients/ingredients");
                return getContext().getContentResolver().query(uri.buildUpon().appendPath(filter).build(), null, null, null, null);
            }
        });
        builder.setView(view)
                .setPositiveButton(R.string.add_ingredient, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        IngredientInputDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}

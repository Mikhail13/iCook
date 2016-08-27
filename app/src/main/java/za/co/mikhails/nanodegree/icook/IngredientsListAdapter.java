package za.co.mikhails.nanodegree.icook;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import za.co.mikhails.nanodegree.icook.data.IngredientsListLoader;

class IngredientsListAdapter extends CursorAdapter {
    private static final String TAG = IngredientsListAdapter.class.getSimpleName();
    private static final NumberFormat formatter = new DecimalFormat("#0.00");

    IngredientsListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_ingredient_list, parent, false);
        view.setTag(R.id.LIST_ITEM_IMAGE, view.findViewById(R.id.image));
        view.setTag(R.id.LIST_ITEM_TITLE, view.findViewById(R.id.title));
        view.setTag(R.id.LIST_ITEM_SECONDARY_TEXT, view.findViewById(R.id.secondary_text));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = (ImageView) view.getTag(R.id.LIST_ITEM_IMAGE);
        String imageUrl = cursor.getString(IngredientsListLoader.Query.IMAGE);
        if (imageUrl != null && imageUrl.length() > 0) {
            Picasso.with(context).load(imageUrl).into(imageView);
        }

        TextView textView = (TextView) view.getTag(R.id.LIST_ITEM_TITLE);
        textView.setText(cursor.getString(IngredientsListLoader.Query.NAME));

        textView = (TextView) view.getTag(R.id.LIST_ITEM_SECONDARY_TEXT);
        String amountString = cursor.getString(IngredientsListLoader.Query.AMOUNT);
        try {
            double amount = Double.parseDouble(amountString);
            if (amount == (int) amount) {
                amountString = Integer.toString((int) amount);
            } else {
                amountString = formatter.format(amount);
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "Unable to parse amount: " + amountString, e);
        }
        amountString += " " + cursor.getString(IngredientsListLoader.Query.UNIT);
        textView.setText(amountString);

        long id = cursor.getLong(IngredientsListLoader.Query.ID);
        view.setTag(R.id.INGREDIENT_ID, id);
    }
}

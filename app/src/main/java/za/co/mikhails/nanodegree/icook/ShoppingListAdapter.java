package za.co.mikhails.nanodegree.icook;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import za.co.mikhails.nanodegree.icook.data.ShoppingListLoader;

class ShoppingListAdapter extends CursorAdapter {

    private static final NumberFormat formatter = new DecimalFormat("#0.00");

    ShoppingListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_shopping_list, parent, false);
        view.setTag(R.id.LIST_ITEM_IMAGE, view.findViewById(R.id.image));
        view.setTag(R.id.LIST_ITEM_TITLE, view.findViewById(R.id.title));
        view.setTag(R.id.LIST_ITEM_SECONDARY_TEXT, view.findViewById(R.id.secondary_text));
        view.setTag(R.id.LIST_ITEM_CHECK_BOX, view.findViewById(R.id.check_box));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = (ImageView) view.getTag(R.id.LIST_ITEM_IMAGE);
        String imageUrl = cursor.getString(ShoppingListLoader.Query.IMAGE);
        if (imageUrl != null || imageUrl.length() > 0) {
            Picasso.with(context).load(imageUrl).into(imageView);
        }

        CheckBox checkBox = (CheckBox) view.getTag(R.id.LIST_ITEM_CHECK_BOX);
        boolean checked = cursor.getInt(ShoppingListLoader.Query.CHECKED) == 1;
        checkBox.setChecked(checked);
        view.setTag(R.id.CHECKED, checked);

        TextView textView = (TextView) view.getTag(R.id.LIST_ITEM_TITLE);
        textView.setText(cursor.getString(ShoppingListLoader.Query.NAME));
        if (checked) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        textView = (TextView) view.getTag(R.id.LIST_ITEM_SECONDARY_TEXT);
        String amountString = cursor.getString(ShoppingListLoader.Query.AMOUNT);
        try {
            double amount = Double.parseDouble(amountString);
            if (amount == (int) amount) {
                amountString = Integer.toString((int) amount);
            } else {
                amountString = formatter.format(amount);
            }
        } catch (NumberFormatException e) {
        }
        textView.setText(amountString + " " +
                cursor.getString(ShoppingListLoader.Query.UNIT));
        if (checked) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        long id = cursor.getLong(ShoppingListLoader.Query.ID);
        view.setTag(R.id.INGREDIENT_ID, id);
    }
}

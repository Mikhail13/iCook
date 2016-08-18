package za.co.mikhails.nanodegree.icook;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import za.co.mikhails.nanodegree.icook.provider.IngredientsListLoader;

class IngredientsListAdapter extends CursorAdapter {

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
        if (imageUrl != null || imageUrl.length() > 0) {
            Picasso.with(context).load(imageUrl).into(imageView);
        }

        TextView textView = (TextView) view.getTag(R.id.LIST_ITEM_TITLE);
        textView.setText(cursor.getString(IngredientsListLoader.Query.NAME));

        textView = (TextView) view.getTag(R.id.LIST_ITEM_SECONDARY_TEXT);
        textView.setText(cursor.getString(IngredientsListLoader.Query.AMOUNT) + " " +
                cursor.getString(IngredientsListLoader.Query.UNIT));

        long id = cursor.getLong(IngredientsListLoader.Query.ID);
        view.setTag(R.id.INGREDIENT_ID, id);
    }
}

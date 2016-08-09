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

public class SearchResultAdapter extends CursorAdapter {
    public static final int COLUMN_ID = 1;
    public static final int COLUMN_TITLE = 2;
    public static final int COLUMN_IMAGE = 3;
    public static final int COLUMN_IMAGE_TYPE = 4;
    public static final int COLUMN_IMAGE_BASE_URL = 5;
    public static final int COLUMN_CALORIES = 6;
    public static final int COLUMN_PROTEIN = 7;
    public static final int COLUMN_FAT = 8;
    public static final int COLUMN_CARBS = 9;

    public SearchResultAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public SearchResultAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_result_list_item, parent, false);
        view.setTag(R.id.LIST_ITEM_IMAGE, view.findViewById(R.id.image));
        view.setTag(R.id.LIST_ITEM_TITLE, view.findViewById(R.id.title));
        view.setTag(R.id.LIST_ITEM_CALORIES, view.findViewById(R.id.calories));
        view.setTag(R.id.LIST_ITEM_PROTEIN, view.findViewById(R.id.protein));
        view.setTag(R.id.LIST_ITEM_FAT, view.findViewById(R.id.fat));
        view.setTag(R.id.LIST_ITEM_CARBS, view.findViewById(R.id.carbs));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = (ImageView) view.getTag(R.id.LIST_ITEM_IMAGE);
        String id = cursor.getString(COLUMN_ID);
        String imageType = cursor.getString(COLUMN_IMAGE_TYPE);
        String imageBaseUrl = cursor.getString(COLUMN_IMAGE_BASE_URL);
        if (id != null && imageType != null && imageBaseUrl != null) {
            String url = imageBaseUrl + id + "-240x150." + imageType;
            Picasso.with(context).load(url).into(imageView);
        }

        TextView textView = (TextView) view.getTag(R.id.LIST_ITEM_TITLE);
        textView.setText(cursor.getString(COLUMN_TITLE));

        textView = (TextView) view.getTag(R.id.LIST_ITEM_CALORIES);
        textView.setText(cursor.getString(COLUMN_CALORIES));

        textView = (TextView) view.getTag(R.id.LIST_ITEM_PROTEIN);
        textView.setText(cursor.getString(COLUMN_PROTEIN));

        textView = (TextView) view.getTag(R.id.LIST_ITEM_FAT);
        textView.setText(cursor.getString(COLUMN_FAT));

        textView = (TextView) view.getTag(R.id.LIST_ITEM_CARBS);
        textView.setText(cursor.getString(COLUMN_CARBS));

        view.setTag(R.id.RECIPE_ID, cursor.getInt(COLUMN_ID));
    }

}

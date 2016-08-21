package za.co.mikhails.nanodegree.icook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import za.co.mikhails.nanodegree.icook.data.SearchResultLoader;

class SearchResultAdapter extends CursorAdapter {

    private String imageWidthXHeight;
    private String secondaryText;

    SearchResultAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        init(context);
    }

    @SuppressLint("StringFormatMatches")
    private void init(Context context) {
        secondaryText = context.getString(R.string.list_item_search_result_secondary_text, null);
        imageWidthXHeight = String.valueOf(context.getResources().getDimensionPixelSize(R.dimen.search_result_image_width) + "x" + context.getResources().getDimensionPixelSize(R.dimen.search_result_image_height));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_search_result, parent, false);
        view.setTag(R.id.LIST_ITEM_IMAGE, view.findViewById(R.id.image));
        view.setTag(R.id.LIST_ITEM_TITLE, view.findViewById(R.id.title));
        view.setTag(R.id.LIST_ITEM_SECONDARY_TEXT, view.findViewById(R.id.secondary_text));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = (ImageView) view.getTag(R.id.LIST_ITEM_IMAGE);
        long id = cursor.getLong(SearchResultLoader.Query.ID);
        String imageType = cursor.getString(SearchResultLoader.Query.IMAGE_TYPE);
        String imageBaseUrl = cursor.getString(SearchResultLoader.Query.IMAGE_BASE_URL);
        if (imageBaseUrl == null || imageBaseUrl.length() == 0 ||
                imageType == null || imageType.equals("webp")) {
            String url = context.getString(R.string.search_result_default_image_url) + imageWidthXHeight;
            Picasso.with(context).load(url).into(imageView);
        } else {
            String url = imageBaseUrl + id + "-" + imageWidthXHeight + (imageType.length() > 0 ? "." + imageType : "");
            Picasso.with(context).load(url).into(imageView);
        }

        TextView textView = (TextView) view.getTag(R.id.LIST_ITEM_TITLE);
        textView.setText(cursor.getString(SearchResultLoader.Query.TITLE));

        textView = (TextView) view.getTag(R.id.LIST_ITEM_SECONDARY_TEXT);
        textView.setText(String.format(secondaryText,
                cursor.getString(SearchResultLoader.Query.CALORIES),
                cursor.getString(SearchResultLoader.Query.PROTEIN),
                cursor.getString(SearchResultLoader.Query.FAT),
                cursor.getString(SearchResultLoader.Query.CARBS)));

        view.setTag(R.id.RECIPE_ID, id);
    }

}

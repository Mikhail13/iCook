package za.co.mikhails.nanodegree.icook.widget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import za.co.mikhails.nanodegree.icook.R;
import za.co.mikhails.nanodegree.icook.data.RecipeContract;
import za.co.mikhails.nanodegree.icook.data.ShoppingListLoader;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ShoppingListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private static final String TAG = ShoppingListRemoteViewsFactory.class.getSimpleName();
    private static final NumberFormat formatter = new DecimalFormat("#0.00");
    private Context context;
    private int appWidgetId;
    private List<WidgetShoppingList> ingredients = new ArrayList<>();

    public ShoppingListRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        List<WidgetShoppingList> newIngredients = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(RecipeContract.ShoppingListEntry.CONTENT_URI, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    WidgetShoppingList widgetShoppingList = new WidgetShoppingList(
                            cursor.getString(ShoppingListLoader.Query.NAME),
                            cursor.getString(ShoppingListLoader.Query.IMAGE),
                            cursor.getString(ShoppingListLoader.Query.AMOUNT),
                            cursor.getString(ShoppingListLoader.Query.UNIT),
                            cursor.getInt(ShoppingListLoader.Query.CHECKED) == 1);
                    newIngredients.add(widgetShoppingList);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.shopping_list_widget);
        if (newIngredients.size() > 0) {
            ingredients = newIngredients;
            remoteViews.setViewVisibility(R.id.widget_list_view, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.no_data_text, View.GONE);
        } else {
            remoteViews.setViewVisibility(R.id.widget_list_view, View.GONE);
            remoteViews.setViewVisibility(R.id.no_data_text, View.VISIBLE);
        }
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onDestroy() {
        ingredients.clear();
    }

    @Override
    public int getCount() {
        return ingredients.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.list_item_widget_shopping_list);

        if (position <= getCount()) {
            WidgetShoppingList listItem = ingredients.get(position);

            remoteViews.setTextViewText(R.id.title, listItem.getName());
            String amountString = listItem.getAmount();
            if (amountString != null && amountString.length() > 0) {
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
            } else {
                amountString = "";
            }

            remoteViews.setTextViewText(R.id.secondary_text, amountString +
                    " " + (listItem.getUnit() == null ? "" : listItem.getUnit()));
            String imageUrl = listItem.getImage();
            if (imageUrl != null && imageUrl.length() > 0) {
                try {
                    remoteViews.setImageViewBitmap(R.id.image, Picasso.with(context).load(imageUrl).get());
                } catch (IOException e) {
                    Log.e(TAG, "Unable to get image: " + imageUrl, e);
                }
            }
            int paintFlag = listItem.isChecked() ? Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG : Paint.ANTI_ALIAS_FLAG;
            remoteViews.setInt(R.id.title, "setPaintFlags", paintFlag);
            remoteViews.setInt(R.id.secondary_text, "setPaintFlags", paintFlag);
        }
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

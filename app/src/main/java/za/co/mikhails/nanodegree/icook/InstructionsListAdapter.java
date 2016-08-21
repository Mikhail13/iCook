package za.co.mikhails.nanodegree.icook;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import za.co.mikhails.nanodegree.icook.data.InstructionsListLoader;

class InstructionsListAdapter extends CursorAdapter {

    private Map<Integer, String> namePositions = new HashMap<>();

    InstructionsListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public void setNamePositions(Map<Integer, String> namePositions) {
        this.namePositions = namePositions;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_instructions_list, parent, false);
        view.setTag(R.id.LIST_ITEM_TITLE, view.findViewById(R.id.title));
        view.setTag(R.id.LIST_ITEM_SECONDARY_TEXT, view.findViewById(R.id.secondary_text));
        return view;
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView textView = (TextView) view.getTag(R.id.LIST_ITEM_TITLE);
        String nameValue = namePositions.get(cursor.getPosition());
        if (nameValue != null) {
            textView.setText(nameValue);
        }

        textView = (TextView) view.getTag(R.id.LIST_ITEM_SECONDARY_TEXT);
        textView.setText(cursor.getString(InstructionsListLoader.Query.NUMBER) + ". " +
                cursor.getString(InstructionsListLoader.Query.STEP));

        long id = cursor.getLong(InstructionsListLoader.Query.ID);
        view.setTag(R.id.INSTRUCTION_ID, id);
    }
}

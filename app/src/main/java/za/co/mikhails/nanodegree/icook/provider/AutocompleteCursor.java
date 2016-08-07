package za.co.mikhails.nanodegree.icook.provider;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import za.co.mikhails.nanodegree.icook.spoonacular.AutocompleteItem;

public class AutocompleteCursor implements Cursor {
    private static final String TAG = AutocompleteCursor.class.getSimpleName();
    private final String ID_COLUMN = "_id";
    private List<AutocompleteItem> autocompleteList;
    private int position = 0;

    public AutocompleteCursor(List<AutocompleteItem> autocompleteList) {
        this.autocompleteList = autocompleteList;
    }


    @Override
    public int getCount() {
        return autocompleteList.size();
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public boolean move(int offset) {
        if (position + offset >= autocompleteList.size()) {
            return false;
        } else {
            position += offset;
            return true;
        }
    }

    @Override
    public boolean moveToPosition(int position) {
        if (position >= autocompleteList.size()) {
            return false;
        } else {
            this.position = position;
            return true;
        }
    }

    @Override
    public boolean moveToFirst() {
        position = 0;
        return true;
    }

    @Override
    public boolean moveToLast() {
        position = autocompleteList.size() - 1;
        return true;
    }

    @Override
    public boolean moveToNext() {
        position++;
        return position < autocompleteList.size();
    }

    @Override
    public boolean moveToPrevious() {
        position++;
        return position >= 0;
    }

    @Override
    public boolean isFirst() {
        return position == 0;
    }

    @Override
    public boolean isLast() {
        return position == autocompleteList.size() - 1;
    }

    @Override
    public boolean isBeforeFirst() {
        return position < 0;
    }

    @Override
    public boolean isAfterLast() {
        return position > autocompleteList.size() - 1;
    }

    @Override
    public int getColumnIndex(String columnName) {
        if (columnName.equals(ID_COLUMN)) {
            return 0;
        } else if (columnName.equals(SearchManager.SUGGEST_COLUMN_TEXT_1)) {
            return 1;
        } else if (columnName.equals(SearchManager.SUGGEST_COLUMN_ICON_1)) {
            return 2;
        } else {
            return -1;
        }
    }

    @Override
    public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
        if (columnName.equals(ID_COLUMN)) {
            return 0;
        } else if (columnName.equals(SearchManager.SUGGEST_COLUMN_TEXT_1)) {
            return 1;
        } else if (columnName.equals(SearchManager.SUGGEST_COLUMN_ICON_1)) {
            return 2;
        } else {
            Log.d(TAG, "getColumnIndexOrThrow: [" + columnName + "]");
            throw new IllegalArgumentException();
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columnIndex == 0) {
            return ID_COLUMN;
        } else if (columnIndex == 1) {
            return SearchManager.SUGGEST_COLUMN_TEXT_1;
        } else if (columnIndex == 2) {
            return SearchManager.SUGGEST_COLUMN_ICON_1;
        } else {
            return null;
        }
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{ID_COLUMN, SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_ICON_1};
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public byte[] getBlob(int columnIndex) {
        return new byte[0];
    }

    @Override
    public String getString(int columnIndex) {
        AutocompleteItem item = autocompleteList.get(position);
        if (columnIndex == 0) {
            return String.valueOf(position);
        } else if (columnIndex == 1) {
            return item.getName();
        } else if (columnIndex == 2) {
            return item.getImage();
        } else {
            return null;
        }
    }

    @Override
    public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {

    }

    @Override
    public short getShort(int columnIndex) {
        return 0;
    }

    @Override
    public int getInt(int columnIndex) {
        return 0;
    }

    @Override
    public long getLong(int columnIndex) {
        return 0;
    }

    @Override
    public float getFloat(int columnIndex) {
        return 0;
    }

    @Override
    public double getDouble(int columnIndex) {
        return 0;
    }

    @Override
    public int getType(int columnIndex) {
        return columnIndex < 0 || columnIndex > 2 ? FIELD_TYPE_NULL : FIELD_TYPE_STRING;
    }

    @Override
    public boolean isNull(int columnIndex) {
        return columnIndex < 0 || columnIndex > 2;
    }

    @Override
    public void deactivate() {

    }

    @Override
    public boolean requery() {
        return false;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public void registerContentObserver(ContentObserver observer) {

    }

    @Override
    public void unregisterContentObserver(ContentObserver observer) {

    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void setNotificationUri(ContentResolver cr, Uri uri) {

    }

    @Override
    public Uri getNotificationUri() {
        return null;
    }

    @Override
    public boolean getWantsAllOnMoveCalls() {
        return false;
    }

    @Override
    public void setExtras(Bundle extras) {

    }

    @Override
    public Bundle getExtras() {
        return null;
    }

    @Override
    public Bundle respond(Bundle extras) {
        return null;
    }
}

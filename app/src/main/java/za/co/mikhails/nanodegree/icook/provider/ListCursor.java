package za.co.mikhails.nanodegree.icook.provider;

import android.database.AbstractCursor;

import java.util.ArrayList;
import java.util.List;

public class ListCursor extends AbstractCursor implements AddRowListener {
    private final String[] columnNames;
    private final List<String[]> list = new ArrayList<>();

    public ListCursor(String[] columnNames) {
        this.columnNames = columnNames;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String[] getColumnNames() {
        return columnNames;
    }

    @Override
    public String getString(int columnIndex) {
        return list.get(getPosition())[columnIndex];
    }

    @Override
    public short getShort(int columnIndex) {
        throw new UnsupportedOperationException("getShort is not supported");
    }

    @Override
    public int getInt(int columnIndex) {
        throw new UnsupportedOperationException("getInt is not supported");
    }

    @Override
    public long getLong(int columnIndex) {
        return Long.parseLong(list.get(getPosition())[columnIndex]);
    }

    @Override
    public float getFloat(int columnIndex) {
        throw new UnsupportedOperationException("getFloat is not supported");
    }

    @Override
    public double getDouble(int columnIndex) {
        throw new UnsupportedOperationException("getDouble is not supported");
    }

    @Override
    public boolean isNull(int columnIndex) {
        return columnIndex < 0 || columnIndex > 2;
    }

    public void addRow(String[] columnValues) {
        list.add(columnValues);
        onChange(true);
    }

    public void clear() {
        list.clear();
        onChange(true);
    }
}

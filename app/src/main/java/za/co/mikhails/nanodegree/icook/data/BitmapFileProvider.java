package za.co.mikhails.nanodegree.icook.data;

import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v4.content.FileProvider;

import java.io.FileNotFoundException;

public class BitmapFileProvider extends FileProvider {
    private static final String CONTENT_AUTHORITY = "za.co.mikhails.nanodegree.icook.provider.bitmap";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return super.query(uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        return super.openFile(uri, mode);
    }

    @Override
    public String getType(Uri uri) {
        return super.getType(uri);
    }
}

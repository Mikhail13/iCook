package za.co.mikhails.nanodegree.icook.spoonacular;

import android.content.Context;
import android.net.Uri;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import za.co.mikhails.nanodegree.icook.R;
import za.co.mikhails.nanodegree.icook.provider.AddRowListener;

public class RestApi {
    private static final String TAG = RestApi.class.getSimpleName();

    public void requestAutocompleteSuggestions(Context context, AddRowListener listener, String query, int limit) {
        HttpURLConnection urlConnection = null;
        JsonReader reader = null;
        try {
            Uri builtUri = Uri.parse(context.getString(R.string.autocomplete_url)).buildUpon()
                    .appendQueryParameter("number", String.valueOf(limit))
                    .appendQueryParameter("query", query.trim())
                    .build();

            URL url = new URL(builtUri.toString());
            Log.d(TAG, "URL: " + url.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("X-Mashape-Key", context.getString(R.string.mashape_key));
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));

            int i = 0;
            if (reader.hasNext()) {
                reader.beginArray();
                while (reader.hasNext()) {
                    reader.beginObject();

                    String[] columnValues = new String[3];
                    columnValues[0] = String.valueOf(i++);
                    while (reader.hasNext()) {
                        String name = reader.nextName();
                        if (name.equals("name")) {
                            columnValues[1] = reader.nextString();
                        } else if (name.equals("image")) {
                            columnValues[2] = reader.nextString();
                        } else {
                            reader.skipValue();
                        }
                    }
                    listener.addRow(columnValues);
                    reader.endObject();
                }
                reader.endArray();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }
    }

}

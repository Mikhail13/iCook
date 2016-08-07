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
import java.util.ArrayList;
import java.util.List;

import za.co.mikhails.nanodegree.icook.R;

public class RestApi {
    private static final String TAG = RestApi.class.getSimpleName();

    public List<AutocompleteItem> getAutocompleteList(Context context, String query) {
        List<AutocompleteItem> result = new ArrayList<>();
        HttpURLConnection urlConnection = null;
        JsonReader reader = null;
        try {
            Uri builtUri = Uri.parse(context.getString(R.string.autocomplete_url)).buildUpon()
                    .appendQueryParameter("number", "10")
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

            if (reader.hasNext()) {
                reader.beginArray();
                while (reader.hasNext()) {
                    reader.beginObject();

                    AutocompleteItem item = new AutocompleteItem();
                    while (reader.hasNext()) {
                        String name = reader.nextName();
                        if (name.equals("name")) {
                            String nextString = reader.nextString();
                            item.setName(nextString);
                            Log.d(TAG, "getAutocompleteList: name [" + nextString + "]");
                        } else if (name.equals("image")) {
                            String nextString = reader.nextString();
                            item.setImage(nextString);
                            Log.d(TAG, "getAutocompleteList: image [" + nextString + "]");
                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();

                    result.add(item);
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
        return result;
    }

}

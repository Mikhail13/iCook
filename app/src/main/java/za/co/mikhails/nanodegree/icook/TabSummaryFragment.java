package za.co.mikhails.nanodegree.icook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TabSummaryFragment extends Fragment {

    private static final String DESCRIPTION_TEXT = "text";
    private TextView description;
    private String text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.tab_summary_fragment, container, false);
        description = (TextView) rootView.findViewById(R.id.recipe_description);
        if (savedInstanceState != null && savedInstanceState.containsKey(DESCRIPTION_TEXT)) {
            setText(savedInstanceState.getString(DESCRIPTION_TEXT));
        }

        rootView.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText("Some sample text")
                        .getIntent(), getString(R.string.action_favorites)));
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null && text != null) {
            outState.putString(DESCRIPTION_TEXT, text);
        }
    }

    public void setText(String text) {
        this.text = text;
        if (description != null) {
            description.setText(Html.fromHtml(text));
        }
    }
}


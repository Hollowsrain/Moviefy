package project.moviefy.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.WeakHashMap;

import project.moviefy.Data.IMDBInfo;
import project.moviefy.Data.IMDBSubjectInternalInfo;
import project.moviefy.R;

public class MoviesFragment extends Fragment implements View.OnClickListener{

    EditText movieName;
    Button checkButton;
    ArrayList<IMDBSubjectInternalInfo> infos;

    TextView matches;
    TextView selectMovie;
    GridLayout mainGrid;
    ArrayList<Button> buttons;

    public MoviesFragment(){ buttons = new ArrayList<>();}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        movieName = rootView.findViewById(R.id.movie_name);
        checkButton = rootView.findViewById(R.id.check_button);
        checkButton.setOnClickListener(this);
        matches = rootView.findViewById(R.id.movie_matches);
        selectMovie = rootView.findViewById(R.id.select_movie);
        mainGrid = rootView.findViewById(R.id.movies_grid);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if(v.getTag() == checkButton.getTag()) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                                Activity.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {;
                                matches.setVisibility(View.VISIBLE);
                                matches.setText("... wait for a moment...");
                            }
                        });
                        infos = new IMDBInfo().search("Movies", null, movieName.getText().toString());
                        display(infos);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction().replace(R.id.content_fragment, new MovieData(v.getTag().toString(), infos)).commit();
        }
    }

    public void display(ArrayList<IMDBSubjectInternalInfo> infos) throws Exception {
        updateMatchesCount("Found matches: " + infos.size(), View.VISIBLE);
        updateMatchesList(infos);
    }

    private void updateMatchesCount(final String text, final int view){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {;
                mainGrid.removeAllViews();
                matches.setText(text);
                matches.setVisibility(view);
                matches.setPaintFlags(matches.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
            }
        });
    }

    private void updateMatchesList(ArrayList<IMDBSubjectInternalInfo> infos) throws Exception {
        String list = "";
        for (int i = 0; i < infos.size(); i++) {
            if (infos.get(i).description2 != null && !infos.get(i).description1.isEmpty() && !infos.get(i).description2.equals(infos.get(i).description1))
                list += i+1 + ". " + infos.get(i).name + "\n" + infos.get(i).description1 + "\n";
        }
        buttons = createButtons(infos);
        for(int i = 0; i < infos.size(); i++){
            buttons.get(i).setOnClickListener(this);
        }

    }

    private  ArrayList<Button> createButtons(final ArrayList<IMDBSubjectInternalInfo> infos){

        ArrayList<Button> _buttons = new ArrayList<>();

        int total = infos.size();
        final int column = 1;
        final int row = infos.size();
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                try {
                    mainGrid.setColumnCount(column);
                    mainGrid.setRowCount(row + 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        for (int i = 0; i < total; i++) {

            final Button button = new Button(getActivity());
            button.setBackgroundColor(Color.TRANSPARENT);
            button.setTextSize(15);
            button.setText(i+1 + ". "+ infos.get(i).name + " (" + infos.get(i).description1 + ")");
            button.setTextColor(Color.BLACK);
            String personId = infos.get(i).id;
            button.setTag(personId);

            GridLayout.Spec rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 1);
            GridLayout.Spec colspan = GridLayout.spec(GridLayout.UNDEFINED, 1);
            final GridLayout.LayoutParams gridParam = new GridLayout.LayoutParams(rowSpan, colspan);
            _buttons.add(button);

            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        mainGrid.addView(button, gridParam);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        return _buttons;
    }
}

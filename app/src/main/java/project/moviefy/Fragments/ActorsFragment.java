package project.moviefy.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.net.HttpURLConnection;

import project.moviefy.R;

public class ActorsFragment extends Fragment{

    static String API_KEY ="8ec930f4bf992e2d795afbf5e87ca58c";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_actors, container, false);
        return rootView;
    }

}

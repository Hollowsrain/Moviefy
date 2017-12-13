package project.moviefy.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import project.moviefy.R;

public class HomeFragment extends Fragment{

    ImageView homeImg;

    TextView txt1;
    TextView txt2;
    TextView txt3;
    TextView txt4;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        homeImg = rootView.findViewById(R.id.home_img);

        txt1 = rootView.findViewById(R.id.txt_1);
        txt2 = rootView.findViewById(R.id.txt_2);
        txt3 = rootView.findViewById(R.id.txt_3);
        txt4 = rootView.findViewById(R.id.txt_4);

        loadImg();
        updateTxts();

        return rootView;
    }

    private void loadImg(){
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                try {
                    String url = "https://vignette.wikia.nocookie.net/phobia/images/b/b2/Movie.jpg/revision/latest?cb=20171109102106";
                    Picasso.with(getActivity()).load(url).into(homeImg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateTxts(){
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                try {
                    String txt_1 = "Moviefy is a perfect choice for people who want to know more about movies, tv series or actors. It is up to date with current/upcoming movies, TV series, actors and actresses";
                    String txt_2 = "If you want to search for specific actor/actress:\n" +
                                    "1. Go to Actors.\n" +
                                    "2. Type in the exact name (upper case independent) of an actor you are searching for.\n" +
                                    "3. Select the match of the desired actor (could be multiple matches) and review the information.";
                    String txt_3 = "If you want to search for a specific movie or TV series:\n" +
                                    "1. Go to Movies.\n" +
                                    "2. Type in the exact name of movie/TV series (ex: The Godfather: Part II)\n" +
                                    "3. Select the movie/TV series and review them";
                    String txt_4 = "This application is only for educational purposes, personal, non-commercial use.\n" +
                                   "Made by: " +
                                    "\n• Arnas Fomenko" +
                                    "\n• Donatas Barkauskas" +
                                    "\n• Lukas Choromanskis\n" +
                                   "Vilnius University";


                    txt1.setText(txt_1);
                    txt2.setText(txt_2);
                    txt3.setText(txt_3);
                    txt4.setText(txt_4);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

package project.moviefy.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import project.moviefy.Data.IMDBMovieOrSeriestInfo;
import project.moviefy.Data.IMDBSubjectInternalInfo;
import project.moviefy.R;

public class MovieData extends Fragment implements View.OnClickListener{

    private String id;
    private ArrayList<IMDBSubjectInternalInfo> infos;
    String report;

    ImageView movieImage;
    TextView waitingTxt;

    TextView movieTitle;
    TextView movieRating;
    TextView movieDirector;
    TextView movieWriters;
    TextView movieStars;
    TextView movieGenre;
    TextView movieStoryLine;
    TextView movieDescription;

    public MovieData(String id, java.util.ArrayList<IMDBSubjectInternalInfo> infos){
        this.id = id;
        this.infos = infos;
        this.report = "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_data, container, false);

        movieImage = rootView.findViewById(R.id.movie_image);
        waitingTxt = rootView.findViewById(R.id.waiting2);

        movieTitle = rootView.findViewById(R.id.movie_name);
        movieRating = rootView.findViewById(R.id.movie_rating);
        movieDirector = rootView.findViewById(R.id.movie_director);
        movieWriters = rootView.findViewById(R.id.movie_writers);
        movieStars = rootView.findViewById(R.id.movie_stars);
        movieGenre = rootView.findViewById(R.id.movie_genre);
        movieStoryLine = rootView.findViewById(R.id.movie_storyLine);
        movieStoryLine.setMovementMethod(new ScrollingMovementMethod());
        movieDescription = rootView.findViewById(R.id.movie_description);

        try {
            getMovieReport();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }


    @Override
    public void onClick(View view) {

    }

    private void getMovieReport() throws InterruptedException {
        for(int i = 0; i < infos.size(); i++) {
            if (infos.get(i).id == id) {
                final int finalI = i;
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            report =  new IMDBMovieOrSeriestInfo().showSubjectInfo(infos.get(finalI));
                            displayData();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
    }

    private void displayData(){
        System.out.println(report);
        String[] data = report.split("\n");
        final String title = data[0];
        final String rating = data[1];
        final String director = data[2];
        final String writers = data[3];
        final String stars = data[4];
        final String storyLine = data[5];
        final String genre = data[6];;
        final String imageUrl = data[7];

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Picasso.with(getActivity()).load(imageUrl).into(movieImage);
                waitingTxt.setVisibility(View.INVISIBLE);

                updateTxtViews(movieTitle, "Title", title);
                updateTxtViews(movieRating, "IMDB Rating", rating);
                if(!writers.equals("@@@")) {
                    updateTxtViews(movieDirector, "Director", director);
                    updateTxtViews(movieWriters, "Writers", writers);
                }else {
                    updateTxtViews(movieDirector, "Creators", director);
                }
                updateTxtViews(movieStars, "Stars", stars);
                updateTxtViews(movieGenre, "Genre", genre);
                updateTxtViews(movieStoryLine, null, storyLine);
                updateTxtViews(movieDescription, "Description", "");

            }
        });

    }

    private void updateTxtViews(TextView textView, String beginning, final String ending){
        if(beginning != null) {
            SpannableString content = new SpannableString(beginning);
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            textView.setText(content);
            textView.append("\n");
        }
        textView.append(ending);
    }
}

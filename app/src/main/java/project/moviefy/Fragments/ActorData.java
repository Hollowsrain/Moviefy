package project.moviefy.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import project.moviefy.Data.IMDBPersontInfo;
import project.moviefy.Data.IMDBSubjectInternalInfo;
import project.moviefy.R;

public class ActorData extends Fragment implements View.OnClickListener {

    private String id;
    private ArrayList<IMDBSubjectInternalInfo> infos;
    String report;
    ImageView actorImage;
    TextView actorName;
    TextView actorBorn;
    TextView actorOccupation;
    Button btnBioAndMovies;
    TextView bioAndMovies;
    TextView waitingTxt;
    Button btnGallery;
    int index;

    public ActorData(){

    }

    public ActorData(String id, ArrayList<IMDBSubjectInternalInfo> infos){
        this.id = id;
        this.infos = infos;
        this.report = "";
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_actor_data, container, false);

        waitingTxt = rootView.findViewById(R.id.waiting);
        actorImage= rootView.findViewById(R.id.actor_image);
        actorName = rootView.findViewById(R.id.actor_name);
        actorBorn = rootView.findViewById(R.id.actor_born);
        actorOccupation = rootView.findViewById(R.id.actor_occupation);
        btnBioAndMovies = rootView.findViewById(R.id.btn_bio_and_movies);
        bioAndMovies = rootView.findViewById(R.id.bio_and_movies);
        bioAndMovies.setMovementMethod(new ScrollingMovementMethod());
        btnBioAndMovies.setOnClickListener(this);

        btnGallery = rootView.findViewById(R.id.btn_gallery);
        btnGallery.setOnClickListener(this);

        try {
            getActorReport();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }


    private void getActorReport() throws InterruptedException {
        for(int i = 0; i < infos.size(); i++) {
            if (infos.get(i).id == id) {
                index = i;
                final int finalI = i;
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            report =  new IMDBPersontInfo().showSubjectInfo(infos.get(finalI));
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
        String[] data = report.split("\n");
        final String name = data[0];
        final String occupation = data[1];
        final String born = data[2];
        final String imageUrl = data[3];
        final String starredIn = data[4];
        final String fullBio = data[5];

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {


                getActivity().setTitle(name);
                waitingTxt.setVisibility(View.INVISIBLE);

                Picasso.with(getActivity()).load(imageUrl).into(actorImage);

                updateTxtViews(actorName, "Name", name);
                updateTxtViews(actorBorn, "Born", born);
                updateTxtViews(actorOccupation, "Occupation", occupation);
                updateTxtViews(bioAndMovies, null, fullBio);

                btnBioAndMovies.setVisibility(View.VISIBLE);
                btnGallery.setVisibility(View.VISIBLE);

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


    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        String btnTxt = button.getText().toString();
        String movies = "See person's movies";
        String biography = "See person's biography";

        switch(btnTxt){
            case "See person's movies":
                replaceWithMovies();
                button.setText(biography);
                break;
            case "See person's biography":
                replaceWithBio();
                button.setText(movies);
                break;
            case "Gallery":
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.content_fragment, new Gallery(infos.get(index))).commit();
                break;
        }
    }
    private void replaceWithMovies(){
        bioAndMovies.scrollTo(0,0);
        String[] data = report.split("\n");
        final String starredIn = data[4];
        String[] movies = starredIn.split("@@");
        String result = "";
        for(int i = 0; i < movies.length; i++){
            result += i+1 + ". ";
            result += movies[i];
            result += "\n";
        }
        bioAndMovies.setText(result);
    }

    private void replaceWithBio() {
        bioAndMovies.scrollTo(0,0);
        String[] data = report.split("\n");
        final String fullBio = data[5];
        bioAndMovies.setText(fullBio);
    }



}

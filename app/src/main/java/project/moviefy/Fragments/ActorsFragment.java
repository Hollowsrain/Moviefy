package project.moviefy.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


import java.util.ArrayList;

import project.moviefy.Data.IMDBInfo;
import project.moviefy.Data.IMDBSubjectInternalInfo;
import project.moviefy.R;

public class ActorsFragment extends Fragment implements View.OnClickListener{

    EditText actorName;
    Button checkButton;
    String info;
    ArrayList<IMDBSubjectInternalInfo> infos;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_actors, container, false);
        actorName = rootView.findViewById(R.id.actor_name);
        checkButton = rootView.findViewById(R.id.check_button);
        checkButton.setOnClickListener(this);
        return rootView;
    }
    @Override
    public void onClick(View v) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    infos = new IMDBInfo().search("Actors", getActorName(), null);
                    System.out.println("@@@@@@@@@@@@@@@@@@@@ :::::" + infos.size());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public String getActorName(){
        return actorName.getText().toString();
    }

}

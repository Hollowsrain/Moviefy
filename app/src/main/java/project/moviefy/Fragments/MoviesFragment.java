package project.moviefy.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.WeakHashMap;

import project.moviefy.R;

public class MoviesFragment extends Fragment {
    static String API_KEY ="8ec930f4bf992e2d795afbf5e87ca58c";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        return rootView;
    }
    public String[] getPathsFromAPI(boolean sortbypop)
    {
        while(true)
        {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String JSONResult;

            try{
                String urlString = null;
                if(sortbypop)
                {
                    urlString = "https://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=" + API_KEY;
                }
                else
                    urlString = "https://api.themoviedb.org/3/discover/movie?sort_by=vote_avarage.desc&vote_count.gte=300&api_key=" + API_KEY;
                //urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                /*if (InputStream == null)
                {
                    return null;

                }*/
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine())!=null)
                {
                    buffer.append(line+"\n");
                }
                if (buffer.length() == 0)
                {
                    return null;
                }
                JSONResult = buffer.toString();

                try{
                    return getPathsFromJSON(JSONResult);

                }catch (JSONException e)
                {
                    return null;
                }


            }catch (Exception e)
            {
                continue;
            }finally {
                if (urlConnection !=null)
                {
                    urlConnection.disconnect();
                }
                if (reader !=null){
                    try{
                        reader.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();

                    }
                }
            }

        }
    }
        public  String[] getPathsFromJSON(String JSONStringParam) throws JSONException{
            JSONObject JSONString = new JSONObject(JSONStringParam);
            //JSONArray
                    JSONArray moviesArray = JSONString.getJSONArray("results");
                    String[] result = new String[moviesArray.length()];

                    for (int i = 0; i<moviesArray.length();i++)
                    {
                        JSONObject movie = moviesArray.getJSONObject(i);
                        String moviePath = movie.getString("poster-path");
                        result[i] = moviePath;
                    }
                    return result;
        }

}

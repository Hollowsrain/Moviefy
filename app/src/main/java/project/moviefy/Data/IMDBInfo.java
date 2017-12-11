package project.moviefy.Data;

import java.util.ArrayList;

public class IMDBInfo {
    
    public ArrayList<IMDBSubjectInternalInfo> search(String category, String actorName, String movieName) {
        ArrayList<IMDBSubjectInternalInfo> infos = null;
        try {
            if (category.equals("Movies")) {
                infos = new IMDBMovieOrSeriestInfo().search(null, movieName);
            } else if (category.equals("Actors")) {
                infos = new IMDBPersontInfo().search(actorName, null);
            } else {
                throw new Exception("Incorrect subject to found.");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return infos;
    }


    
}

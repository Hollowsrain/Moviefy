
package project.moviefy.Data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.*;
import org.jsoup.*;

public abstract class IMDBSubjectInfo {
    
    abstract ArrayList<IMDBSubjectInternalInfo> search(String actorName, String movieName) throws Exception;

    protected ArrayList<IMDBSubjectInternalInfo> searchInfos(String url, String fullName) throws Exception {
        System.out.println("... wait for a moment...");
        String encoded = readUrl(url);
        String decoded = Jsoup.parse(encoded).text();
        JSONObject json = new JSONObject(decoded);
        return parseJSONInfo(json, fullName);
    }
    
    protected static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder buffer = new StringBuilder();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read); 
            }
            return buffer.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
        
    abstract ArrayList<IMDBSubjectInternalInfo> parseJSONInfo(JSONObject json, String fullName) throws Exception;
    
    abstract String showSubjectInfo(IMDBSubjectInternalInfo info) throws Exception;
    
}


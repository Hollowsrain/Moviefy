
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
    
    protected static String getConsoleLine(String prompt) throws Exception {
        Scanner in = new Scanner(System.in);
        System.out.print(prompt);
        return in.nextLine();
    }
    
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
    
    protected void showInfo(ArrayList<IMDBSubjectInternalInfo> infos) throws Exception {

        if (infos.size() <= 0) {
            throw new Exception("Such subject not found.");
        } else if (infos.size() == 1) {
            System.out.println("one match founded");
            showSubjectInfo(infos.get(0));
        } else {
            System.out.println(String.format("Founded %d matches.", infos.size()));
            int id = chooseOne(infos);
            showSubjectInfo(infos.get(id));
        }

    }
    
    protected int chooseOne(ArrayList<IMDBSubjectInternalInfo> infos) throws Exception {
        for (int i = 0; i < infos.size(); i++) {
            String str = String.format("%d. %s (%s)", i + 1, infos.get(i).name, infos.get(i).description1);
            if (infos.get(i).description2 != null && !infos.get(i).description1.isEmpty() && !infos.get(i).description2.equals(infos.get(i).description1)) {
                str += String.format("(%s)", infos.get(i).description2);
            }
            System.out.println(str);
        }
        try {
            String str = getConsoleLine("Choose one of it (enter order number): ").trim();
            int id = Integer.parseInt(str);
            id--;
            if ((id < 0) || (id >= infos.size())) {
                throw new Exception("Incorrect list number selected.");
            }
            return id;
        } catch (Exception e) {
            throw new Exception("Incorrect list number selected.");
        }
    }
    
    abstract String showSubjectInfo(IMDBSubjectInternalInfo info) throws Exception;
    
}


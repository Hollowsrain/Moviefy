
package project.moviefy.Data;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class IMDBPersontInfo extends IMDBSubjectInfo {
    
    public final String SEARCH_URL_STARTS = "http://www.imdb.com/xml/find?json=1&nr=1&nm=on&q=";
    public final String SEARCH_URL_ENDS = "";
    
    public final String[] JSON_PARTS = {
        "name_popular" /*, "name_exact", "name_substring", "name_approx"*/
    };
    
    public final String PERSON_URL_FORMAT = "http://www.imdb.com/name/%s/";
    
    public ArrayList<IMDBSubjectInternalInfo> search(String fullName, String movieName) throws Exception {
        ArrayList<IMDBSubjectInternalInfo> infos;
        try {
            String url = SEARCH_URL_STARTS + URLEncoder.encode(fullName + SEARCH_URL_ENDS, "UTF-8");
            infos = searchInfos(url, fullName);
            showInfo(infos);
        } catch (Exception e) {
            throw e;
        }

        return infos;
    }
    
    public ArrayList<IMDBSubjectInternalInfo> parseJSONInfo(JSONObject json, String fullName) throws Exception {
        ArrayList<IMDBSubjectInternalInfo> result = new ArrayList<IMDBSubjectInternalInfo>();
        for (int i = 0; i < JSON_PARTS.length; i++) {
            try {
                JSONArray array = json.getJSONArray(JSON_PARTS[i]);
                if (array != null) {
                    for (int j = 0; j < array.length(); j++)
                    {
                        JSONObject obj = array.getJSONObject(j);
                        String name = (String) obj.get("name");
                        if (name.toLowerCase().equals(fullName.toLowerCase())) {
                            IMDBSubjectInternalInfo info = new IMDBSubjectInternalInfo();
                            info.name = name;
                            info.id = (String) obj.get("id");
                            info.description1 = (String) obj.get("description");
                            info.description2 = "";
                            info.kind = IMDBSubjectInternalInfo.Kind.PERSON;
                            result.add(info);
                        }
                    }
                }
            } catch (JSONException e) {
                // ...
            }
        }
        return result;
    }
    
    public String showSubjectInfo(IMDBSubjectInternalInfo info) throws Exception {
        if (info == null) {
            throw new Exception("Bad id.");
        }
        String url = String.format(PERSON_URL_FORMAT, info.id);
        System.out.println();
        System.out.println("... wait for a moment...");
        Document doc = Jsoup.connect(url).get();
        String report = getPersonReport(doc);
        System.out.println(report);
        return report;
    }
    
    private String getPersonReport(Document doc) {
        StringBuilder report = new StringBuilder("");
        report.append("Name: ").append(getPersonName(doc)).append("\n");
        report.append("Occupation: ").append(getPersonOccupation(doc)).append("\n");
        report.append("").append(getPersonBorn(doc)).append("\n");
        report.append("Short bio: ").append(getPersonShortBio(doc)).append("\n");
        report.append("Picture URL: ").append(getPersonImageUrl(doc)).append("\n");
        report.append("Starred in: ").append(getPersonMovies(doc)).append("\n");
        return report.toString();
    }
    
    private String getPersonName(Document doc) {
        String result = "";
        Elements wrapper = doc.getElementsByClass("article name-overview");
        Elements name = wrapper.select("[itemprop=name]");
        result = name.text();
        return result;
    }
    
    private String getPersonOccupation(Document doc) {
        String result = "";
        Element name_job_categories = doc.getElementById("name-job-categories");
        Elements occupation = name_job_categories.select("a");
        boolean first = true;
        for (Element e : occupation) {
            if (!first) {
                result += ", ";
            }
            first = false;
            result += e.text();
        }
        return result;
    }
    
    private String getPersonBorn(Document doc) {
        String result = "";
        Element born = doc.getElementById("name-born-info");
        result = born.text();
        return result;
    }
    
    private String getPersonShortBio(Document doc) {
        String result = "";
        Elements wrapper = doc.getElementsByClass("article name-overview");
        Elements name_trivia_bio_text = wrapper.select(".name-trivia-bio-text");
        Elements shortBio = name_trivia_bio_text.select("[itemprop=description]");
        shortBio.select("span").remove();
        result += shortBio.text();
        int idx = result.lastIndexOf("...");
        result = result.substring(0, idx - 1);
        return result;
    }

    private String getPersonImageUrl(Document doc){
        String result = "";
        Elements wrapper = doc.getElementsByClass("article name-overview");
        result = wrapper.select("#name-poster").attr("src");
        return result;
    }

    private String getPersonMovies(Document doc){
        String result = "";
        Elements wrapper = doc.getElementsByClass("filmo-category-section");
        Elements movies = wrapper.select("b");
        boolean first = true;
        for (Element e : movies) {
            if (!first) {
                result += "\n";
            }
            first = false;
            result += e.text();
        }

        return result;
    }
    
}

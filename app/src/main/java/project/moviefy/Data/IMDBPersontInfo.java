
package project.moviefy.Data;

import java.io.IOException;
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
        System.out.println("... wait for a moment...");
        Document doc = Jsoup.connect(url).get();
        String report = getPersonReport(doc, info);
        return report;
    }
    
    private String getPersonReport(Document doc, IMDBSubjectInternalInfo info) throws Exception {
        StringBuilder report = new StringBuilder("");
        report.append(getPersonName(doc)).append("\n");
        report.append(getPersonOccupation(doc)).append("\n");
        report.append(getPersonBorn(doc)).append("\n");
        report.append(getPersonImageUrl(doc)).append("\n");
        report.append(getPersonMovies(doc)).append("\n");
        report.append(getPersonFullBio(info)).append("\n");
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
        result = result.replace("Born: ", "");
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
        Elements years = wrapper.select("span.year_column");
        String[] _years = new String[years.size()];
        int i = 0;
        for(Element e : years){
            _years[i] = e.text();
            if(e.text().length() < 4)
                _years[i] = " (unknown)";
            else _years[i] = " (" + e.text() + ")";
            i++;
        }
        i = 0;
        boolean first = true;
        for (Element e : movies) {
            if (!first) {
                result += "@@";
            }
            first = false;
            result += e.text();
            result += _years[i];
            i++;
        }

        return result;
    }

    private String getPersonFullBio(IMDBSubjectInternalInfo info) throws Exception{
        String result = "";
        String url = String.format(PERSON_URL_FORMAT, info.id);
        url += "bio?ref_=nm_ov_bio_sm";
        Document doc = Jsoup.connect(url).get();
        Elements wrapper = doc.getElementsByClass("soda odd");
        Elements bio = wrapper.select("p");
        boolean first = true;
        for (Element e : bio) {
            if (!first) {
                result += "\n";
            }
            first = false;
            result += e.text();
        }


        return result;
    }

    public ArrayList<String> getGallery(IMDBSubjectInternalInfo info, int page) throws IOException {
        ArrayList<String> result = new ArrayList<>();
        String url = String.format(PERSON_URL_FORMAT, info.id);
        url += "mediaindex?page=" + String.valueOf(page) + "&ref_=nmmi_mi_sm";
        Document doc = Jsoup.connect(url).get();
        Elements wrapper = doc.getElementsByClass("article");
        Elements imgs = wrapper.select("[itemprop=image]");
        boolean first = true;
        for(Element e : imgs){
            if(!first) {
                String img = e.attr("src");
                result.add(img);
            }
            first = false;
        }

        return result;
    }

    public ArrayList<String> getLargeGallery(IMDBSubjectInternalInfo info, int page) throws IOException {
        ArrayList<String> result = new ArrayList<>();
        String url = String.format(PERSON_URL_FORMAT, info.id);
        url += "mediaindex?page=" + String.valueOf(page) + "&ref_=nmmi_mi_sm";
        Document doc = Jsoup.connect(url).get();
        Elements wrapper = doc.getElementsByClass("article");
        Elements imgs = wrapper.select("[itemprop=thumbnailUrl]");
        for(Element e : imgs){
            String img = e.attr("href");
            result.add(img);
        }

        return result;
    }
}

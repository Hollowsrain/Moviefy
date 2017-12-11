package project.moviefy.Data;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.*;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class IMDBMovieOrSeriestInfo extends IMDBSubjectInfo {

    public final String SEARCH_URL_STARTS = "http://www.imdb.com/xml/find?json=1&tt=on&q=";
    public final String SEARCH_URL_ENDS = "%";
    
    public final String[] JSON_PARTS = {
        "title_popular" ,  "title_exact" , "title_substring", /*"title_approx"*/
    };
    
    public final String MOVIE_OR_SERIES_URL_FORMAT = "http://www.imdb.com/title/%s/";

    public ArrayList<IMDBSubjectInternalInfo> search(String actorName, String fullName) throws Exception {
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
                        String title = (String) obj.get("title");
                        if (title.toLowerCase().equals(fullName.toLowerCase())) {
                            IMDBSubjectInternalInfo info = new IMDBSubjectInternalInfo();
                            info.name = title;
                            info.id = (String) obj.get("id");
                            info.description1 = (String) obj.get("title_description");
                            info.description2 = (String) obj.get("description");
                            info.kind = IMDBSubjectInternalInfo.Kind.MOVIE;
                            if (info.description1.contains(IMDBSubjectInternalInfo.DESCRIPTION_SERIES) || info.description2.contains(IMDBSubjectInternalInfo.DESCRIPTION_SERIES)) {
                                info.kind = IMDBSubjectInternalInfo.Kind.SERIES;
                            }
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
        String url = String.format(MOVIE_OR_SERIES_URL_FORMAT, info.id);
        System.out.println("... wait for a moment...");
        Document doc = Jsoup.connect(url).get();
        String report = getSubjectReport(info, doc);
        //System.out.println(report);
        return report;
    }
    
    private String getSubjectReport(IMDBSubjectInternalInfo info, Document doc) throws Exception {
        if (info.kind == IMDBSubjectInternalInfo.Kind.MOVIE) {
            return getMovieReport(doc);
        } else if (info.kind == IMDBSubjectInternalInfo.Kind.SERIES) {
            return getSeriesReport(doc);
        } else {
            throw new Exception("Unknown IMDB subject required.");
        }
    }
    
    private String getMovieReport(Document doc) {
        StringBuilder report = new StringBuilder("");
        report.append(getMovieTitle(doc)).append("\n");
        report.append(getMovieRate(doc)).append("\n");
        report.append(getMovieDirector(doc)).append("\n");
        report.append(getMovieWriters(doc)).append("\n");
        report.append(getMovieStars(doc)).append("\n");
        report.append(getMovieStoryline(doc)).append("\n");
        report.append(getGenre(doc)).append("\n");
        report.append(getImageUrl(doc)).append("\n");
        return report.toString();
    }
    
    private String getMovieTitle(Document doc) {
        String result = "";
        Elements title_wrapper = doc.getElementsByClass("title_wrapper");
        Elements name = title_wrapper.select("[itemprop=name]");
        result = name.text();
        return result;
    }
    
    private String getMovieRate(Document doc) {
        String result = "";
        Elements ratings_wrapper = doc.getElementsByClass("ratings_wrapper");
        Elements ratingValue = ratings_wrapper.select("[itemprop=ratingValue]");
        result = ratingValue.text();
        return result;
    }
    
    private String getMovieDirector(Document doc) {
        String result = "";
        Elements credit_summary_item = doc.getElementsByClass("credit_summary_item");
        for (Element element : credit_summary_item) {
            Elements directors = element.select("span[itemprop=director]");
            if ((directors != null) && (directors.size() > 0)) {
                boolean first = true;
                for (Element director : directors) {
                    if (!first) {
                        result += ", ";
                    }
                    first = false;
                    Elements name = director.select("span[itemprop=name]");
                    result += name.text();
                }
                return result;
            }
        }
        return result;
    }
    
    private String getMovieWriters(Document doc) {
        String result = "";
        Elements credit_summary_item = doc.getElementsByClass("credit_summary_item");
        for (Element element : credit_summary_item) {
            Elements writers = element.select("span[itemprop=creator]");
            if ((writers != null) && (writers.size() > 0)) {
                boolean first = true;
                for (Element writer : writers) {
                    if (!first) {
                        result += ", ";
                    }
                    first = false;
                    Elements name = writer.select("span[itemprop=name]");
                    result += name.text();
                }
                return result;
            }
        }
        return result;
    }
    
    private String getMovieStars(Document doc) {
        String result = "";
        Elements credit_summary_item = doc.getElementsByClass("credit_summary_item");
        for (Element element : credit_summary_item) {
            Elements actors = element.select("span[itemprop=actors]");
            if ((actors != null) && (actors.size() > 0)) {
                boolean first = true;
                for (Element actor : actors) {
                    if (!first) {
                        result += ", ";
                    }
                    first = false;
                    Elements name = actor.select("span[itemprop=name]");
                    result += name.text();
                }
                return result;
            }
        }
        return result;
    }
    
    private String getMovieStoryline(Document doc) {
        String result = "";
        Element titleStoryLine = doc.getElementById("titleStoryLine");
        Elements description = titleStoryLine.select("[itemprop=description]");
        description.select(".nobr").remove();
        result = description.text();
        return result;
    }
    
    private String getSeriesReport(Document doc) {
        StringBuilder report = new StringBuilder("");
        report.append(getSeriesTitle(doc)).append("\n");
        report.append(getSeriesRate(doc)).append("\n");
        report.append(getSeriesCreators(doc)).append("\n");
        report.append("@@@").append("\n");
        report.append(getSeriesStars(doc)).append("\n");
        report.append(getMovieStoryline(doc)).append("\n");
        report.append(getGenre(doc)).append("\n");
        report.append(getImageUrl(doc)).append("\n");
        return report.toString();
    }
    
    private String getSeriesTitle(Document doc) {
        String result = "";
        Elements title_wrapper = doc.getElementsByClass("title_wrapper");
        Elements name = title_wrapper.select("[itemprop=name]");
        result = name.text();
        Elements subtext = title_wrapper.select(".subtext");
        Elements links = subtext.select("a");
        for (Element element : links) {
            String str = element.text();
            if (str.toLowerCase().contains("series".toLowerCase())) {
                result += String.format(", %s", str);
            }
        }
        return result;
    }
    
    private String getSeriesRate(Document doc) {
        String result = "";
        Elements ratings_wrapper = doc.getElementsByClass("ratings_wrapper");
        Elements ratingValue = ratings_wrapper.select("[itemprop=ratingValue]");
        result = ratingValue.text();
        return result;
    }
    
    private String getSeriesCreators(Document doc) {
        String result = "";
        Elements credit_summary_item = doc.getElementsByClass("credit_summary_item");
        for (Element element : credit_summary_item) {
            Elements actors = element.select("span[itemprop=creator]");
            if ((actors != null) && (actors.size() > 0)) {
                boolean first = true;
                for (Element actor : actors) {
                    if (!first) {
                        result += ", ";
                    }
                    first = false;
                    Elements name = actor.select("span[itemprop=name]");
                    result += name.text();
                }
                return result;
            }
        }
        return result;
    }
    
    private String getSeriesStars(Document doc) {
        String result = "";
        Elements credit_summary_item = doc.getElementsByClass("credit_summary_item");
        for (Element element : credit_summary_item) {
            Elements actors = element.select("span[itemprop=actors]");
            if ((actors != null) && (actors.size() > 0)) {
                boolean first = true;
                for (Element actor : actors) {
                    if (!first) {
                        result += ", ";
                    }
                    first = false;
                    Elements name = actor.select("span[itemprop=name]");
                    result += name.text();
                }
                return result;
            }
        }
        return result;
    }
    
    private String getSeriesStoryline(Document doc) {
        String result = "";
        Element titleStoryLine = doc.getElementById("titleStoryLine");
        Elements description = titleStoryLine.select("[itemprop=description]");
        description.select(".nobr").remove();
        result = description.text();
        return result;
    }

    private String getGenre(Document doc){
        String result = "";
        Elements wrapper = doc.getElementsByClass("article");
        Elements genre = wrapper.select("[itemprop=genre]");
        result = genre.text();
        result = result.replace("Genres: ", "");
        result = result.replace(" |", ",");

        return result;
    }

    private String getImageUrl(Document doc){
        String result = "";
        Elements wrapper = doc.getElementsByClass("poster");
        result = wrapper.select("[itemprop=image]").attr("src");
        return result;
    }
}

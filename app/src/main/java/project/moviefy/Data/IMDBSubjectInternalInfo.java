package project.moviefy.Data;

public class IMDBSubjectInternalInfo {

    public String name;
    public String id;
    public String description1;
    public String description2;
    public Kind kind;

    public enum Kind {
        MOVIE, SERIES, PERSON
    }

    public IMDBSubjectInternalInfo() {

    }

    public IMDBSubjectInternalInfo(IMDBSubjectInternalInfo info) {
        this.name = info.name;
        this.id = info.id;
        this.description1 = info.description1;
        this.description2 = info.description2;
        this.kind = info.kind;
    }

    public static final String DESCRIPTION_MOVIE = "movie";
    public static final String DESCRIPTION_SERIES = "series";
    public static final String DESCRIPTION_PERSON = "person";

}

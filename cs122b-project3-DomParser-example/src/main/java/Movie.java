public class Movie {

    private final String id;
    private final String title;
    private final int year;
    private final String director;

    public Movie(String id, String title, int year, String director) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;

    }

    public int getYear() {
        return year;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public String toString() {

        return "id:" + getId() + ", " +
                "title:" + getTitle() + ", " +
                "year:" + getYear() + ", " +
                "director:" + getDirector() + ".";
    }
}

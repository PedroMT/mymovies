package br.com.pmtsouza.mymovies.models;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by Pedro M. on 17/01/2017.
 */

public class Movie extends RealmObject {

    private String id;
    private String imdbID;
    private String title;
    private Integer year;
    private String rated;
    private String released;
    private String runtime;
    private String genre;
    private String director;
    private String writer;
    private String actors;
    private String plot;
    private String language;
    private String country;
    private String posterhref;
    private Integer metascore;
    private Double imdbRating;
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getRated() {
        return rated;
    }

    public void setRated(String rated) {
        this.rated = rated;
    }

    public String getReleased() {
        return released;
    }

    public void setReleased(String released) {
        this.released = released;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPosterhref() {
        return posterhref;
    }

    public void setPosterhref(String posterhref) {
        this.posterhref = posterhref;
    }

    public Integer getMetascore() {
        return metascore;
    }

    public void setMetascore(Integer metascore) {
        this.metascore = metascore;
    }

    public Double getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(Double imdbRating) {
        this.imdbRating = imdbRating;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static Movie create(String title, String imdbId, Integer year, String rated, String released, String runtime, String genre,
                               String director, String writer, String actors, String plot, String language, String country,
                               String posterhref, Integer metascore, Double imdbRating, String type){

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Movie movie = realm.createObject(Movie.class);
        movie.setTitle(title);
        movie.setImdbID(imdbId);
        movie.setYear(year);
        movie.setRated(rated);
        movie.setReleased(released);
        movie.setRuntime(runtime);
        movie.setGenre(genre);
        movie.setDirector(director);
        movie.setWriter(writer);
        movie.setActors(actors);
        movie.setPlot(plot);
        movie.setLanguage(language);
        movie.setCountry(country);
        movie.setPosterhref(posterhref);
        movie.setMetascore(metascore);
        movie.setImdbRating(imdbRating);
        movie.setType(type);
        realm.commitTransaction();
        realm.close();

        return movie;
    }

    public static Movie update(String id, String imdbId, String title, Integer year, String rated, String released, String runtime,
                               String genre, String director, String writer, String actors, String plot, String language,
                               String country, String posterhref, Integer metascore, Double imdbRating, String type){

        Realm realm = Realm.getDefaultInstance();

        Movie movie = realm.where(Movie.class).equalTo("id", id).findFirst();

        realm.beginTransaction();
        movie.setTitle(title);
        movie.setImdbID(imdbId);
        movie.setYear(year);
        movie.setRated(rated);
        movie.setReleased(released);
        movie.setRuntime(runtime);
        movie.setGenre(genre);
        movie.setDirector(director);
        movie.setWriter(writer);
        movie.setActors(actors);
        movie.setPlot(plot);
        movie.setLanguage(language);
        movie.setCountry(country);
        movie.setPosterhref(posterhref);
        movie.setMetascore(metascore);
        movie.setImdbRating(imdbRating);
        movie.setType(type);
        realm.commitTransaction();
        realm.close();

        return movie;
    }
}

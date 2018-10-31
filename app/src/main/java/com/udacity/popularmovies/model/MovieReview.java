package com.udacity.popularmovies.model;

/*{
    "author": "Screen-Space",
    "content": "\"It is a bold undertaking, to readjust what is expected of the MCU/Avengers formula, and there are moments when the sheer scale and momentum match the narrative ambition...\"\r\n\r\nRead the full review here: http://screen-space.squarespace.com/reviews/2018/4/25/avengers-infinity-war.html",
    "id": "5adff809c3a3683daa00ad3d",
    "url": "https://www.themoviedb.org/review/5adff809c3a3683daa00ad3d"
}*/

public class MovieReview {

    private String id;
    private String author;
    private String content;
    private String url;

    public void setId(String id) {
        this.id = id;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return this.id;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getContent() {
        return this.content;
    }

    public String getUrl() {
        return this.url;
    }
}

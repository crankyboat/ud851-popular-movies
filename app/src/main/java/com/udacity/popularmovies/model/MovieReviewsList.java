package com.udacity.popularmovies.model;

/*{
    "id": 299536,
    "page": 1,
    "results": [
        {
        "author": "Screen-Space",
        "content": "\"It is a bold undertaking, to readjust what is expected of the MCU/Avengers formula, and there are moments when the sheer scale and momentum match the narrative ambition...\"\r\n\r\nRead the full review here: http://screen-space.squarespace.com/reviews/2018/4/25/avengers-infinity-war.html",
        "id": "5adff809c3a3683daa00ad3d",
        "url": "https://www.themoviedb.org/review/5adff809c3a3683daa00ad3d"
        },
        {
        "author": "furious_iz",
        "content": "Amazing.  Visually stunning.  So much going on, but somehow also clear and easy to understand.  A little flabby in the middle third, but given the huge cast and story to cover it is very understandable.  The highlight was the parings of characters from different stories and their interactions.\r\n\r\nIf you aren't a Marvel fan this film won't convert you, but if you have liked any of the previous films you will like this too.\r\n\r\n9/10",
        "id": "5ae02e1e0e0a26156900f6da",
        "url": "https://www.themoviedb.org/review/5ae02e1e0e0a26156900f6da"
        },
        ...
    ],
    "total_pages": 2,
    "total_results": 11
}*/

import java.util.List;

public class MovieReviewsList {

    private int id;
    private List<MovieReview> results;

    public void setId(int id) {
        this.id = id;
    }

    public void setResults(List<MovieReview> results) {
        this.results = results;
    }

    public int getId() {
        return this.id;
    }

    public List<MovieReview> getResults() {
        return this.results;
    }

}

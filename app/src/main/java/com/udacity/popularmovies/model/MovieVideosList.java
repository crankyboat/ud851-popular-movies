package com.udacity.popularmovies.model;

/*{
    "id": 299536,
    "results": [
        {
        "id": "5a200baa925141033608f5f0",
        "iso_639_1": "en",
        "iso_3166_1": "US",
        "key": "6ZfuNTqbHE8",
        "name": "Official Trailer",
        "site": "YouTube",
        "size": 1080,
        "type": "Trailer"
        },
        {
        "id": "5a200bcc925141032408d21b",
        "iso_639_1": "en",
        "iso_3166_1": "US",
        "key": "sAOzrChqmd0",
        "name": "Action...Avengers: Infinity War",
        "site": "YouTube",
        "size": 720,
        "type": "Clip"
        },
        {
        "id": "5a200bdd0e0a264cca08d39f",
        "iso_639_1": "en",
        "iso_3166_1": "US",
        "key": "3VbHg5fqBYw",
        "name": "Trailer Tease",
        "site": "YouTube",
        "size": 720,
        "type": "Teaser"
        },
        {
        "id": "5aea2f3e92514172a7001672",
        "iso_639_1": "en",
        "iso_3166_1": "US",
        "key": "PbRmbhdHDDM",
        "name": "\"Family\" Featurette",
        "site": "YouTube",
        "size": 1080,
        "type": "Featurette"
        },
        ...
    ]
}*/

import java.util.List;

public class MovieVideosList {

    private int id;
    private List<MovieVideo> results;

    public void setId(int id) {
        this.id = id;
    }

    public void setResults(List<MovieVideo> results) {
        this.results = results;
    }

    public int getId() {
        return this.id;
    }

    public List<MovieVideo> getResults() {
        return this.results;
    }

}

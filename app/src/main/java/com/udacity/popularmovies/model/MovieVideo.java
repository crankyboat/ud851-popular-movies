package com.udacity.popularmovies.model;

/*{
    "id": "5aea2f3e92514172a7001672",
    "iso_639_1": "en",
    "iso_3166_1": "US",
    "key": "PbRmbhdHDDM",
    "name": "\"Family\" Featurette",
    "site": "YouTube",
    "size": 1080,
    "type": "Featurette"
}*/

public class MovieVideo {

    private String id;
    private String key;
    private String name;
    private String site;
    private String type;

    public void setId(String id) {
        this.id = id;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return this.id;
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public String getSite() {
        return this.site;
    }

    public String getType() {
        return this.type;
    }
}

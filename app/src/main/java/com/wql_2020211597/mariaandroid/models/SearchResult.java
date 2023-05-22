package com.wql_2020211597.mariaandroid.models;

import com.wql_2020211597.mariaandroid.models.Document;

public class SearchResult {
    private double score;
    private Document doc;

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }

}

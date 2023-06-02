package com.wql_2020211597.mariaandroid.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class SearchResult {
    @SerializedName("Score")
    private double score;
    @SerializedName("Doc")
    private Document doc;
    public String getId() {
        return doc.getId();
    }
    public void setId(String id) {
        doc.setId(id);
    }

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

    @NonNull
    @Override
    public String toString() {
        return "SearchResult{" + "doc=" + doc + ", score=" + score + '}';
    }
}

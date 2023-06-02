package com.wql_2020211597.mariaandroid.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class HistoryEntry {
    @SerializedName("query")
    private String query;

    @SerializedName("timestamp")
    private Date timestamp;

    public HistoryEntry(String query) {
        setQuery(query);
        setTimestamp(new Date());
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}

package com.wql_2020211597.mariaandroid.services;

import com.wql_2020211597.mariaandroid.models.Document;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DocumentService {
    @GET("/document")
    Call<Document> getDocument(@Query("id") String doc_id);
}

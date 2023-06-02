package com.wql_2020211597.mariaandroid.searchservice;

import com.wql_2020211597.mariaandroid.models.SearchResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SearchService {
    @GET("/search")
    Call<List<SearchResult>> search(@Query("q") String query, @Query("page") String page);
}
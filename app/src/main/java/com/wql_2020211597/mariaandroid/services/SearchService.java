package com.wql_2020211597.mariaandroid.services;

import com.wql_2020211597.mariaandroid.models.SearchResult;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface SearchService {
    @GET("/search")
    Call<List<SearchResult>> search(@Query("q") String query,
                                    @Query("page") int page);

    @Multipart
    @POST("/search_by_image")
    Call<ImageSearchResponse> uploadImageToServer(@Part MultipartBody.Part file);
}


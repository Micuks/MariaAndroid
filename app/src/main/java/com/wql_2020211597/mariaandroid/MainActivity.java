package com.wql_2020211597.mariaandroid;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.wql_2020211597.mariaandroid.models.SearchResult;
import com.wql_2020211597.mariaandroid.searchservice.SearchService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost" + ":9011")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        SearchService service = retrofit.create(SearchService.class);
        Call<List<SearchResult>> call = service.search("your query");

        call.enqueue(new Callback<List<SearchResult>>() {
            @Override
            public void onResponse(Call<List<SearchResult>> call,
                                   Response<List<SearchResult>> response) {
                if (response.isSuccessful()) {
                    List<SearchResult> results = response.body();

                    // Handle results
                } else {
                    // Handle error
                }
            }

            @Override
            public void onFailure(Call<List<SearchResult>> call, Throwable t) {
                // Handle failure
            }
        });
    }

}
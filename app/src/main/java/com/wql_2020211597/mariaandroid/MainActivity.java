package com.wql_2020211597.mariaandroid;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wql_2020211597.mariaandroid.config.Config;
import com.wql_2020211597.mariaandroid.models.Document;
import com.wql_2020211597.mariaandroid.models.SearchResult;
import com.wql_2020211597.mariaandroid.searchservice.SearchService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private EditText etSearch;
    private Button btnSearch;
    private RecyclerView rvResults;
    private SearchResultsAdapter adapter;

    static private final String addr = Config.getBackendAddr();
    static private final String port = Config.getBackendPort();

    private String backendUrl() {
        return Config.getBackendUrl();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        rvResults = findViewById(R.id.rvResults);

        // Initialize the RecyclerView with an empty adapter
        Log.d(TAG, "Initial RecyclerView and Adapter setup");

        adapter = new SearchResultsAdapter(new ArrayList<>());
        rvResults.setLayoutManager(new LinearLayoutManager(this));
        rvResults.setAdapter(adapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(backendUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        SearchService service = retrofit.create(SearchService.class);

        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString();
            String page = "1"; // FIXME: Adaptive page number
            Log.d(TAG, "query: " + query + ", page: " + page);
            Call<List<SearchResult>> call = service.search(query, page);

            call.enqueue(new Callback<List<SearchResult>>() {
                @Override
                public void onResponse(Call<List<SearchResult>> call,
                                       Response<List<SearchResult>> response) {
                    if (response.isSuccessful()) {
                        List<SearchResult> results = response.body();
                        Log.d(TAG,
                                "Got " + results.size() + " results: " + results.toString());
                        // Update the adapter with the search results
                        adapter.updateResults(results);
                    } else {
                        // Handle error
                        Log.e(TAG,
                                "Failed to fetch response, status code: " + response.code());
                        Log.e(TAG, "Error message: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<List<SearchResult>> call,
                                      Throwable t) {
                    Log.e(TAG, "Search request failed", t);
                }
            });
        });
    }

    private class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsViewHolder> {
        private List<SearchResult> results;

        SearchResultsAdapter(List<SearchResult> results) {
            this.results = results;
            Log.d(TAG,
                    "SearchResultsAdapter initialized with " + results.size() + " results");
        }

        void updateResults(List<SearchResult> results) {
            this.results = results;
            Log.d(TAG,
                    "Adapter results updated, new results count: " + results.size());
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public SearchResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.d(TAG, "onCreateViewHolder called, viewType: " + viewType);
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_result, parent, false);
            return new SearchResultsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchResultsViewHolder holder,
                                     int position) {
            Log.d(TAG, "onBindViewHolder called, position: " + position);
            holder.bind(results.get(position));
        }

        @Override
        public int getItemCount() {
            return results.size();
        }
    }

    private class SearchResultsViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView tvTitle, tvContent, tvUrl, tvDate, tvScore;

        SearchResultsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvUrl = itemView.findViewById(R.id.tvUrl);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvScore = itemView.findViewById(R.id.tvScore);
        }

        void bind(SearchResult result) {
            Document doc = result.getDoc();
            if (doc != null) {
                Log.d(TAG,
                        "Binding document to ViewHolder, Title: " + doc.getTitle());
                tvTitle.setText(Html.fromHtml(doc.getTitle(),
                        Html.FROM_HTML_MODE_COMPACT));
                tvContent.setText(Html.fromHtml(doc.getContent(),
                        Html.FROM_HTML_MODE_COMPACT));
                tvUrl.setText(doc.getUrl());
                tvDate.setText(doc.getDate());
                tvScore.setText(String.valueOf(result.getScore()));

                tvTitle.setOnClickListener(v->{
                    Intent intent=new Intent(v.getContext(), DetailActivity.class);
                    Log.d(TAG, "Doc to be fetched's id: "+result.getId());
                    intent.putExtra("docId", result.getId());
                    v.getContext().startActivity(intent);
                });
            } else {
                Log.e(TAG,
                        "Received null document in result: " + result.toString());
            }
        }
    }
}
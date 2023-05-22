package com.wql_2020211597.mariaandroid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private EditText etSearch;
    private Button btnSearch;
    private RecyclerView rvResults;
    private SearchResultsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        rvResults = findViewById(R.id.rvResults);

        // Initialize the RecyclerView with an empty adapter
        adapter = new SearchResultsAdapter(new ArrayList<>());
        rvResults.setLayoutManager(new LinearLayoutManager(this));
        rvResults.setAdapter(adapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost" + ":9011")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        SearchService service = retrofit.create(SearchService.class);

        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString();
            Call<List<SearchResult>> call = service.search(query);

            call.enqueue(new Callback<List<SearchResult>>() {
                @Override
                public void onResponse(Call<List<SearchResult>> call,
                                       Response<List<SearchResult>> response) {
                    if (response.isSuccessful()) {
                        List<SearchResult> results = response.body();
                        // Update the adapter with the search results
                        adapter.updateResults(results);
                    } else {
                        // Handle error
                    }
                }

                @Override
                public void onFailure(Call<List<SearchResult>> call,
                                      Throwable t) {
                    // Handle failure
                }
            });
        });
    }

    private class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsViewHolder> {
        private List<SearchResult> results;

        SearchResultsAdapter(List<SearchResult> results) {
            this.results = results;
        }

        void updateResults(List<SearchResult> results) {
            this.results = results;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public SearchResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_result, parent, false);
            return new SearchResultsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchResultsViewHolder holder,
                                     int position) {
            holder.bind(results.get(position));
        }

        @Override
        public int getItemCount() {
            return results.size();
        }
    }

    private class SearchResultsViewHolder extends RecyclerView.ViewHolder {
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
            tvTitle.setText(doc.getTitle());
            tvContent.setText(doc.getContent());
            tvUrl.setText(doc.getUrl());
            tvDate.setText(doc.getDate());
            tvScore.setText(String.valueOf(result.getScore()));
        }
    }
}
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wql_2020211597.mariaandroid.config.Config;
import com.wql_2020211597.mariaandroid.history.HistoryStorage;
import com.wql_2020211597.mariaandroid.models.Document;
import com.wql_2020211597.mariaandroid.models.HistoryEntry;
import com.wql_2020211597.mariaandroid.models.SearchResult;
import com.wql_2020211597.mariaandroid.searchservice.SearchService;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {
    private static final String TAG = "MainActivity";
    private HistoryStorage historyStorage;
    private EditText etSearch;
    private Button btnSearch;
    private RecyclerView rvResults;
    private SearchResultsAdapter adapter;

    static private final String addr = Config.getBackendAddr();
    static private final String port = Config.getBackendPort();

    private String GetBackendUrl() {
        return Config.getBackendUrl();
    }

    @androidx.annotation.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @androidx.annotation.Nullable ViewGroup container, @androidx.annotation.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // Load HistoryStorage
        historyStorage = HistoryStorage.getInstance(getContext());

        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        rvResults = view.findViewById(R.id.rvResults);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GetBackendUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        SearchService service = retrofit.create(SearchService.class);
        btnSearch.setOnClickListener(new SearchButtonClickListener(service));

        // Return the flated view
        return view;
    }

    private class SearchButtonClickListener implements View.OnClickListener {
        private final SearchService service;

        SearchButtonClickListener(SearchService service) {
            this.service = service;
        }

        @Override
        public void onClick(View v) {
            String query = etSearch.getText().toString();
            String page = "1"; // FIXME: adaptive page number
            Log.d(TAG, String.format("Query[%s], Page[%d]", query, page));
            Call<List<SearchResult>> call = service.search(query, page);

            call.enqueue(new SearchCallback(query));
        }
    }

    private class SearchCallback implements Callback<List<SearchResult>> {
        private final String query;

        SearchCallback(String query) {
            this.query = query;
        }

        @Override
        public void onResponse(Call<List<SearchResult>> call,
                               Response<List<SearchResult>> response) {
            if (response.isSuccessful()) {
                List<SearchResult> results = response.body();
                Log.d(TAG, String.format("Got [%d] results: %s", results.size(),
                        results.toString()));

                // Save search to history
                historyStorage.add(new HistoryEntry(query));

                // Update the adapter with the search results
                adapter.updateResults(results);
            } else {
                // Handle error
                Log.e(TAG, String.format(
                                "Failed to fetch response, status code[%d]",
                        response.code()));
                Log.e(TAG,
                        String.format("Error message: %s", response.message()));
            }
        }

        @Override
        public void onFailure(Call<List<SearchResult>> call, Throwable t) {
            Log.e(TAG, "Search request failed", t);
        }
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

                tvTitle.setOnClickListener(v -> {
                    Intent intent = new Intent(v.getContext(),
                            DetailActivity.class);
                    Log.d(TAG, "Doc to be fetched's id: " + result.getId());
                    intent.putExtra("docId", result.getId());
                    intent.putExtra("docTitle", Html
                            .fromHtml(result.getDoc().getTitle(),
                                    Html.FROM_HTML_MODE_COMPACT)
                            .toString());
                    v.getContext().startActivity(intent);
                });
            } else {
                Log.e(TAG,
                        "Received null document in result: " + result.toString());
            }
        }
    }

}

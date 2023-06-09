package com.wql_2020211597.mariaandroid.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wql_2020211597.mariaandroid.DetailActivity;
import com.wql_2020211597.mariaandroid.MainActivity;
import com.wql_2020211597.mariaandroid.R;
import com.wql_2020211597.mariaandroid.config.Config;
import com.wql_2020211597.mariaandroid.history.HistoryStorage;
import com.wql_2020211597.mariaandroid.models.Document;
import com.wql_2020211597.mariaandroid.models.HistoryEntry;
import com.wql_2020211597.mariaandroid.models.SearchResult;
import com.wql_2020211597.mariaandroid.services.SearchService;
import com.wql_2020211597.mariaandroid.viewmodels.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private SearchService service;
    private HistoryStorage historyStorage;
    private ProgressBar progressBar;
    private EditText etSearch;
    private Button btnSearch;
    private RecyclerView rvResults;
    private SearchResultsAdapter adapter;
    private HomeViewModel homeViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get home view model, which saves search result status
        homeViewModel = new ViewModelProvider(requireActivity()).get(
                HomeViewModel.class);
    }

    @androidx.annotation.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @androidx.annotation.Nullable ViewGroup container, @androidx.annotation.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Toolbar toolbar = view.findViewById(R.id.homeToolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        // Progress bar
        progressBar = view.findViewById(R.id.prograssBar);
        homeViewModel
                .getIsLoading()
                .observe(getViewLifecycleOwner(), aBoolean -> {
                    if (aBoolean) {
                        progressBar.setVisibility(View.VISIBLE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                });

        // Hide the back button
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity())
                    .getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(false);
            ((AppCompatActivity) getActivity())
                    .getSupportActionBar()
                    .setDisplayShowHomeEnabled(false);
        }
        toolbar.setNavigationOnClickListener(
                v -> getActivity().getSupportFragmentManager().popBackStack());

        // Load HistoryStorage
        historyStorage = HistoryStorage.getInstance(getContext());

        // Initlalize search zone
        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);

        // Initialize adapter
        adapter = new SearchResultsAdapter(
                homeViewModel.getResults().getValue() != null ? homeViewModel
                        .getResults()
                        .getValue() : new ArrayList<>());

        // Initialize RecyclerView
        rvResults = view.findViewById(R.id.rvResults);
        rvResults.setLayoutManager(new LinearLayoutManager(getContext()));
        rvResults.setAdapter(adapter); // Bind adapter

        // HomeViewModel observers. Observes search results modifications and
        // historyQuery modification
        homeViewModel.getResults().observe(getViewLifecycleOwner(), results -> {
            adapter.updateResults(results);
        });
        homeViewModel
                .getHistoryQuery()
                .observe(getViewLifecycleOwner(), query -> {
                    if (!query.isEmpty()) {
                        // Perform the search
                        homeViewModel.search(service, query, 1, null);
                        // Update etSearch text
                        setEtSearchText(query);
                        // Clear historyQuery after using it
                        homeViewModel.getHistoryQuery().setValue("");
                    }
                });

        // Initialize search service
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.getBackendUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(SearchService.class);

        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString();
            int page = 1; // FIXME: adaptive page number
            Log.d(TAG, String.format("Query[%s], Page[%d]", query, page));

            homeViewModel.search(service, query, page, null);
            historyStorage.add(new HistoryEntry(query));
        });

        // Return the flated view
        return view;
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
                    // Pass data using Bundle
                    Intent intent = new Intent((MainActivity) getActivity(),
                            DetailActivity.class);
                    intent.putExtra("docId", result.getId());
                    intent.putExtra("docTitle", Html
                            .fromHtml(result.getDoc().getTitle(),
                                    Html.FROM_HTML_MODE_COMPACT)
                            .toString());

                    Log.d(TAG, "Doc to be fetched's id: " + result.getId());

                    startActivity(intent);
                });
            } else {
                Log.e(TAG, "Received null document in result: " + result);
            }
        }
    }

    public void setEtSearchText(String text) {
        etSearch.setText(text);
    }
}

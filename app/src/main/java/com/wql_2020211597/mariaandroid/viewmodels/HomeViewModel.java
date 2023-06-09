package com.wql_2020211597.mariaandroid.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.wql_2020211597.mariaandroid.config.Config;
import com.wql_2020211597.mariaandroid.models.SearchResult;
import com.wql_2020211597.mariaandroid.services.SearchService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeViewModel extends ViewModel {
    private static final String TAG = "HomeViewModel";
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();


    private MutableLiveData<String> historyQuery = new MutableLiveData<>();
    private MutableLiveData<List<SearchResult>> resultsLiveData =
            new MutableLiveData<>();

    public void search(SearchService service, String query, int page,
                       Runnable callback) {
        isLoading.setValue(true);

        Call<List<SearchResult>> call = service.search(query, page);
        call.enqueue(new Callback<List<SearchResult>>() {
            @Override
            public void onResponse(Call<List<SearchResult>> call,
                                   Response<List<SearchResult>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    List<SearchResult> results = response.body();
                    Log.d(TAG,
                            String.format("Got %d results: %s", results.size(),
                                    results));
                    resultsLiveData.setValue(results);
                    if (callback != null) {
                        callback.run();
                    }
                } else {
                    // Handle error
                    isLoading.setValue(false);
                    Log.e(TAG, String.format(
                            "Failed to fetch response, " + "status code[%d",
                            response.code()));
                    Log.e(TAG, String.format("Error message: %s",
                            response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<SearchResult>> call, Throwable t) {
                Log.e(TAG, "Search request failed", t);
            }
        });
    }

    public void setResults(List<SearchResult> results) {
        resultsLiveData.setValue(results);
    }

    public LiveData<List<SearchResult>> getResults() {
        return resultsLiveData;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setHistoryQuery(MutableLiveData<String> historyQuery) {
        this.historyQuery = historyQuery;
    }

    public MutableLiveData<String> getHistoryQuery() {
        return historyQuery;
    }
}

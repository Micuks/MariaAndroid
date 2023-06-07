package com.wql_2020211597.mariaandroid.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.wql_2020211597.mariaandroid.models.SearchResult;

import java.util.List;

public class HomeViewModel extends ViewModel {
    private MutableLiveData<List<SearchResult>> resultsLiveData =
            new MutableLiveData<>();

    public  void setResults(List<SearchResult> results) {
        resultsLiveData.setValue(results);
    }

    public LiveData<List<SearchResult>> getResults() {
        return resultsLiveData;
    }
}

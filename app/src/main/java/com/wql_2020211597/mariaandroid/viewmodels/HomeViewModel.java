package com.wql_2020211597.mariaandroid.viewmodels;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.wql_2020211597.mariaandroid.models.Resource;
import com.wql_2020211597.mariaandroid.models.SearchResult;
import com.wql_2020211597.mariaandroid.models.Status;
import com.wql_2020211597.mariaandroid.services.ImageSearchResponse;
import com.wql_2020211597.mariaandroid.services.SearchService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {
    private static final String TAG = "HomeViewModel";
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();


    private MutableLiveData<String> historyQuery = new MutableLiveData<>();
    private MutableLiveData<List<SearchResult>> resultsLiveData =
            new MutableLiveData<>();
    private MutableLiveData<Resource<ImageSearchResponse>> searchByImageResult = new MutableLiveData<>();

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

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public LiveData<Resource<ImageSearchResponse>> searchByImage(Context context, SearchService service, Uri imageUri) {
        MutableLiveData<Resource<ImageSearchResponse>> result =
                new MutableLiveData<>();
        isLoading.setValue(true);
        result.setValue(Resource.loading(null));

        // Get InputStream from Uri
        InputStream inputStream = null;
        try {
            inputStream = context
                    .getContentResolver()
                    .openInputStream(imageUri);
        } catch (FileNotFoundException e) {
            Log.e(TAG, String.format("Error: file[]%s not found.", imageUri));
            e.printStackTrace();
        }

        // Create RequestBody instance from file
        RequestBody requestFile = null;
        if (inputStream != null) {
            byte[] bytes = new byte[0];
            try {
                bytes = inputStream.readAllBytes();
            } catch (IOException e) {
                Log.e(TAG, String.format("Error reading bytes from " + "%s",
                        imageUri));
            }
            requestFile = RequestBody.create(MediaType.parse("image/*"), bytes);
        }

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("image",
                "queryImage.jpg", requestFile);

        // Add a new network call to the queue
        Call<ImageSearchResponse> call = service.uploadImageToServer(body);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ImageSearchResponse> call,
                                   Response<ImageSearchResponse> response) {
                if (response.isSuccessful()) {
                    result.setValue(Resource.success(response.body()));
                    Resource<ImageSearchResponse> results =
                            Resource.success(response.body());
                    searchByImageResult.setValue(results);
                } else {
                    isLoading.setValue(false);
                    searchByImageResult.setValue(Resource.error(
                            String.format("Error: %s", response.message()),
                            null));

                    result.setValue(Resource.error(
                            String.format("Error: %s", response.message()),
                            null));
                }
            }

            @Override
            public void onFailure(Call<ImageSearchResponse> call, Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });

        isLoading.setValue(false);
        return result;
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

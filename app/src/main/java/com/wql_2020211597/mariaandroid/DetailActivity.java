package com.wql_2020211597.mariaandroid;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.wql_2020211597.mariaandroid.config.Config;
import com.wql_2020211597.mariaandroid.models.Document;
import com.wql_2020211597.mariaandroid.searchservice.DocumentService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailedActivity";
    private WebView wvDetailTitle;
    private WebView wvDetailContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        wvDetailTitle = findViewById(R.id.wvDetailTitle);
        wvDetailContent = findViewById(R.id.wvDetailContent);

        String resultId = getIntent().getStringExtra("docId");

        // Fetch the document from backend and display it
        fetchDocument(resultId);
    }

    private void fetchDocument(String id) {
        // Create Retrofit instane and API service
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.getBackendUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        DocumentService service = retrofit.create(DocumentService.class);
        Call<Document> call = service.getDocument(id);
        call.enqueue(new Callback<Document>() {
            @Override
            public void onResponse(Call<Document> call,
                                   Response<Document> response) {
                if (response.isSuccessful()) {
                    Document doc = response.body();
                    // Update UI with doc details
                    wvDetailTitle.loadDataWithBaseURL(null, doc.getTitle(),
                            "text/html", "utf-8", null);
                    // Prettify html with css
                    String htmlData = doc.getContent();
                    // Enable JavaScript
                    wvDetailContent.getSettings().setJavaScriptEnabled(true);
                    wvDetailContent.loadDataWithBaseURL(Config.assetsDir(),
                            htmlData, "text/html", "utf-8", null);
                } else {
                    Log.e(TAG, "Failed to fetch document: " + response.code());
                    Log.e(TAG, "Error message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Document> call, Throwable t) {
                Log.e(TAG, "Failed to fetch document", t);
            }
        });
    }
}


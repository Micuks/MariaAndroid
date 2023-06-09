package com.wql_2020211597.mariaandroid;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.wql_2020211597.mariaandroid.config.Config;
import com.wql_2020211597.mariaandroid.models.Document;
import com.wql_2020211597.mariaandroid.services.DocumentService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";
    private WebView wvDetailContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.detailToolbar);
        if(toolbar == null) {
            Log.e(TAG, String.format("Error: toolbar[%s] is null",
                    R.id.detailToolbar));
        }
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getSupportFragmentManager().popBackStack();
                onBackPressed();
            }
        });

        wvDetailContent = findViewById(R.id.wvDetailContent);

        String resultId = getIntent().getStringExtra("docId");
        String title = getIntent().getStringExtra("docTitle");

        getSupportActionBar().setTitle(title);

        // Fetch the document and display it
        fetchDocument(resultId);
    }

    private void fetchDocument(String docId) {
        // Create retrofit instance and API service
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.getBackendUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        DocumentService service = retrofit.create(DocumentService.class);
        Call<Document> call = service.getDocument(docId);
        call.enqueue(new Callback<Document>() {
            @Override
            public void onResponse(Call<Document> call,
                                   Response<Document> response) {
                if (response.isSuccessful()) {
                    Document doc = response.body();
                    // Enable javascript
                    wvDetailContent.getSettings().setJavaScriptEnabled(true);
                    // Prettify html with css
                    String htmlData = doc.getContent();
                    wvDetailContent.loadDataWithBaseURL(Config.assetsDir(),
                            htmlData, "text/html", "utf-8", null);
                } else {
                    Log.e(TAG, String.format(
                            "Failed to fetch document. Status code[%d",
                            response.code()));
                    Log.e(TAG, String.format("Error message: %s",
                            response.message()));
                }
            }

            @Override
            public void onFailure(Call<Document> call, Throwable t) {
                Log.e(TAG, String.format("Failed to fetch document"), t);
            }
        });
    }
}

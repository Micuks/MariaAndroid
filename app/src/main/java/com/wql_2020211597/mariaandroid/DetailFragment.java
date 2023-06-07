package com.wql_2020211597.mariaandroid;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.wql_2020211597.mariaandroid.config.Config;
import com.wql_2020211597.mariaandroid.models.Document;
import com.wql_2020211597.mariaandroid.searchservice.DocumentService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailFragment extends Fragment {
    private static final String TAG = "DetailFragment";
    private WebView wvDetailTitle;
    private WebView wvDetailContent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container,
                false);

        Toolbar toolbar = view.findViewById(R.id.detailToolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity())
                .getSupportActionBar()
                .setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity())
                .getSupportActionBar()
                .setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppCompatActivity) getActivity()).getSupportFragmentManager().popBackStack();
            }
        });

        wvDetailTitle = view.findViewById(R.id.wvDetailTitle);
        wvDetailContent = view.findViewById(R.id.wvDetailContent);

        String resultId = getArguments().getString("docId");
        String title = getArguments().getString("docTitle");
        ((AppCompatActivity) getActivity())
                .getSupportActionBar()
                .setTitle(title);

        // Fetch the document from backend and display it
        fetchDocument(resultId);

        return view;
    }

    private void fetchDocument(String id) {
        // Create Retrofit instance and API service
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.getBackendUrl())
                .addConverterFactory(
                        GsonConverterFactory.create())
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
                    // Enable javascript
                    wvDetailContent.getSettings().setJavaScriptEnabled(true);
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
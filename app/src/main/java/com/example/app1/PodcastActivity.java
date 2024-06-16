package com.example.app1;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PodcastActivity extends AppCompatActivity {
    private EditText searchEditText;
    private Button searchButton;
    private ListView podcastListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast);

        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        podcastListView = findViewById(R.id.podcastListView);

        searchButton.setOnClickListener(v -> {
            String query = searchEditText.getText().toString();
            if (!query.isEmpty()) {
                searchPodcasts(query);
            } else {
                Toast.makeText(PodcastActivity.this, "Lütfen bir arama terimi girin.", Toast.LENGTH_SHORT).show();
            }
        });

        // Örnek olarak en iyi podcast'leri getirme butonu
        Button getBestPodcastsButton = findViewById(R.id.getBestPodcastsButton);
        getBestPodcastsButton.setOnClickListener(v -> {
            getBestPodcasts();
        });
    }

    private void searchPodcasts(String query) {
        ListenNotesAPI apiService = ApiClient2.getClient().create(ListenNotesAPI.class);
        Call<PodcastResponse> call = apiService.searchPodcasts(query);

        call.enqueue(new Callback<PodcastResponse>() {
            @Override
            public void onResponse(Call<PodcastResponse> call, Response<PodcastResponse> response) {
                if (response.isSuccessful()) {
                    List<String> podcastTitles = new ArrayList<>();
                    for (PodcastResponse.Podcast podcast : response.body().getResults()) {
                        podcastTitles.add(podcast.getTitleOriginal());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(PodcastActivity.this, android.R.layout.simple_list_item_1, podcastTitles);
                    podcastListView.setAdapter(adapter);
                } else {
                    Toast.makeText(PodcastActivity.this, "Podcast arama hatası: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PodcastResponse> call, Throwable t) {
                Toast.makeText(PodcastActivity.this, "Podcast arama başarısız: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getBestPodcasts() {
        ListenNotesAPI apiService = ApiClient2.getClient().create(ListenNotesAPI.class);
        Call<BestPodcastsResponse> call = apiService.getBestPodcasts();

        call.enqueue(new Callback<BestPodcastsResponse>() {
            @Override
            public void onResponse(Call<BestPodcastsResponse> call, Response<BestPodcastsResponse> response) {
                if (response.isSuccessful()) {
                    List<String> bestPodcasts = new ArrayList<>();
                    // Örnek olarak, en iyi podcast'leri alarak listeye ekleyebilirsiniz
                    // response.body() ile JSON'dan dönen veriyi işleyebilirsiniz
                    // Detayları BestPodcastsResponse sınıfına göre ayarlayabilirsiniz
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(PodcastActivity.this, android.R.layout.simple_list_item_1, bestPodcasts);
                    podcastListView.setAdapter(adapter);
                } else {
                    Toast.makeText(PodcastActivity.this, "En iyi podcast'leri getirme hatası: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BestPodcastsResponse> call, Throwable t) {
                Toast.makeText(PodcastActivity.this, "En iyi podcast'leri getirme başarısız: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
package com.example.app1;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AudioBookMusicActivity extends AppCompatActivity {
    private ListView musicListView;
    private ListView podcastListView;
    private MusicAdapter musicAdapter;
    private MediaPlayer mediaPlayer;
    private EditText searchEditText;
    private Button searchButton;
    private Button audioBooksButton;
    private Button musicButton;
    private LinearLayout musicLayout;
    private LinearLayout audioBooksLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_book_music);

        musicListView = findViewById(R.id.musicListView);
        podcastListView = findViewById(R.id.podcastListView);
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        audioBooksButton = findViewById(R.id.audioBooksButton);
        musicButton = findViewById(R.id.musicButton);
        musicLayout = findViewById(R.id.musicLayout);
        audioBooksLayout = findViewById(R.id.audioBooksLayout);
        mediaPlayer = new MediaPlayer();

        searchButton.setOnClickListener(v -> {
            String query = searchEditText.getText().toString();
            if (!query.isEmpty()) {
                searchMusic(query);
            } else {
                Toast.makeText(AudioBookMusicActivity.this, "Lütfen bir arama terimi girin.", Toast.LENGTH_SHORT).show();
            }
        });

        audioBooksButton.setOnClickListener(v -> {
            audioBooksLayout.setVisibility(View.VISIBLE);
            musicLayout.setVisibility(View.GONE);
            searchPodcasts("audiobook");
        });

        musicButton.setOnClickListener(v -> {
            audioBooksLayout.setVisibility(View.GONE);
            musicLayout.setVisibility(View.VISIBLE);
        });
    }

    private void searchMusic(String query) {
        DeezerService service = ApiClient.getClient().create(DeezerService.class);
        Call<DeezerResponse> call = service.searchTracks(query);

        call.enqueue(new Callback<DeezerResponse>() {
            @Override
            public void onResponse(Call<DeezerResponse> call, Response<DeezerResponse> response) {
                if (response.isSuccessful()) {
                    List<DeezerResponse.Track> tracks = response.body().getData();
                    musicAdapter = new MusicAdapter(AudioBookMusicActivity.this, tracks, new MusicAdapter.OnTrackClickListener() {
                        @Override
                        public void onTrackClick(DeezerResponse.Track track) {
                            playTrack(track.getPreview());
                        }
                    });
                    musicListView.setAdapter(musicAdapter);
                } else {
                    Toast.makeText(AudioBookMusicActivity.this, "Müzik arama hatası: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DeezerResponse> call, Throwable t) {
                Toast.makeText(AudioBookMusicActivity.this, "Müzik arama başarısız: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchPodcasts(String query) {
        ListenNotesAPI service = ApiClient2.getClient().create(ListenNotesAPI.class);
        Call<PodcastResponse> call = service.searchPodcasts(query);

        call.enqueue(new Callback<PodcastResponse>() {
            @Override
            public void onResponse(Call<PodcastResponse> call, Response<PodcastResponse> response) {
                if (response.isSuccessful()) {
                    List<PodcastResponse.Podcast> podcasts = response.body().getResults();
                    List<DeezerResponse.Track> tracks = new ArrayList<>();
                    for (PodcastResponse.Podcast podcast : podcasts) {
                        DeezerResponse.Track track = new DeezerResponse.Track();
                        track.setTitle(podcast.getTitleOriginal());
                        track.setPreview(podcast.getAudio());
                        tracks.add(track);
                    }
                    musicAdapter = new MusicAdapter(AudioBookMusicActivity.this, tracks, new MusicAdapter.OnTrackClickListener() {
                        @Override
                        public void onTrackClick(DeezerResponse.Track track) {
                            playTrack(track.getPreview());
                        }
                    });
                    podcastListView.setAdapter(musicAdapter);
                } else {
                    Toast.makeText(AudioBookMusicActivity.this, "Podcast arama hatası: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PodcastResponse> call, Throwable t) {
                Toast.makeText(AudioBookMusicActivity.this, "Podcast arama başarısız: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void playTrack(String audioUrl) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(AudioBookMusicActivity.this, "Müziği çalarken hata oluştu.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}

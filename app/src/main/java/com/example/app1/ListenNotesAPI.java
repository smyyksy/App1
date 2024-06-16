package com.example.app1;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ListenNotesAPI {
    @GET("search") // Endpoint'i doğru şekilde belirtin
    Call<PodcastResponse> searchPodcasts(
            @Query("q") String query
    );

    @GET("best_podcasts") // Endpoint'i doğru şekilde belirtin
    Call<BestPodcastsResponse> getBestPodcasts();
}

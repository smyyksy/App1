package com.example.app1;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BestPodcastsResponse {
    @SerializedName("podcasts")
    private List<Podcast> podcasts;

    public List<Podcast> getPodcasts() {
        return podcasts;
    }

    public static class Podcast {
        @SerializedName("id")
        private String id;

        @SerializedName("title")
        private String title;

        // Diğer alanlar buraya eklenebilir

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }
    }
}

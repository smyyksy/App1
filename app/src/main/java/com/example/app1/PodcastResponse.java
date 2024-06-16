package com.example.app1;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PodcastResponse {

    @SerializedName("results")
    private List<Podcast> results;

    public List<Podcast> getResults() {
        return results;
    }

    public static class Podcast {
        @SerializedName("title_original")
        private String titleOriginal;

        @SerializedName("audio")
        private String audio;

        public String getTitleOriginal() {
            return titleOriginal;
        }

        public String getAudio() {
            return audio;
        }
    }
}

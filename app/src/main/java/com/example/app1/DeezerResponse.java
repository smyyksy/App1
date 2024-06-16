package com.example.app1;

import java.util.List;

public class DeezerResponse {
    private List<Track> data;

    public List<Track> getData() {
        return data;
    }

    public void setData(List<Track> data) {
        this.data = data;
    }

    public static class Track {
        private String title;
        private String artistName;
        private String albumCover;
        private String preview;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getArtistName() {
            return artistName;
        }

        public void setArtistName(String artistName) {
            this.artistName = artistName;
        }

        public String getAlbumCover() {
            return albumCover;
        }

        public void setAlbumCover(String albumCover) {
            this.albumCover = albumCover;
        }

        public String getPreview() {
            return preview;
        }

        public void setPreview(String preview) {
            this.preview = preview;
        }
    }
}

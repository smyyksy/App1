package com.example.app1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MusicAdapter extends ArrayAdapter<DeezerResponse.Track> {
    private OnTrackClickListener listener;

    public interface OnTrackClickListener {
        void onTrackClick(DeezerResponse.Track track);
    }

    public MusicAdapter(Context context, List<DeezerResponse.Track> tracks, OnTrackClickListener listener) {
        super(context, 0, tracks);
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DeezerResponse.Track track = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_music, parent, false);
        }

        TextView titleTextView = convertView.findViewById(R.id.trackTitle);
        TextView artistTextView = convertView.findViewById(R.id.trackArtist);
        ImageView albumImageView = convertView.findViewById(R.id.albumCover);

        titleTextView.setText(track.getTitle());
        artistTextView.setText(track.getArtistName());
        Glide.with(getContext()).load(track.getAlbumCover()).into(albumImageView);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onTrackClick(track);
                }
            }
        });

        return convertView;
    }
}

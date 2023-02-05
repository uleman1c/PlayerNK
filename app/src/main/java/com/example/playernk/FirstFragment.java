package com.example.playernk;

import static android.content.Context.AUDIO_SERVICE;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.playernk.databinding.FragmentFirstBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    private JSONArray songs;

    MediaPlayer mediaPlayer;
    AudioManager am;
    CheckBox chbLoop;

    int curSongIndex;

    String url;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        songs = new JSONArray();

        url = "http://188.120.243.243:3001/";

        am = (AudioManager) getActivity().getSystemService(AUDIO_SERVICE);
        chbLoop = binding.chbLoop;

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                curSongIndex = curSongIndex + 1;

                if (curSongIndex >= songs.length()){
                    curSongIndex = 0;
                }

                PlaySong();

            }
        });

        chbLoop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (mediaPlayer != null)
                    mediaPlayer.setLooping(isChecked);
            }
        });

        binding.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                curSongIndex = 0;

                PlaySong();


            }
        });

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                curSongIndex = curSongIndex + 1;

                if (curSongIndex == songs.length()){

                    curSongIndex = 0;

                }

                PlaySong();


            }
        });

        binding.btnPred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                curSongIndex = curSongIndex - 1;

                if (curSongIndex == -1){

                    curSongIndex = songs.length() - 1;

                }

                PlaySong();


            }
        });

        binding.btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying())
                    mediaPlayer.pause();
            }
        });

        binding.btnResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mediaPlayer.isPlaying())
                    mediaPlayer.start();

            }
        });

        binding.btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
            }
        });

        binding.btnBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 3000);
            }
        });

        binding.btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 3000);
            }
        });

        binding.btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                binding.textViewSongInfo.setText(
                        "Playing " + mediaPlayer.isPlaying()
                        + ", Time " + mediaPlayer.getCurrentPosition() + " / "
                                + mediaPlayer.getDuration()
                        + ", Looping " + mediaPlayer.isLooping()
                        + ", Volume " + am.getStreamVolume(AudioManager.STREAM_MUSIC)
                );

            }
        });

        UpdateSongs();

    }

    private void PlaySong() {
        String nameSong = "";
        String idSong = "";
        try {

            JSONObject song = songs.getJSONObject(curSongIndex);

            nameSong = song.getString("name");
            idSong = song.getString("id");

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        binding.textviewNameSong.setText(nameSong);

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(getContext(), Uri.parse(url + "file?id=" + idSong));
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
            mediaPlayer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void UpdateSongs(){

        songs = new JSONArray();

        VolleyRequestQueue.executeRequest(getContext(), url, new JsonCallback() {
            @Override
            public void CallbackObject(JSONObject response) {

                try {
                    songs = response.getJSONArray("rows");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void CallbackArray(JSONArray jsonArray) {

            }
        });


//                NavHostFragment.findNavController(FirstFragment.this)
//                        .navigate(R.id.action_FirstFragment_to_SecondFragment);



    }

}
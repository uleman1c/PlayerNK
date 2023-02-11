package com.example.playernk;

import static android.content.Context.AUDIO_SERVICE;

import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playernk.databinding.FragmentFirstBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    private ArrayList<Song> songs;
    private SongsAdapter songsAdapter;

    MediaPlayer mediaPlayer;
    AudioManager am;
    CheckBox chbLoop;

    Boolean aquare, random, startPlay, newOnly;

    Date lastStart;

    int curSongIndex;

    String url;

    TimerTask timerTask;
    Timer timer;

    Handler handler;

    GetSongSettings getSongSettings;

    RecyclerView rvList;

    String selectedStyles;

    Integer aquareStart, aquareRange;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        url = Conn.addr;

        DB db = new DB(getContext());

        db.open();

        random = db.getConstant("random").equals("true");

        newOnly = db.getConstant("newOnly").equals("true");

        aquare = db.getConstant("aquare").equals("true");

        aquareStart = Integer.valueOf(db.getConstant("aquareStart"));

        aquareRange = Integer.valueOf(db.getConstant("aquareRange"));

        selectedStyles = db.getConstant("selectedStyles");

        db.close();





        startPlay = false;

        songs = new ArrayList<>();
        songsAdapter = new SongsAdapter(getContext(), songs);

        timerTask = new TimerTask(){

            @Override
            public void run() {

                NextSong();

            }
        };

//        timer = new Timer("Timer");
//
//        long delay = 10000L;
//        timer.schedule(timerTask, delay);


        handler = new Handler();
        Thread t = new Thread(new Runnable() {
            public void run() {

                handler.post(nextSong);

            }
        });
        t.start();

        getSongSettings = new GetSongSettings(new ArrayList<>());

        UpdateSongs();




        getParentFragmentManager().setFragmentResultListener("selectStyle", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {

                aquare = bundle.getBoolean("aquaring");
                random = bundle.getBoolean("random");
                newOnly = bundle.getBoolean("newOnly");

                selectedStyles = bundle.getString("selectedStyles");

                aquareStart = bundle.getInt("aquareStart");
                aquareRange = bundle.getInt("aquareRange");


                UpdateSongs();

            }
        });
    }

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

        am = (AudioManager) getActivity().getSystemService(AUDIO_SERVICE);
        chbLoop = binding.chbLoop;

        rvList = binding.rvList;

        rvList.setAdapter(songsAdapter);

        binding.btnAquare.setBackgroundColor(aquare ? Color.parseColor("#00FF00") : Color.parseColor("#0000FF"));

        songsAdapter.setOnItemClickListener(new SongsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Song song) {

                curSongIndex = songs.indexOf(song);

                PlaySong();

            }
        });

        songsAdapter.setOnItemLongClickListener(new SongsAdapter.OnItemLongClickListener() {
            @Override
            public void onLongItemClick(Song item) {

                item.favorite = !item.favorite;

                songsAdapter.notifyDataSetChanged();

                JSONObject params = new JSONObject();
                DefaultJson.put(params,"file_id", item.id);
                DefaultJson.put(params,"mode", item.favorite ? "add" : "del");

                VolleyRequestQueue.executeRequestPost(getContext(), url + "favorites", params, new JsonCallback() {
                    @Override
                    public void CallbackObject(JSONObject response) {

//                        try {
//                            Song.getFromJsonArray(songs, response.getJSONArray("rows"));
//                        } catch (JSONException e) {
//                            throw new RuntimeException(e);
//                        }

                        songsAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void CallbackArray(JSONArray jsonArray) {

                    }
                });




            }
        });

        binding.btnStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.StyleFragment);



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

        binding.btnAquare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                aquare = !aquare;

                DB db = new DB(getContext());

                db.open();

                db.updateConstant("aquare", aquare ? "true" : "false");

                db.close();

                binding.btnAquare.setBackgroundColor(aquare ? Color.parseColor("#00FF00") : Color.parseColor("#0000FF"));

                //PlaySong();


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

                NextSong();


            }
        });

        binding.btnPred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                curSongIndex = curSongIndex - 1;

                if (curSongIndex == -1){

                    curSongIndex = songs.size() - 1;

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
                if (mediaPlayer != null && !mediaPlayer.isPlaying())
                    mediaPlayer.start();

            }
        });

        binding.btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                aquare = false;

                mediaPlayer.stop();
            }
        });

        binding.btnFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.FavoritesFragment);

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

    }

    final Runnable nextSong = new Runnable() {
        public void run() {

            Date curDate = new Date();
            if (aquare && lastStart != null
                    && curDate.getTime()-lastStart.getTime() > aquareRange * 1000) {

                NextSong();

            }

            handler.postDelayed(nextSong, aquareStart * 1000);

        }
    };



    private void NextSong() {
        curSongIndex = curSongIndex + 1;

        if (curSongIndex == songs.size()){

            curSongIndex = 0;

        }

        PlaySong();
    }

    private void PlaySong() {

        PlaySong(0);

    }

    private void PlaySong(int seekTo) {

        if (songs.size() == 0)
            return;

        Song curSong = songs.get(curSongIndex);

        for (Song song: songs
             ) {
            song.nowPlaying = false;
        }

        curSong.nowPlaying = true;

        songsAdapter.notifyDataSetChanged();

        rvList.scrollToPosition(curSongIndex == 0 ? 0 : curSongIndex + 3);

        //binding.textviewNameSong.setText(curSong.name);

        releaseMP();

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

                if (curSongIndex >= songs.size()){
                    curSongIndex = 0;
                }

                PlaySong();

            }
        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

                mediaPlayer.start();

                if (aquare){

                    mediaPlayer.seekTo(Math.toIntExact(30000));

                    lastStart = new Date();

                }
            }
        });


        VolleyRequestQueue.executeRequestPost(getContext(), url + "file?id=" + curSong.id,
                new JsonCallback() {
                    @Override
                    public void CallbackObject(JSONObject response) {

//                        try {
//                            Song.getFromJsonArray(songs, response.getJSONArray("rows"));
//                        } catch (JSONException e) {
//                            throw new RuntimeException(e);
//                        }

                        try {
                            mediaPlayer.reset();
                            mediaPlayer.setDataSource(getContext(), Uri.parse(url + "file?id=" + curSong.id));
                            mediaPlayer.prepare(); // might take long! (for buffering, etc)


                        } catch (IOException e) {
                            //throw new RuntimeException(e);
                        }

                    }

                    @Override
                    public void CallbackArray(JSONArray jsonArray) {

                    }
                });



    }

    private void releaseMP() {
        if (mediaPlayer != null){

            mediaPlayer.release();

            mediaPlayer = null;

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        releaseMP();

    }

    public void UpdateSongs(){

        getSongSettings.styles.clear();

        if (!selectedStyles.isEmpty()){

            for (String curStyle: selectedStyles.split(",")
            ) {

                getSongSettings.styles.add(curStyle);
            }
        }

        songs.clear();

        JSONObject params = new JSONObject();
        DefaultJson.put(params,"limit", 200);
        DefaultJson.put(params,"random", random);
        DefaultJson.put(params,"newOnly", newOnly);

        setFilter(params, newOnly);

        setOrder(params);

        VolleyRequestQueue.executeRequestPost(getContext(), url + "files", params, new JsonCallback() {
            @Override
            public void CallbackObject(JSONObject response) {

                try {
                    Song.getFromJsonArray(songs, response.getJSONArray("rows"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }





                songsAdapter.notifyDataSetChanged();

                curSongIndex = 0;

                PlaySong();

            }

            @Override
            public void CallbackArray(JSONArray jsonArray) {

            }
        });


//        VolleyRequestQueue.executeRequest(getContext(), url + (getSongSettings.styles.size() == 0 ? "" : "?style=" + getSongSettings.styles.get(0)),
//                new JsonCallback() {
//            @Override
//            public void CallbackObject(JSONObject response) {
//
//                try {
//                    Song.getFromJsonArray(songs, response.getJSONArray("rows"));
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
//
//
//
//
//
//                songsAdapter.notifyDataSetChanged();
//
//            }
//
//            @Override
//            public void CallbackArray(JSONArray jsonArray) {
//
//            }
//        });
//

    }

    private void setFilter(JSONObject params, Boolean newOnly) {

        JSONArray jsonArray = new JSONArray();

        if (newOnly){
            jsonArray.put(" requests.song_id is null ");

        }

        String selectedStyles = "";

        for (String style : getSongSettings.styles
             ) {
            selectedStyles = selectedStyles + (selectedStyles.isEmpty() ? "" : " or ") + " style = '" + style + "'";
        }

        if (getSongSettings.styles.size() > 0) {

            jsonArray.put("(" + selectedStyles + ")");

        }

        DefaultJson.put(params,"where", jsonArray.toString() );
    }

    private void setOrder(JSONObject params) {


            JSONArray jsonArray = new JSONArray();

            jsonArray.put(" name  ");

            DefaultJson.put(params,"order", jsonArray.toString() );

    }

}
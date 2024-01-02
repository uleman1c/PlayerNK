package com.example.playernk;

import static android.content.Context.AUDIO_SERVICE;

import static androidx.core.content.ContextCompat.registerReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playernk.databinding.FragmentFirstBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    private ArrayList<Song> songs;
    private SongsAdapter songsAdapter;

    //MediaPlayer mediaPlayer;
    AudioManager am;
    CheckBox chbLoop;

    Boolean aquare, random, startPlay, newOnly, favorites;

    Date lastStart;

    int curSongIndex;

    String url, userName;

    TimerTask timerTask;
    Timer timer;

    Handler handler;

    GetSongSettings getSongSettings;

    RecyclerView rvList;

    String selectedStyles;

    Integer aquareStart, aquareRange;

    private BroadcastReceiver broadcastReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String command = intent.getStringExtra("command");

                if (command.equals("endOfSong")){

                    NextSong();

                }

            }
        };

        IntentFilter filter = new IntentFilter("android.intent.action.playernk.fromMP");
        getContext().registerReceiver(broadcastReceiver, filter);



        url = Conn.addr;

        DB db = new DB(getContext());

        db.open();

        random = db.getConstant("random").equals("true");

        newOnly = db.getConstant("newOnly").equals("true");

        favorites = db.getConstant("favorites").equals("true");

        aquare = db.getConstant("aquare").equals("true");

        aquareStart = Integer.valueOf(db.getConstant("aquareStart"));

        aquareRange = Integer.valueOf(db.getConstant("aquareRange"));

        selectedStyles = db.getConstant("selectedStyles");

        userName = db.getConstant("userName");

        db.close();


        getParentFragmentManager().setFragmentResultListener("selectSongFromHistory", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {



                String id = result.getString("id");
                String name = result.getString("name");
                String ext = result.getString("ext");
                String style = result.getString("style");
                String description = result.getString("description");

                int foundSongIndex = -1;

                for (Song song: songs) {
                    if (song.id.equals(id)){
                        foundSongIndex = songs.indexOf(song);
                    }
                }

                if (foundSongIndex == -1){
                    songs.add(new Song(id, name, ext, style, description, false ));

                    foundSongIndex = songs.size() - 1;
                }

                curSongIndex = foundSongIndex;
                PlaySong();

            }
        });





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

        getParentFragmentManager().setFragmentResultListener("userLogin", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {

                userName = bundle.getString("userName");

                setTextAndButtons();

            }
        });



    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);

        Toolbar toolbar = binding.toolbar2;

        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.setSupportActionBar(toolbar);

        appCompatActivity.getSupportActionBar().setTitle("");


        Spinner spinnerModes = binding.getRoot().findViewById(R.id.spinnerModes);

        ArrayAdapter<CharSequence> adapter= new ArrayAdapter<CharSequence>(getContext(), R.layout.modes_spinner_selected_item,
                        android.R.id.text1,
                        getActivity().getResources().getStringArray(R.array.modes));
        adapter.setDropDownViewResource(R.layout.modes_spinner_item);

        spinnerModes.setAdapter(adapter);
        spinnerModes.setDropDownVerticalOffset(60);

        spinnerModes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                ((TextView)spinnerModes.getSelectedView().findViewById(android.R.id.text1)).setTextColor(getResources().getColor(R.color.white));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                ((TextView)spinnerModes.getSelectedView().findViewById(android.R.id.text1)).setTextColor(getResources().getColor(R.color.white));
            }
        });

        spinnerModes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    ((TextView)spinnerModes.getSelectedView().findViewById(android.R.id.text1)).setTextColor(getResources().getColor(R.color.colorAccent));
                }


                return false;
            }
        });

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        am = (AudioManager) getActivity().getSystemService(AUDIO_SERVICE);
        chbLoop = binding.chbLoop;

        rvList = binding.rvList;

        rvList.setAdapter(songsAdapter);

        binding.btnAquare.setBackgroundColor(aquare ? Color.parseColor("#00FF00") : Color.parseColor("#0000FF"));
        binding.btnFavorites.setBackgroundColor(favorites ? Color.parseColor("#00FF00") : Color.parseColor("#0000FF"));

        setTextAndButtons();


        binding.ibSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                binding.spinnerModes.setVisibility(View.GONE);
                binding.ibSearch.setVisibility(View.GONE);

                binding.clSearch.setVisibility(View.VISIBLE);


            }
        });

        binding.ibSearch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                binding.spinnerModes.setVisibility(View.VISIBLE);
                binding.ibSearch.setVisibility(View.VISIBLE);

                binding.etSearch.setText("");
                binding.clSearch.setVisibility(View.GONE);


            }
        });

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

        binding.ibLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.loginFragment);



            }
        });

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                JSONObject params = new JSONObject();
                DefaultJson.put(params,"limit", 200);
                DefaultJson.put(params,"random", random);
                DefaultJson.put(params,"newOnly", favorites ? false : newOnly);
                DefaultJson.put(params,"favorites", favorites);

                String curSearch = binding.etSearch.getText().toString();

                setFilter(params, favorites ? false : newOnly, curSearch);

                VolleyRequestQueue.executeRequestPost(getContext(), url + "files", params, new JsonCallback() {
                    @Override
                    public void CallbackObject(JSONObject response) {

                        songs.clear();

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



            }
        });

        binding.etSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                Log.d("binding.etSearch.setOnKeyListener", keyEvent.toString());

                if (keyEvent.getAction() == KeyEvent.ACTION_UP
                    || keyEvent.getKeyCode() == KeyEvent.KEYCODE_DEL){



                }

                return false;
            }
        });

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), "Warning", "Are you sure you want to log out?", new Bundle(), new BundleMethodInterface() {
                    @Override
                    public void callMethod(Bundle arguments) {

                        userName = "";

                        DB db = new DB(getContext());
                        db.open();

                        db.updateConstant("userName", userName);

                        db.close();

                        setTextAndButtons();
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

                SetLoopBackground(isChecked);

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

                SetAquareBackground(aquare);

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

                SetPauseBackground();
            }
        });

        binding.ibPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SetPauseBackground();
            }
        });
        binding.btnResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SetStartBackground();

            }
        });

        binding.btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                aquare = false;

                SetStopBackground();
            }
        });

        binding.ibFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                favorites = !favorites;

                DB db = new DB(getContext());

                db.open();

                db.updateConstant("favorites", favorites ? "true" : "false");

                db.close();

                UpdateSongs();

                binding.btnFavorites.setBackgroundColor(favorites ? Color.parseColor("#00FF00") : Color.parseColor("#0000FF"));


            }
        });

        binding.btnFavorites.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.FavoritesFragment);

                return false;
            }
        });

        binding.btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.hystoryFragment);

            }
        });


        binding.btnBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SetSeekToBackground(-3000);

            }
        });

        binding.btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SetSeekToBackground(3000);

            }
        });

        binding.btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                binding.textViewSongInfo.setText(
                        "Playing " + "" //mediaPlayer.isPlaying()
                        + ", Time " + "" //mediaPlayer.getCurrentPosition() + " / "
                                + "" //mediaPlayer.getDuration()
                        + ", Looping " + "" //mediaPlayer.isLooping()
                        + ", Volume " + am.getStreamVolume(AudioManager.STREAM_MUSIC)
                );

            }
        });

    }

    private void SetAquareBackground(Boolean aquare) {

        Intent intent = new Intent();
        intent.putExtra("command", "setAquare");
        intent.putExtra("aquare", aquare);

        SendTaskToBackGround(intent);

    }

    private void SetSeekToBackground(int delta) {

        Intent intent = new Intent();
        intent.putExtra("command", "seekTo");
        intent.putExtra("delta", delta);

        SendTaskToBackGround(intent);

    }

    private void SetStopBackground() {

        Intent intent = new Intent();
        intent.putExtra("command", "stop");

        SendTaskToBackGround(intent);

    }

    private void setTextAndButtons() {
        binding.tvUser.setText(userName);

        if (userName.isEmpty()){
            binding.btnLogin.setVisibility(View.VISIBLE);
            binding.btnLogout.setVisibility(View.GONE);
        } else {
            binding.btnLogin.setVisibility(View.GONE);
            binding.btnLogout.setVisibility(View.VISIBLE);
        }
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

    private void SendTaskToBackGround(Intent intent){

        intent.setAction("android.intent.action.playernk.MP");
        getContext().sendBroadcast(intent);

    }

    private void PlaySongBackground(String curSongid, Boolean aquare){

        Intent intent = new Intent();
        intent.putExtra("command", "playSong");
        intent.putExtra("songId", curSongid);
        intent.putExtra("aquare", aquare);

        SendTaskToBackGround(intent);

    }

    private void SetLoopBackground(Boolean loop){

        Intent intent = new Intent();
        intent.putExtra("command", "setLoop");
        intent.putExtra("loop", loop);

        SendTaskToBackGround(intent);

    }
    private void SetStartBackground(){

        Intent intent = new Intent();
        intent.putExtra("command", "start");

        SendTaskToBackGround(intent);

    }

    private void SetPauseBackground(){

        Intent intent = new Intent();
        intent.putExtra("command", "pause");

        SendTaskToBackGround(intent);

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

        lastStart = new Date();

        if (true) {

            PlaySongBackground(curSong.id, aquare);

        } else {

            releaseMP();

/*
            mediaPlayer = new MediaPlayer();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaPlayer.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                );
            }

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {

                    curSongIndex = curSongIndex + 1;

                    if (curSongIndex >= songs.size()) {
                        curSongIndex = 0;
                    }

                    PlaySong();

                }
            });

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {

                    mediaPlayer.start();

                    if (aquare) {

                        mediaPlayer.seekTo(Math.toIntExact(30000));

                        lastStart = new Date();

                    }
                }
            });


            VolleyRequestQueue.executeRequestPost(getContext(), url + "file?id=" + curSong.id
                            + "&appid=" + DB.getDbConstant(getContext(), "appId")
                            + "&userid=" + DB.getDbConstant(getContext(), "userId"),
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
*/


        }
    }

    private void releaseMP() {
/*
        if (mediaPlayer != null){

            mediaPlayer.release();

            mediaPlayer = null;

        }
*/
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
        DefaultJson.put(params,"newOnly", favorites ? false : newOnly);
        DefaultJson.put(params,"favorites", favorites);

        setFilter(params, favorites ? false : newOnly, "");

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

    private void setFilter(JSONObject params, Boolean newOnly, String search) {

        JSONArray jsonArray = new JSONArray();

        if (newOnly){
            jsonArray.put(" requests.song_id is null ");

        }

        if (!search.isEmpty()){
            jsonArray.put(" files.name like '%" + search + "%'");

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
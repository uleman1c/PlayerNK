package com.example.playernk;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class BackgroundSoundService extends Service {

    MediaPlayer mediaPlayer;

    private BroadcastReceiver broadcastReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String curSongid = intent.getStringExtra("songId");

                String url = Conn.addr;

                VolleyRequestQueue.executeRequestPost(context, url + "file?id=" + curSongid
                        + "&appid=" + DB.getDbConstant(context, "appId")
                        + "&userid=" + DB.getDbConstant(context, "userId"),
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
                            mediaPlayer.setDataSource(context, Uri.parse(url + "file?id=" + curSongid));

                            mediaPlayer.setLooping(true);

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
        };

        IntentFilter filter = new IntentFilter("android.intent.action.playernk.MP");
        registerReceiver(broadcastReceiver, filter);



        mediaPlayer = new MediaPlayer();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );
        }

//        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mediaPlayer) {
//
//                curSongIndex = curSongIndex + 1;
//
//                if (curSongIndex >= songs.size()){
//                    curSongIndex = 0;
//                }
//
//                PlaySong();
//
//            }
//        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

                mediaPlayer.start();

/*
                if (aquare){

                    mediaPlayer.seekTo(Math.toIntExact(30000));

                    lastStart = new Date();

                }
*/
            }
        });



    }

    @Override
    public void onDestroy() {

        mediaPlayer.stop();
        mediaPlayer.release();

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        mediaPlayer.start();

        return super.onStartCommand(intent, flags, startId);
    }
}

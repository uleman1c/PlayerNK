package com.example.playernk;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class BackgroundSoundService extends Service {

    MediaPlayer mediaPlayer;

    private BroadcastReceiver broadcastReceiver;
    private NotificationManager notificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

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

    void sendNotif() {

//        Intent intent = new Intent(this, AlertDetails.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(getBaseContext(), getString(R.string.playernk_channel))
                // Show controls on lock screen even when user hides sensitive content.
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                .setSmallIcon(R.drawable.ic_stat_player)
//                // Add media control buttons that invoke intents in your media service
//                .addAction(R.drawable.ic_prev, "Previous", prevPendingIntent) // #0
//                .addAction(R.drawable.ic_pause, "Pause", pausePendingIntent)  // #1
//                .addAction(R.drawable.ic_next, "Next", nextPendingIntent)     // #2
//                // Apply the media style template.
//                .setStyle(new android.support.v4.media.app.Notification.MediaStyle()
//                        .setShowActionsInCompactView(1 /* #1: pause button */)
//                        .setMediaSession(mediaSession.getSessionToken()))
                .setContentTitle("Wonderful music")
                .setContentText("My Awesome Band")
//                .setLargeIcon(albumArtBitmap)
                .build();




        // 1-я часть
//        Notification notif = new Notification.Builder();
//
//        // 3-я часть
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.putExtra(MainActivity.FILE_NAME, "somefile");
//        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
//
//        // 2-я часть
//        notif.setLatestEventInfo(this, "Notification's title", "Notification's text", pIntent);
//
//        // ставим флаг, чтобы уведомление пропало после нажатия
//        notif.flags |= Notification.FLAG_AUTO_CANCEL;
//
//        // отправляем
//        notificationManager.notify(1, notif);
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

        sendNotif();

        return super.onStartCommand(intent, flags, startId);
    }
}

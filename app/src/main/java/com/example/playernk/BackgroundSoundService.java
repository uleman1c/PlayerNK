package com.example.playernk;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

public class BackgroundSoundService extends Service {

    MediaPlayer mediaPlayer;

    Boolean aquare;

    private BroadcastReceiver broadcastReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        String CHANNEL_ID = "my_channel_01";
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("")
                .setContentText("").build();

        startForeground(1, notification);


        aquare = false;

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String command = intent.getStringExtra("command");

                if (command.equals("pause")){

                        mediaPlayer.pause();

                }
                else if (command.equals("resume")){

                        mediaPlayer.start();

                }
                else if (command.equals("start")) {

                    if (mediaPlayer != null && !mediaPlayer.isPlaying())
                        mediaPlayer.start();

                }
                else if (command.equals("seekTo")) {

                    int delta = intent.getIntExtra("delta", 0);

                    if (mediaPlayer != null && mediaPlayer.isPlaying())
                        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + delta);

                }
                else if (command.equals("stop")) {

                    if (mediaPlayer != null && mediaPlayer.isPlaying())
                        mediaPlayer.stop();

                }
                else if (command.equals("setLoop")) {

                    Boolean loop = intent.getBooleanExtra("loop", false);

                    if (mediaPlayer != null) {
                        mediaPlayer.setLooping(loop);
                    }

                }
                else if (command.equals("setAquare")) {

                    aquare = intent.getBooleanExtra("aquare", false);

                }
                else if (command.equals("playSong")) {

                    String curSongid = intent.getStringExtra("songId");
                    aquare = intent.getBooleanExtra("aquare", false);

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

                                        mediaPlayer.setLooping(false);

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

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                Intent intent = new Intent();
                intent.putExtra("command", "endOfSong");
                SendTaskToClient(intent);

            }
        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

                mediaPlayer.start();

                if (aquare){

                    mediaPlayer.seekTo(Math.toIntExact(30000));

                }

            }
        });


    }

    private void SendTaskToClient(Intent intent){

        intent.setAction("android.intent.action.playernk.fromMP");
        sendBroadcast(intent);

    }


    void sendNotif() {

        String notificationId = "1";

//        Intent intent = new Intent(this, AlertDetails.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(getBaseContext(), getString(R.string.playernk_channel))
                // Show controls on lock screen even when user hides sensitive content.
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
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

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define.
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, notification);


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

        //sendNotif();

        return super.onStartCommand(intent, flags, startId);
    }
}

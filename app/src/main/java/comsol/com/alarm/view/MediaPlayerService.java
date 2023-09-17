package comsol.com.alarm.view;

/**
 * Created by pc on 9/22/2017.
 */
import comsol.com.alarm.R;
import comsol.com.alarm.controller.AlarmsManager;
import comsol.com.alarm.model.Alarm;
import comsol.com.alarm.view.activity.RingingAlarmActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class MediaPlayerService extends Service {

    private AlarmsManager alarmsManger;
    private final CharSequence tickerText = "Rise and shine";
    private final CharSequence contentTitle = "mp3 alarm clock";
    public static final int RINGING_NOTIFICATION_ID = 111;
    public static String START_PLAY = "START_PLAY";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && intent.getBooleanExtra(START_PLAY, false)) {
            long alarmId = intent.getLongExtra(Alarm.INTENT_ID, -1);
            playMusicAndShowNotification(alarmId);
        }
        return Service.START_REDELIVER_INTENT;
    }

    private void playMusicAndShowNotification(long alarmId) {

        alarmsManger = new AlarmsManager(getApplicationContext());
        alarmsManger.playMusic(alarmId);

        Intent restartRingingActivityIntent = new Intent(
                getApplicationContext(), RingingAlarmActivity.class);
        restartRingingActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        restartRingingActivityIntent.putExtra(Alarm.INTENT_ID, alarmId);
        PendingIntent restartRingingActivityPenInt = PendingIntent.getActivity(
                getApplicationContext(), (int) System.currentTimeMillis(),
                restartRingingActivityIntent, 0);

        Notification notiBuilder = new Notification.Builder(
                getApplicationContext()).setTicker(tickerText)
                .setSmallIcon(R.drawable.ic_action_onoff_pressed)
                .setOngoing(true)
                .setContentIntent(restartRingingActivityPenInt)
                .setContentTitle(contentTitle).setContentText("Now ringing")
                .build();

        NotificationManager notiMng = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notiMng.cancel(RINGING_NOTIFICATION_ID);
        notiMng.cancel(RingingAlarmActivity.SNOOZING_NOTIFICATION_ID);

        startForeground(RINGING_NOTIFICATION_ID, notiBuilder);
    }

    @Override
    public void onDestroy() {
        stop();
    }

    private void stop() {
        alarmsManger.stopMusic();
        stopForeground(true);
    }

}


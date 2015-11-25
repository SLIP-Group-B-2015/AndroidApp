package edu.smartdoor.imank.smartdoor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Creates a service to update door events periodically
 * @author Iman Kalyan Majumdar
 */
public class Updater extends Service {

    private String uuid;
    private Integer time_sleep;
    public LocalBroadcastManager broadcaster;

    static final public String UPDATER_RESULT = "edu.smartdoor.imank.smartdoor.Updater.REQUEST_PROCESSED";
    static final public String UPDATER_MESSAGE = "edu.smartdoor.imank.smartdoor.Updater.MESSAGE";

    private static final String LOG_TAG = Updater.class.getSimpleName();
    private UpdaterThread updaterThread = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public synchronized void onDestroy() {
        super.onDestroy();
        // Stop service if it is running
        if (updaterThread.getIsRunning()) {
            updaterThread.interrupt();
        }
        updaterThread = null;
    }

    @Override
    public synchronized void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        // Start updater service if it is not running
        uuid = intent.getExtras().getString("userid");
        time_sleep = Integer.parseInt(intent.getExtras().getString("time_sleep"));

        if (updaterThread == null)
            updaterThread = new UpdaterThread(time_sleep);

        broadcaster = LocalBroadcastManager.getInstance(this);

        if (!updaterThread.getIsRunning())
        {
            updaterThread.start();
        }
    }

    public void sendBroadcast(Boolean complete)
    {
        Intent updated = new Intent(UPDATER_RESULT);
        updated.putExtra(UPDATER_MESSAGE, complete);
        broadcaster.sendBroadcast(updated);
    }

    class UpdaterThread extends Thread
    {
        private int delay;
        private boolean isRunning;
        private Context context;

        public UpdaterThread(int delay)
        {
            this.delay = delay;
            this.context = getApplicationContext();
        }

        @Override
        public void run() {
            isRunning = true;
            while (isRunning)
            {
                try
                {
                    Log.d(LOG_TAG, "Update Started");
                    Tasks.StoreEvents se = new Tasks.StoreEvents(uuid, context, "UNSENT");
                    try
                    {
                        boolean complete = se.execute((Void) null).get();
                        if (complete)
                        {
                            Log.d(LOG_TAG, "Sending Notification");
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                            builder.setSmallIcon(R.drawable.logo);
                            builder.setContentTitle("You have a new event.");
                            builder.setContentText("Click here ...");
                            Intent intent = new Intent(Updater.this, TimelineActivity.class);
                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                            stackBuilder.addParentStack(TimelineActivity.class);
                            stackBuilder.addNextIntent(intent);

                            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                            builder.setContentIntent(pendingIntent);

                            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            notificationManager.notify(0, builder.build());
                        }
                        sendBroadcast(complete);
                    }
                    catch (Exception e)
                    {
                        //Do nothing
                    }
                    Log.d(LOG_TAG, "Updated");
                    Thread.sleep(delay);
                }
                catch (InterruptedException ie)
                {
                    isRunning = false;
                }
            }
        }

        public boolean getIsRunning(){
            return this.isRunning;
        }

    }

}

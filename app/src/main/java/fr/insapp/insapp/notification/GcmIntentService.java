package fr.insapp.insapp.notification;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.Random;

import fr.insapp.insapp.Comments;
import fr.insapp.insapp.EventProfil;
import fr.insapp.insapp.PostActivity;
import fr.insapp.insapp.R;
import fr.insapp.insapp.Signin;
import fr.insapp.insapp.modeles.Notification;

public class GcmIntentService extends IntentService {

    public static int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    /* * Constantes permettant la récupération des informations
    * du message dans les extras de la notification envoyée
    * par le serveur de notification. */

    // Récupération de l'identification du message
    static final String MESSAGE_ID = "id";
    // Récupération du nom du message
    static final String MESSAGE_TITRE = "titre";
    // Récupération de la date et heure du message
    static final String MESSAGE_DATE_CREATION = "dateCreation";
    // Récupération du texte du message
    static final String MESSAGE_TEXTE = "texte";

    public GcmIntentService() {
        super("GcmIntentService");
    }
    public static final String TAG = "GCM Demo";

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // Le paramètre intent de la méthode getMessageType() est la notification push
        // reçue par le BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            /* * On filtre le message (ou notification) sur son type. * On met de côté les messages d'erreur pour nous concentrer sur * le message de notre notification. */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString());
                // Si c'est un message "classique".
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(Signin.class.getSimpleName(), Signin.MODE_PRIVATE);
                if(sharedPref.getBoolean("notifications", false)) {
                    Random random = new Random();
                    NOTIFICATION_ID = random.nextInt(9999 - 1000) + 1000;

                    sendMessageNotification(extras);
                }

                int nb = sharedPref.getInt("nb_notifs", 0) + 1;
                SharedPreferences.Editor sharedPref_edit = getApplicationContext().getSharedPreferences(Signin.class.getSimpleName(), Signin.MODE_PRIVATE).edit();
                sharedPref_edit.putInt("nb_notifs", nb);
                sharedPref_edit.commit();
            }
        }
        // On indique à notre broadcastReceiver que nous avons fini le traitement.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    /** * Cette méthode permet de créer une notification à partir * d'un message passé en paramètre. */
    private void sendNotification(String msg) {
        Log.d(TAG, "Preparing to send notification...: " + msg);
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, Signin.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg);
                        //.setDefaults(Notification.DEFAULT_ALL);

        mBuilder.setContentIntent(contentIntent);

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);

        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG"); wl.acquire(15000);


        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        Log.d(TAG, "Notification ID = " + NOTIFICATION_ID);
        Log.d(TAG, "Notification sent successfully.");
    }

    /**
     *  Cette méthode permet à partir des informations envoyées par le serveur * de notification de créer le message et la notification à afficher sur * le terminal de l'utilisateur. * *
     * @param extras les extras envoyés par le serveur de notification
     */
    private void sendMessageNotification(Bundle extras) {
        Log.d(TAG, "Preparing to send notification with message...: " + extras.toString());
        // On crée un objet Message à partir des informations récupérées dans
        // le flux JSON du message envoyé par l'application server

        Notification msg = new Notification(extras);


        // On associe notre notification à une Activity. Ici c'est l'activity
        // qui affiche le message à l'utilisateur
        PendingIntent contentIntent = null;

        // On récupère le gestionnaire de notification android
        mNotificationManager = (NotificationManager)
        this.getSystemService(Context.NOTIFICATION_SERVICE);

        String title = "Insapp";
        if(msg.getType().equals("tag"))
            contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, Comments.class).putExtra("notification", msg).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), PendingIntent.FLAG_UPDATE_CURRENT);
        else if(msg.getType().equals("post"))
            contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, PostActivity.class).putExtra("notification", msg).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), PendingIntent.FLAG_UPDATE_CURRENT);
        else if(msg.getType().equals("event"))
            contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, EventProfil.class).putExtra("notification", msg).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(largeIcon)
                        .setContentTitle(title)
                        .setVibrate(new long[]{400, 400})
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg.getMessage()))
                        .setContentText(msg.getMessage())
                        .setAutoCancel(true)
                        .setDefaults(android.app.Notification.DEFAULT_SOUND);

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wl.acquire(15000);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        Log.d(TAG, "Notification sent successfully.");
    }

    /** * Cette méthode permet d'extraire les informations du message de la notification
     * afin de créer un message.
     * @param extras l'objet contenant les informations du message.
     * @return le message
    private Notification extractMessageFromExtra(Bundle extras) {
        Notification msg = null;
        if (extras != null) {
            msg = new Notification();
            final String id = extras.getString(MESSAGE_ID);
            try {
                msg.setId(id);
            } catch (NumberFormatException nfe) {
                //Log.e("error : le message n'a pas un identifiant valide. ",nfe.getMessage());
                nfe.printStackTrace();
            }
            final String dateTime = extras.getString(MESSAGE_DATE_CREATION);
            try {
                msg.setDate(new Date(Long.parseLong(dateTime)));
            } catch (NumberFormatException nfe) {
                //Log.e("error : le message n'a pas une date valide. ",nfe.getMessage());
                nfe.printStackTrace();
            }
            msg.setContent(extras.getString(MESSAGE_TITRE));
            msg.setMessage(extras.getString(MESSAGE_TEXTE));
        }
        Log.d(TAG, "extractMessageFromExtra - fin");
        return msg;
    }
    */
}
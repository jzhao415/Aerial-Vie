package com.example.android.wearable.jumpingjack;

/**
 * Created by jeffzhao415 on 7/13/15.
 */
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v4.app.RemoteInput;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;

public class NotificationBuilder {

    private static final String notification_title="Did you fall?";
    private static final String notification_content="Are you OK?";
    private static final String notification_Description="Vie detected that you may have fell, please swipe left to choose a reply";
    // Key for the string that's delivered in the action's intent
    private static final String EXTRA_VOICE_REPLY = "extra_voice_reply";
    private int notification_id = 1;
    private final String NOTIFICATION_ID = "notification_id";

    private Intent replyIntent;

    public Notification buildNotification(Context context) {
        Intent open_activity_intent = new Intent(context, MainActivity.class);
        open_activity_intent.putExtra(NOTIFICATION_ID, notification_id);
        PendingIntent pending_intent = PendingIntent.getActivity(context, 0, open_activity_intent, PendingIntent.FLAG_CANCEL_CURRENT);

        //Add Pre-defined Text Responses

        //Add remoteInput first
        String replyLabel = context.getResources().getString(R.string.reply_label);
        String[] replyChoices = context.getResources().getStringArray(R.array.reply_choices);

        RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_VOICE_REPLY)
                .setLabel(replyLabel)
                .setChoices(replyChoices)
                .build();

        // Create an intent for the reply action
        replyIntent = new Intent(context, MainActivity.class);
        PendingIntent replyPendingIntent =
                PendingIntent.getActivity(context, 0, replyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);


        // Create the reply action and add the remote input
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.slip,
                        context.getString(R.string.reply_label), replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();

        NotificationCompat.BigTextStyle bigStyle = new NotificationCompat.BigTextStyle();
        bigStyle.bigText(notification_Description);
        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(notification_title)
                .setContentText(notification_content)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_launcher))
                .setStyle(bigStyle)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pending_intent)
                .extend(new WearableExtender().addAction(action))
                .build();

        return notification;
    }

    public Intent getReplyIntent() {
        return replyIntent;
    }

}

package util;

/**
 * Created by jeffzhao415 on 9/9/15.
 */
import android.content.Context;
import android.telephony.SmsManager;
import android.text.format.DateUtils;

public class SMSUtil {

    /**
     * Use the default {@link SmsManager} to send a message to phone number
     *
     * @param number
     *         {@link String}
     * @param message
     *         {@link String}
     */
    public static void sendSms(String number, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number, null, message, null, null);
    }

    /**
     * Get a formatted timestamp to be included in the SMS
     *
     * @param context
     *         {@link Context}
     * @param timestamp
     *         {@link long}
     * @return {@link String}
     */
    public static String getFormattedTimestamp(Context context, long timestamp) {
        return DateUtils.formatDateTime(context, timestamp,
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                        | DateUtils.FORMAT_SHOW_TIME
        );
    }

}
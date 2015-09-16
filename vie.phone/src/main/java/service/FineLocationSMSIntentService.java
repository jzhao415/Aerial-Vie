package service;

/**
 * Created by jeffzhao415 on 9/9/15.
 */
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.jeffzhao415.vie.BuildConfig;
import com.example.jeffzhao415.vie.R;

import util.SMSUtil;


/**
 * This service is only responsible for finding an accurate and current {@link Location} and send it as a SMS message to
 * the specified phone number passed as an {@link Intent} extra on {@link #onStartCommand(Intent, int, int)}
 *
 */
public class FineLocationSMSIntentService extends Service implements LocationListener {

    // Constants
    public static final String LOG_TAG = "FineLocationSMSService";
    private static final int WAKELOCK_TIMEOUT_MS = 600000; // 10 minutes
    public static final String KEY_SMS_PHONE_NUMBER = "_key_phone_number";

    // Members
    private PowerManager.WakeLock mWakeLock = null;
    private LocationManager mLocationManager;
    private String mTempSmsNumberToSendLocationTo = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate() {
        super.onCreate();

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOG_TAG);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(this);
        }

        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }

        super.onDestroy();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("NullableProblems")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (BuildConfig.DEBUG) {
            if (intent != null) {
                Log.d(LOG_TAG, "onStartCommand(intent=" + intent.toString() + ", flags=" + flags + "," +
                        "startId=" + startId + ")");
                if (intent.hasExtra(KEY_SMS_PHONE_NUMBER)) {
                    Log.d(LOG_TAG, " -- KEY_SMS_PHONE_NUMBER=" + intent.getStringExtra(KEY_SMS_PHONE_NUMBER));
                }
            } else {
                Log.d(LOG_TAG, "onStartCommand(intent=null)");
            }
        }

        if (intent == null) {
            return START_FLAG_RETRY;
        }

        String smsNumber = intent.getStringExtra(KEY_SMS_PHONE_NUMBER);

        if (!TextUtils.isEmpty(smsNumber)) {
            if (mTempSmsNumberToSendLocationTo == null) {
                // Need to fetch a new fine location

                // Acquire a wake lock to make sure we don't die while we wait for a location
                mWakeLock.acquire(WAKELOCK_TIMEOUT_MS);

                // Build a criteria that will be used to find the best location provider
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setPowerRequirement(Criteria.POWER_HIGH);

                // Get the name of the best location provider based on the criteria above
                String locationProvider = mLocationManager.getBestProvider(criteria, true);

                if (locationProvider != null) {
                    // If our provider is valid, request a single location update using this provider, then wait
                    mTempSmsNumberToSendLocationTo = smsNumber;
                    mLocationManager.requestSingleUpdate(locationProvider,
                            FineLocationSMSIntentService.this, null);
                }
            } else {
                // If a number is already set, that means we are currently waiting for the
                // location to be found, simply make sure the phone number is updated
                mTempSmsNumberToSendLocationTo = smsNumber;
            }
        }

        return START_FLAG_REDELIVERY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLocationChanged(Location location) {
        if (mLocationManager != null && location != null) {
            // Remove all pending location updates. we got what we needed
            mLocationManager.removeUpdates(this);
        }

        if (location != null && mTempSmsNumberToSendLocationTo != null) {
            // Get the location coordinates
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            // Get the coordinates in a user readable format
            String message = String.format(getString(R.string.message_current_location),
                    Double.toString(latitude), Double.toString(longitude),
                    SMSUtil.getFormattedTimestamp(this, location.getTime()));

            // Send the SMS to the specific number with the fine location
            SMSUtil.sendSms(mTempSmsNumberToSendLocationTo, message);

            // Clear out the phone number variable to avoid duplicates
            mTempSmsNumberToSendLocationTo = null;

            if (mWakeLock.isHeld()) {
                // Release the wake lock
                mWakeLock.release();
            }

            // Message sent, wakelock released, we are done here, kill the service
            stopSelf();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // No implementation
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onProviderEnabled(String provider) {
        // No implementation
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onProviderDisabled(String provider) {
        // No implementation
    }

}

package de.cyface.datacapturing.backend;

import static de.cyface.datacapturing.Constants.BACKGROUND_TAG;
import static de.cyface.datacapturing.MessageCodes.GLOBAL_BROADCAST_PING;
import static de.cyface.datacapturing.MessageCodes.GLOBAL_BROADCAST_PONG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import de.cyface.datacapturing.BuildConfig;
import de.cyface.datacapturing.BundlesExtrasCodes;
import de.cyface.utils.Validate;

/**
 * A <code>BroadcastReceiver</code> that receives ping messages send to the <code>DataCapturingBackgroundService</code>.
 * This can be used to check if the service is alive.
 *
 * @author Klemens Muthmann
 * @version 1.0.3
 * @since 2.0.0
 */
public class PingReceiver extends BroadcastReceiver {

    /**
     * The tag used to identify Logcat messages.
     */
    private static final String TAG = BACKGROUND_TAG;

    @Override
    public void onReceive(final @NonNull Context context, final @NonNull Intent intent) {
        Validate.notNull(intent.getAction());
        if (intent.getAction().equals(GLOBAL_BROADCAST_PING)) {
            Intent pongIntent = new Intent(GLOBAL_BROADCAST_PONG);
            if (BuildConfig.DEBUG) {
                String pingPongIdentifier = intent.getStringExtra(BundlesExtrasCodes.PING_PONG_ID);
                Log.d(TAG, "PingReceiver.onReceive(): Received Ping with identifier " + pingPongIdentifier
                        + ". Sending Pong.");
                pongIntent.putExtra(BundlesExtrasCodes.PING_PONG_ID, pingPongIdentifier);
            }
            context.sendBroadcast(pongIntent);
        }
    }
}

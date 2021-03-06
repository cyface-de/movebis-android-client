/*
 * Copyright 2017 Cyface GmbH
 *
 * This file is part of the Cyface SDK for Android.
 *
 * The Cyface SDK for Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Cyface SDK for Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Cyface SDK for Android. If not, see <http://www.gnu.org/licenses/>.
 */
package de.cyface.datacapturing;

import android.app.Notification;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import de.cyface.datacapturing.backend.DataCapturingBackgroundService;

/**
 * Interface for strategies to respond to events triggered by the {@link DataCapturingBackgroundService}.
 * E.g.: Show a notification when little space is available and stop the capturing.
 * <p>
 * Must be {@code Parcelable} to be passed from the {@link DataCapturingService} via {@code Intent}.
 * <p>
 * An implementation of this class must also provide a {@code Notification} shown by the data capturing service,
 * during capturing.
 *
 * @author Armin Schnabel
 * @author Klemens Muthmann
 * @version 2.0.3
 * @since 2.5.0
 */
public interface EventHandlingStrategy extends Parcelable {

    /**
     * Implement a strategy to react to a low space warning.
     *
     * @param dataCapturingBackgroundService A reference to the background service to allow operations
     *            on it like stopping the capturing.
     */
    void handleSpaceWarning(@NonNull final DataCapturingBackgroundService dataCapturingBackgroundService);

    /**
     * Provides an Android representation of a {@code Notification}, that can be displayed on screen.
     * <p>
     * <b>Attention:</b>
     * If you want to use a **vector xml drawable as Notification icon** make sure to do the following:
     * Even with `vectorDrawables.useSupportLibrary` enabled the vector drawable won't work as a notification icon
     * (`notificationBuilder.setSmallIcon()`) on devices with API < 21.
     * We assume that's because of the way we need to inject your custom notification.
     * A simple fix is to have a the xml in `res/drawable-anydpi-v21/icon.xml` and to generate notification icon PNGs
     * under the same resource name in the usual paths (`res/drawable-**dpi/icon.png`).
     *
     * @param context The {@link DataCapturingBackgroundService} as context for the new {@code Notification}.
     * @return An Android {@code Notification} object configured to work as capturing notification.
     */
    @NonNull
    Notification buildCapturingNotification(@NonNull final DataCapturingBackgroundService context);
}

package com.github.fontoura.androidutils.plugins;

import android.os.Bundle;
import android.os.PersistableBundle;

/**
 * Interface for plugins that can be attached to an Activity.
 * <p/>
 * See {@link ActivityPlugins} for more information.
 */
public interface ActivityPlugin {

    /**
     * Called when the activity is created.
     * <p/>
     * See {@link android.app.Activity#onCreate(Bundle)} for more information.
     * @param bundle The bundle passed to the activity.
     */
    default void onCreate(Bundle bundle) {
    }

    /**
     * Called when the activity is started.
     * <p/>
     * See {@link android.app.Activity#onStart()} for more information.
     */
    default void onStart() {
    }

    /**
     * Called when the activity is resumed.
     * <p/>
     * See {@link android.app.Activity#onResume()} for more information.
     */
    default void onResume() {
    }

    /**
     * Called when the activity is paused.
     * <p/>
     * See {@link android.app.Activity#onPause()} for more information.
     */
    default void onPause() {
    }

    /**
     * Called when the activity is stopped.
     * <p/>
     * See {@link android.app.Activity#onStop()} for more information.
     */
    default void onStop() {
    }

    /**
     * Called when the instance state of the activity is saved.
     * <p/>
     * See {@link android.app.Activity#onSaveInstanceState(Bundle)} for more information.
     * @param bundle The bundle passed to the activity.
     * @param persistentState The persistent state passed to the activity.
     */
    default void onSaveInstanceState(Bundle bundle, PersistableBundle persistentState) {
    }

    /**
     * Called when the activity is destroyed.
     * <p/>
     * See {@link android.app.Activity#onDestroy()} for more information.
     */
    default void onDestroy() {
    }
}

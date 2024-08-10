package com.github.fontoura.androidutils.plugins;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that manages a list of {@link ActivityPlugin} instances.
 * <p/>
 * The plugins are aware of the lifecycle of an activity.
 */
public class ActivityPlugins {

    private boolean created = false;
    private final List<ActivityPlugin> pluginList;
    private final String logTag;

    private ActivityPlugins(ActivityPluginsBuilder builder) {
        this.logTag = builder.logTag != null ? builder.logTag : ActivityPlugins.class.getName();
        this.pluginList = builder.pluginList;
    }

    /**
     * Creates a {@linkplain ActivityPluginsBuilder builder} of {@link ActivityPlugins}.
     * @return A builder.
     */
    public static ActivityPluginsBuilder builder() {
        return new ActivityPluginsBuilder();
    }

    /**
     * A method that should be called during {@link android.app.Activity#onCreate(Bundle) onCreate}
     * of the activity.
     * @param bundle The bundle passed to the activity.
     */
    public void onCreate(Bundle bundle) {
        created = true;
        for (ActivityPlugin plugin : pluginList) {
            try {
                plugin.onCreate(bundle);
            } catch (Exception e) {
                Log.e(logTag, String.format("Failed to invoke onCreate on plugin %s", plugin), e);
            }
        }
    }

    /**
     * A method that should be called during {@link android.app.Activity#onStart() onStart} of the
     * activity.
     */
    public void onStart() {
        if (!created) {
            return;
        }

        for (ActivityPlugin plugin : pluginList) {
            try {
                plugin.onStart();
            } catch (Exception e) {
                Log.e(logTag, String.format("Failed to invoke onStart on plugin %s", plugin), e);
            }
        }
    }

    /**
     * A method that should be called during {@link android.app.Activity#onResume() onResume} of the
     * activity.
     */
    public void onResume() {
        if (!created) {
            return;
        }

        for (ActivityPlugin plugin : pluginList) {
            try {
                plugin.onResume();
            } catch (Exception e) {
                Log.e(logTag, String.format("Failed to invoke onResume on plugin %s", plugin), e);
            }
        }
    }

    /**
     * A method that should be called during {@link android.app.Activity#onPause() onPause} of the
     * activity.
     */
    public void onPause() {
        if (!created) {
            return;
        }

        for (ActivityPlugin plugin : pluginList) {
            try {
                plugin.onPause();
            } catch (Exception e) {
                Log.e(logTag, String.format("Failed to invoke onPause on plugin %s", plugin), e);
            }
        }
    }

    /**
     * A method that should be called during {@link android.app.Activity#onStop() onStop} of the
     * activity.
     */
    public void onStop() {
        if (!created) {
            return;
        }

        for (ActivityPlugin plugin : pluginList) {
            try {
                plugin.onStop();
            } catch (Exception e) {
                Log.e(logTag, String.format("Failed to invoke onStop on plugin %s", plugin), e);
            }
        }
    }

    /**
     * A method that should be called during {@link android.app.Activity#onSaveInstanceState(Bundle,
     * PersistableBundle) onSaveInstanceState} of the activity.
     * @param bundle The bundle passed to the activity.
     * @param persistentState The persistent state passed to the activity.
     */
    public void onSaveInstanceState(Bundle bundle, PersistableBundle persistentState) {
        if (!created) {
            return;
        }

        for (ActivityPlugin plugin : pluginList) {
            try {
                plugin.onSaveInstanceState(bundle, persistentState);
            } catch (Exception e) {
                Log.e(logTag, String.format("Failed to invoke onSaveInstanceState on plugin %s", plugin), e);
            }
        }
    }

    /**
     * A method that should be called during {@link android.app.Activity#onDestroy() onDestroy} of
     * the activity.
     */
    public void onDestroy() {
        if (!created) {
            return;
        }
        created = false;

        for (ActivityPlugin plugin : pluginList) {
            try {
                plugin.onDestroy();
            } catch (Exception e) {
                Log.e(logTag, String.format("Failed to invoke onDestroy on plugin %s", plugin), e);
            }
        }
    }

    /**
     * A builder for {@link ActivityPlugins}.
     */
    public static class ActivityPluginsBuilder {
        private String logTag;
        private final List<ActivityPlugin> pluginList = new ArrayList<>();

        /**
         * Set the log tag to be used by the plugins.
         * @param logTag The log tag.
         * @return The builder.
         * @throws NullPointerException If the log tag is null.
         */
        public ActivityPluginsBuilder withLogTag(String logTag) {
            if (logTag == null) {
                throw new NullPointerException("The log tag cannot be null");
            }
            this.logTag = logTag;
            return this;
        }

        /**
         * Add a plugin to the list of plugins.
         * @param plugin The plugin to be added.
         * @return The builder.
         * @throws NullPointerException If the plugin is null.
         * @throws IllegalArgumentException If the plugin has already been added.
         */
        public ActivityPluginsBuilder withPlugin(ActivityPlugin plugin) {
            if (plugin == null) {
                throw new NullPointerException("The plugin cannot be null");
            }
            if (pluginList.contains(plugin)) {
                throw new IllegalArgumentException("The plugin has already been added");
            }
            pluginList.add(plugin);
            return this;
        }

        /**
         * Build the {@link ActivityPlugins} instance.
         * @return The instance.
         */
        public ActivityPlugins build() {
            return new ActivityPlugins(this);
        }
    }
}

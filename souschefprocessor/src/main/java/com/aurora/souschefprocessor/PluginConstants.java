package com.aurora.souschefprocessor;

/**
 * Important Constants for the plugin
 */
public class PluginConstants {

    /**
     * Prevent instantiation of utility class
     */
    private PluginConstants() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Unique name of the plugin, used by the caching operation.
     */
    public static final String UNIQUE_PLUGIN_NAME = "com.aurora.souschef";
}
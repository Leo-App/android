package de.leoapp_slg.core.preferences;

public abstract class PreferenceNameManager {
    public static abstract class User {
        public static final String NAME = "preference_key_user_name";
        public static final String ID = "preference_key_user_id";
        public static final String KLASSE = "preference_key_user_klasse";
        public static final String NAME_DEFAULT = "preference_key_user_defaultname";
    }

    public static abstract class Device {
        public static final String AUTHENTICATION = "preference_key_device_authentication";
        public static final String NAME = "preference_key_device_name";
    }
}

package com.wodriver.AWS;

import android.support.v4.app.Fragment;

import com.wodriver.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by user on 2016-11-24.
 */

public class Configuration {

    private static final List<Feature> features = new ArrayList<Feature>();

    static {
//        addFeature("user_identity", R.mipmap.user_identity, R.string.feature_sign_in_title,
//                R.string.feature_sign_in_subtitle, R.string.feature_sign_in_overview,
//                R.string.feature_sign_in_description, R.string.feature_sign_in_powered_by,
//                new Item(R.string.main_fragment_title_user_identity, R.mipmap.user_identity,
//                        R.string.feature_sign_in_demo_button, IdentityFragment.class));
//        addFeature("push_notification", R.mipmap.push,
//                R.string.feature_push_notifications_title,
//                R.string.feature_push_notifications_subtitle,
//                R.string.feature_push_notifications_overview,
//                R.string.feature_push_notifications_description,
//                R.string.feature_push_notifications_powered_by,
//                new Item(R.string.main_fragment_title_push_notification, R.mipmap.push,
//                        R.string.feature_push_notifications_demo_button, PushFragment.class));
//        addFeature("user_data_storage", R.mipmap.user_data_storage,
//                R.string.feature_user_data_storage_title,
//                R.string.feature_user_data_storage_subtitle,
//                R.string.feature_user_data_storage_overview,
//                R.string.feature_user_data_storage_description,
//                R.string.feature_user_data_storage_powered_by
//                , new Item(R.string.main_fragment_title_user_files, R.mipmap.user_files,
//                        R.string.feature_user_data_storage_demo_button_user_file_storage,
//                        UserFilesFragment.class)
//                , new Item(R.string.main_fragment_title_user_settings, R.mipmap.user_profile_data,
//                        R.string.feature_user_data_storage_demo_button_user_settings,
//                        UserSettingsFragment.class)
//        );
//        addFeature("app_analytics", R.mipmap.app_analytics,
//                R.string.feature_app_analytics_title, R.string.feature_app_analytics_subtitle,
//                R.string.feature_app_analytics_overview,
//                R.string.feature_app_analytics_description,
//                R.string.feature_app_analytics_powered_by,
//                new Item(R.string.main_fragment_title_app_analytics,
//                        R.mipmap.app_analytics, R.string.feature_app_analytics_demo_button,
//                        AppAnalyticsFragment.class));
//        addFeature("nosql_database", R.mipmap.database, R.string.feature_nosql_database_title,
//                R.string.feature_nosql_database_subtitle, R.string.feature_nosql_database_overview,
//                R.string.feature_nosql_database_description, R.string.feature_nosql_database_powered_by,
//                new Item(R.string.main_fragment_title_nosql_database, R.mipmap.database,
//                        R.string.feature_nosql_database_demo_button, NoSQLSelectTableFragment.class));
    }

    public static List<Feature> getFeatureList() {
        return Collections.unmodifiableList(features);
    }

    public static Feature getFeatureByName(final String name) {
        for (Feature feature : features) {
            if (feature.name.equals(name)) {
                return feature;
            }
        }
        return null;
    }

    private static void addFeature(final String name, final int iconResId, final int titleResId,
                                       final int subtitleResId, final int overviewResId,
                                       final int descriptionResId, final int poweredByResId,
                                       final Item... items) {
        Feature feature = new Feature(name, iconResId, titleResId, subtitleResId,
                overviewResId, descriptionResId, poweredByResId, items);
        features.add(feature);
    }

    public static class Feature{
        public String name;
        public int iconResId;
        public int titleResId;
        public int subtitleResId;
        public int overviewResId;
        public int descriptionResId;
        public int poweredByResId;
        public List<Item> demos;

        public Feature(){

        }

        public Feature(final String name, final int iconResId, final int titleResId, final int subtitleResId, final int overviewResId, final int descriptionResId, final int poweredByResId, final Item... items){
            this.name = name;
            this.iconResId = iconResId;
            this.titleResId = titleResId;
            this.subtitleResId = subtitleResId;
            this.overviewResId = overviewResId;
            this.descriptionResId = descriptionResId;
            this.poweredByResId = poweredByResId;
            this.demos = Arrays.asList(items);
        }
    }

    public static class Item {
        public int titleResId;
        public int iconResId;
        public int buttonTextResId;
        public String fragmentClassName;

        public Item(final int titleResId, final int iconResId, final int buttonTextResId,
                        final Class<? extends Fragment> fragmentClass) {
            this.titleResId = titleResId;
            this.iconResId = iconResId;
            this.buttonTextResId = buttonTextResId;
            this.fragmentClassName = fragmentClass.getName();
        }
    }
}

package dev.olog.msc.musicservice.helper;

public class WearHelper {
    private static final String WEAR_APP_PACKAGE_NAME = "com.google.android.wearable.app";

    public static boolean isValidWearCompanionPackage(String packageName) {
        return WEAR_APP_PACKAGE_NAME.equals(packageName);
    }

}

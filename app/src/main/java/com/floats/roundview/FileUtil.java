package com.floats.roundview;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;

public class FileUtil {

    /**
     *
     * @param context
     * @param defType
     * @param file_name
     * @return
     */
    public static int getResIdFromFileName(Context context, String defType,
                                           String file_name) {
        Resources rs = context.getResources();
        String packageName = getMyPackageName(context);
        return rs.getIdentifier(file_name, defType, packageName);
    }

    /**
     *
     * @param context
     * @return
     */
    public static String getMyPackageName(Context context) {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}

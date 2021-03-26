package andreibunu.projects.utils;

import android.content.Context;
import android.content.res.Resources;

public class UiUtils {
    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static int getScreenWidth(){
        return Resources.getSystem().getDisplayMetrics().densityDpi;
    }
}

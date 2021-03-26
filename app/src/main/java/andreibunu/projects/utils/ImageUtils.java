package andreibunu.projects.utils;

import android.icu.text.SimpleDateFormat;
import android.os.Environment;
import android.util.Log;

import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class ImageUtils {

    public static int getCameraPhotoOrientation(String imagePath) {
        int rotation = 0;
        try {
            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotation = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotation = 90;
                    break;
            }
            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotation);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotation;
    }

    public static Date getDateFromName(String name) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
        return formatter.parse(name.substring(0, 8) + name.substring(9, 15));
    }

    public static String getMonthForInt(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11) {
            month = months[num];
        }
        return month;
    }

    public static File[] getCameraPhotos() {
        String ExternalStorageDirectoryPath = Environment
                .getExternalStorageDirectory()
                .getAbsolutePath();
        String targetPath = ExternalStorageDirectoryPath + "/DCIM/Camera";
        File targetDirector = new File(targetPath);
        return targetDirector.listFiles();
    }

}

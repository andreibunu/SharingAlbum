package andreibunu.projects.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.os.Environment;
import android.util.Log;

import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotation;
    }

    public static Date getDateFromName(String name) throws ParseException {
        name = name.replaceFirst("IMG_", "");
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


    public static File saveBitmapToFile(File file) {
        try {
            ExifInterface oldExif = new ExifInterface(file.getAbsolutePath());
            String exifOrientation = oldExif.getAttribute(ExifInterface.TAG_ORIENTATION);
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            FileInputStream inputStream = new FileInputStream(file);
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();
            final int REQUIRED_SIZE = 75;
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            if (exifOrientation != null) {
                ExifInterface newExif = new ExifInterface(file.getAbsolutePath());
                newExif.setAttribute(ExifInterface.TAG_ORIENTATION, exifOrientation);
                newExif.saveAttributes();
            }
            return file;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getFileFormat(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return ""; //empty extension
        }
        return fileName.substring(lastDotIndex + 1);
    }

}

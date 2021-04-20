package andreibunu.projects.apiService.facerecognition;

import android.util.Log;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import andreibunu.projects.database.DatabaseHandler;
import andreibunu.projects.database.DatabaseImage;
import andreibunu.projects.utils.ImageUtils;
import io.reactivex.Single;

public class FaceRecognitionHandler {

    private static final String TAG = FaceRecognitionHandler.class.getCanonicalName();
    private final DatabaseHandler databaseHandler;
    private FaceRecognitionProxy proxy;

    @Inject
    public FaceRecognitionHandler(FaceRecognitionProxy proxy, DatabaseHandler databaseHandler) {
        this.proxy = proxy;
        this.databaseHandler = databaseHandler;
    }

    public Single<String> sendImage(File file) {

        return Single.create(emitter -> {
            try {
                List<Integer> faces = proxy.testImg2(file);
                String filename = file.getName();
                Date date = ImageUtils.getDateFromName(filename);
                DatabaseImage x = databaseHandler.insertImage(filename, date.toString(), faces.toString(), "", "").blockingGet();
                emitter.onSuccess(faces.toString());
            } catch (Exception e) {
                Log.e(TAG, "getProducts: " + e.getMessage());
                emitter.onError(e);
            }
        });
    }
}

package andreibunu.projects.database;

import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

public class DatabaseHandler {
    public AppDatabase appDatabase;
    private static final String TAG = DatabaseHandler.class.getCanonicalName();

    @Inject
    public DatabaseHandler(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
    }

    public Single<DatabaseImage> insertImage(String imageName, String date, String person, String hashtags, String location) {
        return Single.create(emitter -> {
            try {
                DatabaseImage image = new DatabaseImage(imageName, date, person, hashtags, location);
                appDatabase.galleryDao().insertImage(image);
                emitter.onSuccess(image);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                emitter.onError(e);
            }
        });
    }

    public Single<List<DatabaseImage>> getImages() {
        return Single.create(emitter -> {
            try {
                List<DatabaseImage> images = appDatabase.galleryDao().getImages();
                emitter.onSuccess(images);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                emitter.onError(e);
            }
        });
    }


}

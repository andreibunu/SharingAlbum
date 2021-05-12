package andreibunu.projects.database;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
                Log.d(TAG, Objects.requireNonNull(e.getMessage()));
                emitter.onError(e);
            }
        });
    }

    public Single<DatabaseImage> updatePersonIndex(DatabaseImage databaseImage) {
        return Single.create(emitter -> {
            try {
                appDatabase.galleryDao().update(databaseImage);
                emitter.onSuccess(databaseImage);
            } catch (Exception e) {
                Log.d(TAG, Objects.requireNonNull(e.getMessage()));
                emitter.onError(e);
            }
        });
    }


    public Single<List<DatabaseImage>> getImages() {
        return Single.create(emitter -> {
            try {
                Cursor cursor = appDatabase.galleryDao().getImages();
                List<DatabaseImage> images = new ArrayList<>();

                /**
                 * DESIGN PATTERNS BEHAVIORAL : iterator
                 * cursor is used to iterate over the DatabaseImage items returned by a query
                 */
                while (cursor.moveToNext()) {
                    DatabaseImage img = new DatabaseImage(cursor.getString(cursor.getColumnIndex("imageName")),
                            cursor.getString(cursor.getColumnIndex("date")),
                            cursor.getString(cursor.getColumnIndex("person")),
                            cursor.getString(cursor.getColumnIndex("hashtags")),
                            cursor.getString(cursor.getColumnIndex("location"))
                    );
                    images.add(img);
                }
                emitter.onSuccess(images);
            } catch (Exception e) {
                Log.d(TAG, Objects.requireNonNull(e.getMessage()));
                emitter.onError(e);
            }
        });
    }


}

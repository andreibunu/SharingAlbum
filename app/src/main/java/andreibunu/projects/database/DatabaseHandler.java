package andreibunu.projects.database;

import android.database.Cursor;
import android.util.Log;

import androidx.room.Database;

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

    public Single<DatabaseImage> getImage(String dbName){
        return Single.create(emitter -> {
            try {
                DatabaseImage databaseImage = appDatabase.galleryDao().getImage(dbName);
                emitter.onSuccess(databaseImage);
            } catch (Exception e) {
                Log.d(TAG, Objects.requireNonNull(e.getMessage()));
                emitter.onError(e);
            }
        });
    }

    public Single<DatabaseUserLink> getUsername(String index){
        return Single.create(emitter -> {
            try {
                DatabaseUserLink username = appDatabase.userLinksDao().getUsername(index);
                emitter.onSuccess(username);
            } catch (Exception e) {
                Log.d(TAG, Objects.requireNonNull(e.getMessage()));
                emitter.onError(e);
            }
        });
    }


    public Single<DatabaseUserLink> insertLink(String idx, String username, int number, String friendId) {
        return Single.create(emitter -> {
            try {
                DatabaseUserLink link = new DatabaseUserLink(idx, username, number, friendId);
                appDatabase.userLinksDao().addLink(link);
                emitter.onSuccess(link);
            } catch (Exception e) {
                Log.d(TAG, Objects.requireNonNull(e.getMessage()));
                emitter.onError(e);
            }
        });
    }


}

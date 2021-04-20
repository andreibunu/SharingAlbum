package andreibunu.projects.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import andreibunu.projects.database.dao.GalleryDao;

@Database(entities = {DatabaseImage.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract GalleryDao galleryDao();
}

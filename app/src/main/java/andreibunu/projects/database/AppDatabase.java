package andreibunu.projects.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import andreibunu.projects.database.dao.GalleryDao;
import andreibunu.projects.database.dao.UserLinksDao;

@Database(entities = {DatabaseImage.class, DatabaseUserLink.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract GalleryDao galleryDao();
    public abstract UserLinksDao userLinksDao();
}

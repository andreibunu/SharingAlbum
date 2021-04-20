package andreibunu.projects.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import andreibunu.projects.database.DatabaseImage;

@Dao
public interface GalleryDao {
    @Query("select * from images")
    List<DatabaseImage> getImages();

    @Insert
    void insertImage(DatabaseImage image);
}

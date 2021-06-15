package andreibunu.projects.database.dao;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import andreibunu.projects.database.DatabaseImage;
import retrofit2.http.GET;


/**
 * DESIGN PATTERN STRUCTURAL, Facade
 */
@Dao
public interface GalleryDao {
    @Query("select * from images")
    Cursor getImages();

    @Insert
    void insertImage(DatabaseImage image);

    @Update
    void update(DatabaseImage image);

    @Query("Select * from images where imageName = :dbname")
    DatabaseImage getImage(String dbname);
}

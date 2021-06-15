package andreibunu.projects.database.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import andreibunu.projects.database.DatabaseUserLink;

@Dao
public interface UserLinksDao {
    @Query("Select * from userlinks where idx = :index")
    DatabaseUserLink getUsername(String index);

    @Insert
    void addLink(DatabaseUserLink link);
}

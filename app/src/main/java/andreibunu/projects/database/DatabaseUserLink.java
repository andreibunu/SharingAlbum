package andreibunu.projects.database;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "userlinks")
public class DatabaseUserLink {
    @PrimaryKey
    @NonNull
    @SerializedName("idx")
    private String idx;

    @SerializedName("username")
    private String username;

    @SerializedName("number")
    private int number;

    @SerializedName("friendid")
    private String friendId;

    public DatabaseUserLink(@NonNull String idx, String username, int number, String friendId) {
        this.idx = idx;
        this.username = username;
        this.number = number;
        this.friendId = friendId;
    }

    @NonNull
    public String getIdx() {
        return idx;
    }

    public void setIdx(@NonNull String idx) {
        this.idx = idx;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }
}

package andreibunu.projects.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "images")
public class DatabaseImage {
    @PrimaryKey
    @NonNull
    @SerializedName("image_name")
    private String imageName;

    @SerializedName("date")
    private String date;

    @SerializedName("person")
    private String person;

    @SerializedName("hashtags")
    private String hashtags;

    @SerializedName("location")
    private String location;

    public DatabaseImage(@NonNull String imageName, String date, String person, String hashtags, String location) {
        this.imageName = imageName;
        this.date = date;
        this.person = person;
        this.hashtags = hashtags;
        this.location = location;
    }

    @NonNull
    public String getImageName() {
        return imageName;
    }

    public void setImageName(@NonNull String imageName) {
        this.imageName = imageName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getHashtags() {
        return hashtags;
    }

    public void setHashtags(String hashtags) {
        this.hashtags = hashtags;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @NotNull
    @Override
    public String toString() {
        return "DatabaseImage{" +
                "imageName='" + imageName + '\'' +
                ", date='" + date + '\'' +
                ", person='" + person + '\'' +
                ", hashtags='" + hashtags + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}

package andreibunu.projects.domain;

import java.util.Date;

public class PhonePhoto {
    private String absolutePath;
    private Date date;

    public PhonePhoto(String absolutePath, Date date) {
        this.absolutePath = absolutePath;
        this.date = date;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

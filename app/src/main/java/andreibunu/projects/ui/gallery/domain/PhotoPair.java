package andreibunu.projects.ui.gallery.domain;

import andreibunu.projects.domain.PhonePhoto;

public class PhotoPair {

    private PhonePhoto left;
    private PhonePhoto right;

    public PhotoPair(PhonePhoto left, PhonePhoto right) {
        this.left = left;
        this.right = right;
    }

    public PhonePhoto getLeft() {
        return left;
    }

    public void setLeft(PhonePhoto left) {
        this.left = left;
    }

    public PhonePhoto getRight() {
        return right;
    }

    public void setRight(PhonePhoto right) {
        this.right = right;
    }
}

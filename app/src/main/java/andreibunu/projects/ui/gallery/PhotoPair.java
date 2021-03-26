package andreibunu.projects.ui.gallery;

import andreibunu.projects.domain.PhonePhoto;

public class PhotoPair {
    enum Orientation{
        PORTRAIT,
        LANDSCAPE
    }

    private PhonePhoto left;
    private PhonePhoto right;

    private Orientation leftOrientation;
    private Orientation rightOrientation;


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

    public Orientation getLeftOrientation() {
        return leftOrientation;
    }

    public void setLeftOrientation(Orientation leftOrientation) {
        this.leftOrientation = leftOrientation;
    }

    public Orientation getRightOrientation() {
        return rightOrientation;
    }

    public void setRightOrientation(Orientation rightOrientation) {
        this.rightOrientation = rightOrientation;
    }
}

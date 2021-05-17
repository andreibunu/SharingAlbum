package andreibunu.projects.ui.gallery.domain;

import andreibunu.projects.domain.FriendPhoto;

public class FriendPhotoPair {
    private FriendPhoto left;
    private FriendPhoto right;

    public FriendPhoto getLeft() {
        return left;
    }

    public FriendPhotoPair(FriendPhoto left, FriendPhoto right) {
        this.left = left;
        this.right = right;
    }

    public void setLeft(FriendPhoto left) {
        this.left = left;
    }

    public FriendPhoto getRight() {
        return right;
    }

    public void setRight(FriendPhoto right) {
        this.right = right;
    }


}

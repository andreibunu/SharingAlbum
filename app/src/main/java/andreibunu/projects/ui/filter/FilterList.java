package andreibunu.projects.ui.filter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import andreibunu.projects.ui.filter.adapter.domain.FriendFilter;

public class FilterList implements Serializable {

    private List<FriendFilter> friends;

    private List<String> tags;

    private List<Date> date;

    public List<FriendFilter> getFriends() {
        return friends;
    }

    public void setFriends(List<FriendFilter> friends) {
        this.friends = friends;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<Date> getDate() {
        return date;
    }

    public void setDate(List<Date> date) {
        this.date = date;
    }
}

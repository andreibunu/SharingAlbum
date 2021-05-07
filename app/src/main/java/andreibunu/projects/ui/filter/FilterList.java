package andreibunu.projects.ui.filter;

import java.io.Serializable;
import java.util.List;

import andreibunu.projects.ui.filter.adapter.domain.FriendFilter;

public class FilterList implements Serializable {

    private List<FriendFilter> friends;

    private List<String> tags;

    private List<String> locations;

    private List<String> date;

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

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public List<String> getDate() {
        return date;
    }

    public void setDate(List<String> date) {
        this.date = date;
    }
}

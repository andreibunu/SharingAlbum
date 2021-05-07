package andreibunu.projects.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {

    public static String getEmailBase(String email) {
        int index = email.indexOf("@");
        return email.substring(0, index);
    }

    public static List<Integer> getListFromStringifiedList(String people) {
        people = people.substring(1, people.length() - 1).replace(" ", "");
        List<String> myList = new ArrayList<String>(Arrays.asList(people.split(",")));
        List<Integer> ret = new ArrayList<>();
        myList.forEach(el -> {
            if(!el.isEmpty()) {
                ret.add(Integer.parseInt(el));
            }
        });
        return ret;
    }
}

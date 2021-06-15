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
        if (people.length() == 0) {
            return new ArrayList<>();
        }
        people = people.substring(1, people.length() - 1).replace(" ", "");
        List<String> myList = new ArrayList<String>(Arrays.asList(people.split(",")));
        List<Integer> ret = new ArrayList<>();
        myList.forEach(el -> {
            if (!el.isEmpty()) {
                ret.add(Integer.parseInt(el));
            }
        });
        return ret;
    }

    public static String getMonth(int month) {
        if (month == 1)
            return "JAN";
        if (month == 2)
            return "FEB";
        if (month == 3)
            return "MAR";
        if (month == 4)
            return "APR";
        if (month == 5)
            return "MAY";
        if (month == 6)
            return "JUN";
        if (month == 7)
            return "JUL";
        if (month == 8)
            return "AUG";
        if (month == 9)
            return "SEP";
        if (month == 10)
            return "OCT";
        if (month == 11)
            return "NOV";
        if (month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }
}

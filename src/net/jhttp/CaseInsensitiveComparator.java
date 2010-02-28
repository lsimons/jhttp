package net.jhttp;

import java.util.Comparator;

class CaseInsensitiveComparator implements Comparator<String> {
    public int compare(String o1, String o2) {
        if (o1 == null) {
            if (o2 == null) {
                return 0;
            } else {
                return 1;
            }
        } else if (o2 == null) {
            return -1;
        }

        return o2.compareToIgnoreCase(o2);
    }
}

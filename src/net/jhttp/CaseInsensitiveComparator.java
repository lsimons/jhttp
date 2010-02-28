package net.jhttp;

import java.util.Comparator;

class CaseInsensitiveComparator implements Comparator<String> {
    static final Comparator<String> CASE_INSENSITIVE =
            new CaseInsensitiveComparator();

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

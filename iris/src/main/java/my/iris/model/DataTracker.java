package my.iris.model;

import my.iris.util.LogUtils;

/**
 * Data tracker for JPA select statement.
 * Prints all received parameters for debugging purposes.
 *
 * <p>This class is intended to assist in testing JPQL queries
 * by logging each parameter's type and value.</p>
 */
public class DataTracker {
    public DataTracker(Object obj0) {
        init(obj0);
    }

    public DataTracker(Object obj0, Object obj1) {
        init(obj0, obj1);
    }

    public DataTracker(Object obj0, Object obj1, Object obj2) {
        init(obj0, obj1, obj2);
    }

    public DataTracker(Object obj0, Object obj1, Object obj2, Object obj3) {
        init(obj0, obj1, obj2, obj3);
    }

    public DataTracker(Object obj0, Object obj1, Object obj2, Object obj3, Object obj4) {
        init(obj0, obj1, obj2, obj3, obj4);
    }

    public DataTracker(Object obj0, Object obj1, Object obj2, Object obj3, Object obj4, Object obj5) {
        init(obj0, obj1, obj2, obj3, obj4, obj5);
    }

    public DataTracker(Object obj0, Object obj1, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6) {
        init(obj0, obj1, obj2, obj3, obj4, obj5, obj6);
    }

    public DataTracker(Object obj0, Object obj1, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7) {
        init(obj0, obj1, obj2, obj3, obj4, obj5, obj6, obj7);
    }

    public DataTracker(Object obj0, Object obj1, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7,
                       Object obj8) {
        init(obj0, obj1, obj2, obj3, obj4, obj5, obj6, obj7, obj8);
    }

    public DataTracker(Object obj0, Object obj1, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7,
                       Object obj8, Object obj9) {
        init(obj0, obj1, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9);
    }

    public DataTracker(Object obj0, Object obj1, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7,
                       Object obj8, Object obj9, Object obj10) {
        init(obj0, obj1, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10);
    }

    public DataTracker(Object obj0, Object obj1, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7,
                       Object obj8, Object obj9, Object obj10, Object obj11) {
        init(obj0, obj1, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11);
    }

    public DataTracker(Object obj0, Object obj1, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7,
                       Object obj8, Object obj9, Object obj10, Object obj11, Object obj12) {
        init(obj0, obj1, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12);
    }

    public DataTracker(Object obj0, Object obj1, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7,
                       Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13) {
        init(obj0, obj1, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13);
    }

    public DataTracker(Object obj0, Object obj1, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7,
                       Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13, Object obj14) {
        init(obj0, obj1, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13, obj14);
    }

    public DataTracker(Object obj0, Object obj1, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7,
                       Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13, Object obj14, Object obj15) {
        init(obj0, obj1, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13, obj14, obj15);
    }

    public DataTracker(Object obj0, Object obj1, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7,
                       Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13, Object obj14, Object obj15,
                       Object obj16) {
        init(obj0, obj1, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13, obj14, obj15, obj16);
    }


    private void init(Object... args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; ++i) {
            var arg = args[i];
            sb.append(i).append(" ");
            if (arg == null) {
                sb.append("null\n");
            } else {
                sb.append(arg.getClass().getName())
                        .append(":")
                        .append(arg)
                        .append("\n");
            }
        }
        if (sb.length() > 1) {
            sb.insert(0, "\n")
                    .delete(sb.length() - 1, sb.length());
        }
        LogUtils.info(getClass(), sb.toString());
    }
}
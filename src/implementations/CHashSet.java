package implementations;

import enumerations.CompilerEnum;
import enumerations.LexemeEnum;

import java.util.ArrayList;
import java.util.HashSet;

public class CHashSet<T> extends HashSet<T> implements Parsable {
    @Override
    public Object doMethod(String methodName, ArrayList<Object> arguments, CompilerEnum type) {
        if (methodName.equals("add")) {
            if (arguments.size() == 1) {
                if (type.equals(LexemeEnum.INT_TP))
                    add((T) getIntValue(arguments.get(0)));
                else if (type.equals(LexemeEnum.DOUBLE_TP))
                    add((T) getDoubleValue(arguments.get(0)));
                return 1;
            }
            return null;
        } else if (methodName.equals("size")) {
            if (arguments.size() == 0)
                return size();
            return null;
        } else if (methodName.equals("isEmpty")) {
            if (arguments.size() == 0)
                return isEmpty() ? 1 : 0;
            return null;
        } else if (methodName.equals("contains")) {
            if (arguments.size() == 1)
                return contains(arguments.get(0)) ? 1 : 0;
            return null;
        } else if (methodName.equals("remove")) {
            if (arguments.size() == 1)
                return remove(arguments.get(0)) ? 1 : 0;
            return null;
        } else if (methodName.equals("clear")) {
            if (arguments.size() == 0) {
                clear();
                return 1;
            }
            return null;
        }
        return null;
    }

    private Integer getIntValue(Object obj) {
        if (obj instanceof Double) {
            return (int) ((double) obj);
        } else if (obj instanceof Integer) {
            return (Integer) obj;
        }
        return null;
    }

    private Double getDoubleValue(Object obj) {
        if (obj instanceof Integer) {
            return (double) ((int) obj);
        } else if (obj instanceof Double) {
            return (Double) obj;
        }
        return null;
    }
}

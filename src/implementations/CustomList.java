package implementations;

import enumerations.CompilerEnum;
import enumerations.LexemeEnum;

import java.util.*;

public class CustomList<T> implements List<T>, Parsable {
    private Object[] list;
    private int capacity = 16;
    private int resizeValue = 16;
    private int size = 0;

    public CustomList() {
        list = new Object[capacity];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        for (int i = 0; i < size; i++) {
            if (o.equals(list[i]))
                return true;
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return null;
    }

    private Object[] extendArray(Object[] array, int size) {
        Object[] tmpArray = new Object[size];
        for (int i = 0; i < array.length && i < size; i++) {
            tmpArray[i] = array[i];
        }
        return tmpArray;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {
        list = new Object[resizeValue];
        size = 0;
        capacity = resizeValue;
    }

    @Override
    public T get(int index) {
        return (T) list[index];
    }

    @Override
    public T set(int index, T element) {
        list[index] = element;
        return element;
    }

    @Override
    public void add(int index, T element) {
        if (size == capacity) {
            list = extendArray(list, size + resizeValue);
            capacity += resizeValue;
        }
        size++;
        for (int i = index; i < size; i++) {
            list[i + 1] = list[i];
        }
        list[index] = element;
    }

    @Override
    public T remove(int index) {
        T tmp = (T) list[index];
        for (int i = index; i < size - 1; i++) {
            list[i] = list[i + 1];
        }
        return tmp;
    }

    @Override
    public boolean add(T t) {
        if (size == capacity) {
            list = extendArray(list, size + resizeValue);
            capacity += resizeValue;
        }
        if (list == null)
            return false;
        list[size] = t;
        size++;
        return true;
    }

    @Override
    public int indexOf(Object o) {
        return 0;
    }

    @Override
    public int lastIndexOf(Object o) {
        return 0;
    }

    @Override
    public ListIterator<T> listIterator() {
        return null;
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return null;
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return null;
    }

    @Override
    public Object doMethod(String methodName, ArrayList<Object> arguments, CompilerEnum type) {
        if (methodName.equals("get")) {
            if (arguments.size() == 1)
                return get((Integer) arguments.get(0));
            return null;
        } else if (methodName.equals("add")) {
            if (arguments.size() == 1) {
                if (type.equals(LexemeEnum.INT_TP))
                    add((T) getIntValue(arguments.get(0)));
                else if (type.equals(LexemeEnum.DOUBLE_TP))
                    add((T) getDoubleValue(arguments.get(0)));
                return 1;
            }
            if (arguments.size() == 2) {
                if (type.equals(LexemeEnum.INT_TP))
                    add((Integer) arguments.get(0), (T) getIntValue(arguments.get(1)));
                else if (type.equals(LexemeEnum.DOUBLE_TP))
                    add((Integer) arguments.get(0), (T) getDoubleValue(arguments.get(1)));
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
                return remove((Integer) arguments.get(0)) ? 1 : 0;
            return null;
        } else if (methodName.equals("set")) {
            if (arguments.size() == 2)
                if (type.equals(LexemeEnum.INT_TP))
                    return set((Integer) arguments.get(0), (T) getIntValue(arguments.get(1)));
                else if (type.equals(LexemeEnum.DOUBLE_TP))
                    return set((Integer) arguments.get(0), (T) getDoubleValue(arguments.get(1)));
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

    @Override
    public String toString() {
        if (size == 0)
            return "[]";
        String str = "[";
        for (int i = 0; i < size - 1; i++) {
            str += list[i] + ", ";
        }
        str += list[size - 1] + "]";
        return str;
    }
}

package info.kgeorgiy.ja.novitskii.arrayset;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

public class ArraySet<T> extends AbstractSet<T> implements SortedSet<T> {
    private final ArrayList<T> objects;
    private final Comparator<T> sortComparator;

    public ArraySet() {
        this(List.of(), null);
    }

    public ArraySet(Comparator<T> sortComparator) {
        this(List.of(), sortComparator);
    }

    public ArraySet(Collection<T> collection) {
        this(collection, null);
    }

    public ArraySet(Collection<T> collection, Comparator<T> sortComparator) {
        TreeSet<T> uniqueElements = new TreeSet<>(sortComparator);
        uniqueElements.addAll(collection);
        objects = new ArrayList<>(uniqueElements);
        this.sortComparator = sortComparator;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
        return Collections.binarySearch(objects, (T) o, sortComparator) >= 0;
    }

    @Override
    public Comparator<? super T> comparator() {
        return sortComparator;
    }

    private ArraySet<T> subSetImpl(T fromElement, boolean inclusiveFrom, T toElement, boolean inclusiveTo) {
        int firstId = Collections.binarySearch(objects, fromElement, sortComparator);
        int secondId = Collections.binarySearch(objects, toElement, sortComparator);
        if (firstId < 0) {
            firstId = -firstId - 1;
        }
        if (secondId < 0) {
            secondId = -secondId - 1;
        }
        if (firstId > secondId) {
            if (secondId != objects.size() - 1) {
                throw new IllegalArgumentException("firstElement > toElement");
            }
        }
        if (inclusiveFrom && !inclusiveTo) {
            return new ArraySet<>(objects.subList(firstId, secondId), sortComparator);
        } else if (inclusiveFrom) {
            return new ArraySet<>(objects.subList(firstId, secondId + 1), sortComparator);
        } else if (inclusiveTo) {
            return new ArraySet<>(objects.subList(firstId + 1, secondId + 1), sortComparator);
        } else {
            return new ArraySet<>(objects.subList(firstId + 1, secondId), sortComparator);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public ArraySet<T> subSet(T fromElement, T toElement) {
        if (sortComparator != null && sortComparator.compare(fromElement, toElement) > 0) {
            throw new IllegalArgumentException("fromElement > toElement");
        } else if (sortComparator == null && ((Comparator<T>) Comparator.naturalOrder()).compare(fromElement, toElement) > 0) {
            throw new IllegalArgumentException("fromElement > toElement");
        }
        return subSetImpl(fromElement, true, toElement, false);
    }

    @Override
    public ArraySet<T> headSet(T toElement) {
        if (this.isEmpty()) {
            return new ArraySet<>(sortComparator);
        }
        return subSetImpl(objects.get(0), true, toElement, false);
    }

    @Override
    public ArraySet<T> tailSet(T fromElement) {
        if (this.isEmpty()) {
            return new ArraySet<T>(sortComparator);
        }
        return subSetImpl(fromElement, true, objects.get(objects.size() - 1), true);
    }

    @Override
    public T first() {
        if (this.isEmpty()) {
            throw new NoSuchElementException("No elements in empty ArraySet");
        } else {
            return objects.get(0);
        }
    }

    @Override
    public T last() {
        if (this.isEmpty()) {
            throw new NoSuchElementException("No elements in empty ArraySet");
        }
        return objects.get(objects.size() - 1);
    }

    @Override
    public int size() {
        return objects.size();
    }

    @Override
    public Iterator<T> iterator() {
        // :NOTE: list.iterator()
        return objects.iterator();
    }


    @Override
    public boolean add(T t) {
        throw new UnsupportedOperationException("Can't add element to immutable ArraySet");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Can't remove element from immutable ArraySet");
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException("Can't add elements from immutable ArraySet");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Can't modify element from immutable ArraySet");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Can't clear element from immutable ArraySet");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Can't clear element from immutable ArraySet");
    }
}

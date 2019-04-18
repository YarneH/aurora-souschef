package android.util;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SparseArray<E> {

    private HashMap<Integer, E> mHashMap;

    public SparseArray() {
        mHashMap = new HashMap<>();
    }

    public void put(int key, E value) {
        mHashMap.put(key, value);
    }

    public E get(int key) {
        return mHashMap.get(key);
    }

    public int size(){
        return mHashMap.size();
    }

    public int keyAt(int index){
        Set<Integer> set = mHashMap.keySet();
        List<Integer> ordered = new ArrayList<>(new TreeSet<>(set));
        return ordered.get(index);
    }

    public E valueAt(int index){
        return mHashMap.get(keyAt(index));
    }

}


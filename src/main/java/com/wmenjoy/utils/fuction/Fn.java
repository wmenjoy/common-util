package com.wmenjoy.utils.fuction;



import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.wmenjoy.utils.fuction.functions.Action1;
import com.wmenjoy.utils.fuction.functions.Func1;
import com.wmenjoy.utils.fuction.functions.Func2;
import com.wmenjoy.utils.fuction.functions.Predicate;

public final class Fn {
    
    private Fn(){}

    // ==================================================================
    // ==================================================================
    // Common Operators
    // ==================================================================
    // ==================================================================
    /***
     * List归一化处理
     * @param lists
     * @return
     */
    public static <T> List<T> concat(List<List<T>> lists) {
        List<T> list = new ArrayList<T>();
        for (List<? extends T> l : lists) {
            list.addAll(l);
        }
        return list;
    }
    
    /***
     * 数组归一化处理
     * @param lists
     * @return
     */
    public static <T> List<T> concat(List<T>... lists) {
        List<T> list = new ArrayList<T>();
        for (List<? extends T> l : lists) {
            list.addAll(l);
        }
        return list;
    }

    /***
     * 集合归一化处理
     * @param collections
     * @return
     */
    public static <T> Collection<T> concat(Collection<T>... collections) {
        List<T> list = new ArrayList<T>();
        for (Collection<? extends T> c : collections) {
            list.addAll(c);
        }
        return list;
    }

    /**
     * 集合翻转
     * @param list
     * @return
     */
    public static <T> List<T> reverse(List<T> list) {
        List<T> copy = new ArrayList<T>(list);
        Collections.reverse(copy);
        return copy;
    }
    
    public static <T> List<T> shuffle(List<T> list) {
        List<T> copy = new ArrayList<T>(list);
        Collections.shuffle(copy);
        return copy;
    }

    /**
     * 赋值
     * @param t
     * @param times
     * @return
     */
    public static <T> List<T> repeat(T t, int times) {
        return new ArrayList<T>(Collections.nCopies(times, t));
    }

    public static List<Integer> range(int end) {
        return range(0, end, 1);
    }

    public static List<Integer> range(int start, int end) {
        return range(start, end, 1);
    }

    public static List<Integer> range(int start, int end, int step) {
        List<Integer> list = new ArrayList<Integer>();

        if (step > 0) {
            while (start < end) {
                list.add(start);
                start += step;
            }
        } else {
            while (start > end) {
                list.add(start);
                start += step;
            }
        }
        return list;
    }
    
    public static <T extends Comparable<? super T>> List<T> sort(List<T> list){
        Collections.sort(list);
        return list;
    }
    
    public static <T> List<T> sort(List<T> list, Comparator<? super T> c){
        Collections.sort(list, c);
        return list;
    }
    /***
     * groupBy
     * @param collection
     * @return
     */
    
    /***
     * indexBy
     * @param collection
     * @return
     * 
     *
     */
    
    /**
     * countBy
     * @param collection
     * @return
     */
    
    
    /***
     * sample 随机取样
     * @param collection
     * @return
     */
    
    /**
     * partition 分区
     * @param collection
     * @return
     */
    
    public static <T extends Object & Comparable<? super T>> T max(Collection<? extends T> collection) {
        return Collections.max(collection);
    }

    public static <T extends Object & Comparable<? super T>> T min(Collection<? extends T> collection) {
        return Collections.min(collection);
    }

    public static <T> T max(Comparator<? super T> cmp, Collection<? extends T> collection) {
        return Collections.max(collection, cmp);
    }

    public static <T> T min(Comparator<? super T> cmp, Collection<? extends T> collection) {
        return Collections.min(collection, cmp);
    }

    // ==================================================================
    // ==================================================================
    // Conditional Operators
    // ==================================================================
    // ==================================================================


    public static <T> Collection<T> distinct(Collection<? extends T> collection) {
        return new HashSet<T>(collection);
    }

    public static <T> List<T> distinct(List<? extends T> list) {
        return new ArrayList<T>(new HashSet<T>(list));
    }

    public static <T> T find(Predicate<? super T> p,
            Iterable<? extends T> iterable) {
        for (T t : iterable) {
            if (p.accept(t)) {
                return t;
            }
        }
        return null;
    }

    public static <T> T find(Predicate<? super T> p,
      List<? extends T> list) {
        for (T t : list) {
            if (p.accept(t)) {
                return t;
            }
        }
        return null;
    }
    
    public static <T> Collection<T> filter(Predicate<? super T> p,
                                           Iterable<? extends T> iterable) {
        List<T> result = new ArrayList<T>();
        for (T t : iterable) {
            if (p.accept(t)) {
                result.add(t);
            }
        }
        return result;
    }

    public static <T> List<T> filter(Predicate<? super T> p,
                                     List<? extends T> list) {
        if(list == null ||p == null){
            return Collections.emptyList();
        }
        
        List<T> result = new ArrayList<T>();
        for (T t : list) {
            if (p.accept(t)) {
                result.add(t);
            }
        }
        return result;
    }
    
    public static <T> Collection<T> reject(Predicate<? super T> p,
            Iterable<? extends T> iterable) {
        List<T> result = new ArrayList<T>();
        for (T t : iterable) {
            if (!p.accept(t)) {
                result.add(t);
            }
        }
        return result;
    }

    public static <T> List<T> reject(Predicate<? super T> p,
            List<? extends T> list) {
        List<T> result = new ArrayList<T>();
        for (T t : list) {
            if (!p.accept(t)) {
                result.add(t);
            }
        }
        return result;
    }

    public static <T> boolean all(Predicate<? super T> p, Collection<T> iterable) {
        for (final T t : iterable) {
            if (!p.accept(t)) {
                return false;
            }
        }
        return true;
    }

    public static <T> boolean any(Predicate<? super T> p, Collection<T> iterable) {
        for (final T t : iterable) {
            if (p.accept(t)) {
                return true;
            }
        }
        return false;
    }
    
    public static <T> boolean all(Predicate<? super T> p, Iterable<T> iterable) {
        for (final T t : iterable) {
            if (!p.accept(t)) {
                return false;
            }
        }
        return true;
    }

    public static <T> boolean any(Predicate<? super T> p, Iterable<T> iterable) {
        for (final T t : iterable) {
            if (p.accept(t)) {
                return true;
            }
        }
        return false;
    }
    
    public static <T> boolean every(Predicate<? super T> p, Iterable<T> iterable) {
       return all(p, iterable);
    }

    public static <T> boolean some(Predicate<? super T> p, Iterable<T> iterable) {
        return any(p, iterable);
    }
    
    public static <T> boolean every(Predicate<? super T> p, Collection<T> iterable) {
        return all(p, iterable);
     }

     public static <T> boolean some(Predicate<? super T> p, Collection<T> iterable) {
         return any(p, iterable);
     }
    
     /**
      * TODO 提取制定对象的制定属性
      * @param propertieName
      * @param list
      * @return
      */
     public static <T, R> List<R> pluck(String propertieName, List<T> list){
         return null;
     }
     
    public static <T> boolean contain(List<T> list, T value) {
        return value == null ? false : (list == null || list.size() == 0) ? false : list.contains(value);
    }


    // ==================================================================
    // ==================================================================
    // Map Operators
    // ==================================================================
    // ==================================================================

    public static <T, R> void forEach(Action1<? super T> func, Iterable<T> iterable) {
        for (T t : iterable) {
            func.call(t);
        }
    }

    /**
     * 数组到list的转换
     * */
    public static <T> List<List<T>> zip(List<T>... lists) {
        List<List<T>> zipped = new ArrayList<List<T>>();
        for (List<T> list : lists) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                List<T> zippedItem;
                if (i >= zipped.size()) {
                    zipped.add(zippedItem = new ArrayList<T>());
                } else {
                    zippedItem = zipped.get(i);
                }
                zippedItem.add(list.get(i));
            }
        }
        return zipped;
    }

    public static <T, R> R reduce(final Func2<R, ? super T, R> func2,
                                  final Iterable<? extends T> iterable,
                                  final R initializer) {
        R r = initializer;
        final Iterator<? extends T> it = iterable.iterator();
        while (it.hasNext()) {
            r = func2.call(r, it.next());
        }
        return r;
    }

    @SuppressWarnings("unchecked")
    public static <T, R> R reduce(final Func2<R, ? super T, R> func2,
                                  final Iterable<? extends T> iterable) {
        R r = null;
        final Iterator<? extends T> it = iterable.iterator();
        if (it.hasNext()) {
            r = (R) it.next();
            while (it.hasNext()) {
                r = func2.call(r, it.next());
            }
        }
        return r;
    }

    public static <T, R> void map(Func1<? super T, ? extends R> func, Collection<T> from, Collection<R> to) {
        for (T c : from) {
            to.add(func.call(c));
        }
    }

    public static <T, R> Iterable<R> map(Func1<? super T, ? extends R> func, Iterable<T> iterable) {
        List<R> r = new ArrayList<R>();
        for (final T t : iterable) {
            r.add(func.call(t));
        }
        return r;
    }

    public static <T, R> Collection<R> map(Func1<? super T, ? extends R> func, Collection<T> collection) {
        List<R> r = new ArrayList<R>();
        for (T c : collection) {
            r.add(func.call(c));
        }
        return r;
    }

    public static <T, R> List<R> map(Func1<? super T, ? extends R> func, List<T> list) {
        List<R> r = new ArrayList<R>();
        for (T l : list) {
            r.add(func.call(l));
        }
        return r;
    }

    public static <T, R> Set<R> map(Func1<? super T, ? extends R> func, Set<T> set) {
        Set<R> r = new HashSet<R>();
        for (T s : set) {
            r.add(func.call(s));
        }
        return r;
    }


    public static <T, R> List<R> flatMap(Func1<? super T, ? extends R> func, List<List<T>> lists) {
        List<R> list = new ArrayList<R>();
        for (List<? extends T> l : lists) {
            list.addAll(map(func, l));
        }
        return list;
    }

    public static <T, R> List<R> flatMap(Func1<? super T, ? extends R> func, List<T>... lists) {
        List<R> result = new ArrayList<R>();
        for (List<? extends T> list : lists) {
            result.addAll(map(func, list));
        }
        return result;
    }

    public static <T, R> Collection<R> flatMap(Func1<? super T, ? extends R> func, Collection<T>... collections) {
        List<R> list = new ArrayList<R>();
        for (Collection<? extends T> l : collections) {
            list.addAll(map(func, l));
        }
        return list;
    }
}

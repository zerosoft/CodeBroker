package com.codebroker.redis.collections.builder;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;

import java.util.*;

abstract class RedisCollection<T> implements Collection<T> {

    protected Class<T> clazz;

    protected String keyWithNameSpace;

    protected Jedis jedis;

    protected Gson gson;

    RedisCollection(Jedis jedis, Class<T> clazz, String keyWithNameSpace) {
        this.clazz = clazz;
        this.keyWithNameSpace = keyWithNameSpace;
        this.jedis = jedis;
        gson = new Gson();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        Objects.requireNonNull(c);
        for (T bean : c) {
            if (bean != null) {
                add(bean);
            }
        }
        return true;
    }

    @Override
    public int size() {
        return jedis.llen(keyWithNameSpace).intValue();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    @Override
    public Iterator<T> iterator() {
        return toArrayList().iterator();
    }

    @Override
    public Object[] toArray() {
        return toArrayList().toArray();
    }

    @Override
    public <E> E[] toArray(E[] a) {
        return toArrayList().toArray(a);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Use add all instead");
    }

    @Override
    public boolean containsAll(Collection<?> elements) {
        Objects.requireNonNull(elements);
        boolean containsAll = true;
        for (T element : (Collection<T>) elements) {
            if (!contains(element)) {
                containsAll = false;
            }
        }
        return containsAll;
    }

    @Override
    public boolean removeAll(Collection<?> elements) {
        Objects.requireNonNull(elements);
        boolean result = false;
        for (T element : (Collection<T>) elements) {
            if (remove(element)) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public boolean remove(Object o) {
        if (!clazz.isInstance(o)) {
            throw new ClassCastException("The object required is " + clazz.getName());
        }
        int index = indexOf(o);

        if (index == -1) {
            return false;
        } else {
            remove(index);
        }

        return true;
    }

    protected T remove(int index) {
        String value = jedis.lindex(keyWithNameSpace, (long) index);
        if (StringUtils.isNotBlank(value)) {
            jedis.lrem(keyWithNameSpace, 1, value);
            return gson.fromJson(value, clazz);
        }
        return null;
    }

    protected int indexOf(Object o) {
        if (!clazz.isInstance(o)) {
            return -1;
        }
        String value = gson.toJson(o);
        for (int index = 0; index < size(); index++) {
            String findValue = jedis.lindex(keyWithNameSpace, (long) index);
            if (value.equals(findValue)) {
                return index;
            }
        }
        return -1;
    }

    protected List<T> toArrayList() {
        List<T> list = new ArrayList<>();
        for (int index = 0; index < size(); index++) {
            T element = get(index);
            if (element != null) {
                list.add(element);
            }
        }
        return list;
    }

    protected T get(int index) {
        String value = jedis.lindex(keyWithNameSpace, index);
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return gson.fromJson(value, clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(keyWithNameSpace);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (RedisCollection.class.isInstance(obj)) {
            RedisCollection otherRedis = RedisCollection.class.cast(obj);
            return Objects.equals(otherRedis.keyWithNameSpace, keyWithNameSpace);
        }
        return false;
    }

}

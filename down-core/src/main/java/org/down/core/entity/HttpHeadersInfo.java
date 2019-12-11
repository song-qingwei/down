package org.down.core.entity;

import io.netty.handler.codec.http.HttpHeaders;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * <p>{@link HttpHeadersInfo}</p>
 * http 头信息
 * @author 白菜
 * @since Created in 2019/12/3 15:30
 */
public class HttpHeadersInfo extends HttpHeaders implements Serializable {

    private static final long serialVersionUID = -444162833390564651L;

    private Map<String, String> map;

    public HttpHeadersInfo() {
        this.map = new LinkedHashMap<>();
    }

    @Override
    public String get(String name) {
        Optional<String> optional = map.keySet().stream().filter(key -> key.equalsIgnoreCase(name)).findAny();
        return optional.map(key -> map.get(key)).orElse(null);
    }

    @Override
    public Integer getInt(CharSequence name) {
        String value = get(name);
        return StringUtils.isBlank(value) ? null : Integer.valueOf(value);
    }

    @Override
    public int getInt(CharSequence name, int defaultValue) {
        Integer value = getInt(name);
        return Objects.isNull(value) ? defaultValue : value;
    }

    @Override
    public Short getShort(CharSequence name) {
        String value = get(name);
        return StringUtils.isBlank(value) ? null : Short.valueOf(value);
    }

    @Override
    public short getShort(CharSequence name, short defaultValue) {
        Short value = getShort(name);
        return Objects.isNull(value) ? defaultValue : value;
    }

    @Override
    public Long getTimeMillis(CharSequence name) {
        String value = get(name);
        return StringUtils.isBlank(value) ? null : Long.valueOf(value);
    }

    @Override
    public long getTimeMillis(CharSequence name, long defaultValue) {
        Long value = getTimeMillis(name);
        return Objects.isNull(value) ? defaultValue : value;
    }

    @Override
    public List<String> getAll(String name) {
        String value = get(name);
        return StringUtils.isBlank(value) ? new ArrayList<>() : Collections.singletonList(value);
    }

    @Override
    public List<Map.Entry<String, String>> entries() {
        return new ArrayList<>(map.entrySet());
    }

    @Override
    public boolean contains(String name) {
        return map.keySet().stream().anyMatch(key -> key.equalsIgnoreCase(name));
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return map.entrySet().iterator();
    }

    @Override
    public Iterator<Map.Entry<CharSequence, CharSequence>> iteratorCharSequence() {
        Map<CharSequence, CharSequence> temp = new HashMap<>(map.size());
        map.forEach(temp::put);
        return temp.entrySet().iterator();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public Set<String> names() {
        return map.keySet();
    }

    @Override
    public HttpHeaders add(String name, Object value) {
        map.put(name, value.toString());
        return this;
    }

    @Override
    public HttpHeaders add(String name, Iterable<?> iterable) {
        return add(name, iterable);
    }

    @Override
    public HttpHeaders addInt(CharSequence name, int value) {
        return add(name, value);
    }

    @Override
    public HttpHeaders addShort(CharSequence name, short value) {
        return add(name, value);
    }

    @Override
    public HttpHeaders set(String name, Object value) {
        return add(name, value);
    }

    @Override
    public HttpHeaders set(String name, Iterable<?> iterable) {
        return add(name, iterable);
    }

    @Override
    public HttpHeaders setInt(CharSequence name, int value) {
        return add(name, value);
    }

    @Override
    public HttpHeaders setShort(CharSequence name, short value) {
        return add(name, value);
    }

    @Override
    public HttpHeaders remove(String name) {
        map.keySet().stream()
                .filter(key -> key.equalsIgnoreCase(name))
                .findAny()
                .ifPresent(key -> map.remove(name));
        return this;
    }

    @Override
    public HttpHeaders clear() {
        map.clear();
        return this;
    }

    public Map<String, String> toMap() {
        return this.map;
    }
}

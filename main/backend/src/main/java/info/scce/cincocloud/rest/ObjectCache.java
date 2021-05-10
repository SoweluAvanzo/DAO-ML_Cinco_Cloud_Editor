package info.scce.cincocloud.rest;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.RequestScoped;

@RequestScoped
public class ObjectCache {

    private final static int initialSize = 100;
    /**
     * Cache for mapping dbTos to RestTOs.
     */
    private final Map<PanacheEntityBase, RESTBaseType> restCache;
    /**
     * Cache for caching (rest) objects, when resolving cyclic references when receiving information from the client
     */
    private final Map<RESTBaseType, Set<String>> selectiveReadCache;
    /**
     * Cache for caching which selectives were already written through, to the db objects
     */
    private final Map<PanacheEntityBase, Set<String>> selectiveWriteCache;


    public ObjectCache() {
        this.restCache = new HashMap<>(initialSize);
        this.selectiveReadCache = new HashMap<>(initialSize);
        this.selectiveWriteCache = new HashMap<>(initialSize);
    }

    /**
     * @see ObjectCache#restCache
     */
    public <T extends RESTBaseType> T getRestTo(final PanacheEntityBase identifiable) {
        return (T) this.restCache.get(identifiable);
    }


    /**
     * @see ObjectCache#restCache
     */
    public boolean containsRestTo(final PanacheEntityBase identifiable) {
        return this.restCache.containsKey(identifiable);
    }

    /**
     * @see ObjectCache#selectiveReadCache
     */
    public boolean containsSelective(final RESTBaseType restTo, final String selective) {
        return this.selectiveReadCache.getOrDefault(restTo, Collections.emptySet()).contains(selective);
    }

    /**
     * @see ObjectCache#selectiveWriteCache
     */
    public boolean containsSelective(final PanacheEntityBase restTo, final String selective) {
        return this.selectiveWriteCache.getOrDefault(restTo, Collections.emptySet()).contains(selective);
    }


    /**
     * @see ObjectCache#restCache
     */
    public <T extends RESTBaseType> void putRestTo(final PanacheEntityBase identifiable, final T obj) {
        this.restCache.put(identifiable, obj);
    }

    /**
     * @see ObjectCache#selectiveReadCache
     */
    public void putSelective(final RESTBaseType restTo, final String selective) {
        this.putInternal(this.selectiveReadCache, restTo, selective);
    }

    /**
     * @see ObjectCache#selectiveWriteCache
     */
    public void putSelective(final PanacheEntityBase restTo, final String selective) {
        this.putInternal(this.selectiveWriteCache, restTo, selective);
    }

    private <T> void putInternal(final Map<T, Set<String>> cache, final T key, final String selective) {
        cache.putIfAbsent(key, new HashSet<>());
        cache.get(key).add(selective);
    }

}

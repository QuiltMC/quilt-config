package org.quiltmc.config.api;

public interface ConfigTypeWrapper<T, V> {

    /**
     * @return a new instance of this class created from the given representation
     */
    V convertFrom(T representation);

    /**
     * @return some representation value for serializationw
     */
    T getRepresentation(V value);
}

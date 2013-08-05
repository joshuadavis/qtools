package org.qtools.core;

/**
 * Provides instances of a specific class
 * <br>
 * User: josh
 * Date: 8/5/13
 * Time: 12:55 PM
 */
public interface Provider<T> {

    /**
     * Produces an instance of {@code T}.
     */
    T get();
}

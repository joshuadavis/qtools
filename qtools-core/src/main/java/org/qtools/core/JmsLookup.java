package org.qtools.core;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.Topic;

/**
 * Implementations look up JMS objects in JNDI, or otherwise (embedded HornetQ, for example).
 * <br>
 * User: josh
 * Date: 8/1/13
 * Time: 10:00 AM
 */
public interface JmsLookup
{
    /**
     * Get the connection factory.  May throw an exception if JNDI cannot be accessed.
     * @return the connection factory
     */
    ConnectionFactory getConnectionFactory();

    /**
     * Look up a Queue by name.
     * @param name the queue name
     * @return the Queue
     */
    Queue getQueue(String name);

    /**
     * Look up a topic by name.
     * @param name the topic name
     * @return the Topic
     */
    Topic getTopic(String name);

    /**
     * Close the lookup, release all resources.
     */
    void close();
}

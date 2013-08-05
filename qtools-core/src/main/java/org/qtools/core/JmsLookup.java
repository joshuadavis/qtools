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
    ConnectionFactory getConnectionFactory();

    Queue getQueue(String name);

    Topic getTopic(String name);

    void close();
}

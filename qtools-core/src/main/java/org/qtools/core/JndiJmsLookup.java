package org.qtools.core;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * JNDI Implementation of JmsLookup.
 * <br>
 * User: josh
 * Date: 8/6/13
 * Time: 10:01 AM
 */
public class JndiJmsLookup implements JmsLookup
{
    private final Provider<InitialContext> icProvider;
    private final String connectionFactoryName;

    public JndiJmsLookup(Provider<InitialContext> icProvider, String connectionFactoryName)
    {
        this.icProvider = icProvider;
        this.connectionFactoryName = connectionFactoryName;
    }

    public ConnectionFactory getConnectionFactory()
    {
        try
        {
            return (ConnectionFactory) icProvider.get().lookup(connectionFactoryName);
        }
        catch (NamingException e)
        {
            throw new RuntimeException("JNDI lookup of " + connectionFactoryName + " failed due to: " + e,e);
        }
    }

    public Queue getQueue(String name)
    {
        try
        {
            return (Queue) icProvider.get().lookup(name);
        }
        catch (NamingException e)
        {
            throw new RuntimeException("JNDI lookup of " + name + " failed due to: " + e,e);
        }
    }

    public Topic getTopic(String name)
    {
        try
        {
            return (Topic) icProvider.get().lookup(name);
        }
        catch (NamingException e)
        {
            throw new RuntimeException("JNDI lookup of " + name + " failed due to: " + e,e);
        }
    }

    public void close()
    {
        try
        {
            icProvider.get().close();
        }
        catch (NamingException e)
        {
            throw new RuntimeException("Unexpected exception while closing InitialContext: " + e,e);
        }
    }
}

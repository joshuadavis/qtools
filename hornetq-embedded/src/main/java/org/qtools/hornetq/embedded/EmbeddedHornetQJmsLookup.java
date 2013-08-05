package org.qtools.hornetq.embedded;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.HornetQClient;
import org.hornetq.api.core.client.ServerLocator;
import org.hornetq.core.remoting.impl.invm.InVMConnectorFactory;
import org.hornetq.jms.client.HornetQDestination;
import org.hornetq.jms.client.HornetQJMSConnectionFactory;
import org.hornetq.jms.client.HornetQQueue;
import org.hornetq.jms.client.HornetQTopic;
import org.qtools.core.JmsLookup;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.Topic;

/**
 * Embedded HornetQ JMS Lookup Implementation - doesn't use JNDI.
 * <br>
 * User: josh
 * Date: 8/1/13
 * Time: 10:07 AM
 */
public class EmbeddedHornetQJmsLookup implements JmsLookup
{
    private final ServerLocator serverLocator;
    private final HornetQJMSConnectionFactory cf;

    public EmbeddedHornetQJmsLookup()
    {
        serverLocator = HornetQClient.createServerLocatorWithoutHA(
                new TransportConfiguration(InVMConnectorFactory.class.getName()));
        cf = new HornetQJMSConnectionFactory(serverLocator);
    }

    public ConnectionFactory getConnectionFactory()
    {
        return cf;
    }

    public Queue getQueue(String name)
    {
        return new HornetQQueue(name);
    }

    public Topic getTopic(String name)
    {
        return new HornetQTopic(name);
    }

    public void close()
    {
        if (cf != null)
            cf.close();

        if (serverLocator != null)
            serverLocator.close();
    }
}

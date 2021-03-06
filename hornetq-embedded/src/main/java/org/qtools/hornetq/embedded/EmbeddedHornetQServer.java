package org.qtools.hornetq.embedded;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.core.config.Configuration;
import org.hornetq.core.config.impl.ConfigurationImpl;
import org.hornetq.core.remoting.impl.invm.InVMAcceptorFactory;
import org.hornetq.core.remoting.impl.netty.NettyAcceptorFactory;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.jms.client.HornetQDestination;
import org.hornetq.jms.server.config.ConnectionFactoryConfiguration;
import org.hornetq.jms.server.config.JMSConfiguration;
import org.hornetq.jms.server.config.impl.ConnectionFactoryConfigurationImpl;
import org.hornetq.jms.server.config.impl.JMSConfigurationImpl;
import org.hornetq.jms.server.config.impl.JMSQueueConfigurationImpl;
import org.hornetq.jms.server.config.impl.TopicConfigurationImpl;
import org.hornetq.jms.server.embedded.EmbeddedJMS;
import org.qtools.core.LoggerHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Helper class to start the embedded HornetQ JMS server.
 * <br>
 * User: Josh
 * Date: 7/26/13
 * Time: 7:50 AM
 */
public class EmbeddedHornetQServer
{
    private static final Logger log = Logger.getLogger(EmbeddedHornetQServer.class.getName());

    private static final String CONNECTOR_NAME = "connector";
    private static final String HORNET_Q_USER = "HornetQ_User";
    private static final String HORNET_Q_PASSWORD = "HornetQizK00l";
    private static final String CONNECTION_FACTORY_NAME = "cf";
    private static final String CONNECTION_FACTORY_BINDING = "/cf";
    private static final String JOURNAL_DATA_PATH = "target/data/journal";

    private final Set<String> queueNames = new HashSet<String>();
    private final Set<String> topicNames = new HashSet<String>();

    private EmbeddedJMS server;

    public void start()
    {
        try
        {
            synchronized (this)
            {
                doStart();
            }
        }
        catch (Exception e)
        {
            LoggerHelper.unexpectedError(log, e);
            throw new RuntimeException(e);
        }
    }

    private void doStart() throws Exception
    {
        // Create the Configuration, and set the properties accordingly
        Configuration configuration = new ConfigurationImpl();
        // we only need this for the server lock file
        configuration.setJournalDirectory(JOURNAL_DATA_PATH);
        configuration.setPersistenceEnabled(false);
        configuration.setSecurityEnabled(false);

        TransportConfiguration remoteTransport = new TransportConfiguration(NettyAcceptorFactory.class.getName());
        TransportConfiguration localTransport = new TransportConfiguration(InVMAcceptorFactory.class.getName());
        HashSet<TransportConfiguration> setTransp = new HashSet<TransportConfiguration>();
        setTransp.add(remoteTransport);
        setTransp.add(localTransport);
        configuration.setAcceptorConfigurations(setTransp);

        TransportConfiguration connectorConfig = new TransportConfiguration(NettyConnectorFactory.class.getName());

        configuration.getConnectorConfigurations().put(CONNECTOR_NAME, connectorConfig);

        // Set the cluster username and password to avoid warnings.
        configuration.setClusterUser(HORNET_Q_USER);
        configuration.setClusterPassword(HORNET_Q_PASSWORD);

        // Create the JMS configuration
        JMSConfiguration jmsConfig = new JMSConfigurationImpl();

        // Configure the JMS ConnectionFactory
        ArrayList<String> connectorNames = new ArrayList<String>();
        connectorNames.add(CONNECTOR_NAME);
        ConnectionFactoryConfiguration cfConfig = new ConnectionFactoryConfigurationImpl(
                CONNECTION_FACTORY_NAME,
                false, connectorNames,
                CONNECTION_FACTORY_BINDING);

        cfConfig.setRetryInterval(1000);
        cfConfig.setRetryIntervalMultiplier(1.0);
        cfConfig.setCallTimeout(30000);
        cfConfig.setReconnectAttempts(-1);

        jmsConfig.getConnectionFactoryConfigurations().add(cfConfig);

        for (String queueName : queueNames)
        {
            jmsConfig.getQueueConfigurations().add(
                    new JMSQueueConfigurationImpl(
                            queueName, null, false,
                            HornetQDestination.JMS_QUEUE_ADDRESS_PREFIX + queueName));
        }

        for (String topicName : topicNames)
        {
            jmsConfig.getTopicConfigurations().add(
                    new TopicConfigurationImpl(topicName)
            );
        }

        server = new EmbeddedJMS();
        server.setConfiguration(configuration);
        server.setJmsConfiguration(jmsConfig);
        server.start();
    }

    private void doStop() throws Exception
    {
        if (server != null)
        {
            EmbeddedJMS s = server;
            server = null;
            s.stop();
        }

    }

    public void stop()
    {
        try
        {
            synchronized (this)
            {
                doStop();
            }
        }
        catch (Exception e)
        {
            LoggerHelper.unexpectedError(log, e);
            throw new RuntimeException(e);
        }
    }

    public void addQueue(String queueName)
    {
        queueNames.add(queueName);
    }

    public void addTopic(String topicName)
    {
        topicNames.add(topicName);
    }
}

package org.qtools.hornetq.embedded;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.core.config.Configuration;
import org.hornetq.core.config.impl.ConfigurationImpl;
import org.hornetq.core.remoting.impl.invm.InVMAcceptorFactory;
import org.hornetq.core.remoting.impl.netty.NettyAcceptorFactory;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.jms.server.config.ConnectionFactoryConfiguration;
import org.hornetq.jms.server.config.JMSConfiguration;
import org.hornetq.jms.server.config.impl.ConnectionFactoryConfigurationImpl;
import org.hornetq.jms.server.config.impl.JMSConfigurationImpl;
import org.hornetq.jms.server.embedded.EmbeddedJMS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Helper class to start the embedded HornetQ JMS server.
 * <br>
 * User: Josh
 * Date: 7/26/13
 * Time: 7:50 AM
 */
public class EmbeddedHornetQServer
{
    private static final Logger log = LoggerFactory.getLogger(EmbeddedHornetQServer.class);
    public static final String CONNECTOR_NAME = "connector";
    public static final String HORNET_Q_USER = "HornetQ_User";
    public static final String HORNET_Q_PASSWORD = "HornetQizK00l";
    public static final String CONNECTION_FACTORY_NAME = "cf";
    public static final String CONNECTION_FACTORY_BINDING = "/cf";
    public static final String JOURNAL_DATA_PATH = "target/data/journal";

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
            log.error("Unexpected: " + e, e);
            throw new RuntimeException(e);
        }
    }

    private void doStart() throws Exception
    {
        log.info("Starting embedded HornetQ JMS...");
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
        jmsConfig.getConnectionFactoryConfigurations().add(cfConfig);

        // Create and start the server
        server = new EmbeddedJMS();
        server.setConfiguration(configuration);
        server.setJmsConfiguration(jmsConfig);
        server.start();
        log.info("HornetQ Server started.");
    }

    private void doStop() throws Exception
    {
        if (server != null)
        {
            log.info("Stopping embedded HornetQ JMS...");
            EmbeddedJMS s = server;
            server = null;
            s.stop();
            log.info("HornetQ Server stopped.");
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
            log.error("Unexpected: " + e, e);
            throw new RuntimeException(e);
        }
    }
}

package org.qtools.hornetq.embedded;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.core.config.Configuration;
import org.hornetq.core.config.impl.ConfigurationImpl;
import org.hornetq.core.remoting.impl.invm.InVMAcceptorFactory;
import org.hornetq.core.remoting.impl.netty.NettyAcceptorFactory;
import org.hornetq.core.server.HornetQServer;
import org.hornetq.core.server.HornetQServers;

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
    public void start()
    {
        try
        {
            // Step 1. Create the Configuration, and set the properties accordingly
            Configuration configuration = new ConfigurationImpl();
            //we only need this for the server lock file
            configuration.setJournalDirectory("target/data/journal");
            configuration.setPersistenceEnabled(false);
            configuration.setSecurityEnabled(false);

            TransportConfiguration remoteTransport = new TransportConfiguration(NettyAcceptorFactory.class.getName());
            TransportConfiguration localTransport = new TransportConfiguration(InVMAcceptorFactory.class.getName());
            HashSet<TransportConfiguration> setTransp = new HashSet<TransportConfiguration>();
            setTransp.add(remoteTransport);
            setTransp.add(localTransport);
            configuration.setAcceptorConfigurations(setTransp);

            // Step 2. Create and start the server
            HornetQServer server = HornetQServers.newHornetQServer(configuration);
            server.start();
            System.out.println("HornetQ Server started.");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

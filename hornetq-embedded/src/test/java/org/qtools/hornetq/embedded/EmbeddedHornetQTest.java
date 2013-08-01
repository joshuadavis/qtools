package org.qtools.hornetq.embedded;


import org.junit.Assert;
import org.junit.Test;
import org.qtools.core.JmsLookup;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

/**
 * Tests the embedded HornetQ JMS server startup / lookup helpers.
 * <br>
 * User: josh
 * Date: 8/1/13
 * Time: 12:02 PM
 */
public class EmbeddedHornetQTest
{
    @Test
    public void testStartStop() throws JMSException
    {
        JmsLookup lookup = new EmbeddedHornetQJmsLookup();

        EmbeddedHornetQServer server = new EmbeddedHornetQServer();
        server.start();

        ConnectionFactory cf = lookup.getConnectionFactory();

        Connection con = cf.createConnection();

        con.close();


        server.stop();

        // Make sure we can't get a connection.
        JMSException expected = null;
        try
        {
            Connection con2 = cf.createConnection();

            con2.close();

        } catch (JMSException jmse)
        {
            expected = jmse;
        }

        Assert.assertNotNull(expected);

        lookup.close();
    }
}

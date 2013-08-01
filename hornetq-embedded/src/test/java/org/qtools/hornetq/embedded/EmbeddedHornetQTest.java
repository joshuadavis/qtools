package org.qtools.hornetq.embedded;


import org.junit.Assert;
import org.junit.Test;
import org.qtools.core.JmsHelper;
import org.qtools.core.JmsLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

/**
 * Tests the embedded HornetQ JMS server startup / lookup helpers.
 * <br>
 * User: josh
 * Date: 8/1/13
 * Time: 12:02 PM
 */
public class EmbeddedHornetQTest
{
    private static final Logger log = LoggerFactory.getLogger(EmbeddedHornetQTest.class);

    @Test
    public void testStartStop() throws JMSException
    {
        JmsLookup lookup = new EmbeddedHornetQJmsLookup();

        EmbeddedHornetQServer server = new EmbeddedHornetQServer();
        server.addQueue("queue1");
        server.start();

        sendAFewMessages(lookup);

        server.stop();

        // Make sure we can't get a connection.
        JMSException expected = null;
        try
        {
            sendAFewMessages(lookup);

        } catch (JMSException jmse)
        {
            expected = jmse;
        }

        Assert.assertNotNull(expected);

        lookup.close();
    }

    private void sendAFewMessages(JmsLookup lookup) throws JMSException
    {
        ConnectionFactory cf = lookup.getConnectionFactory();
        Connection con = cf.createConnection();

        Queue queue = lookup.getQueue("queue1");

        log.info("queue=" + queue);

        Session session = con.createSession(false,Session.AUTO_ACKNOWLEDGE);

        MessageConsumer consumer = session.createConsumer(queue);
        con.start();
        MessageProducer producer = session.createProducer(queue);


        for (int i = 0; i < 10 ; i++)
        {
            TextMessage message = session.createTextMessage("message #" + i);
            producer.send(message);
            log.info("Sent: " + message);
        }

        for (int i = 0; i < 10 ; i++)
        {
            TextMessage x = (TextMessage) consumer.receive(1000);
            log.info("Received: " + x);
        }

        JmsHelper.close(producer);
        JmsHelper.close(consumer,session,con);
    }
}

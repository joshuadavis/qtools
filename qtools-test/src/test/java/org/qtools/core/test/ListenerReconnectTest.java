package org.qtools.core.test;

import org.junit.Test;
import org.qtools.core.JmsConversation;
import org.qtools.core.JmsListener;
import org.qtools.hornetq.embedded.EmbeddedHornetQJmsEnvironment;
import org.qtools.hornetq.embedded.EmbeddedHornetQServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

/**
 * Test the reconnect cababilities of JmsListener
 * <br>
 * User: josh
 * Date: 8/5/13
 * Time: 4:42 PM
 */
public class ListenerReconnectTest
{
    private static final Logger log = LoggerFactory.getLogger(ListenerReconnectTest.class);

    private EmbeddedHornetQJmsEnvironment env;

    @Test
    public void testListenBeforeServerStart() throws Exception
    {
        env = new EmbeddedHornetQJmsEnvironment();
        final EmbeddedHornetQServer server = env.getServer();
        server.addTopic("topic1");

        // Create a listener.
        JmsListener listener = JmsListener.nonAuthNonTx(env.getLookup(),"topic1",true,true);
        listener.setDelegate(new MessageListener()
        {
            public void onMessage(Message message)
            {
                log.info("topic listener 1 - onMessage: " + message);
            }
        });
        listener.startNowait();

        Thread.sleep(5000);

        log.info("Starting server...");
        server.start();

        log.info("Wait for listener...");
        listener.waitForReady();

        log.info("Listener ready.");

        JmsListener listener2 = JmsListener.nonAuthNonTx(env.getLookup(),"topic1",true,true);
        listener2.setDelegate(new MessageListener()
        {
            public void onMessage(Message message)
            {
                log.info("topic listener 2 - onMessage: " + message);
            }
        });
        listener2.start();

        // Send a message.
        JmsConversation conversation = JmsConversation.nonAuthNonTx(env.getLookup());
        conversation.doAction(new JmsConversation.Action()
        {
            public void go(JmsConversation.Context context) throws JMSException
            {
                MessageProducer producer = context.getTopicProducer("topic1");
                for (int i = 0; i < 10; i++)
                {
                    TextMessage m = context.getSession().createTextMessage("this is #" + i + 1);
                    producer.send(m);
                }
                log.info("Messages sent.");
            }
        });
        log.info("Stopping listener....");
        listener.stop();
        listener2.stop();
        env.close();
    }
}

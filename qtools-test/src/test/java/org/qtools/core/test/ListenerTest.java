package org.qtools.core.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.qtools.core.JmsConversation;
import org.qtools.core.JmsListener;
import org.qtools.hornetq.embedded.EmbeddedHornetQJmsEnvironment;
import org.qtools.hornetq.embedded.EmbeddedHornetQServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.jms.*;

/**
 * Test for the JmsListener class.
 * <br>
 * User: josh
 * Date: 8/5/13
 * Time: 3:36 PM
 */
public class ListenerTest
{
    private static final Logger log = LoggerFactory.getLogger(ListenerTest.class);

    private EmbeddedHornetQJmsEnvironment env;

    @Before
    public void setUp()
    {
        SLF4JBridgeHandler.install();
        env = new EmbeddedHornetQJmsEnvironment();
        final EmbeddedHornetQServer server = env.getServer();
        server.addQueue("queue1");
        server.addTopic("topic1");
        server.start();
    }

    @After
    public void tearDown()
    {
        env.close();
    }

    @Test
    public void testQueueListener() throws Exception
    {
        // Create a listener.
        JmsListener listener = JmsListener.nonAuthNonTx(env.getLookup(),"queue1",false,false);
        listener.setDelegate(new MessageListener()
        {
            public void onMessage(Message message)
            {
                log.info("queue - onMessage: " + message);
            }
        });

        listener.start();

        // Send a message.
        JmsConversation conversation = JmsConversation.nonAuthNonTx(env.getLookup());
        conversation.doAction(new JmsConversation.Action()
        {
            public void go(JmsConversation.Context context) throws JMSException
            {
                MessageProducer producer = context.getQueueProducer("queue1");
                for (int i = 0 ; i < 10 ; i++)
                {
                    TextMessage m = context.getSession().createTextMessage("this is #" + i+1);
                    producer.send(m);
                }
                log.info("Messages sent.");
            }
        });
        Thread.sleep(1000);
        listener.stop();
    }

    @Test
    public void testTopicListener() throws Exception
    {
        // Create a listener.
        JmsListener listener = JmsListener.nonAuthNonTx(env.getLookup(),"topic1",true,false);
        listener.setDelegate(new MessageListener()
        {
            public void onMessage(Message message)
            {
                log.info("topic - onMessage: " + message);
            }
        });

        listener.start();

        // Send a message.
        JmsConversation conversation = JmsConversation.nonAuthNonTx(env.getLookup());
        conversation.doAction(new JmsConversation.Action()
        {
            public void go(JmsConversation.Context context) throws JMSException
            {
                MessageProducer producer = context.getTopicProducer("topic1");
                for (int i = 0 ; i < 10 ; i++)
                {
                    TextMessage m = context.getSession().createTextMessage("this is #" + i+1);
                    producer.send(m);
                }
                log.info("Messages sent.");
            }
        });
        Thread.sleep(1000);
        listener.stop();
    }

}

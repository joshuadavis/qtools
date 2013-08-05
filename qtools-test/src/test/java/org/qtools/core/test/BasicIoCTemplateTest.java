package org.qtools.core.test;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.qtools.core.JmsConversation;
import org.qtools.hornetq.embedded.EmbeddedHornetQJmsEnvironment;
import org.qtools.hornetq.embedded.EmbeddedHornetQServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.jms.*;

/**
 * Test the IoC template classes.
 * <br>
 * User: josh
 * Date: 8/5/13
 * Time: 11:50 AM
 */
public class BasicIoCTemplateTest
{

    private static final Logger log = LoggerFactory.getLogger(BasicIoCTemplateTest.class);

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
    public void testQueueConversation() throws Exception
    {
        JmsConversation conv = JmsConversation.nonAuthNonTx(env.getLookup());

        conv.doAction(new JmsConversation.Action()
        {
            public void go(JmsConversation.Context context) throws JMSException
            {
                Session ses = context.getSession();
                // Create the consumer first.
                MessageConsumer consumer = context.getQueueConsumer("queue1");

                TextMessage textMessage = ses.createTextMessage("test text message");
                MessageProducer producer = context.getQueueProducer("queue1");
                producer.send(textMessage);
                // NOTE: The message ID will not be set until after it is sent.
                final String id = textMessage.getJMSMessageID();
                log.info("Sent message id = " + id);

                log.info("receiving...");
                Message m = consumer.receive(1000);
                log.info("received : " + m.getJMSMessageID());
            }
        });
    }

    @Test
    public void testTopicConversation() throws Exception
    {
        JmsConversation conv = JmsConversation.nonAuthNonTx(env.getLookup());

        conv.doAction(new JmsConversation.Action()
        {
            public void go(JmsConversation.Context context) throws JMSException
            {
                Session ses = context.getSession();
                // Create the consumer first.
                MessageConsumer consumer = context.getTopicConsumer("topic1");

                TextMessage textMessage = ses.createTextMessage("test text message");
                MessageProducer producer = context.getTopicProducer("topic1");
                producer.send(textMessage);
                // NOTE: The message ID will not be set until after it is sent.
                final String id = textMessage.getJMSMessageID();
                log.info("Sent message id = " + id);

                log.info("receiving...");
                Message m = consumer.receive(1000);
                log.info("received : " + m.getJMSMessageID());
            }
        });
    }

    @Test
    public void testBadDestinationConversation() throws Exception
    {
        JmsConversation conv = JmsConversation.nonAuthNonTx(env.getLookup());

        JMSException expected = null;

        try
        {
            conv.doAction(new JmsConversation.Action()
            {
                public void go(JmsConversation.Context context) throws JMSException
                {
                    Session ses = context.getSession();
                    // Create the consumer first.
                    MessageConsumer consumer = context.getTopicConsumer("bad-topic1");

                    TextMessage textMessage = ses.createTextMessage("test text message");
                    MessageProducer producer = context.getTopicProducer("bad-topic1");
                    producer.send(textMessage);
                    // NOTE: The message ID will not be set until after it is sent.
                    final String id = textMessage.getJMSMessageID();
                    log.info("Sent message id = " + id);

                    log.info("receiving...");
                    Message m = consumer.receive(1000);
                    log.info("received : " + m.getJMSMessageID());
                }
            });
        }
        catch (JMSException e)
        {
            expected = e;
        }
        Assert.assertNotNull(expected);
    }

}

package org.qtools.core.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.qtools.core.JmsConversation;
import org.qtools.hornetq.embedded.EmbeddedHornetQJmsEnvironment;
import org.qtools.hornetq.embedded.EmbeddedHornetQServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        env = new EmbeddedHornetQJmsEnvironment();
        final EmbeddedHornetQServer server = env.getServer();
        server.addQueue("queue1");
        server.start();
    }

    @After
    public void tearDown()
    {
        env.close();
    }

    @Test
    public void testTemplate() throws Exception
    {
        JmsConversation conv = JmsConversation.nonAuthNonTx(env.getLookup());

        conv.go(new JmsConversation.Action()
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
}

package org.qtools.core;

import javax.jms.*;
import java.util.logging.Logger;

/**
 * Listens for messages on a JMS queue or topic.
 * <br>
 * User: josh
 * Date: 8/5/13
 * Time: 3:22 PM
 */
public class JmsListener extends JmsAccess
{
    private static final Logger log = Logger.getLogger(JmsListener.class.getName());

    private final String name;
    private final boolean topic;
    private Connection con;
    private Session ses;
    private MessageConsumer consumer;
    private MessageListener delegate;

    public static JmsListener nonAuthNonTx(JmsLookup lookup,String name,boolean topic)
    {
        return new JmsListener(lookup,null,null,false, Session.AUTO_ACKNOWLEDGE,name,topic);
    }

    public JmsListener(JmsLookup lookup,
                          String username, String password,
                          boolean transacted, int ackMode,
                          String name,boolean topic)
    {
        super(lookup, username, password, transacted, ackMode);
        this.name = name;
        this.topic = topic;
    }

    public void setDelegate(MessageListener messageListener)
    {
        this.delegate = messageListener;
    }

    public void start()
    {
       ConnectionFactory cf = getConnectionFactory();
        try
        {
            Destination destination = topic ? getLookup().getTopic(name) : getLookup().getQueue(name);
            con = getConnection(cf);
            ses = createSession(con);
            consumer = ses.createConsumer(destination);
            consumer.setMessageListener(delegate);
            con.start();
            log.info("Listener started.");
        }
        catch (JMSException e)
        {
            JmsHelper.close(con);
            con = null;
        }
    }

    public void stop()
    {
        JmsHelper.close(consumer,ses,con);
        consumer = null;
        ses = null;
        con = null;
    }
}

package org.qtools.core;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An IoC template for interacting with JMS.
 * <br>
 * User: josh
 * Date: 8/5/13
 * Time: 12:13 PM
 */
public class JmsConversation extends JmsAccess
{
    private static final Logger log = Logger.getLogger(JmsConversation.class.getName());

    public static interface Context
    {
        Session getSession();

        MessageProducer getQueueProducer(String name) throws JMSException;

        MessageConsumer getQueueConsumer(String name) throws JMSException;

        MessageProducer getTopicProducer(String name) throws JMSException;

        MessageConsumer getTopicConsumer(String name) throws JMSException;
    }

    public static interface Action
    {
        void go(Context context) throws JMSException;
    }

    public static JmsConversation nonAuthNonTx(JmsLookup lookup)
    {
        return new JmsConversation(lookup,null,null,false,Session.AUTO_ACKNOWLEDGE);
    }

    public JmsConversation(JmsLookup lookup, String username, String password, boolean transacted,int ackMode)
    {
        super(lookup, username, password, transacted, ackMode);

    }

    private static class DestinationEntry
    {
        private final Destination destination;
        private MessageProducer producer;
        private MessageConsumer consumer;

        private DestinationEntry(Destination destination)
        {
            this.destination = destination;
        }

        private MessageProducer getProducer(Session ses) throws JMSException
        {
            if (producer == null)
                producer = ses.createProducer(destination);
            return producer;
        }

        private MessageConsumer getConsumer(Session ses) throws JMSException
        {
            if (consumer == null)
                consumer = ses.createConsumer(destination);
            return consumer;
        }

        private void close()
        {
            JmsHelper.close(consumer);
            JmsHelper.close(producer);
        }
    }

    private static class ContextImpl implements Context
    {
        private final JmsLookup lookup;
        private final Connection con;
        private final Session ses;
        private final Map<String,DestinationEntry> destinationEntryByName = new HashMap<String,DestinationEntry>();

        public ContextImpl(JmsLookup lookup,Connection con, Session ses)
        {
            this.lookup = lookup;
            this.con = con;
            this.ses = ses;
        }

        public Session getSession()
        {
            return ses;
        }

        private DestinationEntry getDestinationEntry(String name, Provider<Destination> destinationProvider)
        {
            DestinationEntry destinationEntry = destinationEntryByName.get(name);
            if (destinationEntry == null)
            {
                Destination destination = destinationProvider.get();
                destinationEntry = new DestinationEntry(destination);
                destinationEntryByName.put(name,destinationEntry);
            }
            return destinationEntry;
        }

        private DestinationEntry getQueueEntry(final String name)
        {
            return getDestinationEntry(name, JmsHelper.getQueueProvider(lookup, name));
        }

        private DestinationEntry getTopicEntry(final String name)
        {
            return getDestinationEntry(name, JmsHelper.getTopicProvider(lookup, name));
        }

        public MessageProducer getQueueProducer(final String name) throws JMSException
        {
            DestinationEntry entry = getQueueEntry(name);
            return entry.getProducer(ses);
        }

        public MessageConsumer getQueueConsumer(String name) throws JMSException
        {
            DestinationEntry entry = getQueueEntry(name);
            return entry.getConsumer(ses);
        }

        public MessageProducer getTopicProducer(final String name) throws JMSException
        {
            DestinationEntry entry = getTopicEntry(name);
            return entry.getProducer(ses);
        }

        public MessageConsumer getTopicConsumer(String name) throws JMSException
        {
            DestinationEntry entry = getTopicEntry(name);
            return entry.getConsumer(ses);
        }

        public void close()
        {
            for (DestinationEntry entry : destinationEntryByName.values())
            {
                try
                {
                    entry.close();
                }
                catch (Throwable e)
                {
                    LoggerHelper.unexpectedWarn(log,e);
                }
            }
        }
    }

    public void doAction(Action action) throws JMSException
    {
        final ConnectionFactory cf = getConnectionFactory();
        Connection con = null;
        Session ses = null;
        ContextImpl context = null;
        try
        {
            con = getConnection(cf);
            ses = createSession(con);
            con.start();
            context = new ContextImpl(getLookup(),con,ses);
            action.go(context);
            context.close();
            con.stop();
        }
        catch (JMSException e)
        {
            log.log(Level.SEVERE,"Exception thrown during JmsConversation: " + e,e);
            throw e;
        }
        finally
        {
            if (context != null)
                context.close();
            JmsHelper.close(ses);
            JmsHelper.close(con);
        }
    }

}

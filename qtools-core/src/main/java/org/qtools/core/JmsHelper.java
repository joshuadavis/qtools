package org.qtools.core;

import javax.jms.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper methods for JMS
 * <br>
 * User: josh
 * Date: 8/1/13
 * Time: 2:01 PM
 */
public class JmsHelper
{
    private static final Logger log = Logger.getLogger(JmsHelper.class.getName());
    /**
     * Clean up JMS producer objects.  Typically used in a finally block.
     *
     * @param sender  the sender
     * @param session the session (may be null)
     * @param conn    the connection (may be null)
     */
    public static void close(MessageProducer sender, Session session, Connection conn) {
        close(sender);
        close(session);
        close(conn);
    }

    /**
     * Clean up JMS consumer objects.    Typically used in a finally block.
     *
     * @param consumer the consumer
     * @param session  the session (may be null)
     * @param conn     the connection (may be null)
     */
    public static void close(MessageConsumer consumer, Session session, Connection conn) {
        stop(conn);
        close(consumer);
        close(session);
        close(conn);
    }

    public static void stop(Connection conn) {
        if (conn != null) {
            //noinspection EmptyCatchBlock
            try {
                conn.stop();
            } catch (Exception e) {
                LoggerHelper.unexpectedWarn(log, e);
            }
        }
    }

    public static void close(MessageProducer sender) {
        if (sender != null) {
            //noinspection EmptyCatchBlock
            try {
                sender.close();
            } catch (Exception ignore) {
            }
        }
    }

    public static void close(MessageConsumer consumer) {
        if (consumer != null) {
            //noinspection EmptyCatchBlock
            try {
                consumer.close();
            } catch (Exception ignore) {
            }
        }
    }

    public static void close(Session session) {
        if (session != null) {
            //noinspection EmptyCatchBlock
            try {
                session.close();
            } catch (Exception ignore) {
            }
        }
    }

    public static void close(Connection conn) {
        if (conn != null) {
            //noinspection EmptyCatchBlock
            try {
                conn.close();
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * Get a destination provider for a specific queue.
     * @param lookup the lookup interface
     * @param name the queue name
     * @return a destination provider that looks up this specific queue.
     */
    public static Provider<Destination> getQueueProvider(final JmsLookup lookup, final String name)
    {
        return new Provider<Destination>()
        {
            public Destination get()
            {
                return lookup.getQueue(name);
            }
        };
    }

    /**
     * Get a destination provider for a specific topic.
     * @param lookup the lookup interface
     * @param name the topic name
     * @return a destination provider that looks up this specific topic.
     */
    public static Provider<Destination> getTopicProvider(final JmsLookup lookup, final String name)
    {
        return new Provider<Destination>()
        {
            public Destination get()
            {
                return lookup.getTopic(name);
            }
        };
    }

    public static void sleep(long interval)
    {
        try
        {
            Thread.sleep(interval);
        }
        catch (InterruptedException ignore)
        {
        }
    }
}

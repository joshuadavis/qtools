package org.qtools.core;

import javax.jms.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
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
    private static final long RECONNECT_INTERVAL = 5000;

    private final String name;
    private final boolean topic;
    private final boolean reconnect;

    private final Lock lock = new ReentrantLock();
    private final Condition ready = lock.newCondition();

    private Connection con;
    private Session ses;
    private MessageConsumer consumer;
    private MessageListener delegate;
    private boolean running;

    public static JmsListener nonAuthNonTx(JmsLookup lookup,String name,boolean topic,boolean reconnect)
    {
        return new JmsListener(lookup,null,null,false, Session.AUTO_ACKNOWLEDGE,name,topic,reconnect);
    }

    public JmsListener(JmsLookup lookup,
                          String username, String password,
                          boolean transacted, int ackMode,
                          String name,
                          boolean topic,
                          boolean reconnect)
    {
        super(lookup, username, password, transacted, ackMode);
        this.name = name;
        this.topic = topic;
        this.running = false;
        this.reconnect = reconnect;
    }

    public void setDelegate(MessageListener messageListener)
    {
        this.delegate = messageListener;
    }

    public void start()
    {
        boolean loop = true;
        while (loop)
        {
            lock.lock();
            try
            {
                doStart();
                if (running)
                {
                    ready.signal();
                    return;
                }
                loop = !running && reconnect;
            }
            finally
            {
                lock.unlock();
            }

            if (loop)
            {
                log.info("Unable to connect, waiting to reconnect...");
                JmsHelper.sleep(RECONNECT_INTERVAL);
            }
        }
    }

    public void startNowait()
    {
        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                start();
            }
        });
        t.start();
    }

    private void doStart()
    {
        if (running)
            return;
        try
        {
            ConnectionFactory cf = getConnectionFactory();
            Destination destination = topic ? getLookup().getTopic(name) : getLookup().getQueue(name);
            con = getConnection(cf);    // This will fail if the server isn't there.
            ses = createSession(con);
            consumer = ses.createConsumer(destination);
            consumer.setMessageListener(delegate);
            con.start();
            running = true;
        }
        catch (JMSException e)
        {
            if (!reconnect)
                LoggerHelper.unexpectedError(log, e);
            stop();
        }
    }

    public void stop()
    {
        synchronized (this)
        {
            doStop();
        }
    }

    private void doStop()
    {
        if (!running)
            return;

        JmsHelper.close(consumer, ses, con);
        consumer = null;
        ses = null;
        con = null;
        running = false;
    }

    public void waitForReady() throws InterruptedException
    {
        lock.lock();
        try
        {
            while (!running)
            {
                ready.await();
            }
        }
        catch (InterruptedException e)
        {
            log.log(Level.SEVERE,"Interrupted!" + e,e);
            throw e;
        }
        finally {
            lock.unlock();
        }
    }
}

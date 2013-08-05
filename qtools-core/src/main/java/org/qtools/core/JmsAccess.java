package org.qtools.core;

import javax.jms.*;

/**
 * Base JMS interaction object, helps manage connection, session, lookup.
 * <br>
 * User: josh
 * Date: 8/5/13
 * Time: 3:27 PM
 */
class JmsAccess
{
    private final JmsLookup lookup;
    private final String username;
    private final String password;
    private final boolean transacted;
    private final int ackMode;

    protected JmsAccess(JmsLookup lookup, String username, String password,
                        boolean transacted, int ackMode)
    {
        if (lookup == null)
            throw new IllegalArgumentException("lookup cannot be null!");
        if (password != null && username == null)
            throw new IllegalArgumentException("username cannot be null if password is provided!");
        this.ackMode = ackMode;
        this.password = password;
        this.username = username;
        this.transacted = transacted;
        this.lookup = lookup;
    }

    protected Session createSession(Connection con) throws JMSException
    {
        return con.createSession(transacted,ackMode);
    }

    protected Connection getConnection(ConnectionFactory cf) throws JMSException
    {
        return (username == null) ? cf.createConnection() : cf.createConnection(username,password);
    }

    protected JmsLookup getLookup()
    {
        return lookup;
    }

    protected ConnectionFactory getConnectionFactory()
    {
        return lookup.getConnectionFactory();
    }
}

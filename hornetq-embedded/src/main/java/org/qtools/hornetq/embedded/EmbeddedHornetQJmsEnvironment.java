package org.qtools.hornetq.embedded;

/**
 * Encapsulates the embedded hornetq JMS server and client (Lookup) in one object.
 * <br>
 * User: josh
 * Date: 8/5/13
 * Time: 11:52 AM
 */
public class EmbeddedHornetQJmsEnvironment
{
    private final EmbeddedHornetQServer server;
    private final EmbeddedHornetQJmsLookup lookup;

    public EmbeddedHornetQJmsEnvironment()
    {
        lookup = new EmbeddedHornetQJmsLookup();
        server = new EmbeddedHornetQServer();
    }

    public EmbeddedHornetQServer getServer()
    {
        return server;
    }

    public EmbeddedHornetQJmsLookup getLookup()
    {
        return lookup;
    }

    public void close()
    {
        lookup.close();

        server.stop();
    }
}

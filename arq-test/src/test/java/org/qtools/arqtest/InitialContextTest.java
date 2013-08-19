package org.qtools.arqtest;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.naming.*;
import java.util.Hashtable;

/**
 * Test the container's initial context.
 * <br>
 * User: josh
 * Date: 8/6/13
 * Time: 11:39 AM
 */
@RunWith(Arquillian.class)
public class InitialContextTest
{
    @ArquillianResource
    private InitialContext initialContext;

    @Deployment(testable=false)
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class,"test.jar")
                .addClasses(Simple.class,SimpleSlsb.class);
    }


    @Test
    public void checkRemoteInterface() throws NamingException
    {
        Hashtable jndiProps = new Hashtable();
        jndiProps.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
        jndiProps.put(Context.PROVIDER_URL, "remote://localhost:4447");
        jndiProps.put("jboss.naming.client.ejb.context", true);
        InitialContext ic = new InitialContext(jndiProps);

        System.out.println("InitialContext = " + ic);
        NamingEnumeration<NameClassPair> names = ic.list("");
        while (names.hasMore())
        {
            NameClassPair pair = names.next();
            System.out.println(pair.toString());
        }
    }
}

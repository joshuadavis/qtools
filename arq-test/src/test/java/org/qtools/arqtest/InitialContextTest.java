package org.qtools.arqtest;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
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
    @Deployment(testable=false)
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class,"test.jar")
                .addClasses(Simple.class,SimpleSlsb.class);
    }

    private <T> T lookupEJB(Class<? extends T> remoteInterfaceClass, Class<?> implClass, Context c) throws NamingException
    {
        String appName = "";
        String moduleName = "test";
        String distinctName = "";
        String beanName = implClass.getSimpleName();
        String viewClassName = remoteInterfaceClass.getName();
        Object o = c.lookup("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
        return remoteInterfaceClass.cast(o);
    }

    @Test
    public void checkRemoteInterface() throws NamingException
    {
        Hashtable jndiProps = new Hashtable();
        jndiProps.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
        jndiProps.put(Context.PROVIDER_URL, "remote://localhost:4447");

        InitialContext ic = new InitialContext(jndiProps);

        Simple s = lookupEJB(Simple.class,SimpleSlsb.class,ic);

        s.check();
    }
}

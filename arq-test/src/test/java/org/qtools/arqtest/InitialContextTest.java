package org.qtools.arqtest;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

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
    @Deployment(testable=true)
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class,"test.jar")
                .addClasses(Simple.class,SimpleSlsb.class);
    }

    @Test
    public void checkRemoteInterface()
    {
    }
}

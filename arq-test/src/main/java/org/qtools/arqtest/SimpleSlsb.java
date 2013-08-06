package org.qtools.arqtest;

import javax.ejb.Stateless;

/**
 * Simple stateless session bean.
 * <br>
 * User: josh
 * Date: 8/6/13
 * Time: 11:45 AM
 */
@Stateless
public class SimpleSlsb implements Simple
{
    @Override
    public void check()
    {
        System.out.println("yyep");
    }
}

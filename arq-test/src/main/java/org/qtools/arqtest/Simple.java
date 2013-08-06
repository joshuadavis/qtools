package org.qtools.arqtest;

import javax.ejb.Remote;

/**
 * Remote interface to the simple SLSB.
 * <br>
 * User: josh
 * Date: 8/6/13
 * Time: 11:46 AM
 */
@Remote
public interface Simple
{
    void check();
}

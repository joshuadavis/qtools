package org.qtools.core;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JULI Logger helper methods.
 * <br>
 * User: josh
 * Date: 8/5/13
 * Time: 1:16 PM
 */
public class LoggerHelper
{
    public static void unexpectedWarn(Logger log, Throwable e)
    {
        log.log(Level.WARNING,"Unexpected: " + e, e);
    }

    public static void unexpectedError(Logger log, Throwable e)
    {
        log.log(Level.SEVERE,"Unexpected: " + e, e);
    }
}

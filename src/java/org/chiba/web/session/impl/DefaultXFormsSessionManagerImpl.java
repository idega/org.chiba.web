// Copyright 2001-2007 ChibaXForms GmbH
/*
 *
 * Artistic License
 *
 * Preamble
 *
 * The intent of this document is to state the conditions under which a
 * Package may be copied, such that the Copyright Holder maintains some
 * semblance of artistic control over the development of the package, while
 * giving the users of the package the right to use and distribute the
 * Package in a more-or-less customary fashion, plus the right to make
 * reasonable modifications.
 *
 * Definitions:
 *
 * "Package" refers to the collection of files distributed by the Copyright
 * Holder, and derivatives of that collection of files created through
 * textual modification.
 *
 * "Standard Version" refers to such a Package if it has not been modified,
 * or has been modified in accordance with the wishes of the Copyright
 * Holder.
 *
 * "Copyright Holder" is whoever is named in the copyright or copyrights
 * for the package.
 *
 * "You" is you, if you're thinking about copying or distributing this
 * Package.
 *
 * "Reasonable copying fee" is whatever you can justify on the basis of
 * media cost, duplication charges, time of people involved, and so
 * on. (You will not be required to justify it to the Copyright Holder, but
 * only to the computing community at large as a market that must bear the
 * fee.)
 *
 * "Freely Available" means that no fee is charged for the item itself,
 * though there may be fees involved in handling the item. It also means
 * that recipients of the item may redistribute it under the same
 * conditions they received it.
 *
 * 1. You may make and give away verbatim copies of the source form of the
 *    Standard Version of this Package without restriction, provided that
 *    you duplicate all of the original copyright notices and associated
 *    disclaimers.
 *
 * 2. You may apply bug fixes, portability fixes and other modifications
 *    derived from the Public Domain or from the Copyright Holder. A
 *    Package modified in such a way shall still be considered the Standard
 *    Version.
 *
 * 3. You may otherwise modify your copy of this Package in any way,
 *    provided that you insert a prominent notice in each changed file
 *    stating how and when you changed that file, and provided that you do
 *    at least ONE of the following:
 *
 *    a) place your modifications in the Public Domain or otherwise make
 *    them Freely Available, such as by posting said modifications to
 *    Usenet * or an equivalent medium, or placing the modifications on a
 *    major * archive site such as ftp.uu.net, or by allowing the Copyright
 *    Holder * to include your modifications in the Standard Version of the
 *    Package.
 *
 *    b) use the modified Package only within your corporation or *
 *    organization.
 *
 *    c) rename any non-standard executables so the names do not conflict
 *    with standard executables, which must also be provided, and provide a
 *    separate manual page for each non-standard executable that clearly
 *    documents how it differs from the Standard Version.
 *
 *    d) make other distribution arrangements with the Copyright Holder.
 *
 * 4. You may distribute the programs of this Package in object code or
 *    executable form, provided that you do at least ONE of the following:
 *
 *    a) distribute a Standard Version of the executables and library
 *    files, together with instructions (in the manual page or equivalent)
 *    on where to get the Standard Version.
 *
 *    b) accompany the distribution with the machine-readable source of the
 *    Package with your modifications.
 *
 *    c) accompany any non-standard executables with their corresponding
 *    Standard Version executables, giving the non-standard executables
 *    non-standard names, and clearly documenting the differences in manual
 *    pages (or equivalent), together with instructions on where to get the
 *    Standard Version.
 *
 *    d) make other distribution arrangements with the Copyright Holder.
 *
 * 5. You may charge a reasonable copying fee for any distribution of this
 *    Package. You may charge any fee you choose for support of this
 *    Package. You may not charge a fee for this Package itself.  However,
 *    you may distribute this Package in aggregate with other (possibly
 *    commercial) programs as part of a larger (possibly commercial)
 *    software distribution provided that you do not advertise this Package
 *    as a product of your own.
 *
 * 6. The scripts and library files supplied as input to or produced as
 *    output from the programs of this Package do not automatically fall
 *    under * the copyright of this Package, but belong to whomever
 *    generated them, * and may be sold commercially, and may be aggregated
 *    with this Package.
 *
 * 7. C or perl subroutines supplied by you and linked into this Package
 *    shall not be considered part of this Package.
 *
 * 8. The name of the Copyright Holder may not be used to endorse or
 *    promote products derived from this software without specific prior *
 *    written permission.
 *
 * 9. THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
 *    WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 *    MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 *
 */

package org.chiba.web.session.impl;

import org.apache.log4j.Logger;
import org.chiba.web.session.XFormsSession;
import org.chiba.web.session.XFormsSessionManager;

import java.util.*;

/**
 * Simple default implementation of a XFormsSessionManager.
 *
 * @author joern turner</a>
 * @version $Id: DefaultXFormsSessionManagerImpl.java,v 1.3 2007/04/20 18:40:05 civilis Exp $
 *          <p/>
 *          todo: handle maxSessions
 *          todo: move timeout to XFormsSession
 *          todo: implement persistent sessions
 */
public class DefaultXFormsSessionManagerImpl extends Thread implements XFormsSessionManager {
    private static DefaultXFormsSessionManagerImpl instance = null;
    private Map xformsSessions;
    protected int maxSessions;
    private static final Logger LOGGER = Logger.getLogger(DefaultXFormsSessionManagerImpl.class);

    private static final int DEFAULT_TIMEOUT = 5*60*1000; //default is 5 Minutes (expressed in milliseconds)
    private int timeout = DEFAULT_TIMEOUT;
    private boolean stopped = false;
    private long interval = 1500;
    private boolean threadStarted = false;
    private static final String INSTANCE="instance";

    public static DefaultXFormsSessionManagerImpl getInstance() {
        if (instance == null) {
            instance = new DefaultXFormsSessionManagerImpl(INSTANCE);
        }
        return instance;

    }

    /**
     * set the maximum amount of transient (in-memory) sessions allowed for a user
     *
     * @param max the maximum amount of transient (in-memory) sessions allowed for a user
     */
    public void setMaxSessions(int max) {
        this.maxSessions = max;
    }

    public void setTimeout(int milliseconds) {
        this.timeout = milliseconds;
    }

    /**
     * set the interval the wiper thread will check for expired sessions. Setting a value of 0 causes the wiper
     * thread to be *not* started. Therefore XFormsSessions will never expire unless the Http Session is still
     * alive.
     *
     * @param milliseconds the interval the wiper is checking for expired sessions
     */
    public void setInterval(int milliseconds) {
        this.interval = milliseconds;
    }

    public void init() {
        start();
    }

    public int getSessionCount() {
        return this.xformsSessions.size();
    }

    private DefaultXFormsSessionManagerImpl(String instance) {
        this.xformsSessions = Collections.synchronizedMap(new HashMap());
        setDaemon(true);
    }

    /**
     * must be called to register a XFormsSession with the Manager
     */
    public void addXFormsSession(XFormsSession xfSession) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("added XFormsSession to SessionManager: " + xfSession.getKey());
            LOGGER.debug("Session count now: " + xformsSessions.size());
        }
        this.xformsSessions.put(xfSession.getKey(), xfSession);
    }

    /**
     * factory method for creating a XFormsSession object.
     *
     * @return an object of type XFormsSession
     */
    public XFormsSession createXFormsSession() {
        XFormsSession xFormsSession = new DefaultXFormsSessionImpl(this);
        this.xformsSessions.put(xFormsSession.getKey(), xFormsSession);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("created XFormsSession: " + xFormsSession.getKey());
        }

        return xFormsSession;
    }


    /**
     * fetches a XFormsSession by its id
     *
     * @param id the id of the session as created during createXFormsSession() call
     * @return returns the XFormsSession object associated with given id or null if object does not exist
     */
    public XFormsSession getXFormsSession(String id) {

        if (this.xformsSessions.containsKey(id)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("returning XFormsSession: " + id);
                LOGGER.debug("Session count now: " + xformsSessions.size());
            }
            return (XFormsSession) this.xformsSessions.get(id);
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("XFormsSession: " + id + " not found");
            }
            return null;
        }
    }

    /**
     * deletes  XFormsSession object from internal pool of objects.
     *
     * @param id the XFormsSession id
     */
    public void deleteXFormsSession(String id) {

        if (this.xformsSessions.containsKey(id)) {
            this.xformsSessions.remove(id);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("deleted XFormsSession from SessionManager: " + id);
                LOGGER.debug("Session count now: " + xformsSessions.size());
            }
        }
    }

    /**
     * checks for timed-out XFormsSessions and deletes these from internal pool.
     */
    private void wipe() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("checking for expired sessions");
        }

        XFormsSession session;
        Iterator allSessions = this.xformsSessions.values().iterator();
        while (allSessions.hasNext()) {
            session = (XFormsSession) allSessions.next();

            if (isExpired(session)) {
                allSessions.remove();
                System.gc();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Removed expired XFormsSession: " + session.getKey() + " - lastUsed: " + new Date(session.getLastUseTime()));
                    LOGGER.debug("Session count now: " + xformsSessions.size());
                }
            }
        }
    }

    private boolean isExpired(XFormsSession session) {
        long now = System.currentTimeMillis();

        if ((now - session.getLastUseTime()) > (timeout)) {
            return true;
        } else {
            return false;
        }
    }


    public void run() {
        while (!stopped) {
            try {
                Thread.sleep(interval);
                wipe();
            } catch (InterruptedException e) {
                LOGGER.error("Exception while trying to sleep Thread");
            }
        }
    }

    public synchronized void start() {
        if (this.interval != 0 && !threadStarted) {
            super.start();
            this.threadStarted = true;
        }else{
            LOGGER.warn("No XForms session cleanup. Your server might run out of memory under load. To avoid this configure your web.xml accordingly.");
        }

    }

    public void kill() {
        this.stopped = true;
        instance = null;

    }

}

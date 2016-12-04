package jmri.jmris.simpleserver;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests for the jmri.jmris.simpleserver.SimpleServerPreferencesPanel class
 *
 * @author Paul Bender Copyright (C) 2016
 */
public class SimpleServerPreferencesPanelTest {

    @Test public void testCtor() {
        SimpleServerPreferencesPanel a = new SimpleServerPreferencesPanel();
        Assert.assertNotNull(a);
    }

    @Before public void setUp() {
        apps.tests.Log4JFixture.setUp();
        jmri.util.JUnitUtil.resetInstanceManager();
    }

    @After public void tearDown() throws Exception {
        jmri.util.JUnitUtil.resetInstanceManager();
        apps.tests.Log4JFixture.tearDown();
    }

}

// SimpleTimebaseTest.java

package jmri.jmrit.simpleclock;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.util.Date;

/**
 * Tests for the SimpleTimebase class
 * @author	Bob Jacobsen
 * @version $Revision$
 */
public class SimpleTimebaseTest extends TestCase {

	void wait(int msec) {
		try {
			super.wait(msec);
		}
		catch (Exception e) {
		}
	}
	
	// test creation
	public void testCreate() {
		SimpleTimebase p = new SimpleTimebase();
		Assert.assertNotNull("exists", p );
	}

	// test quick access (should be quite close to zero)
	public void testNoDelay() {
		SimpleTimebase p = new SimpleTimebase();
		Date now = new Date();
		p.setTime(now);
		Date then = p.getTime();
		long delta = then.getTime()-now.getTime();
		Assert.assertTrue("delta ge zero", delta>=0);
		Assert.assertTrue("delta lt 100 msec (nominal value)", delta<100);
	}

    public void testSetStartTime() {
		SimpleTimebase p = new SimpleTimebase();
		Date now = new Date();

		p.setStartSetTime(true, now);
		
		Assert.assertTrue("startSetTime true", p.getStartSetTime());

		p.setStartSetTime(false, now);
		Assert.assertTrue("startSetTime false", !p.getStartSetTime());
				
		Assert.assertEquals("setTime now", now, p.getStartTime());
		
		Date then = new Date(now.getTime()+100);
		p.setStartSetTime(false, then);

		Assert.assertEquals("setTime then", then, p.getStartTime());
    }
    
/* 	public void testShortDelay() { */
/* 		SimpleTimebase p = new SimpleTimebase(); */
/* 		Date now = new Date(); */
/* 		p.setTime(now); */
/* 		p.setRate(100.); */
/* 		wait(100); */
/* 		Date then = p.getTime(); */
/* 		long delta = then.getTime()-now.getTime(); */
/* 		Assert.assertTrue("delta ge 50 (nominal value)", delta>=50); */
/* 		Assert.assertTrue("delta lt 150 (nominal value)", delta<150); */
/* 	} */

	// from here down is testing infrastructure

	public SimpleTimebaseTest(String s) {
		super(s);
	}

	// Main entry point
	static public void main(String[] args) {
		String[] testCaseName = {SimpleTimebaseTest.class.getName()};
		junit.swingui.TestRunner.main(testCaseName);
	}

	// test suite from all defined tests
	public static Test suite() {
		TestSuite suite = new TestSuite(SimpleTimebaseTest.class);
		return suite;
	}

}
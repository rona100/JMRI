package jmri.jmrit.roster;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Set;
import javax.swing.JComboBox;
import jmri.jmrit.roster.swing.RosterEntryComboBox;
import jmri.util.FileUtil;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Tests for the jmrit.roster.Roster class.
 *
 * @author	Bob Jacobsen Copyright (C) 2001, 2002, 2012
 */
public class RosterTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testDirty() {
        Roster r = new Roster();
        Assert.assertEquals("new object ", false, r.isDirty());
        r.addEntry(new RosterEntry());
        Assert.assertEquals("after add ", true, r.isDirty());
    }

    @Test
    public void testAdd() {
        Roster r = new Roster();
        Assert.assertEquals("empty length ", 0, r.numEntries());
        r.addEntry(new RosterEntry("file name Bob"));
        Assert.assertEquals("one item ", 1, r.numEntries());
    }

    @Test
    public void testDontAddNullEntriesLater() {
        // test as documentation...
        Roster r = new Roster();
        r.addEntry(new RosterEntry());
        r.addEntry(new RosterEntry());

        boolean pass = false;
        try {
            r.addEntry(null);
        } catch (NullPointerException e) {
            pass = true;
        }
        Assert.assertTrue("Adding null entry should have caused NPE", pass);
    }

    @Test
    public void testDontAddNullEntriesFirst() {
        // test as documentation...
        Roster r = new Roster();

        boolean pass = false;
        try {
            r.addEntry(null);
        } catch (NullPointerException e) {
            pass = true;
        }
        Assert.assertTrue("Adding null entry should have caused NPE", pass);
    }

    @Test
    public void testAddrSearch() {
        Roster r = new Roster();
        RosterEntry e = new RosterEntry("file name Bob");
        e.setRoadNumber("123");
        r.addEntry(e);
        Assert.assertEquals("search not OK ", false, r.checkEntry(0, null, "321", null, null, null, null, null, null));
        Assert.assertEquals("search OK ", true, r.checkEntry(0, null, "123", null, null, null, null, null, null));
    }

    @Test
    public void testGetByDccAddress() {
        Roster r = new Roster();
        RosterEntry e = new RosterEntry("file name Bob");
        e.setDccAddress("456");
        r.addEntry(e);
        Assert.assertEquals("search not OK ", false, r.checkEntry(0, null, null, "123", null, null, null, null, null));
        Assert.assertEquals("search OK ", true, r.checkEntry(0, null, null, "456", null, null, null, null, null));

        List<RosterEntry> l;

        l = r.matchingList(null, null, "123", null, null, null, null);
        Assert.assertEquals("match 123", 0, l.size());

        l = r.matchingList(null, null, "456", null, null, null, null);
        Assert.assertEquals("match 456", 1, l.size());

        l = r.getEntriesByDccAddress("123");
        Assert.assertEquals("address 123", 0, l.size());

        l = r.getEntriesByDccAddress("456");
        Assert.assertEquals("address 456", 1, l.size());
    }

    @Test
    public void testSearchList() {
        Roster r = new Roster();
        RosterEntry e;
        e = new RosterEntry("file name Bob");
        e.setRoadNumber("123");
        e.setRoadName("SP");
        r.addEntry(e);
        e = new RosterEntry("file name Bill");
        e.setRoadNumber("123");
        e.setRoadName("ATSF");
        e.setDecoderModel("81");
        e.setDecoderFamily("33");
        r.addEntry(e);
        e = new RosterEntry("file name Ben");
        e.setRoadNumber("123");
        e.setRoadName("UP");
        r.addEntry(e);

        List<RosterEntry> l;
        l = r.matchingList(null, "321", null, null, null, null, null);
        Assert.assertEquals("search for 0 ", 0, l.size());

        l = r.matchingList("UP", null, null, null, null, null, null);
        Assert.assertEquals("search for 1 ", 1, l.size());
        Assert.assertEquals("search for 1 ", "UP", l.get(0).getRoadName());
        Assert.assertEquals("search for 1 ", "123", l.get(0).getRoadNumber());

        l = r.matchingList(null, "123", null, null, null, null, null);
        Assert.assertEquals("search for 3 ", 3, l.size());
        Assert.assertEquals("search for 3 ", "SP", l.get(2).getRoadName());
        Assert.assertEquals("search for 3 ", "123", l.get(2).getRoadNumber());
        Assert.assertEquals("search for 3 ", "UP", l.get(0).getRoadName());
        Assert.assertEquals("search for 3 ", "123", l.get(0).getRoadNumber());
    }

    @Test
    public void testComboBox() {
        Roster r = new Roster();
        RosterEntry e1;
        RosterEntry e2;
        RosterEntry e3;
        e1 = new RosterEntry("file name Bob");
        e1.setRoadNumber("123");
        e1.setRoadName("SP");
        e1.setId("entry 1");
        r.addEntry(e1);
        e2 = new RosterEntry("file name Bill");
        e2.setRoadNumber("123");
        e2.setRoadName("ATSF");
        e2.setDecoderModel("81");
        e2.setDecoderFamily("33");
        e2.setId("entry 2");
        r.addEntry(e2);
        e3 = new RosterEntry("file name Ben");
        e3.setRoadNumber("123");
        e3.setRoadName("UP");
        e3.setId("entry 3");
        r.addEntry(e3);

        JComboBox<Object> box;

        // "Select Loco" is the first entry in the RosterEntryComboBox, so an
        // empty comboBox has 1 item, and the first item is not a RosterEntry
        box = new RosterEntryComboBox(r, null, "321", null, null, null, null, null);
        Assert.assertEquals("search for zero matches", 1, box.getItemCount());

        box = new RosterEntryComboBox(r, "UP", null, null, null, null, null, null);
        Assert.assertEquals("search for one match", 2, box.getItemCount());
        Assert.assertEquals("search for one match", e3, box.getItemAt(1));

        box = new RosterEntryComboBox(r, null, "123", null, null, null, null, null);
        Assert.assertEquals("search for three matches", 4, box.getItemCount());
        Assert.assertEquals("search for three matches", e1, box.getItemAt(1));
        Assert.assertEquals("search for three matches", e2, box.getItemAt(2));
        Assert.assertEquals("search for three matches", e3, box.getItemAt(3));

    }

    @Test
    public void testBackupFile() throws Exception {
        // this test uses explicit filenames intentionally, to ensure that
        // the resulting files go into the test tree area.

        // create a file in "temp"
        File rosterDir = folder.newFolder();
        File backupDir = folder.newFolder();
        FileUtil.createDirectory(rosterDir);
        Roster.getDefault().setRosterLocation(rosterDir.getAbsolutePath());
        File f = new File(rosterDir, "roster.xml");
        // remove it if its there
        f.delete();
        // load a new one
        String contents = "stuff" + "           ";
        PrintStream p = new PrintStream(new FileOutputStream(f));
        p.println(contents);
        p.close();
        // delete previous backup file if there's one
        File bf = new File(rosterDir, "rosterBackupTest");
        bf.delete();

        // now do the backup
        Roster r = new Roster() {
            @Override
            public String backupFileName(String name) {
                return new File(rosterDir, "rosterBackupTest").getAbsolutePath();
            }
        };
        r.makeBackupFile(new File(rosterDir, "roster.xml").getAbsolutePath());

        // and check
        InputStream in = new FileInputStream(new File(rosterDir, "rosterBackupTest"));
        Assert.assertEquals("read 0 ", contents.charAt(0), in.read());
        Assert.assertEquals("read 1 ", contents.charAt(1), in.read());
        Assert.assertEquals("read 2 ", contents.charAt(2), in.read());
        Assert.assertEquals("read 3 ", contents.charAt(3), in.read());
        in.close();

        // now see if backup works when a backup file already exists
        contents = "NEWER JUNK" + "           ";
        p = new PrintStream(new FileOutputStream(f));
        p.println(contents);
        p.close();

        // now do the backup
        r.makeBackupFile(f.getAbsolutePath());

        // and check
        in = new FileInputStream(new File(rosterDir, "rosterBackupTest"));
        Assert.assertEquals("read 4 ", contents.charAt(0), in.read());
        Assert.assertEquals("read 5 ", contents.charAt(1), in.read());
        Assert.assertEquals("read 6 ", contents.charAt(2), in.read());
        Assert.assertEquals("read 7 ", contents.charAt(3), in.read());
        in.close();
    }

    @Test
    public void testReadWrite() throws Exception {
        // create a test roster & store in file
        Roster r = createTestRoster();
        Assert.assertNotNull("exists", r);

        // create new roster & read
        Roster t = new Roster();
        t.readFile(Roster.getDefault().getRosterIndexPath());

        // check contents
        Assert.assertEquals("search for 0 ", 0, t.matchingList(null, "321", null, null, null, null, null).size());
        Assert.assertEquals("search for 1 ", 1, t.matchingList("UP", null, null, null, null, null, null).size());
        Assert.assertEquals("search for 3 ", 3, t.matchingList(null, "123", null, null, null, null, null).size());
    }

    @Test
    public void testAttributeAccess() throws Exception {
        // create a test roster & store in file
        Roster r = createTestRoster();
        Assert.assertNotNull("exists", r);

        List<RosterEntry> l;

        l = r.getEntriesWithAttributeKey("key a");
        Assert.assertEquals("match key a", 2, l.size());
        l = r.getEntriesWithAttributeKey("no match");
        Assert.assertEquals("no match", 0, l.size());

    }

    @Test
    public void testAttributeValueAccess() throws Exception {
        // create a test roster & store in file
        Roster r = createTestRoster();
        Assert.assertNotNull("exists", r);

        List<RosterEntry> l;

        l = r.getEntriesWithAttributeKeyValue("key a", "value a");
        Assert.assertEquals("match key a", 2, l.size());
        l = r.getEntriesWithAttributeKeyValue("key a", "none");
        Assert.assertEquals("no match key a", 0, l.size());
        l = r.getEntriesWithAttributeKeyValue("no match", "none");
        Assert.assertEquals("no match", 0, l.size());

    }

    @Test
    public void testAttributeList() throws Exception {
        // create a test roster & store in file
        Roster r = createTestRoster();
        Assert.assertNotNull("exists", r);

        Set<String> s;

        s = r.getAllAttributeKeys();

        Assert.assertTrue("contains right key", s.contains("key b"));
        Assert.assertTrue("not contains wrong key", !s.contains("no key"));
        Assert.assertEquals("length", 2, s.size());

    }

    public Roster createTestRoster() throws IOException, FileNotFoundException {
        // this uses explicit filenames intentionally, to ensure that
        // the resulting files go into the test tree area.

        // store files in random temp directory
        File rosterDir = folder.newFolder();
        FileUtil.createDirectory(rosterDir);
        Roster.getDefault().setRosterLocation(rosterDir.getAbsolutePath());
        Roster.getDefault().setRosterIndexFileName("rosterTest.xml");

        File f = new File(rosterDir, "rosterTest.xml");
        // remove existing roster if its there
        f.delete();

        // create a roster with known contents
        Roster r = new Roster();
        RosterEntry e;
        e = new RosterEntry("file name Bob");
        e.setId("Bob");
        e.setDccAddress("123");
        e.setRoadNumber("123");
        e.setRoadName("SP");
        e.ensureFilenameExists();
        e.putAttribute("key a", "value a");
        e.putAttribute("key b", "value b");
        r.addEntry(e);
        e = null;
        e = new RosterEntry("file name Bill");
        e.setId("Bill");
        e.setDccAddress("456");
        e.setRoadNumber("123");
        e.setRoadName("ATSF");
        e.setDecoderModel("81");
        e.setDecoderFamily("33");
        e.ensureFilenameExists();
        e.putAttribute("key a", "value a");
        r.addEntry(e);
        e = null;
        e = new RosterEntry("file name Ben");
        e.setId("Ben");
        e.setRoadNumber("123");
        e.setRoadName("UP");
        e.ensureFilenameExists();
        e.putAttribute("key b", "value b");
        r.addEntry(e);

        // write it
        r.writeFile(Roster.getDefault().getRosterIndexPath());

        return r;
    }

    // The minimal setup for log4J
    @Before
    public void setUp() {
        apps.tests.Log4JFixture.setUp();
        JUnitUtil.resetInstanceManager();
    }

    @After
    public void tearDown() {
        JUnitUtil.resetInstanceManager();
        apps.tests.Log4JFixture.tearDown();
    }

}

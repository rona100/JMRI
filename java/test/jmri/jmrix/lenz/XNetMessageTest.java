package jmri.jmrix.lenz;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * XNetMessageTest.java
 *
 * Description:	tests for the jmri.jmrix.lenz.XNetMessage class
 *
 * @author	Bob Jacobsen
 */
public class XNetMessageTest extends TestCase {

    public void testCtor() {
        XNetMessage m = new XNetMessage(3);
        Assert.assertEquals("length", 3, m.getNumDataElements());
      
        m = new XNetMessage(21);
        jmri.util.JUnitAppender.assertErrorMessage("Invalid length in ctor: 21");
    }

    // check opcode inclusion in message
    public void testOpCode() {
        XNetMessage m = new XNetMessage(5);
        m.setOpCode(4);
        Assert.assertEquals("read=back op code", 4, m.getOpCode());
        Assert.assertEquals("stored op code", 0x43, m.getElement(0));
    }

    // Test the string constructor.
    public void testStringCtor() {
        XNetMessage m = new XNetMessage("12 34 AB 3 19 6 B B1");
        Assert.assertEquals("length", 8, m.getNumDataElements());
        Assert.assertEquals("0th byte", 0x12, m.getElement(0) & 0xFF);
        Assert.assertEquals("1st byte", 0x34, m.getElement(1) & 0xFF);
        Assert.assertEquals("2nd byte", 0xAB, m.getElement(2) & 0xFF);
        Assert.assertEquals("3rd byte", 0x03, m.getElement(3) & 0xFF);
        Assert.assertEquals("4th byte", 0x19, m.getElement(4) & 0xFF);
        Assert.assertEquals("5th byte", 0x06, m.getElement(5) & 0xFF);
        Assert.assertEquals("6th byte", 0x0B, m.getElement(6) & 0xFF);
        Assert.assertEquals("7th byte", 0xB1, m.getElement(7) & 0xFF);
    }

    public void testStringCtorEmptyString() {
        XNetMessage m = new XNetMessage("");
        Assert.assertEquals("length", 0, m.getNumDataElements());
        Assert.assertTrue("empty reply",m.toString().equals(""));
    }

    public void testCtorXNetReply(){
        XNetReply x = new XNetReply("12 34 AB 03 19 06 0B B1");
        XNetMessage m = new XNetMessage(x);
        Assert.assertEquals("length", x.getNumDataElements(), m.getNumDataElements());
        Assert.assertEquals("0th byte", x.getElement(0)& 0xFF, m.getElement(0)& 0xFF);
        Assert.assertEquals("1st byte", x.getElement(1)& 0xFF, m.getElement(1)& 0xFF);
        Assert.assertEquals("2nd byte", x.getElement(2)& 0xFF, m.getElement(2)& 0xFF);
        Assert.assertEquals("3rd byte", x.getElement(3)& 0xFF, m.getElement(3)& 0xFF);
        Assert.assertEquals("4th byte", x.getElement(4)& 0xFF, m.getElement(4)& 0xFF);
        Assert.assertEquals("5th byte", x.getElement(5)& 0xFF, m.getElement(5)& 0xFF);
        Assert.assertEquals("6th byte", x.getElement(6)& 0xFF, m.getElement(6)& 0xFF);
        Assert.assertEquals("7th byte", x.getElement(7)& 0xFF, m.getElement(7)& 0xFF);
    }

    // test setting/getting the opcode
    public void testSetAndGetOpCode(){
        XNetMessage m = new XNetMessage("12 34 56");
        Assert.assertEquals(0x1,m.getOpCode());
        m.setOpCode(0xA);
        Assert.assertEquals(0xA,m.getOpCode()); 
        m.setOpCode(21);
        jmri.util.JUnitAppender.assertErrorMessage("Opcode invalid: 21");
    }

    // test setting/getting the opcode
    public void testGetOpCodeHex(){
        XNetMessage m = new XNetMessage("12 34 56");
        Assert.assertEquals("0x1",m.getOpCodeHex());
        m.setOpCode(0xA);
        Assert.assertEquals("0xa",m.getOpCodeHex()); 
    }

    // check parity operations
    public void testParity() {
        XNetMessage m;
        m = new XNetMessage(3);
        m.setElement(0, 0x21);
        m.setElement(1, 0x21);
        m.setParity();
        Assert.assertEquals("parity set test 1", 0, m.getElement(2));
        Assert.assertEquals("parity check test 1", true, m.checkParity());

        m = new XNetMessage(3);
        m.setElement(0, 0x21);
        m.setElement(1, ~0x21);
        m.setParity();
        Assert.assertEquals("parity set test 2", 0xFF, m.getElement(2));
        Assert.assertEquals("parity check test 2", true, m.checkParity());

        m = new XNetMessage(3);
        m.setElement(0, 0x18);
        m.setElement(1, 0x36);
        m.setParity();
        Assert.assertEquals("parity set test 3", 0x2E, m.getElement(2));
        Assert.assertEquals("parity check test 3", true, m.checkParity());

        m = new XNetMessage(3);
        m.setElement(0, 0x87);
        m.setElement(1, 0x31);
        m.setParity();
        Assert.assertEquals("parity set test 4", 0xB6, m.getElement(2));
        Assert.assertEquals("parity check test 4", true, m.checkParity());

        m = new XNetMessage(3);
        m.setElement(0, 0x18);
        m.setElement(1, 0x36);
        m.setElement(2, 0x0e);
        Assert.assertEquals("parity check test 5", false, m.checkParity());

        m = new XNetMessage(3);
        m.setElement(0, 0x18);
        m.setElement(1, 0x36);
        m.setElement(2, 0x8e);
        Assert.assertEquals("parity check test 6", false, m.checkParity());
    }

    public void testGetElementBCD(){
        XNetMessage m = new XNetMessage("12 34 56");
        Assert.assertEquals("BCD value",Integer.valueOf(12),m.getElementBCD(0));
        Assert.assertEquals("BCD value",Integer.valueOf(34),m.getElementBCD(1));
        Assert.assertEquals("BCD value",Integer.valueOf(56),m.getElementBCD(2));
    }

    public void testLength(){
        XNetMessage m = new XNetMessage("12 34 56");
        Assert.assertEquals("length",3,m.length());
        m = new XNetMessage("12 34 56 78");
        Assert.assertEquals("length",4,m.length());
    }

    public void testSetXNetMessageRetries(){
        XNetMessage m = new XNetMessage("12 34 56");
        Assert.assertEquals("Retries ",5,m.getRetries());
        XNetMessage.setXNetMessageRetries(0); 
        // the default number of retires should be 0, so create a message to see. 
        m = new XNetMessage("56 34 12");
        Assert.assertEquals("Retries",0,m.getRetries());
    }

    public void testSetXNetMessageTimeout(){
        XNetMessage m = new XNetMessage("12 34 56");
        Assert.assertEquals("Timeout",5000,m.getTimeout());
        XNetMessage.setXNetMessageTimeout(0); 
        // the default timeout should be 0, so create a message to see. 
        m = new XNetMessage("56 34 12");
        Assert.assertEquals("Timeout",0,m.getTimeout());
    }

    public void testGetAndSetBroadcastReply(){
        XNetMessage m = new XNetMessage("12 34 56");
        Assert.assertTrue(m.replyExpected()); // reply expected returns 
                                              // !broadcastReply, which defaults
                                              // to false.
        m.setBroadcastReply();
        Assert.assertFalse(m.replyExpected());
    }



    // test canned messages.
    public void testGetNMRAXNetMsg() {
       XNetMessage m = XNetMessage.getNMRAXNetMsg(jmri.NmraPacket.opsCvWriteByte(2,false,29,32));
       Assert.assertEquals(0xE5,m.getElement(0));
       Assert.assertEquals(0x30,m.getElement(1));
       Assert.assertEquals(0x02,m.getElement(2));
       Assert.assertEquals(0xEC,m.getElement(3));
       Assert.assertEquals(0x1C,m.getElement(4));
       Assert.assertEquals(0x20,m.getElement(5));
       Assert.assertEquals(0x07,m.getElement(6));
    }

    public void testGetTurnoutCommandMsg() {
       XNetMessage m = XNetMessage.getTurnoutCommandMsg(5,false,true,true);
       Assert.assertEquals(0x52,m.getElement(0));
       Assert.assertEquals(0x01,m.getElement(1));
       Assert.assertEquals(0x89,m.getElement(2));
       Assert.assertEquals(0xDA,m.getElement(3));

       m = XNetMessage.getTurnoutCommandMsg(5,true,false,true);
       Assert.assertEquals(0x52,m.getElement(0));
       Assert.assertEquals(0x01,m.getElement(1));
       Assert.assertEquals(0x88,m.getElement(2));
       Assert.assertEquals(0xDB,m.getElement(3));

       m = XNetMessage.getTurnoutCommandMsg(5,false,true,false);
       Assert.assertEquals(0x52,m.getElement(0));
       Assert.assertEquals(0x01,m.getElement(1));
       Assert.assertEquals(0x81,m.getElement(2));
       Assert.assertEquals(0xD2,m.getElement(3));

       m = XNetMessage.getTurnoutCommandMsg(5,true,false,false);
       Assert.assertEquals(0x52,m.getElement(0));
       Assert.assertEquals(0x01,m.getElement(1));
       Assert.assertEquals(0x80,m.getElement(2));
       Assert.assertEquals(0xD3,m.getElement(3));

       // both thrown and close generates an error message.
       m = XNetMessage.getTurnoutCommandMsg(5,true,true,false);
       jmri.util.JUnitAppender.assertErrorMessage("XPressNet turnout logic can't handle both THROWN and CLOSED yet");
    }

    public void testGetFeedbackRequestMsg() {
       XNetMessage m = XNetMessage.getFeedbackRequestMsg(5,true);
       Assert.assertEquals(0x42,m.getElement(0));
       Assert.assertEquals(0x01,m.getElement(1));
       Assert.assertEquals(0x80,m.getElement(2));
       Assert.assertEquals(0xC3,m.getElement(3));

       m = XNetMessage.getFeedbackRequestMsg(5,false);
       Assert.assertEquals(0x42,m.getElement(0));
       Assert.assertEquals(0x01,m.getElement(1));
       Assert.assertEquals(0x81,m.getElement(2));
       Assert.assertEquals(0xC2,m.getElement(3));
   }
    
    public void testGetServiceModeREsultMsg(){
       XNetMessage m = XNetMessage.getServiceModeResultsMsg();
       Assert.assertEquals(0x21,m.getElement(0));
       Assert.assertEquals(0x10,m.getElement(1));
       Assert.assertEquals(0x31,m.getElement(2));
       Assert.assertEquals("programming mode required",jmri.jmrix.AbstractMRTrafficController.PROGRAMINGMODE,m.getNeededMode());
    }

    public void testGetExitProgModeMsg(){
       XNetMessage m = XNetMessage.getExitProgModeMsg();
       Assert.assertEquals(0x21,m.getElement(0));
       Assert.assertEquals(0x81,m.getElement(1));
       Assert.assertEquals(0xA0,m.getElement(2));
       Assert.assertEquals("programming mode required",jmri.jmrix.AbstractMRTrafficController.PROGRAMINGMODE,m.getNeededMode());
    }

    public void testGetReadPagedCVMsg(){
       XNetMessage m = XNetMessage.getReadPagedCVMsg(29);
       Assert.assertEquals(0x22,m.getElement(0));
       Assert.assertEquals(0x14,m.getElement(1));
       Assert.assertEquals(0x1D,m.getElement(2));
       Assert.assertEquals(0x2B,m.getElement(3));
       Assert.assertEquals("programming mode required",jmri.jmrix.AbstractMRTrafficController.PROGRAMINGMODE,m.getNeededMode());
    }

    public void testGetReadDirectCVMsg(){
       XNetMessage m = XNetMessage.getReadDirectCVMsg(29);
       Assert.assertEquals(0x22,m.getElement(0));
       Assert.assertEquals(0x15,m.getElement(1));
       Assert.assertEquals(0x1D,m.getElement(2));
       Assert.assertEquals(0x2A,m.getElement(3));
       Assert.assertEquals("programming mode required",jmri.jmrix.AbstractMRTrafficController.PROGRAMINGMODE,m.getNeededMode());

       // CV > 256, version 3.6+ command.
       m = XNetMessage.getReadDirectCVMsg(300);
       Assert.assertEquals(0x22,m.getElement(0));
       Assert.assertEquals(0x19,m.getElement(1));
       Assert.assertEquals(0x2C,m.getElement(2));
       Assert.assertEquals(0x17,m.getElement(3));
       Assert.assertEquals("programming mode required",jmri.jmrix.AbstractMRTrafficController.PROGRAMINGMODE,m.getNeededMode());

       // CV = 1024, version 3.6+ command, a special case.
       // (sends 0 for CV value).
       m = XNetMessage.getReadDirectCVMsg(1024);
       Assert.assertEquals(0x22,m.getElement(0));
       Assert.assertEquals(0x18,m.getElement(1));
       Assert.assertEquals(0x00,m.getElement(2));
       Assert.assertEquals(0x3A,m.getElement(3));
       Assert.assertEquals("programming mode required",jmri.jmrix.AbstractMRTrafficController.PROGRAMINGMODE,m.getNeededMode());
    }

    public void testGetWritePagedCVMsg(){
       XNetMessage m = XNetMessage.getWritePagedCVMsg(29,5);
       Assert.assertEquals(0x23,m.getElement(0));
       Assert.assertEquals(0x17,m.getElement(1));
       Assert.assertEquals(0x1D,m.getElement(2));
       Assert.assertEquals(0x05,m.getElement(3));
       Assert.assertEquals(0x2C,m.getElement(4));
       Assert.assertEquals("programming mode required",jmri.jmrix.AbstractMRTrafficController.PROGRAMINGMODE,m.getNeededMode());
    }

    public void testGetWriteDirectCVMsg(){
       XNetMessage m = XNetMessage.getWriteDirectCVMsg(29,5);
       Assert.assertEquals(0x23,m.getElement(0));
       Assert.assertEquals(0x16,m.getElement(1));
       Assert.assertEquals(0x1D,m.getElement(2));
       Assert.assertEquals(0x05,m.getElement(3));
       Assert.assertEquals(0x2D,m.getElement(4));
       Assert.assertEquals("programming mode required",jmri.jmrix.AbstractMRTrafficController.PROGRAMINGMODE,m.getNeededMode());

       // CV > 256, version 3.6+ command.
       m = XNetMessage.getWriteDirectCVMsg(300,5);
       Assert.assertEquals(0x23,m.getElement(0));
       Assert.assertEquals(0x1D,m.getElement(1));
       Assert.assertEquals(0x2C,m.getElement(2));
       Assert.assertEquals(0x05,m.getElement(3));
       Assert.assertEquals(0x17,m.getElement(4));
       Assert.assertEquals("programming mode required",jmri.jmrix.AbstractMRTrafficController.PROGRAMINGMODE,m.getNeededMode());

       // CV = 1024, version 3.6+ command, a special case.
       // (sends 0 for CV value).
       m = XNetMessage.getWriteDirectCVMsg(1024,5);
       Assert.assertEquals(0x23,m.getElement(0));
       Assert.assertEquals(0x1C,m.getElement(1));
       Assert.assertEquals(0x00,m.getElement(2));
       Assert.assertEquals(0x05,m.getElement(3));
       Assert.assertEquals(0x3A,m.getElement(4));
       Assert.assertEquals("programming mode required",jmri.jmrix.AbstractMRTrafficController.PROGRAMINGMODE,m.getNeededMode());
    }

    public void testGetReadRegisterMsg(){
       XNetMessage m = XNetMessage.getReadRegisterMsg(5);
       Assert.assertEquals(0x22,m.getElement(0));
       Assert.assertEquals(0x11,m.getElement(1));
       Assert.assertEquals(0x05,m.getElement(2));
       Assert.assertEquals(0x36,m.getElement(3));
       Assert.assertEquals("programming mode required",jmri.jmrix.AbstractMRTrafficController.PROGRAMINGMODE,m.getNeededMode());
       m = XNetMessage.getReadRegisterMsg(10);
       jmri.util.JUnitAppender.assertErrorMessage("register number too large: 10");
    }

    public void testGetWriteRegisterMsg(){
       XNetMessage m = XNetMessage.getWriteRegisterMsg(5,5);
       Assert.assertEquals(0x23,m.getElement(0));
       Assert.assertEquals(0x12,m.getElement(1));
       Assert.assertEquals(0x05,m.getElement(2));
       Assert.assertEquals(0x05,m.getElement(3));
       Assert.assertEquals(0x31,m.getElement(4));
       Assert.assertEquals("programming mode required",jmri.jmrix.AbstractMRTrafficController.PROGRAMINGMODE,m.getNeededMode());
       m = XNetMessage.getWriteRegisterMsg(10,5);
       jmri.util.JUnitAppender.assertErrorMessage("register number too large: 10");
    }

    public void testGetWriteOpsModeCVMsg(){
       XNetMessage m = XNetMessage.getWriteOpsModeCVMsg(0,5,29,5);
       Assert.assertEquals(0xE6,m.getElement(0));
       Assert.assertEquals(0x30,m.getElement(1));
       Assert.assertEquals(0x00,m.getElement(2));
       Assert.assertEquals(0x05,m.getElement(3));
       Assert.assertEquals(0xEC,m.getElement(4));
       Assert.assertEquals(0x1C,m.getElement(5));
       Assert.assertEquals(0x05,m.getElement(6));
       Assert.assertEquals(0x26,m.getElement(7));
    }

    public void testGetVerifyOpsModeCVMsg(){
       XNetMessage m = XNetMessage.getVerifyOpsModeCVMsg(0,5,29,5);
       Assert.assertEquals(0xE6,m.getElement(0));
       Assert.assertEquals(0x30,m.getElement(1));
       Assert.assertEquals(0x00,m.getElement(2));
       Assert.assertEquals(0x05,m.getElement(3));
       Assert.assertEquals(0xE4,m.getElement(4));
       Assert.assertEquals(0x1C,m.getElement(5));
       Assert.assertEquals(0x05,m.getElement(6));
       Assert.assertEquals(0x2E,m.getElement(7));
    }

    public void testGetBitWriteOpsModeCVMsg(){
       XNetMessage m = XNetMessage.getBitWriteOpsModeCVMsg(0,5,29,2,true);
       Assert.assertEquals(0xE6,m.getElement(0));
       Assert.assertEquals(0x30,m.getElement(1));
       Assert.assertEquals(0x00,m.getElement(2));
       Assert.assertEquals(0x05,m.getElement(3));
       Assert.assertEquals(0xE8,m.getElement(4));
       Assert.assertEquals(0x1C,m.getElement(5));
       Assert.assertEquals(0xEA,m.getElement(6));
       Assert.assertEquals(0xCD,m.getElement(7));

       m = XNetMessage.getBitWriteOpsModeCVMsg(0,5,29,2,false);
       Assert.assertEquals(0xE6,m.getElement(0));
       Assert.assertEquals(0x30,m.getElement(1));
       Assert.assertEquals(0x00,m.getElement(2));
       Assert.assertEquals(0x05,m.getElement(3));
       Assert.assertEquals(0xE8,m.getElement(4));
       Assert.assertEquals(0x1C,m.getElement(5));
       Assert.assertEquals(0xE2,m.getElement(6));
       Assert.assertEquals(0xC5,m.getElement(7));
    }

    public void testGetBitVerifyOpsModeCVMsg(){
       XNetMessage m = XNetMessage.getBitVerifyOpsModeCVMsg(0,5,29,2,true);
       Assert.assertEquals(0xE6,m.getElement(0));
       Assert.assertEquals(0x30,m.getElement(1));
       Assert.assertEquals(0x00,m.getElement(2));
       Assert.assertEquals(0x05,m.getElement(3));
       Assert.assertEquals(0xE8,m.getElement(4));
       Assert.assertEquals(0x1C,m.getElement(5));
       Assert.assertEquals(0xFA,m.getElement(6));
       Assert.assertEquals(0xDD,m.getElement(7));

       m = XNetMessage.getBitVerifyOpsModeCVMsg(0,5,29,2,false);
       Assert.assertEquals(0xE6,m.getElement(0));
       Assert.assertEquals(0x30,m.getElement(1));
       Assert.assertEquals(0x00,m.getElement(2));
       Assert.assertEquals(0x05,m.getElement(3));
       Assert.assertEquals(0xE8,m.getElement(4));
       Assert.assertEquals(0x1C,m.getElement(5));
       Assert.assertEquals(0xF2,m.getElement(6));
       Assert.assertEquals(0xD5,m.getElement(7));
    }

    public void testGetBuildDoubleHeaderMessage(){
       XNetMessage m = XNetMessage.getBuildDoubleHeaderMsg(1234,4567);
       Assert.assertEquals(0xE5,m.getElement(0));
       Assert.assertEquals(0x43,m.getElement(1));
       Assert.assertEquals(0xC4,m.getElement(2));
       Assert.assertEquals(0xD2,m.getElement(3));
       Assert.assertEquals(0xD1,m.getElement(4));
       Assert.assertEquals(0xD7,m.getElement(5));
       Assert.assertEquals(0xB6,m.getElement(6));
    }

    public void testGetDisolveDoubleHeaderMessage(){
       XNetMessage m = XNetMessage.getDisolveDoubleHeaderMsg(1234);
       Assert.assertEquals(0xE5,m.getElement(0));
       Assert.assertEquals(0x43,m.getElement(1));
       Assert.assertEquals(0xC4,m.getElement(2));
       Assert.assertEquals(0xD2,m.getElement(3));
       Assert.assertEquals(0x00,m.getElement(4));
       Assert.assertEquals(0x00,m.getElement(5));
       Assert.assertEquals(0xB0,m.getElement(6));
    }

    public void testGetAddLocoToConsistMsg(){
       XNetMessage m = XNetMessage.getAddLocoToConsistMsg(42,1234,true);
       Assert.assertEquals(0xE4,m.getElement(0));
       Assert.assertEquals(0x40,m.getElement(1));
       Assert.assertEquals(0xC4,m.getElement(2));
       Assert.assertEquals(0xD2,m.getElement(3));
       Assert.assertEquals(0x2A,m.getElement(4));
       Assert.assertEquals(0x98,m.getElement(5));

       m = XNetMessage.getAddLocoToConsistMsg(42,1234,false);
       Assert.assertEquals(0xE4,m.getElement(0));
       Assert.assertEquals(0x41,m.getElement(1));
       Assert.assertEquals(0xC4,m.getElement(2));
       Assert.assertEquals(0xD2,m.getElement(3));
       Assert.assertEquals(0x2A,m.getElement(4));
       Assert.assertEquals(0x99,m.getElement(5));
    }

    public void testGetRemoveLocoFromConsistMsg(){
       XNetMessage m = XNetMessage.getRemoveLocoFromConsistMsg(42,1234);
       Assert.assertEquals(0xE4,m.getElement(0));
       Assert.assertEquals(0x42,m.getElement(1));
       Assert.assertEquals(0xC4,m.getElement(2));
       Assert.assertEquals(0xD2,m.getElement(3));
       Assert.assertEquals(0x2A,m.getElement(4));
       Assert.assertEquals(0x9A,m.getElement(5));
    }

    public void testGetNextAddressOnStackMsg(){
       // search forward
       XNetMessage m = XNetMessage.getNextAddressOnStackMsg(1234,true);
       Assert.assertEquals(0xE3,m.getElement(0));
       Assert.assertEquals(0x05,m.getElement(1));
       Assert.assertEquals(0xC4,m.getElement(2));
       Assert.assertEquals(0xD2,m.getElement(3));
       Assert.assertEquals(0xF0,m.getElement(4));

       // search backward
       m = XNetMessage.getNextAddressOnStackMsg(1234,false);
       Assert.assertEquals(0xE3,m.getElement(0));
       Assert.assertEquals(0x06,m.getElement(1));
       Assert.assertEquals(0xC4,m.getElement(2));
       Assert.assertEquals(0xD2,m.getElement(3));
       Assert.assertEquals(0xF3,m.getElement(4));
    }

    public void testGetDBSearchMsgConsistAddress(){
       // search forward
       XNetMessage m = XNetMessage.getDBSearchMsgConsistAddress(42,true);
       Assert.assertEquals(0xE2,m.getElement(0));
       Assert.assertEquals(0x03,m.getElement(1));
       Assert.assertEquals(0x2A,m.getElement(2));
       Assert.assertEquals(0xCB,m.getElement(3));

       // search backward
       m = XNetMessage.getDBSearchMsgConsistAddress(42,false);
       Assert.assertEquals(0xE2,m.getElement(0));
       Assert.assertEquals(0x04,m.getElement(1));
       Assert.assertEquals(0x2A,m.getElement(2));
       Assert.assertEquals(0xCC,m.getElement(3));
    }

    public void testGetDBSearchMsgNextMULoco(){
       // search forward
       XNetMessage m = XNetMessage.getDBSearchMsgNextMULoco(42,1234,true);
       Assert.assertEquals(0xE4,m.getElement(0));
       Assert.assertEquals(0x01,m.getElement(1));
       Assert.assertEquals(0x2A,m.getElement(2));
       Assert.assertEquals(0xC4,m.getElement(3));
       Assert.assertEquals(0xD2,m.getElement(4));
       Assert.assertEquals(0xD9,m.getElement(5));

       // search backward
       m = XNetMessage.getDBSearchMsgNextMULoco(42,1234,false);
       Assert.assertEquals(0xE4,m.getElement(0));
       Assert.assertEquals(0x02,m.getElement(1));
       Assert.assertEquals(0x2A,m.getElement(2));
       Assert.assertEquals(0xC4,m.getElement(3));
       Assert.assertEquals(0xD2,m.getElement(4));
       Assert.assertEquals(0xDA,m.getElement(5));
    }


    public void testGetDeleteAddressOnStackMsg(){
       XNetMessage m = XNetMessage.getDeleteAddressOnStackMsg(1234);
       Assert.assertEquals(0xE3,m.getElement(0));
       Assert.assertEquals(0x44,m.getElement(1));
       Assert.assertEquals(0xC4,m.getElement(2));
       Assert.assertEquals(0xD2,m.getElement(3));
       Assert.assertEquals(0xB1,m.getElement(4));
    }

    public void testGetLocomoitveInfoRequestMsg(){
       XNetMessage m = XNetMessage.getLocomotiveInfoRequestMsg(1234);
       Assert.assertEquals(0xE3,m.getElement(0));
       Assert.assertEquals(0x00,m.getElement(1));
       Assert.assertEquals(0xC4,m.getElement(2));
       Assert.assertEquals(0xD2,m.getElement(3));
       Assert.assertEquals(0xF5,m.getElement(4));
    }

    public void testGetLocomoitveFunctionStatusMsg(){
       XNetMessage m = XNetMessage.getLocomotiveFunctionStatusMsg(1234);
       Assert.assertEquals(0xE3,m.getElement(0));
       Assert.assertEquals(0x07,m.getElement(1));
       Assert.assertEquals(0xC4,m.getElement(2));
       Assert.assertEquals(0xD2,m.getElement(3));
       Assert.assertEquals(0xF2,m.getElement(4));
    }

    public void testGetLocomoitveFunctionHighOnStatusMsg(){
       XNetMessage m = XNetMessage.getLocomotiveFunctionHighOnStatusMsg(1234);
       Assert.assertEquals(0xE3,m.getElement(0));
       Assert.assertEquals(0x09,m.getElement(1));
       Assert.assertEquals(0xC4,m.getElement(2));
       Assert.assertEquals(0xD2,m.getElement(3));
       Assert.assertEquals(0xFC,m.getElement(4));
    }

    public void testGetLocomoitveFunctionHighMomStatusMsg(){
       XNetMessage m = XNetMessage.getLocomotiveFunctionHighMomStatusMsg(1234);
       Assert.assertEquals(0xE3,m.getElement(0));
       Assert.assertEquals(0x08,m.getElement(1));
       Assert.assertEquals(0xC4,m.getElement(2));
       Assert.assertEquals(0xD2,m.getElement(3));
       Assert.assertEquals(0xFD,m.getElement(4));
    }

    public void testGetAddressedEmergencyStop(){
       XNetMessage m = XNetMessage.getAddressedEmergencyStop(1234);
       Assert.assertEquals(0x92,m.getElement(0));
       Assert.assertEquals(0xC4,m.getElement(1));
       Assert.assertEquals(0xD2,m.getElement(2));
       Assert.assertEquals(0x84,m.getElement(3));
    }

    public void testGetResumeOperationsMsg() {
       XNetMessage m = XNetMessage.getResumeOperationsMsg();
       Assert.assertEquals(0x21,m.getElement(0));
       Assert.assertEquals(0x81,m.getElement(1));
       Assert.assertEquals(0xA0,m.getElement(2));
    }

    public void testGetEmergencyOffMsg() {
       XNetMessage m = XNetMessage.getEmergencyOffMsg();
       Assert.assertEquals(0x21,m.getElement(0));
       Assert.assertEquals(0x80,m.getElement(1));
       Assert.assertEquals(0xA1,m.getElement(2));
    }

    public void testGetCSVersionRequestMessage() {
       XNetMessage m = XNetMessage.getCSVersionRequestMessage();
       Assert.assertEquals(0x21,m.getElement(0));
       Assert.assertEquals(0x21,m.getElement(1));
       Assert.assertEquals(0x00,m.getElement(2));
    }

    public void testGetCSStatusRequestMessage() {
       XNetMessage m = XNetMessage.getCSStatusRequestMessage();
       Assert.assertEquals(0x21,m.getElement(0));
       Assert.assertEquals(0x24,m.getElement(1));
       Assert.assertEquals(0x05,m.getElement(2));
    }

    public void testGetCsAutoStartMessge() {
       // test autostart mode.
       XNetMessage m = XNetMessage.getCSAutoStartMessage(true);
       Assert.assertEquals(0x22,m.getElement(0));
       Assert.assertEquals(0x22,m.getElement(1));
       Assert.assertEquals(0x04,m.getElement(2));
       Assert.assertEquals(0x04,m.getElement(3));
       // test manual mode.
       m = XNetMessage.getCSAutoStartMessage(false);
       Assert.assertEquals(0x22,m.getElement(0));
       Assert.assertEquals(0x22,m.getElement(1));
       Assert.assertEquals(0x00,m.getElement(2));
       Assert.assertEquals(0x00,m.getElement(3));
    }

    public void testGetLIVersionRequestMessage() {
       XNetMessage m = XNetMessage.getLIVersionRequestMessage();
       Assert.assertEquals(0xF0,m.getElement(0));
       Assert.assertEquals(0xF0,m.getElement(1));
    }

    public void testGetLIAddressRequestMsg() {
       XNetMessage m = XNetMessage.getLIAddressRequestMsg(1);
       Assert.assertEquals(0xF2,m.getElement(0));
       Assert.assertEquals(0x01,m.getElement(1));
       Assert.assertEquals(0x01,m.getElement(2));
       Assert.assertEquals(0xF2,m.getElement(3));
    }

    public void testGetLISpeedReqeustMessage() {
       XNetMessage m = XNetMessage.getLISpeedRequestMsg(1);
       Assert.assertEquals(0xF2,m.getElement(0));
       Assert.assertEquals(0x02,m.getElement(1));
       Assert.assertEquals(0x01,m.getElement(2));
       Assert.assertEquals(0xF1,m.getElement(3));
    }

    // from here down is testing infrastructure
    public XNetMessageTest(String s) {
        super(s);
    }

    // Main entry point
    static public void main(String[] args) {
        String[] testCaseName = {"-noloading", XNetMessageTest.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }

    // test suite from all defined tests
    public static Test suite() {
        TestSuite suite = new TestSuite(XNetMessageTest.class);
        return suite;
    }

    // The minimal setup for log4J
    protected void setUp() {
        apps.tests.Log4JFixture.setUp();
    }

    protected void tearDown() {
        apps.tests.Log4JFixture.tearDown();
        // make sure the message timeouts and retries are set to
        // the defaults.
        XNetMessage.setXNetMessageTimeout(5000); 
        XNetMessage.setXNetMessageRetries(5); 
    }

}

package jmri.jmrix.jmriclient.networkdriver.configurexml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jmri.InstanceManager;
import jmri.jmrix.configurexml.AbstractNetworkConnectionConfigXml;
import jmri.jmrix.jmriclient.networkdriver.ConnectionConfig;
import jmri.jmrix.jmriclient.networkdriver.NetworkDriverAdapter;
import jmri.jmrix.jmriclient.JMRIClientSystemConnectionMemo;

import org.jdom2.Element;

/**
 * Handle XML persistance of layout connections by persistening
 * the NetworkDriverAdapter (and connections).
 * <P>
 * Note this is
 * named as the XML version of a ConnectionConfig object,
 * but it's actually persisting the NetworkDriverAdapter.
 * <P>
 * This class is invoked from jmrix.JmrixConfigPaneXml on write,
 * as that class is the one actually registered. Reads are brought
 * here directly via the class attribute in the XML.
 *
 * @author Bob Jacobsen Copyright: Copyright (c) 2003
 * @author Paul Bender Copyright: Copyright (c) 2010
 * @version $Revision$
 */
public class ConnectionConfigXml extends AbstractNetworkConnectionConfigXml {

    public ConnectionConfigXml() {
        super();
    }

    @Override
    protected void getInstance() {
        if(adapter == null)
           adapter = new NetworkDriverAdapter();
    }

    @Override
    protected void getInstance(Object object) {
           adapter = ((ConnectionConfig)object).getAdapter();
    }

    @Override
    protected void register() {
        InstanceManager.configureManagerInstance().registerPref(new ConnectionConfig(adapter));
    }


    /**
     * Customizable method if you need to add anything more
     * @param e Element being created, update as needed
     */
    @Override
    protected void extendElement(Element e) {
       if (adapter.getSystemConnectionMemo()!=null){
            e.setAttribute("transmitPrefix", ((JMRIClientSystemConnectionMemo)adapter.getSystemConnectionMemo()).getTransmitPrefix());
        }
    }

    /**
     * Customizable method if you need to add anything more
     * @param e Element being created, update as needed
     */
    @Override
    protected void unpackElement(Element e) {
       if (e.getAttribute("transmitPrefix")!=null) {
           ((JMRIClientSystemConnectionMemo)adapter.getSystemConnectionMemo()).setTransmitPrefix(e.getAttribute("transmitPrefix").getValue());
       }
    }



    // initialize logging
    static Logger log = LoggerFactory.getLogger(ConnectionConfigXml.class.getName());

}
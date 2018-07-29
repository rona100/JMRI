package jmri.jmrit.beantable.signalmast;

import javax.annotation.Nonnull;

import jmri.*;
import jmri.implementation.SignalHeadSignalMast;
import jmri.util.swing.BeanSelectCreatePanel;

import org.openide.util.lookup.ServiceProvider;

/**
 * A pane for configuring SignalHeadSignalMast objects
 * <P>
 * @see jmri.jmrit.beantable.signalmast.SignalMastAddPane
 * @author Bob Jacobsen Copyright (C) 2018
 * @since 4.11.2
 */
public class SignalHeadSignalMastAddPane extends SignalMastAddPane {

    /** {@inheritDoc} */
    @Override
    @Nonnull public String getPaneName() {
        return Bundle.getMessage("HeadCtlMast");
    }

    /** {@inheritDoc} */
    @Override
    public boolean canHandleMast(@Nonnull SignalMast mast) {
        return mast instanceof SignalHeadSignalMast;
    }

    @ServiceProvider(service = SignalMastAddPane.SignalMastAddPaneProvider.class)
    static public class SignalMastAddPaneProvider extends SignalMastAddPane.SignalMastAddPaneProvider {
        /** {@inheritDoc} */
        @Override
        @Nonnull public String getPaneName() {
            return Bundle.getMessage("HeadCtlMast");
        }
        /** {@inheritDoc} */
        @Override
        @Nonnull public SignalMastAddPane getNewPane() {
            return new SignalHeadSignalMastAddPane();
        }
    }
}

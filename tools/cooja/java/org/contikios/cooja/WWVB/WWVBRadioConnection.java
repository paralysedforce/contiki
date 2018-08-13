package org.contikios.cooja.WWVB;

import org.contikios.cooja.RadioConnection;
import org.contikios.cooja.interfaces.Radio;

/**
 * Created by vyasalwar on 8/13/18.
 */
public class WWVBRadioConnection extends RadioConnection {
    /**
     * Creates a new radio connection with given source and no destinations.
     *
     * @param sourceRadio Source radio
     */
    private WWVBTransmitter source;

    public WWVBRadioConnection(WWVBTransmitter transmitter) {
        super(null);
        this.source = transmitter;
    }
}

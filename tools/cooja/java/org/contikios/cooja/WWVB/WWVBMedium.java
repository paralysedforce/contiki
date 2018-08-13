package org.contikios.cooja.WWVB;

import org.contikios.cooja.Mote;
import org.contikios.cooja.RadioConnection;
import org.contikios.cooja.RadioMedium;
import org.contikios.cooja.Simulation;
import org.contikios.cooja.interfaces.Radio;
import org.contikios.cooja.radiomediums.AbstractRadioMedium;
import org.jdom.Element;

import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by vyasalwar on 8/13/18.
 */
public class WWVBMedium extends AbstractRadioMedium {

    public WWVBMedium(Simulation simulation){
        super(simulation);
    }

    @Override
    public RadioConnection createConnections(Radio radio) {
        return null;
    }
}

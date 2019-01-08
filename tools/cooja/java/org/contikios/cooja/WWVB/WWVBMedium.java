package org.contikios.cooja.WWVB;

import org.apache.log4j.Logger;
import org.contikios.cooja.*;
import org.contikios.cooja.interfaces.Log;
import org.contikios.cooja.interfaces.Radio;
import org.contikios.cooja.radiomediums.AbstractRadioMedium;
import org.jdom.Element;

import java.util.Collection;


/**
 * Created by vyasalwar on 8/13/18.
 */

@ClassDescription("WWVB Radio Signals Recieved")
public class WWVBMedium extends AbstractRadioMedium {

    private Logger logger = Logger.getLogger(WWVBMedium.class);
    private static boolean debug = false;

    private WWVBTransmitter wwvbTransmitter;
    private WWVBRadioConnection wwvbRadioConnection;
    private Simulation simulation;



    public WWVBMedium(Simulation simulation){
        super(simulation);
        this.simulation = simulation;
        setTransmitter(simulation.getWwvbTransmitter());

    }

    public void setTransmitter(WWVBTransmitter transmitter){
        this.wwvbTransmitter = transmitter;
        if (wwvbTransmitter != null){
            wwvbTransmitter.setMedium(this);
            wwvbRadioConnection = new WWVBRadioConnection(wwvbTransmitter);
        }
    }

    @Override
    public void registerRadioInterface(Radio radio, Simulation simulation){
        super.registerRadioInterface(radio, simulation);
        if (radio != null){
            createConnections(radio);
        }
    }

    @Override
    public void unregisterRadioInterface(Radio radio, Simulation simulation){
        super.unregisterRadioInterface(radio, simulation);
        if (radio != null){
            wwvbRadioConnection.removeDestination(radio);
        }
    }

    @Override
    public RadioConnection createConnections(Radio radio) {
        wwvbRadioConnection.addDestination(radio);
        if (debug) logger.info("Connection Created");
        return wwvbRadioConnection;
    }

    public Collection<Element> getConfigXML() {
        return null;
    }

    public boolean setConfigXML(Collection<Element> configXML, boolean visAvailable) {
        return true;
    }

    public void scheduleEvent(long time, WWVBCode code){
        simulation.scheduleEvent(new TimeEvent(time) {
            @Override
            public void execute(long t) {
                if (debug) {
                    StringBuilder msg = new StringBuilder();
                    msg.append(t)
                            .append(": ")
                            .append(code.name());

                    logger.info(msg.toString());
                }

                for (Radio radio: wwvbRadioConnection.getAllDestinations()){
                    if (debug) logger.info("Radio " + radio.toString());
                    radio.setReceivedPacket(new WWVBPacket(code));
                }
            }
        }, time);
    }

}

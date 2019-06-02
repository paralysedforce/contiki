package org.contikios.cooja.mspmote.interfaces;

import org.apache.log4j.Logger;
import org.contikios.cooja.BatteryListener;
import org.contikios.cooja.Mote;
import org.contikios.cooja.RadioPacket;
import org.contikios.cooja.Simulation;
import org.contikios.cooja.WWVB.WWVBCode;
import org.contikios.cooja.WWVB.WWVBPacket;
import org.contikios.cooja.interfaces.CustomDataRadio;
import org.contikios.cooja.interfaces.Position;
import org.contikios.cooja.interfaces.Radio;
import org.contikios.cooja.mspmote.MspMote;
import org.contikios.cooja.mspmote.WWVBMote;
import org.jdom.Element;
import se.sics.mspsim.chip.Button;
import se.sics.mspsim.chip.WWVBRadio;
import se.sics.mspsim.core.Chip;
import se.sics.mspsim.core.OperatingModeListener;
import se.sics.mspsim.core.StateChangeListener;
import se.sics.mspsim.core.USARTSource;
import se.sics.mspsim.platform.WWVB.WWVBNode;

import java.util.Collection;


/**
 * Created by vyasalwar on 8/20/18.
 */
public class MspWWVBRadio extends Radio implements BatteryListener{

    private Logger logger = Logger.getLogger(MspWWVBRadio.class);
    private static boolean debug = false;

    private final WWVBMote mote;
    private final WWVBRadio radio;

    private RadioEvent lastEvent;
    private WWVBCode lastCodeRecieved = null;
    private boolean isReceiving;
    private double currentSignalStrength;

    public MspWWVBRadio(Mote mote){
        this.mote = (WWVBMote)mote;

        this.radio = this.mote.getCPU().getChip(WWVBRadio.class);
        if (radio == null || !(radio instanceof WWVBRadio)) {
            StringBuilder msg = new StringBuilder("Radio not found. Interfaces:\n\t");
            for (Chip chip: this.mote.getCPU().getChips()){
                msg.append(chip.getID())
                        .append("\n");
            }


            throw new IllegalStateException(msg.toString());
        }

        this.lastEvent = RadioEvent.UNKNOWN;
        this.isReceiving = false;
        this.currentSignalStrength = 100; // idfk

        /* Add listeners to radio */
        radio.addOperatingModeListener(new OperatingModeListener() {
            @Override
            public void modeChanged(Chip source, int mode) {
                if (isRadioOn()) {
                    lastEvent = RadioEvent.HW_ON;
                    setChanged();
                    notifyObservers();
                }
                else {
                    isReceiving = false;
                    lastEvent = RadioEvent.HW_OFF;
                    setChanged();
                    notifyObservers();
                }
            }
        });

        radio.powerUp();
        radio.enableCommunication();
    }

    public void setReceivedPacket(RadioPacket radioPacket) {
        if (!(radioPacket instanceof WWVBPacket)){
            logger.warn("WWVB Radio receiving incompatible data");
            return;
        }

        lastEvent = RadioEvent.RECEPTION_STARTED;
        isReceiving = true;
        setChanged();
        notifyObservers();

        WWVBPacket packet = (WWVBPacket) radioPacket;
        lastCodeRecieved = WWVBCode.parse(packet.getPacketData()[0]);

        if (lastCodeRecieved.equals(WWVBCode.AMP_INC)) {
            if (debug) logger.info("Setting amplitude to high");
            setSerialValue(true);

        } else if (lastCodeRecieved.equals(WWVBCode.AMP_DEC)) {
            if (debug) logger.info("Setting amplitude to low");
            setSerialValue(false);
        } else if (lastCodeRecieved.equals(WWVBCode.PHASE_CHANGE)){
            // Do something
            long time = mote.getSimulation().getSimulationTime();
        }

        if (debug) logger.info("WWVBPacket Received");
        setChanged();
        notifyObservers();
    }

    private void setSerialValue(boolean high){
        USARTSource source = (USARTSource) (mote.getCPU().getIOUnit("USCI B0"));
        source.byteReceived(high ? 255: 0);
    }

    public RadioPacket getLastPacketTransmitted() {
        return null;
    }

    public RadioPacket getLastPacketReceived() {
        return new WWVBPacket(lastCodeRecieved);
    }



    public RadioEvent getLastEvent() {
        return lastEvent;
    }

    public boolean isTransmitting() {
        return false;
    }

    public boolean isReceiving() {
        return isReceiving;
    }

    public boolean isInterfered() {
        return false;
    }

    public boolean isRadioOn() {;
        return radio.isRadioOn();
    }

    public void interfereAnyReception() {
    }

    public double getCurrentOutputPower() {
        return radio.getCurrentOutputPower();
    }

    public int getCurrentOutputPowerIndicator() {
        return 100;
    }

    public int getOutputPowerIndicatorMax() {
        return 100;
    }

    public double getCurrentSignalStrength() {
        return currentSignalStrength;
    }

    public void setCurrentSignalStrength(double v) {
        currentSignalStrength = v;
    }


    public Position getPosition() {
        return mote.getInterfaces().getPosition();
    }

    public Mote getMote() {
        return mote;
    }

    public int getChannel() {
        return 0;
    }
    public void signalReceptionStart() {

    }
    public void signalReceptionEnd() {

    }

    public Collection<Element> getConfigXML() {
        return null;
    }

    public void setConfigXML(Collection<Element> collection, boolean b) {

    }

    public void rebootInterface() {
        radio.powerUp();
        radio.enableCommunication();
    }

    public void shutdownInterface() {
        radio.powerDown();
    }
}

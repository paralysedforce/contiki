package org.contikios.cooja.WWVB;

import org.contikios.cooja.Mote;
import org.contikios.cooja.RadioPacket;
import org.contikios.cooja.Simulation;
import org.contikios.cooja.interfaces.ApplicationRadio;
import org.contikios.cooja.interfaces.Position;
import org.contikios.cooja.interfaces.Radio;
import org.jdom.Element;

import java.util.Collection;

/**
 *
 * Represents a peripheral WWVB time signal receiver
 * Created by vyasalwar on 8/3/18.
 */
public class WWVBRadio extends ApplicationRadio {


    private WWVBPacket lastReceivedPacket;
    private double signalStrength;

    // Don't know what this means.
    final int outputPowerIndicator = 100;

    public WWVBRadio(Mote mote){
        super(mote);
    }

    @Override
    public void setReceivedPacket(RadioPacket packet) {
        lastReceivedPacket = (WWVBPacket) packet;
    }

    // WWVB Receivers cannot transmit
    @Override
    public RadioPacket getLastPacketTransmitted() {
        return null;
    }

    @Override
    public RadioPacket getLastPacketReceived() {
        return lastReceivedPacket;
    }

    @Override
    public boolean isTransmitting() {
        return false;
    }

    @Override
    public boolean isReceiving() {
        return true;
    }

    @Override
    public boolean isInterfered() {
        return false;
    }

    @Override
    public boolean isRadioOn() {
        return true;
    }

    // WWVB Radios are passive
    @Override
    public void interfereAnyReception() {
    }

    // TODO: Find value of this
    @Override
    public double getCurrentOutputPower() {
        return 0;
    }
    @Override
    public int getCurrentOutputPowerIndicator() {
        return outputPowerIndicator;
    }

    @Override
    public int getOutputPowerIndicatorMax() {
        return 0;
    }

    @Override
    public double getCurrentSignalStrength() {
        return signalStrength;
    }

    @Override
    public void setCurrentSignalStrength(double signalStrength) {
        this.signalStrength = signalStrength;
    }

    @Override
    public int getChannel() {
        return 0;
    }

    @Override
    public Position getPosition() {
        return this.mote.getInterfaces().getPosition();
    }

    @Override
    public Mote getMote() {
        return this.mote;
    }

    // Junk from here to the end of the file
    @Override
    public Collection<Element> getConfigXML() {
        return null;
    }

    @Override
    public void setConfigXML(Collection<Element> configXML, boolean visAvailable) {

    }
}

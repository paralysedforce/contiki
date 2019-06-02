package org.contikios.cooja.WWVB;

import org.contikios.cooja.Mote;
import org.contikios.cooja.RadioPacket;
import org.contikios.cooja.interfaces.ApplicationRadio;
import org.contikios.cooja.interfaces.Position;
import org.contikios.cooja.interfaces.Radio;
import org.contikios.cooja.radiomediums.DGRMDestinationRadio;
import org.jdom.Element;

import java.util.Collection;

/**
 * Created by vyasalwar on 8/15/18.
 */
public class WWVBTransmitterRadio extends Radio {
    WWVBPacket transmissionPacket;

    public void setTransmissionPacket(WWVBPacket transmissionPacket){
        this.transmissionPacket = transmissionPacket;
    }

    @Override
    public void setReceivedPacket(RadioPacket packet) {
    }

    @Override
    public RadioPacket getLastPacketTransmitted() {
        return transmissionPacket;
    }

    @Override
    public RadioPacket getLastPacketReceived() {
        return null;
    }

    @Override
    public void signalReceptionStart() {

    }

    @Override
    public void signalReceptionEnd() {

    }

    @Override
    public RadioEvent getLastEvent() {
        return null;
    }

    @Override
    public boolean isTransmitting() {
        return true;
    }

    @Override
    public boolean isReceiving() {
        return false;
    }

    @Override
    public boolean isInterfered() {
        return false;
    }

    @Override
    public boolean isRadioOn() {
        return true;
    }

    @Override
    public void interfereAnyReception() {

    }

    @Override
    public Collection<Element> getConfigXML() {
        return null;
    }

    @Override
    public double getCurrentOutputPower() {
        return 0;
    }

    @Override
    public int getCurrentOutputPowerIndicator() {
        return 0;
    }

    @Override
    public int getOutputPowerIndicatorMax() {
        return 0;
    }

    @Override
    public void setConfigXML(Collection<Element> configXML, boolean visAvailable) {

    }

    @Override
    public double getCurrentSignalStrength() {
        return 0;
    }

    @Override
    public void setCurrentSignalStrength(double signalStrength) {

    }

    @Override
    public int getChannel() {
        return 0;
    }

    @Override
    public Position getPosition() {
        return null;
    }

    @Override
    public Mote getMote() {
        return null;
    }
}

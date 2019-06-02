package org.contikios.cooja.plugins.skins;

import org.contikios.cooja.ClassDescription;
import org.contikios.cooja.Mote;
import org.contikios.cooja.SimEventCentral;
import org.contikios.cooja.Simulation;
import org.contikios.cooja.interfaces.Battery;
import org.contikios.cooja.plugins.Visualizer;
import org.contikios.cooja.plugins.VisualizerSkin;
import org.contikios.cooja.SimEventCentral.MoteCountListener;

import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by vyasalwar on 9/14/18.
 */

@ClassDescription("Mote Shutdowns")
public class IntermittentVisualizerSkin implements VisualizerSkin {
    private Simulation simulation;
    private Visualizer visualizer;

    private Observer repaintObserver = new Observer() {
        public void update(Observable o, Object arg) {
            visualizer.repaint();
        }
    };

    private MoteCountListener moteShutdownListener = new MoteCountListener() {
        @Override
        public void moteWasAdded(Mote mote) {
            Battery battery = mote.getInterfaces().getBattery();
            if (battery != null){
                battery.addObserver(repaintObserver);
            }
        }

        @Override
        public void moteWasRemoved(Mote mote) {
            Battery battery = mote.getInterfaces().getBattery();
            if (battery != null){
                battery.deleteObserver(repaintObserver);
            }
        }
    };

    public void setActive(Simulation sim, Visualizer visualizer) {
        this.simulation = sim;
        this.visualizer = visualizer;

        sim.getEventCentral().addMoteCountListener(moteShutdownListener);

        for (Mote mote: sim.getMotes()){
            moteShutdownListener.moteWasAdded(mote);
        }


    }

    @Override
    public void setInactive() {
        this.simulation.getEventCentral().addMoteCountListener(moteShutdownListener);

        for (Mote mote: simulation.getMotes()){
            moteShutdownListener.moteWasRemoved(mote);
        }
    }

    @Override
    public Color[] getColorOf(Mote mote) {
        if (!mote.getInterfaces().getBattery().isPowered()){
            return new Color[]{Color.GRAY, Color.BLUE};
        }
        else return null;
    }

    public void paintBeforeMotes(Graphics g) { }

    public void paintAfterMotes(Graphics g)  { }

    @Override
    public Visualizer getVisualizer() {
        return null;
    }
}

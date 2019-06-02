package org.contikios.cooja;

/**
 * An interface that
 *
 * Created by vyasalwar on 9/24/18.
 */
public interface BatteryListener {

    /**
     * Invoked in a simulation whenever the battery regains power in Simulation.rebootMote()
     */
    public void rebootInterface();

    /**
     * Invoked in a simulation whenever the battery runs out of power in Simulation.shutdownMote()
     */
    public void shutdownInterface();
}

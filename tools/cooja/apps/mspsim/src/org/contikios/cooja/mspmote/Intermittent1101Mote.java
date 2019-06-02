package org.contikios.cooja.mspmote;

import org.apache.log4j.Logger;
import org.contikios.cooja.ClassDescription;
import org.contikios.cooja.Simulation;
import se.sics.mspsim.platform.ti.Intermittent1101Node;

import java.io.File;
import java.io.IOException;

/**
 * Created by vyasalwar on 9/17/18.
 */

public class Intermittent1101Mote extends MspMote {

    private static Logger logger = Logger.getLogger(Intermittent1101Mote.class);

    public Intermittent1101Mote(MspMoteType mspMoteType, Simulation simulation){
        super(mspMoteType, simulation);
    }

    @Override
    protected boolean initEmulator(File elfFile){
        try {
            Intermittent1101Node node = new Intermittent1101Node();
            registry = node.getRegistry();
            prepareMote(elfFile, node);
        } catch (IOException e){
            logger.fatal("Error creating Intermittent1101 Mote " + getID());
            return false;
        }
        return true;
    }

    @Override
    public String toString(){
        return "Simeon's Mote: " + getID();
    }
}


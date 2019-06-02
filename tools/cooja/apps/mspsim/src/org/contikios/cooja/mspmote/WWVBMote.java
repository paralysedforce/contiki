package org.contikios.cooja.mspmote;

import org.apache.log4j.Logger;
import org.contikios.cooja.Simulation;
import se.sics.mspsim.platform.WWVB.WWVBNode;
import se.sics.mspsim.platform.wismote.WismoteNode;

import javax.imageio.IIOException;
import java.io.File;
import java.io.IOException;

/**
 * Created by vyasalwar on 8/20/18.
 */
public class WWVBMote extends MspMote {

    private static Logger logger = Logger.getLogger(WWVBMote.class);

    public WWVBMote(MspMoteType mspMoteType, Simulation simulation){
        super(mspMoteType, simulation);
    }

    @Override
    protected boolean initEmulator(File elfFile){
        try {
            WWVBNode node = new WWVBNode();
            registry = node.getRegistry();
            prepareMote(elfFile, node);
        } catch (IOException e){
            logger.fatal("Error creating WWVB Mote " + getID());
            return false;
        }
        return true;
    }

    @Override
    public String toString(){
        return "WWVB Mote: " + getID();
    }
}

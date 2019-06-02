package org.contikios.cooja.mspmote;

import org.contikios.cooja.*;
import org.contikios.cooja.interfaces.*;
import org.contikios.cooja.mspmote.interfaces.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Created by vyasalwar on 9/17/18.
 */
@ClassDescription("Simeon's Mote")
@AbstractionLevelDescription("Emulated Level")
public class Intermittent1101MoteType extends AbstractMspMoteType {

    @Override
    protected MspMote createMote(Simulation simulation) {
        return new Intermittent1101Mote(this, simulation);
    }

    @Override
    public String getMoteType() {
        return "elf";
    }

    @Override
    public String getMoteName() {
        return "elf";
    }

    @Override
    protected String getMoteImage() {
        return "images/wismote.jpg";
    }

    @Override
    public Class<? extends MoteInterface>[] getAllMoteInterfaceClasses() {
        return getDefaultMoteInterfaceClasses();
    }

    @Override
    public Class<? extends MoteInterface>[] getDefaultMoteInterfaceClasses() {
        return new Class[]{
                Battery.class,
                Position.class,
                RimeAddress.class,
                Mote2MoteRelations.class,
                MoteAttributes.class,
                MspClock.class,
                MspMoteID.class,
                MspDefaultSerial.class,
                CC1101Radio.class,
                MspLED.class,
                MspDebugOutput.class, /* EXPERIMENTAL: Enable me for COOJA_DEBUG(..) */
        };
    }

    @Override
    public File getExpectedFirmwareFile(File source) {
        File parentDir = source.getParentFile();
        String sourceNoExtension = source.getName().substring(0, source.getName().length()-2);

        return new File(parentDir, sourceNoExtension + ".elf");
    }

}

package org.contikios.cooja.mspmote;

import org.contikios.cooja.*;
import org.contikios.cooja.interfaces.*;
import org.contikios.cooja.mspmote.interfaces.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Currently just a copy of wismote
 *
 * Created by vyasalwar on 8/20/18.
 */

@ClassDescription("Custom WWVB Mote")
@AbstractionLevelDescription("Emulated Level")
public class WWVBMoteType extends AbstractMspMoteType {
    @Override
    public String getMoteType() {
        return "wismote";
    }

    @Override
    public String getMoteName() {
        return "WWVB";
    }

    @Override
    protected String getMoteImage() {
        return "images/wismote.jpg";
    }

    @Override
    protected MspMote createMote(Simulation simulation) {
        return new WWVBMote(this, simulation);
    }

    @Override
    public Class<? extends MoteInterface>[] getAllMoteInterfaceClasses() {
        Class<? extends MoteInterface>[] list = createMoteInterfaceList(
                Battery.class,
                Position.class,
                RimeAddress.class,
                IPAddress.class,
                Mote2MoteRelations.class,
                MoteAttributes.class,
                MspClock.class,
                MspMoteID.class,
                MspButton.class,
//                SkyFlash.class,
//                SkyCoffeeFilesystem.class,
                MspWWVBRadio.class,
                MspDefaultSerial.class,
                MspLED.class,
                MspDebugOutput.class /* EXPERIMENTAL: Enable me for COOJA_DEBUG(..) */
        );
        return list;
    }

    @Override
    public Class<? extends MoteInterface>[] getDefaultMoteInterfaceClasses() {
        return getAllMoteInterfaceClasses();
    }
}

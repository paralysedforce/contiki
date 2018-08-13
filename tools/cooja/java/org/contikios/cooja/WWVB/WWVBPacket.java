package org.contikios.cooja.WWVB;

import org.contikios.cooja.RadioPacket;

/**
 * Created by vyasalwar on 8/6/18.
 */
public class WWVBPacket implements RadioPacket {
    private WWVBCode code;

    public WWVBPacket(WWVBCode code) {
        this.code = code;
    }

    @Override
    public byte[] getPacketData() {
        return new byte[]{ code.getValue() };
    }
}

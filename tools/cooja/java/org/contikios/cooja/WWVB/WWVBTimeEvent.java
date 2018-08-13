package org.contikios.cooja.WWVB;

import org.contikios.cooja.EventQueue;
import org.contikios.cooja.Simulation;
import org.contikios.cooja.TimeEvent;

/**
 * Created by vyasalwar on 8/7/18.
 */
public class WWVBTimeEvent extends TimeEvent {
    private WWVBCode code;

    public WWVBTimeEvent(long time, WWVBCode code){
        super(time, code.name());
        this.code = code;
    }

    @Override
    public void execute(long t) {

    }
}

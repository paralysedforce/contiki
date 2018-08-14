package org.contikios.cooja.WWVB;

import org.apache.log4j.Logger;
import org.contikios.cooja.EventQueue;
import org.contikios.cooja.Simulation;
import org.contikios.cooja.TimeEvent;



/**
 * Created by vyasalwar on 8/7/18.
 */
public class WWVBTimeEvent extends TimeEvent {
    private WWVBCode code;
    private static Logger logger = Logger.getLogger(WWVBTimeEvent.class);

    public WWVBTimeEvent(long time, WWVBCode code){
        super(time, code.name());
        this.code = code;
    }

    @Override
    public void execute(long t) {
        StringBuilder msg = new StringBuilder();
        msg.append(t)
                .append(": ")
                .append(code.name());

        logger.info(msg.toString());
    }
}

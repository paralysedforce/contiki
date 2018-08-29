package org.contikios.cooja.WWVB;

/**
 * An enum representing a digital encoding of a WWVB radio, using both legacy and enhanced (phase)
 *
 * Created by vyasalwar on 8/6/18.
 */
public enum WWVBCode {
    AMP_DEC(0), AMP_INC(1), PHASE_CHANGE(2);

    private final int value;
    WWVBCode(int value){
        this.value = value;
    }

    public byte getValue(){
        return (byte)value;
    }

    public static WWVBCode parse(byte b){
        switch(b){
            case 0:
                return AMP_DEC;
            case 1:
                return AMP_INC;
            case 2:
                return PHASE_CHANGE;
            default:
                throw new IllegalArgumentException("WWVBCode Parse Failure");
        }
    }
}

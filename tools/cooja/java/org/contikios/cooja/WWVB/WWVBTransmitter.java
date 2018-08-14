package org.contikios.cooja.WWVB;

import org.apache.log4j.Logger;
import org.contikios.cooja.Simulation;

import java.util.*;

/**
 * Created by vyasalwar on 8/6/18.
 */
public class WWVBTransmitter extends Observable implements Observer {
    private long time;

    public void setUtcCorrection(int utcCorrection) {
        this.utcCorrection = utcCorrection;
    }

    private int utcCorrection; // Represents the (signed) UTC correction in tenths of seconds
    private boolean leapSecondIndicator;
    private boolean dstAnnouncementIndicator;
    private DSTCode dstCode;
    private PhaseCode lastPhaseCode;

    Logger logger = Logger.getLogger(WWVBTransmitter.class);
    private Simulation simulation;

    public WWVBTransmitter(long time) {
        this.time = time;
        utcCorrection = 0;
        leapSecondIndicator = false;
        dstCode = DSTCode.NO_DST;
        lastPhaseCode = PhaseCode.PH_ZERO;
        dstAnnouncementIndicator = false;
    }

    public WWVBTransmitter() {
        this(0);
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    public Simulation getSimulation() {
        return simulation;
    }

    public void setDstAnnouncementIndicator(boolean dstAnnouncementIndicator){
        this.dstAnnouncementIndicator = dstAnnouncementIndicator;
    }

    public void setDate(int year, int month, int day, int hour, int minute) {
        Calendar calendar = GregorianCalendar.getInstance();
        try {
            calendar.set(year, month, day, hour, minute, 0);
            logger.info(calendar.getTime().toString());

            this.time = calendar.getTimeInMillis();
        } catch (Exception e) {
            logger.info("Calendar set failed");
        }
    }

    public DSTCode getDstCode() {
        return dstCode;
    }

    public void setDstCode(DSTCode dstCode) {
        this.dstCode = dstCode;
    }

    public void setLeapSecondIndicator(boolean leapSecondIndicator) {
        this.leapSecondIndicator = leapSecondIndicator;
    }

    // Encoding amplitude data (Legacy WWVB format)
    private enum AmplitudeCode {
        AM_MARKER, AM_ZERO, AM_ONE;
    }

    private List<AmplitudeCode> generateAmplitudeData() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.time);

        List<AmplitudeCode> packet = new LinkedList<>();

        // Bit 0
        packet.add(AmplitudeCode.AM_MARKER);

        // Bits 1 - 8
        int minute = calendar.get(Calendar.MINUTE);
        ArrayList<AmplitudeCode> minuteData = encodeAmplitudeBits(minute, 40, 20, 10, 0, 8, 4, 2, 1);
        packet.addAll(minuteData);

        // Bits 9 - 11
        packet.add(AmplitudeCode.AM_MARKER);
        packet.add(AmplitudeCode.AM_ZERO);
        packet.add(AmplitudeCode.AM_ZERO);

        // Bits 12 - 18
        int hour = calendar.get(Calendar.HOUR);
        ArrayList<AmplitudeCode> hourData = encodeAmplitudeBits(hour, 20, 10, 8, 4, 2, 1);
        hourData.add(2, AmplitudeCode.AM_ZERO);

        // Bits 19 - 21
        packet.add(AmplitudeCode.AM_MARKER);
        packet.add(AmplitudeCode.AM_ZERO);
        packet.add(AmplitudeCode.AM_ZERO);

        // Bits 22 - 33
        int day = calendar.get(Calendar.DAY_OF_YEAR);
        ArrayList<AmplitudeCode> dayData = encodeAmplitudeBits(day, 200, 100, 0, 80, 40, 20, 10, -1, 8, 4, 2, 1);
        packet.addAll(dayData);

        // Bits 34 - 35
        packet.add(AmplitudeCode.AM_ZERO);
        packet.add(AmplitudeCode.AM_ZERO);

        // Bits 36 - 38
        if (utcCorrection < 0) {
            packet.add(AmplitudeCode.AM_ZERO);
            packet.add(AmplitudeCode.AM_ONE);
            packet.add(AmplitudeCode.AM_ZERO);
        } else {
            packet.add(AmplitudeCode.AM_ONE);
            packet.add(AmplitudeCode.AM_ZERO);
            packet.add(AmplitudeCode.AM_ONE);
        }

        // Bits 39 - 43: Date
        packet.addAll(encodeAmplitudeBits(utcCorrection, 8, 4, 2, 1));

        // Bits 44: Unused
        packet.add(AmplitudeCode.AM_ZERO);

        // Bits 45 - 53: Year
        int year = calendar.get(Calendar.YEAR);
        int yearFinalDigits = year % 100;
        ArrayList<AmplitudeCode> yearData = encodeAmplitudeBits(yearFinalDigits, 80, 40, 20, 10, -1, 8, 4, 2, 1);
        packet.addAll(yearData);

        // Bit 54: Unused
        packet.add(AmplitudeCode.AM_ZERO);

        // Bit 55: Leap Year Indicator
        boolean leapYear = year % 400 == 0 || (year % 100 != 0 && year % 4 == 0);
        packet.add(leapYear ? AmplitudeCode.AM_ONE : AmplitudeCode.AM_ZERO);

        // Bit 56: Leap Second at End of Month
        packet.add(leapSecondIndicator ? AmplitudeCode.AM_ONE : AmplitudeCode.AM_ZERO);

        // Bit 57 - 58: DST Codes
        packet.add((0b10 & dstCode.value) != 0 ? AmplitudeCode.AM_ONE : AmplitudeCode.AM_ZERO);
        packet.add((0b01 & dstCode.value) != 0 ? AmplitudeCode.AM_ONE : AmplitudeCode.AM_ZERO);

        // Bit 59: Marker
        packet.add(AmplitudeCode.AM_MARKER);

        return packet;
    }

    private ArrayList<AmplitudeCode> encodeAmplitudeBits(int n, int... divisors) {
        ArrayList<AmplitudeCode> encodedBits = new ArrayList<>();
        for (int divisor : divisors) {

            if (divisor == 0)
                encodedBits.add(AmplitudeCode.AM_ZERO);
            else if (divisor == -1) {
                encodedBits.add(AmplitudeCode.AM_MARKER);
            } else {
                encodedBits.add(getAmplitudeBit(n, divisor));
            }
        }
        return encodedBits;
    }

    private AmplitudeCode getAmplitudeBit(int n, int divisor) {
        return (n / divisor) % 2 == 1 ? AmplitudeCode.AM_ONE : AmplitudeCode.AM_ZERO;
    }

    // Encoding Phase Data (Revised WWVB format)

    private enum PhaseCode {
        PH_ZERO, PH_ONE
    }

    private List<Byte> generatePhaseData(){
        List<Byte> packet = new LinkedList<>();

        Byte[] header = new Byte[]{
                0, 0, 1, 1, 1, 0, 1, 1, 0, 1, 0, 0, 0};
        packet.addAll(Arrays.asList(header));

        long minuteOfCentury = getMinuteOfCentury();
        List<Byte> minuteList = longToPhaseList(minuteOfCentury, 26);
        List<Byte> hammondCode = getHammondCode(minuteList);
        packet.addAll(hammondCode);

        List<Byte> minuteListWithReservedBits = addReservedBits(minuteList);
        packet.addAll(minuteListWithReservedBits);



        packet.add((byte)0);
        return packet;

    }

    private long getMinuteOfCentury(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.time);


        int yearsOfCentury = calendar.get(Calendar.YEAR) % 100;
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minuteOfHour = calendar.get(Calendar.MINUTE);

        int year = calendar.get(Calendar.YEAR);
        int century = year - yearsOfCentury;
        int numLeapDaysInCentury = year / 4;
        if (century % 400 != 0)
            numLeapDaysInCentury++;

        long minutesOfCentury = (525600 * yearsOfCentury)
                + ((dayOfYear + numLeapDaysInCentury) * 1440)
                + (hourOfDay * 60)
                + minuteOfHour;

        return minutesOfCentury;
    }

    private List<Byte> longToPhaseList(long toConvert, int numBits){
        List<Byte> phaseCodeList = new LinkedList<>();

        for (long mask = 1 << (numBits - 1); mask != 0; mask >>= 1){
            if ((mask & toConvert) != 0)
                phaseCodeList.add((byte) 1);
            else
                phaseCodeList.add((byte) 0);
        }

        return phaseCodeList;
    }

    // TODO: Implement Hammond Codes
    private List<Byte> getHammondCode(List<Byte> minuteList){
        /* byte[] minuteListCopy = new byte[32];
        List<Byte> hammondCode = new LinkedList<>();


        for (int i = 0, j = 0; i < 32; i++){
            if (Math.log(i + 1) % 1 != 0){
                minuteListCopy[i] = minuteList.get(j);
                j++;
            }
        }

        for (int mask = 1; mask = (mask << 1 + 1){
            for (int j = 0; j < 32; j++){
                if ()
            }
        }
        */

        Byte[] ret = new Byte[]{0, 0, 0, 0, 0, 0};
        return Arrays.asList(ret);

    }

    private List<Byte> addReservedBits(List<Byte> minuteList){
        minuteList.add(1, minuteList.get(25));
        minuteList.add(11, (byte) 0);
        minuteList.add(21, (byte) 1);

        return minuteList;
    }

    public enum DSTCode {
        NO_DST(0), DST_BEGINS(2), DST_IN_EFFECT(3), DST_ENDS(1);

        private final int value;
        DSTCode(int value){
            this.value = value;
        }
    }


    /**
     * The transmitter watches the simulation's clock to determine which byte it should
     *   transmit
     * */
    @Override
    public void update(Observable o, Object arg) {
        time = (long) arg;
        logger.info("Update Called");

        // Schedule every second
        if (time % (1000 * Simulation.MILLISECOND) != 0)
            return;

        List<AmplitudeCode> amplitudeData = generateAmplitudeData();
        long lastSecond = time;

        for (AmplitudeCode code: amplitudeData){
            // Schedule a downward edge at the beginning of each second
            simulation.scheduleEvent(new WWVBTimeEvent(lastSecond, WWVBCode.AMP_DEC), lastSecond);

            long highPowerReturnTime = lastSecond;

            // Find out when high power returns again
            if (code.equals(AmplitudeCode.AM_MARKER)){
                highPowerReturnTime += 800 * Simulation.MILLISECOND;
            } else if (code.equals(AmplitudeCode.AM_ONE)){
                highPowerReturnTime += 500 * Simulation.MILLISECOND;
            } else if (code.equals(AmplitudeCode.AM_ZERO)){
                highPowerReturnTime += 200 * Simulation.MILLISECOND;
            }

            simulation.scheduleEvent(new WWVBTimeEvent(highPowerReturnTime, WWVBCode.AMP_INC), highPowerReturnTime);

            lastSecond += 1000 * Simulation.MILLISECOND;
        }
    }
}

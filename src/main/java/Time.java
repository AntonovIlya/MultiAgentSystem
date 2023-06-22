public class Time {
    public static long iniTime = System.currentTimeMillis();
    public static long hourDuration = 12_000;

    public static int getCurrentHour() {
        return (int)((System.currentTimeMillis() - iniTime));
    }
    public static long CalcMillisTillNextHour(){
        return hourDuration - (System.currentTimeMillis() - iniTime) % hourDuration;
    }
}

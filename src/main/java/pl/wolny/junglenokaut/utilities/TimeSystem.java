package pl.wolny.junglenokaut.utilities;

public class TimeSystem {
    public static int getMinute(float time){
        return (int)time/60;
    }
    public static String getSecond(int min, int rest){
        int base = rest-(min*60);
        if(base<10){
            return "0" + base;
        }
        return String.valueOf(base);
    }
}

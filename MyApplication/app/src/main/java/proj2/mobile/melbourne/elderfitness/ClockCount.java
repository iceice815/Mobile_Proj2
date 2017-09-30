package proj2.mobile.melbourne.elderfitness;

import android.util.Log;

/**
 * Created by iceice on 30/9/17.
 */

public class ClockCount {

    private int second;
    private int minite;
    private int hour;
    public ClockCount(){
        this.second=0;
        this.minite=0;
        this.hour=0;

    }
    public void start(){
        second++;
        if(second==60){
            minite++;
            second=0;
        }
        if(minite==60){
            hour++;
            minite=0;
        }
    }
    public String getTime(){
        String sec ="00";
        String min ="00";
        String hou ="00";
        if(second<10){
            sec = "0"+second;
        }else{
            sec = ""+second;
        }
        if(minite<10){
            min = "0"+minite;
        }else{
            min = ""+minite;
        }
        if(hour<10){
            hou = "0"+hour;
        }else{
            hou = ""+hour;
        }
        return hou+":"+min+":"+sec;
    }
    public void clear(){
        second=0;
        minite=0;
        hour=0;
    }



}

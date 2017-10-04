package proj2.mobile.melbourne.elderfitness.util;

import java.util.TimerTask;


/**
 * Created by iceice on 3/10/17.
 */

/**
 * This abstract MyTimerTask extends from TimerTask in order to realize
 * automatic safety mechanism
 */
public abstract class MyTimerTask extends TimerTask {
    private int cnt=0;
    private int last_distance=0;



    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt++;
    }


    public int getLast_distance() {
        return last_distance;
    }

    public void setLast_distance(int last_distance) {
        this.last_distance = last_distance;
    }


}

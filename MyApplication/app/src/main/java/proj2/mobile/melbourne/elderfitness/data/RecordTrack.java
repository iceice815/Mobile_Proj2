package proj2.mobile.melbourne.elderfitness.data;

/**
 * Created by iceice on 11/9/17.
 */

public class RecordTrack {
    /**
     * Flieds
     */
    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    @com.google.gson.annotations.SerializedName("username")
    private String mUsername;

    @com.google.gson.annotations.SerializedName("date")
    private String mDate;

    @com.google.gson.annotations.SerializedName("distance")
    private int mDistance;

    @com.google.gson.annotations.SerializedName("flightsClimbed")
    private int mFlightsClimbed;

    @com.google.gson.annotations.SerializedName("calories")
    private double mColories;

    /**
     * constructor
     */
    public RecordTrack(){
    }

    /**
     * Overload
     */
    public RecordTrack(String name, String date, int distance, int flightsClimbed, double colories){
        this.mUsername = name;
        this.mDate = date;
        this.mDistance = distance;
        this.mFlightsClimbed = flightsClimbed;
        this.mColories = colories;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmUsername() {
        return mUsername;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public int getmDistance() {
        return mDistance;
    }

    public void setmDistance(int mDistance) {
        this.mDistance = mDistance;
    }

    public int getmFlightsClimbed() {
        return mFlightsClimbed;
    }

    public void setmFlightsClimbed(int mFlightsClimbed) {
        this.mFlightsClimbed = mFlightsClimbed;
    }

    public double getmColories() {
        return mColories;
    }

    public void setmColories(double mColories) {
        this.mColories = mColories;
    }

    /**
     * getter and setter
     */



    @Override
    public boolean equals(Object o) {
        return o instanceof RecordTrack && ((RecordTrack) o).mId == mId;
    }
}

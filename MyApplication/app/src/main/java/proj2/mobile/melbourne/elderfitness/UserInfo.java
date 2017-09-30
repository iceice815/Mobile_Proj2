package proj2.mobile.melbourne.elderfitness;

/**
 * Created by iceice on 9/9/17.
 */

public class UserInfo {
    /**
     * Flieds
     */
    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    @com.google.gson.annotations.SerializedName("username")
    private String mUsername;



    @com.google.gson.annotations.SerializedName("password")
    private String mPassword;

    @com.google.gson.annotations.SerializedName("emergencyNumber")
    private String mEmergencyNumber;


    /**
     * constructor
     */
    public UserInfo(){
    }

    /**
     * Overload
     * @param name
     * @param password
     * @param emergencyNumber
     */
    public UserInfo(String name,  String password,String emergencyNumber){
        this.setmUsername(name);
        this.setmPassword(password);
        this.setmEmergencyNumber(emergencyNumber);
    }

    /**
     * getter and setter
     */
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

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getmEmergencyNumber() {
        return mEmergencyNumber;
    }

    public void setmEmergencyNumber(String mEmergencyNumber) {
        this.mEmergencyNumber = mEmergencyNumber;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof UserInfo && ((UserInfo) o).mId == mId;
    }
}

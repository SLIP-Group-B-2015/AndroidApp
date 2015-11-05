package edu.smartdoor.imank.smartdoor;

import android.app.Application;

/**
 * Created by Marshall on 04/11/2015.
 */
public class SmartDoor extends Application {

    private String userid;

    public String getUserid()
    {
        return userid;
    }

    public void setUserid(String id)
    {
        userid = id;
    }

}

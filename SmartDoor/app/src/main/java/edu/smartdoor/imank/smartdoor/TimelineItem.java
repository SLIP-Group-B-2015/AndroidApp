package edu.smartdoor.imank.smartdoor;

/**
 * Created by s1132294 on 14/10/15.
 */
public class TimelineItem {

    public static final String LOG_TAG = TimelineItem.class.getSimpleName();

    private String raspberryID;
    private String eventID;
    private String eventName;
    private String time;
    private String senderName;
    private String description;

    public TimelineItem(String raspberryID, String eventID, String eventName, String time, String description, String senderName)
    {
        this.raspberryID = raspberryID;
        this.eventID = eventID;
        this.eventName = eventName;
        this.time = time;
        this.description = description;
        this.senderName = senderName;
    }

    public String getRaspberryID() {
        return raspberryID;
    }

    public void setRaspberryID(String raspberryID) {
        this.raspberryID = raspberryID;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getEventID()
    {
        return eventID;
    }

    public void setEventID(String eventID)
    {
        this.eventID = eventID;
    }

    public String getEventName()
    {
        return eventName;
    }

    public void setEventName(String eventName)
    {
        this.eventName = eventName;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public String getSenderName()
    {
        return senderName;
    }

    public void setSenderName(String senderName)
    {
        this.senderName = senderName;
    }

}

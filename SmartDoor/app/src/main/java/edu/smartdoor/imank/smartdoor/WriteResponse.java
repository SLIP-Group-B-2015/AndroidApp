package edu.smartdoor.imank.smartdoor;

/**
 * Created by imank on 11/3/2015.
 */
public class WriteResponse
{
    int status;
    String message;

    WriteResponse(int Status, String Message) {
        this.status = Status;
        this.message = Message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}

package edu.smartdoor.imank.smartdoor;

/**
 * Created by Iman Majumdar on 21-Nov-15.
 */
public class PI {

    private String PI_ID;
    private String name;

    public PI(String PI_ID, String name)
    {
        this.PI_ID = PI_ID;
        this.name = name;
    }

    public String getPI_ID() {
        return PI_ID;
    }

    public void setPI_ID(String PI_ID) {
        this.PI_ID = PI_ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

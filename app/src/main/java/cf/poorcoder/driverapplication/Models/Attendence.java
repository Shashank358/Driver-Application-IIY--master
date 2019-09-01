package cf.poorcoder.driverapplication.Models;

public class Attendence {

    String attendence,date;

    public Attendence(String attendence, String date) {
        this.attendence = attendence;
        this.date = date;
    }

    Attendence()
    {

    }

    public String getAttendence() {
        return attendence;
    }

    public void setAttendence(String attendence) {
        this.attendence = attendence;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

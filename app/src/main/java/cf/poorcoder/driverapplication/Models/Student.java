package cf.poorcoder.driverapplication.Models;

public class Student {
    String name;
    boolean attendence;
    String image;
    String from;
    String to;
    String reason;
    String driver_id;

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    String parent_id;

    Student()
    {

    }
    public Student(String name, boolean attendence, String image, String from, String to, String reason, String driver_id,String parent_id) {
        this.name = name;
        this.attendence = attendence;
        this.image = image;
        this.from = from;
        this.to = to;
        this.reason = reason;
        this.driver_id = driver_id;
        this.parent_id = parent_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAttendence() {
        return attendence;
    }

    public void setAttendence(boolean attendence) {
        this.attendence = attendence;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }
}

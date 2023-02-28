package fragment.bean;

public class MenuBean {
    private String week;
    private int day;
    private int year;
    private int month;
    private boolean check;

    public MenuBean(String week, int year, int day, int month, boolean check) {
        this.week = week;
        this.year = year;
        this.day = day;
        this.month = month;
        this.check = check;

    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}

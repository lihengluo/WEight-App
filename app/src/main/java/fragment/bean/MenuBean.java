package fragment.bean;

/**
 * @Author : kezhijie
 * @Email : 827112947@qq.com
 * @Date : on 2022-09-27 13:28.
 * @Description :描述
 */
public class MenuBean {
    private String week;
    private String day;
    private boolean check;

    public MenuBean(String week, String day, boolean check) {
        this.week = week;
        this.day = day;
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

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}

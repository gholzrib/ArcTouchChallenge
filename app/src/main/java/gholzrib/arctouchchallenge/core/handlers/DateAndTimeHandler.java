package gholzrib.arctouchchallenge.core.handlers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateAndTimeHandler {

    private SimpleDateFormat sdf;

    public DateAndTimeHandler() {
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT);
    }

    public DateAndTimeHandler(SimpleDateFormat sdf) {
        this.sdf = sdf;
    }

    private long getTimeDifference(String date) {

        Calendar today = Calendar.getInstance();
        Calendar thatDay = Calendar.getInstance();
        try {
            thatDay.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return   today.getTimeInMillis() - thatDay.getTimeInMillis();
    }

    public String getCurrentDate(){
        return sdf.format(Calendar.getInstance().getTime());
    }

    public long getTimeDifferenceInDays(String date) {
        return TimeUnit.MILLISECONDS.toDays(getTimeDifference(date));
    }
}

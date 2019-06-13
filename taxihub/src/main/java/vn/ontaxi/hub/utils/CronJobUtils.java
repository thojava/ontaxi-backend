package vn.ontaxi.hub.utils;

import vn.ontaxi.common.jpa.entity.EmailScheduler;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CronJobUtils {

    public static String buildCronJobs(Date datetime, EmailScheduler.RECURRING_TYPE recurringType) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm HH dd MM yyyy");
        String timeFormat = simpleDateFormat.format(datetime);
        String cronJobTemplate = "01 {0} {1} {2} {3} ? {4}";
        String [] timeSplits = timeFormat.split(" ");

        switch (recurringType) {
            case DAY:
                timeSplits[2] = "*";
                timeSplits[3] = "*";
                timeSplits[4] = "*";
                break;
            case WEEK:
                timeSplits[2] = timeSplits[2] + "/" + 7;
                timeSplits[3] = "*";
                timeSplits[4] = "*";
                break;
            case MONTH:
                timeSplits[3] = "*";
                timeSplits[4] = "*";
                break;
            case YEAR:
                timeSplits[4] = "*";
                break;
            case NONE:
                break;
        }

        return MessageFormat.format(cronJobTemplate, timeSplits[0], timeSplits[1], timeSplits[2], timeSplits[3], timeSplits[4]);

    }

}

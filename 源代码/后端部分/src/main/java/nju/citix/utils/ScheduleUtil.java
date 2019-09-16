package nju.citix.utils;


import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class ScheduleUtil {


    /**
     * 每天凌晨5点更新推荐列表
     */
    @Transactional
    @Scheduled(cron = "0 0 5 * * *")
    public void updateRecommendList() {
        try {
            PythonUtil.updateRecommendList(false);
        } catch (Exception e) {
            LoggerFactory.getLogger(ScheduleUtil.class).warn(e.getMessage());
            PythonUtil.updateDatabase(PythonUtil.readRecommendCSV(PythonUtil.RECOMMEND_CSV));
        }
    }


    /**
     * 每天凌晨5点更新推荐列表
     */
    @Scheduled(cron = "0 0 5 * * *")
    public void updateValue() {
        try {
            PythonUtil.renewFundValue();
        } catch (Exception e) {
            LoggerFactory.getLogger(ScheduleUtil.class).warn(e.getMessage());
            PythonUtil.updateDatabase(PythonUtil.readRecommendCSV(PythonUtil.RECOMMEND_CSV));
        }
    }
}

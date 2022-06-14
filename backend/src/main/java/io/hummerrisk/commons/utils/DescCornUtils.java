package io.hummerrisk.commons.utils;

/**
 * @author harris
 * @Desc 将cron表达式翻译成中文
 */
public class DescCornUtils {

    static final String ALL_ = "*";
    static final String MARK_ = "?";
    static final String TO_ = "-";
    static final String EVERY_ = "/";
    static final String AND_ = ",";

    public static String descCorn(String cronExp) throws Exception {
        String[] tmpCorns = cronExp.split(" ");
        StringBuffer sBuffer = new StringBuffer();
        if (tmpCorns.length > 7) {
            throw new Exception("请补全表达式,必须标准的cron表达式才能解析");
        }
        if (tmpCorns.length == 7) {
            // 解析年
            descYear(tmpCorns[6], sBuffer);
        }
        // 解析月
        descMonth(tmpCorns[4], sBuffer);

        // 解析周
        descWeek(tmpCorns[5], sBuffer);

        // 解析日
        descDay(tmpCorns[3], sBuffer);

        // 解析时
        descHour(tmpCorns[2], sBuffer);

        // 解析分
        descMintue(tmpCorns[1], sBuffer);

        // 解析秒
        descSecond(tmpCorns[0], sBuffer);
        sBuffer.append(" 执行");
        return sBuffer.toString();

    }

    /**
     * 描述: 翻译
     * @param s
     * @param sBuffer
     * @param unit
     */
    private static void desc(String s, StringBuffer sBuffer, String unit) throws Exception {
        if (s.equals("1/1")) {
            s = "*";
        }
        if (s.equals("0/0")) {
            s = "0";
        }
        if (ALL_.equals(s)) {
            sBuffer.append("每" + unit);
            return;
        }
        if (MARK_.equals(s)) {
            return;
        }
        if (s.contains(AND_)) {
            String[] arr = s.split(AND_);
            for (int i = 0; i < arr.length; i++) {
                if (arr[i].length() != 0) {
                    sBuffer.append("第" + arr[i] + unit + "和");
                }
            }
            sBuffer.deleteCharAt(sBuffer.length() - 1);
            sBuffer.append("的");
            return;
        }
        if (s.contains(TO_)) {
            String[] arr = s.split(TO_);
            if (arr.length != 2) {
                throw new Exception("表达式错误" + s);
            }
            sBuffer.append("从第" + arr[0] + unit + "到第" + arr[1] + unit + "每" + unit);
            sBuffer.append("的");
            return;
        }

        if (s.contains(EVERY_)) {
            String[] arr = s.split(EVERY_);
            if (arr.length != 2) {
                throw new Exception("表达式错误" + s);
            }
            if (arr[0].equals(arr[1]) || arr[0].equals("0")) {
                sBuffer.append("每" + arr[1] + unit);
            } else {
                sBuffer.append("每" + arr[1] + unit + "的第" + arr[0] + unit);
            }
            return;
        }
        sBuffer.append("第" + s + unit);
    }

    /**
     * 描述: 秒
     * @param s
     * @param sBuffer
     */
    private static void descSecond(String s, StringBuffer sBuffer) throws Exception {
        String unit = "秒";
        desc(s, sBuffer, unit);
    }

    /**
     * 描述: 分钟
     * @param s
     * @param sBuffer
     */
    private static void descMintue(String s, StringBuffer sBuffer) throws Exception {
        desc(s, sBuffer, "分钟");
    }

    /**
     * 描述: 小时
     * @param s
     * @param sBuffer
     */
    private static void descHour(String s, StringBuffer sBuffer) throws Exception {
        desc(s, sBuffer, "小时");
    }

    /**
     * 描述: 天
     * @param s
     * @param sBuffer
     */
    private static void descDay(String s, StringBuffer sBuffer) throws Exception {
        desc(s, sBuffer, "天");
    }

    /**
     * 描述: 周
     * @param s
     * @param sBuffer
     */
    private static void descWeek(String s, StringBuffer sBuffer) throws Exception {
        desc(turnWeek(s), sBuffer, "周");
    }

    private static String turnWeek(String week){
        return week.replace("MON", "1").replace("TUE", "2").replace("WED", "3").replace("THU", "4")
                .replace("FRI", "5").replace("SAT", "6").replace("SUN", "7");
    }

    /**
     * 描述: 月
     * @param s
     * @param sBuffer
     */
    private static void descMonth(String s, StringBuffer sBuffer) throws Exception {
        desc(s, sBuffer, "月");
    }

    /**
     * 描述: 年
     * @param s
     * @param sBuffer
     */
    private static void descYear(String s, StringBuffer sBuffer) throws Exception {
        desc(s, sBuffer, "年");
    }

}

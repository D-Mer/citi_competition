package nju.citix.utils;

import nju.citix.po.Recommend;
import nju.citix.po.TradeRecord;
import nju.citix.service.fund.FundForService;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class PythonUtil {
    public static String EXE;
    public static final String SEP = System.getProperty("file.separator");
    public static final String PYPATH = "." + SEP + "python" + SEP;//TODO : 这个目录有点问题，打包后路径应该是 "." + SEP + "python" + SEP
    public static final String MAIN_PY = "main.py";
    public static final String TEST_MAIN_PY = "test_main.py";
    public static final String RECOMMEND_CSV = "RecommendRes.csv";
    public static final String TEST_RECOMMEND_CSV = "TestRecommendRes.csv";
    public static final String TRADE_RECORD_CSV = "交易记录.csv";
    public static final String TEST_TRADE_RECORD_CSV = "交易记录_小.csv";
    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final String SUCCESS_FLAG = "Over!";

    public static final String EXEC_ERROR = "运行推荐算法失败";
    public static final String ADD_TRADE_RECORD_ERROR = "添加消费记录失败";
    public static final String READ_TRADE_RECORD_ERROR = "读取消费记录失败";
    public static final String WRITE_TRADE_RECORD_ERROR = "写入消费记录失败";
    public static final String TOO_LONG_TO_UPDATE_RECOMMEND_ERROR = "经过4h仍未跑完推荐更新";
    private static final String READ_RECOMMEND_CSV_ERROR = "读取recommend.csv失败";

    private static FundForService fundForService;

    public PythonUtil(FundForService fundForService) {
        final String property = System.getProperty("os.name");
        if (property.contains("indows")) {
            EXE = "python";
        } else {
            EXE = "python3";
        }
        PythonUtil.fundForService = fundForService;
    }


    public static boolean addTradeRecord(TradeRecord record) {
        LinkedList<TradeRecord> records = new LinkedList<>();
        records.add(record);
        return addTradeRecords(records);
    }

    public static boolean addTradeRecords(List<TradeRecord> records) {
        LinkedList<String> lines;
        try {
            lines = getFile(TRADE_RECORD_CSV);
        } catch (Exception e) {
            throw new PythonException(READ_TRADE_RECORD_ERROR);
        }
        if (!appendCSV(lines, records)) throw new PythonException(ADD_TRADE_RECORD_ERROR);
        if (!write(TRADE_RECORD_CSV, lines)) throw new PythonException(WRITE_TRADE_RECORD_ERROR);
        return true;
    }

    public static LinkedList<String> getFile(String filename) throws Exception {
        LinkedList<String> lines = new LinkedList<>();
        File f = new File(PYPATH + filename);
        FileInputStream fis = new FileInputStream(f);
        InputStreamReader read = new InputStreamReader(fis, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(read);
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        reader.close();
        read.close();
        fis.close();
        return lines;
    }

    public static boolean appendCSV(LinkedList<String> fileLines, List<TradeRecord> records) {
        String last = fileLines.getLast();
        int index = Integer.parseInt(last.split(",")[0]);
        for (TradeRecord t : records) {
            fileLines.add((index + 1) + ","
                    + t.getCustomerId() + ","
                    + dateTimeFormatter.format(t.getTradeTime()) + ","
                    + t.getAmount().setScale(1, RoundingMode.DOWN) + ","
                    + t.getFundType() + ","
                    + t.getFundCode());
            index++;
        }
        return true;
    }

    public static boolean write(String fileName, List<String> lines) {
        try {
            File file = new File(PYPATH + fileName);
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            BufferedWriter bw = new BufferedWriter(osw);
            for (String s : lines) {
                bw.write(s);
                bw.newLine();
            }
            bw.close();
            osw.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void updateRecommendList(boolean isTest) throws Exception {
        boolean execSuccess;
        LinkedList<Recommend> recommendList;
        if (isTest) {
            execSuccess = exec(TEST_MAIN_PY);
            recommendList = readRecommendCSV(TEST_RECOMMEND_CSV);
        } else {
            execSuccess = exec(MAIN_PY);
            recommendList = readRecommendCSV(RECOMMEND_CSV);
        }
        if (!execSuccess) throw new PythonException(EXEC_ERROR);
        updateDatabase(recommendList);
    }

    public static void updateDatabase(LinkedList<Recommend> recommendList) {
        fundForService.deleteRecommendList();
        List<Recommend> part;
        for (int i = 0; i < recommendList.size(); i += 10000) {
            part = recommendList.subList(i, Math.min((i + 10000), recommendList.size()));
            fundForService.insertRecommendList(part);
        }
    }

    public static boolean exec(String filename) throws Exception {
        String[] cmdArgs = new String[]{EXE, "-u", PYPATH + filename};// -u是取消py的print缓存
        Process process = Runtime.getRuntime().exec(cmdArgs);
        InputStream pis = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(pis, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(isr);
        Timer timer = new Timer();
        String flag;
        final ArrayList<Boolean> threadFlag = new ArrayList<>();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    bufferedReader.close();
                    isr.close();
                    pis.close();
                    process.getOutputStream().close();
                    process.destroy();
                    threadFlag.add(false);
                } catch (Exception e) {
                    throw new PythonException(TOO_LONG_TO_UPDATE_RECOMMEND_ERROR);
                }
            }
        };
        timer.schedule(task, 14400000);//在4h后杀死进程
        while (!SUCCESS_FLAG.equals((flag = bufferedReader.readLine()))) {
            if (flag == null || "null".equals(flag)) {
                System.out.println("执行程序出现错误" + filename);
                System.out.println(Calendar.getInstance());
                break;
            }
            System.out.println(flag);
        }
        bufferedReader.close();
        isr.close();
        pis.close();
        process.getOutputStream().close();
        task.cancel();
        timer.cancel();
        return threadFlag.isEmpty();
    }

    public static LinkedList<Recommend> readRecommendCSV(String filename) throws PythonException {
        LinkedList<String> lines;
        try {
            lines = getFile(filename);
        } catch (Exception e) {
            throw new PythonException("文件读取失败：" + filename);
        }
        LinkedList<Recommend> recommendList = new LinkedList<>();
        String[] temp;
        Iterator i = lines.iterator();
        if (!i.next().equals("User,RecommendFundCode")) throw new PythonException(READ_RECOMMEND_CSV_ERROR);
        try {
            while (i.hasNext()) {
                temp = ((String) i.next()).split(",");
                temp[1] = temp[1].split("\\.")[0];
                int x = 6 - temp[1].length();
                for (int j = 0; j < x; j++) {
                    temp[1] = "0" + temp[1];
                }
                recommendList.add(new Recommend(Integer.parseInt(temp[0]), temp[1]));
            }
        } catch (Exception e) {
            throw new PythonException(READ_RECOMMEND_CSV_ERROR);
        }
        return recommendList;
    }

    /**
     * 在调用Python过程中出现的异常
     */
    public static class PythonException extends RuntimeException {
        public PythonException() {
            super();
        }

        public PythonException(String message) {
            super(message);
        }
    }

    public static void renewFundValue(){
        try {
            exec("daily_new_value.py");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

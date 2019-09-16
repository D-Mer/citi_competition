package nju.citix.utils;

import nju.citix.dao.fund.FundMapper;
import nju.citix.po.*;
import nju.citix.service.customer.CustomerService;
import nju.citix.service.fund.FundService;
import nju.citix.vo.UserForm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static nju.citix.utils.PythonUtil.*;

/**
 * @author jiang hui
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class PythonUtilTest {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private FundService fundService;
    @Resource
    private FundMapper fundMapper;

    private Manager newManager;

    private UserForm newUser = new UserForm();
    private Customer newCustomer;
    private String newCustomerToken;

    private String requestIp = "192.168.10.12";
    private LocalDateTime testStartTime = LocalDateTime.now();

    @Before
    public void setUp() throws Exception {
        String username = "123abc";
        String password = "123adsfA";
        String email = "814775538@qq.com";
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setEmail(email);
        newCustomer = customerService.register(newUser);
        newCustomer = customerService.login(username, password, requestIp);
        newCustomerToken = JWTUtil.getToken(newCustomer);
    }

    @Test
    public void addTradeRecordSuccess() throws Exception {
        BigDecimal amount = BigDecimal.valueOf(132.987);
        Fund fund = fundMapper.selectFundByCode("000001");
        LinkedList<String> before = PythonUtil.getFile(TRADE_RECORD_CSV);
        Assert.assertNotNull(fund);
        TradeRecord record = new TradeRecord(newCustomer.getCustomerId(), testStartTime, amount, fund.getType(), fund.getFundCode());
        Assert.assertTrue(PythonUtil.addTradeRecord(record));
        LinkedList<String> after = PythonUtil.getFile(TRADE_RECORD_CSV);
        PythonUtil.write(TRADE_RECORD_CSV, before);
        Assert.assertEquals(before.size() + 1, after.size());
        String[] args = after.getLast().split(",");
        Assert.assertEquals(after.size() - 2, Integer.parseInt(args[0]));
        Assert.assertEquals((int) newCustomer.getCustomerId(), Integer.parseInt(args[1]));
        Assert.assertEquals(dateTimeFormatter.format(testStartTime), args[2]);
        Assert.assertEquals(0, amount.setScale(1, RoundingMode.DOWN).compareTo(BigDecimal.valueOf(Double.parseDouble(args[3]))));
        Assert.assertEquals(fund.getType(), args[4]);
        Assert.assertEquals(fund.getFundCode(), args[5]);
    }

    @Test
    public void addTradeRecordsSuccess() throws Exception {
        BigDecimal amount1 = BigDecimal.valueOf(132.2).setScale(1, RoundingMode.DOWN);
        BigDecimal amount2 = BigDecimal.valueOf(133.1).setScale(1, RoundingMode.DOWN);
        Fund fund1 = fundMapper.selectFundByCode("000001");
        Fund fund2 = fundMapper.selectFundByCode("000003");
        LinkedList<String> before = PythonUtil.getFile(TRADE_RECORD_CSV);
        Assert.assertNotNull(fund1);
        Assert.assertNotNull(fund2);
        LinkedList<TradeRecord> records = new LinkedList<>();
        TradeRecord record1 = new TradeRecord(newCustomer.getCustomerId(), testStartTime, amount1, fund1.getType(), fund1.getFundCode());
        TradeRecord record2 = new TradeRecord(newCustomer.getCustomerId(), testStartTime, amount2, fund2.getType(), fund2.getFundCode());
        records.add(record1);
        records.add(record2);
        Assert.assertTrue(PythonUtil.addTradeRecords(records));
        LinkedList<String> after = PythonUtil.getFile(TRADE_RECORD_CSV);
        PythonUtil.write(TRADE_RECORD_CSV, before);
        Assert.assertEquals(before.size() + 2, after.size());
        String[] args = after.getLast().split(",");
        Assert.assertEquals(after.size() - 2, Integer.parseInt(args[0]));
        Assert.assertEquals((int) newCustomer.getCustomerId(), Integer.parseInt(args[1]));
        Assert.assertEquals(dateTimeFormatter.format(testStartTime), args[2]);
        Assert.assertEquals(0, amount2.compareTo(BigDecimal.valueOf(Double.parseDouble(args[3])).setScale(1, RoundingMode.DOWN)));
        Assert.assertEquals(fund2.getType(), args[4]);
        Assert.assertEquals(fund2.getFundCode(), args[5]);
    }

    @Test
    public void readAndUpdateRecommendSuccess() throws Exception {
        File testRecommendFile = new File(PYPATH + "testRecommend.csv");
        if (!testRecommendFile.exists()) {
            Assert.assertTrue(testRecommendFile.createNewFile());
        }
        FileOutputStream fos = new FileOutputStream(testRecommendFile);
        OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
        BufferedWriter bw = new BufferedWriter(osw);
        String[] lines = {
                "User,RecommendFundCode",
                "9999999,000011",
                "9999999,000123"
        };
        for (String s : lines) {
            bw.write(s);
            bw.newLine();
        }
        bw.close();
        osw.close();
        fos.close();
        LinkedList<Recommend> recommendList = PythonUtil.readRecommendCSV("testRecommend.csv");
        Assert.assertTrue(testRecommendFile.delete());
        fundMapper.deleteRecommendList();
        fundMapper.insertRecommendList(recommendList);
        List<Fund> funds = fundMapper.selectRecommendListByUserId(9999999);
        Fund fund1 = fundMapper.selectFundByCode("000011");
        Fund fund2 = fundMapper.selectFundByCode("000123");
        Assert.assertEquals(fund1, funds.get(0));
        Assert.assertEquals(fund2, funds.get(1));
    }

    @Test
    public void updateRecommendListSuccess() throws Exception {
        PythonUtil.updateRecommendList(true);
        List<Fund> funds = fundMapper.selectRecommendListByUserId(1);
        Assert.assertFalse(funds.isEmpty());
    }
}
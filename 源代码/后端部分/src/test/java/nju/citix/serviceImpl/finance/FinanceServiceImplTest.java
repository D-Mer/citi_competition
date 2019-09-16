package nju.citix.serviceImpl.finance;

import nju.citix.controller.finance.FinanceController;
import nju.citix.dao.customer.CustomerCompositionMapper;
import nju.citix.dao.customer.CustomerMapper;
import nju.citix.dao.finance.FinanceMapper;
import nju.citix.dao.fund.FundCompositionMapper;
import nju.citix.po.Customer;
import nju.citix.po.CustomerComposition;
import nju.citix.po.FinanceRecord;
import nju.citix.po.FundComposition;
import nju.citix.service.customer.CustomerService;
import nju.citix.service.finance.FinanceService;
import nju.citix.utils.AlipayUtil;
import nju.citix.utils.PythonUtil;
import nju.citix.vo.UserForm;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static nju.citix.utils.PythonUtil.TRADE_RECORD_CSV;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class FinanceServiceImplTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Resource
    private CustomerService customerService;
    @Resource
    private CustomerMapper customerMapper;
    @Resource
    private FinanceService financeService;
    @Resource
    private FinanceMapper financeMapper;
    @Resource
    private CustomerCompositionMapper customerCompositionMapper;
    @Resource
    private FundCompositionMapper fundCompositionMapper;

    private UserForm newUser = new UserForm();
    private Customer newCustomer;
    private String requestIp = "192.168.10.12";
    private LocalDateTime testStartTime = LocalDateTime.now();

    private LinkedList<String> before;

    @Before
    public void setUp() throws Exception {
        before = PythonUtil.getFile(TRADE_RECORD_CSV);
        String username = "123abc";
        String password = "123adsfA";
        String email = "814775538@qq.com";
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setEmail(email);
        newCustomer = customerService.register(newUser);
        newCustomer = customerService.login(username, password, requestIp);
    }

    @After
    public void recover(){
        PythonUtil.write(TRADE_RECORD_CSV, before);
    }

    @Test
    public void createRechargeWithNetworkSuccess() {
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble("123.00"));
        FinanceRecord record = financeService.recharge(newCustomer.getCustomerId(), amount, AlipayUtil.getOutBizNum(newCustomer.getCustomerId()), true);
        Assert.assertNotNull(record.getId());
        Assert.assertEquals(newCustomer.getCustomerId(), record.getCustomerId());
        Assert.assertEquals(amount, record.getAmount());
        Assert.assertEquals(FinanceServiceImpl.UNFINISHED, record.getState());
        Assert.assertEquals(FinanceServiceImpl.RECHARGE_REMARK, record.getRemark());
        Assert.assertNotNull(record.getTradeNum());
        Assert.assertTrue(Duration.between(testStartTime, record.getTradeTime()).toMinutes() < 5);
    }

    @Test
    public void createRechargeWithoutNetworkSuccess() {
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble("123.00"));
        FinanceRecord record = financeService.recharge(newCustomer.getCustomerId(), amount, AlipayUtil.getOutBizNum(newCustomer.getCustomerId()), false);
        Assert.assertNotNull(record.getId());
        Assert.assertEquals(newCustomer.getCustomerId(), record.getCustomerId());
        Assert.assertEquals(amount, record.getAmount());
        Assert.assertEquals(FinanceServiceImpl.FINISHED, record.getState());
        Assert.assertEquals(FinanceServiceImpl.RECHARGE_REMARK, record.getRemark());
        Assert.assertNotNull(record.getTradeNum());
        Assert.assertTrue(Duration.between(testStartTime, record.getTradeTime()).toMinutes() < 5);
    }

    @Test
    public void completeOnlineTradeSuccess() {
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble("123.00"));
        FinanceRecord record = financeService.recharge(newCustomer.getCustomerId(), amount, AlipayUtil.getOutBizNum(newCustomer.getCustomerId()), true);
        Assert.assertTrue(financeService.completeTrade(record.getTradeNum()));
        Assert.assertNotNull(record.getId());
        record = financeMapper.selectById(record.getId());
        Assert.assertEquals(newCustomer.getCustomerId(), record.getCustomerId());
        Assert.assertEquals(0, record.getAmount().compareTo(amount));
        Assert.assertEquals(FinanceServiceImpl.FINISHED, record.getState());
        Assert.assertEquals(FinanceServiceImpl.RECHARGE_REMARK, record.getRemark());
        Assert.assertNotNull(record.getTradeNum());
    }

    @Test
    public void completeFinishedTradeSuccess() {
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble("123.00"));
        FinanceRecord record = financeService.recharge(newCustomer.getCustomerId(), amount, AlipayUtil.getOutBizNum(newCustomer.getCustomerId()), false);
        Assert.assertTrue(financeService.completeTrade(record.getTradeNum()));
        Assert.assertNotNull(record.getId());
        record = financeMapper.selectById(record.getId());
        Assert.assertEquals(newCustomer.getCustomerId(), record.getCustomerId());
        Assert.assertEquals(0, record.getAmount().compareTo(amount));
        Assert.assertEquals(FinanceServiceImpl.FINISHED, record.getState());
        Assert.assertEquals(FinanceServiceImpl.RECHARGE_REMARK, record.getRemark());
        Assert.assertNotNull(record.getTradeNum());
    }

    @Test
    public void completeTradeNonexistentTradeError() {
        Assert.assertFalse(financeService.completeTrade("1999-1-1-12-00-00-01-2"));
    }


    @Test
    public void confirmFundCompositionSuccess() {
        Map<Integer, BigDecimal> composition = new HashMap<>();
        composition.put(1, new BigDecimal(0.45));
        composition.put(2, new BigDecimal(0.55));
        Integer compositionId = financeService.confirmFundComposition(1, composition);
        List<FundComposition> fundComposition = fundCompositionMapper.selectFundCompositionByCompositionId(compositionId);
        Assert.assertEquals(2, fundComposition.size());
        Assert.assertEquals(new Integer(1), fundComposition.get(0).getFundId());
        Assert.assertEquals(new Integer(2), fundComposition.get(1).getFundId());
    }

    @Test
    public void buyFundCompositionSuccess1() throws Exception {
        BigDecimal money = new BigDecimal(1000 * 10000);
        FinanceRecord record = financeService.recharge(newCustomer.getCustomerId(), money, AlipayUtil.getOutBizNum(newCustomer.getCustomerId()), false);
        financeService.completeTrade(record.getTradeNum());
        Map<Integer, BigDecimal> composition = new HashMap<>();
        composition.put(1, new BigDecimal(0.45));
        composition.put(2, new BigDecimal(0.55));
        Integer compositionId = financeService.confirmFundComposition(newCustomer.getCustomerId(), composition);
        BigDecimal purchaseAmount = new BigDecimal(100 * 10000);
        boolean re = financeService.buyFundComposition(newCustomer.getCustomerId(), compositionId, purchaseAmount);
        CustomerComposition customerComposition = customerCompositionMapper.selectCompositionOfCustomerById(compositionId);
        List<FundComposition> fundCompositions = fundCompositionMapper.selectFundCompositionByCompositionId(compositionId);

        Assert.assertTrue(re);
        Assert.assertEquals(100 * 10000, customerComposition.getPurchaseAmount().intValue());
        Assert.assertTrue(Math.abs(45 * 10000 * (1 - 1 / (1 + 0.015)) - fundCompositions.get(0).getPurchaseLoss().doubleValue()) <= 1);
    }

    @Test
    public void buyFundCompositionSuccess2() throws Exception{
        BigDecimal money = new BigDecimal(10000 * 10000);
        FinanceRecord record = financeService.recharge(newCustomer.getCustomerId(), money, AlipayUtil.getOutBizNum(newCustomer.getCustomerId()), false);
        financeService.completeTrade(record.getTradeNum());
        Map<Integer, BigDecimal> composition = new HashMap<>();
        composition.put(1, new BigDecimal(0.1));
        composition.put(2, new BigDecimal(0.9));
        Integer compositionId = financeService.confirmFundComposition(newCustomer.getCustomerId(), composition);
        BigDecimal purchaseAmount = new BigDecimal(10000 * 10000);
        boolean re = financeService.buyFundComposition(newCustomer.getCustomerId(), compositionId, purchaseAmount);
        CustomerComposition customerComposition = customerCompositionMapper.selectCompositionOfCustomerById(compositionId);
        List<FundComposition> fundCompositions = fundCompositionMapper.selectFundCompositionByCompositionId(compositionId);

        Assert.assertTrue(re);
        Assert.assertEquals(10000 * 10000, customerComposition.getPurchaseAmount().intValue());
        Assert.assertTrue((1000 - fundCompositions.get(0).getPurchaseLoss().doubleValue()) <= 0.001);
    }

    @Test
    public void sellFundCompositionSuccess() throws Exception {
        Map<Integer, BigDecimal> composition = new HashMap<>();
        composition.put(1, new BigDecimal(0.45));
        composition.put(2, new BigDecimal(0.55));
        Integer compositionId = financeService.confirmFundComposition(1, composition);
        BigDecimal purchaseAmount = new BigDecimal(100 * 10000);
        LinkedList<String> before = PythonUtil.getFile(TRADE_RECORD_CSV);
        financeService.buyFundComposition(1, compositionId, purchaseAmount);
        PythonUtil.write(TRADE_RECORD_CSV, before);
        CustomerComposition c = financeService.sellFundComposition(compositionId);

        Assert.assertTrue(c.getRequestTime() != null);
    }

    @Test
    public void sellFundCompositionNoExisted() {
        thrown.expectMessage(FinanceController.PARAMETER_ERROR);
        financeService.sellFundComposition(-1);
    }

    @Test
    public void sellFundCompositionRepeat() throws Exception {
        thrown.expectMessage(FinanceController.REPEATED_SOLD_REQUEST);
        Map<Integer, BigDecimal> composition = new HashMap<>();
        composition.put(1, new BigDecimal(0.45));
        composition.put(2, new BigDecimal(0.55));
        Integer compositionId = financeService.confirmFundComposition(1, composition);
        BigDecimal purchaseAmount = new BigDecimal(100 * 10000);
        LinkedList<String> before = PythonUtil.getFile(TRADE_RECORD_CSV);
        financeService.buyFundComposition(1, compositionId, purchaseAmount);
        PythonUtil.write(TRADE_RECORD_CSV, before);
        financeService.sellFundComposition(compositionId);
        financeService.sellFundComposition(compositionId);
    }

    @Test
    public void sellFundCompositionAlreadySold() throws Exception {
        thrown.expectMessage(FinanceController.ALREADY_SOLD);
        Map<Integer, BigDecimal> composition = new HashMap<>();
        composition.put(1, new BigDecimal(0.45));
        composition.put(2, new BigDecimal(0.55));
        Integer compositionId = financeService.confirmFundComposition(1, composition);
        BigDecimal purchaseAmount = new BigDecimal(100 * 10000);
        LinkedList<String> before = PythonUtil.getFile(TRADE_RECORD_CSV);
        financeService.buyFundComposition(1, compositionId, purchaseAmount);
        PythonUtil.write(TRADE_RECORD_CSV, before);
        CustomerComposition c = financeService.sellFundComposition(compositionId);
        c.setSoldTime(LocalDateTime.now());
        customerCompositionMapper.updateCompositionOfCustomer(c);
        financeService.sellFundComposition(compositionId);
    }
}
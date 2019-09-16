package nju.citix.service.finance;

import nju.citix.dao.customer.CustomerMapper;
import nju.citix.dao.finance.FinanceMapper;
import nju.citix.po.Customer;
import nju.citix.po.FinanceRecord;
import nju.citix.service.customer.CustomerService;
import nju.citix.vo.UserForm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

import static nju.citix.serviceImpl.finance.FinanceServiceImpl.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class FinanceServiceForBLTest {
    @Resource
    private FinanceServiceForBL financeServiceForBL;
    @Resource
    private FinanceMapper financeMapper;
    @Resource
    private CustomerService customerService;
    @Resource
    private CustomerMapper customerMapper;
    private UserForm newUser = new UserForm();
    private Customer newCustomer;
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
    }

    @Test
    public void addBuyRecordSuccess(){
        newCustomer.setBalance(BigDecimal.valueOf(Double.parseDouble("1000.00")));
        customerMapper.updateByPrimaryKeySelective(newCustomer);
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble("123.00"));
        FinanceRecord record = new FinanceRecord();
        try {
            record = financeServiceForBL.addBalanceChangeRecord(newCustomer.getCustomerId(), amount, BUY_REMARK);
        }catch (Exception e){
            Assert.fail();
        }
        FinanceRecord expected = new FinanceRecord(newCustomer.getCustomerId(), BUY_REMARK, amount, FINISHED);
        Customer customer = customerMapper.selectByPrimaryKey(newCustomer.getCustomerId());
        Assert.assertNotNull(record.getId());
        Assert.assertNotNull(record.getTradeNum());
        Assert.assertEquals(expected.getCustomerId(), record.getCustomerId());
        Assert.assertEquals(expected.getAmount().negate(), record.getAmount());
        Assert.assertEquals(expected.getState(), record.getState());
        Assert.assertEquals(expected.getRemark(), record.getRemark());
        Assert.assertTrue(Duration.between(expected.getTradeTime(), record.getTradeTime()).toMinutes() < 5);
        Assert.assertNotNull(customer);
        Assert.assertEquals(0, BigDecimal.valueOf(Double.parseDouble("877.00")).compareTo(customer.getBalance()));
    }

    @Test
    public void addRedemptionRecordSuccess(){
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble("123.00"));
        FinanceRecord record = new FinanceRecord();
        try {
            record = financeServiceForBL.addBalanceChangeRecord(newCustomer.getCustomerId(), amount, REDEMPTION_REMARK);
        }catch (Exception e){
            Assert.fail();
        }
        FinanceRecord expected = new FinanceRecord(newCustomer.getCustomerId(), REDEMPTION_REMARK, amount, FINISHED);
        Customer customer = customerMapper.selectByPrimaryKey(newCustomer.getCustomerId());
        Assert.assertNotNull(record.getId());
        Assert.assertNotNull(record.getTradeNum());
        Assert.assertEquals(expected.getCustomerId(), record.getCustomerId());
        Assert.assertEquals(expected.getAmount(), record.getAmount());
        Assert.assertEquals(expected.getState(), record.getState());
        Assert.assertEquals(expected.getRemark(), record.getRemark());
        Assert.assertTrue(Duration.between(expected.getTradeTime(), record.getTradeTime()).toMinutes() < 5);
        Assert.assertNotNull(customer);
        Assert.assertEquals(0, amount.compareTo(customer.getBalance()));
    }

    @Test
    public void addBalanceChangeRecordRemarkError(){
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble("123.00"));
        try {
            financeServiceForBL.addBalanceChangeRecord(newCustomer.getCustomerId(), amount, "bad remark");
        }catch (Exception e){
            Assert.assertEquals(REMARK_ERROR, e.getMessage());
            return;
        }
        Assert.fail();
    }

    public void addBalanceChangeRecordAmountError(){
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble("-123.00"));
        try {
            financeServiceForBL.addBalanceChangeRecord(newCustomer.getCustomerId(), amount, BUY_REMARK);
        }catch (Exception e){
            Assert.assertEquals(AMOUNT_NON_POSITIVE_ERROR, e.getMessage());
            return;
        }
        Assert.fail();
    }

    @Test
    public void addBalanceChangeRecordInsufficientError(){
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble("123.00"));
        try {
            financeServiceForBL.addBalanceChangeRecord(newCustomer.getCustomerId(), amount, BUY_REMARK);
        }catch (Exception e){
            Assert.assertEquals(INSUFFICIENT_BALANCE_ERROR, e.getMessage());
            return;
        }
        Assert.fail();
    }

    @Test
    public void addBalanceChangeRecordCustomerNonexistentError(){
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble("123.00"));
        try {
            financeServiceForBL.addBalanceChangeRecord(-1, amount, BUY_REMARK);
        }catch (Exception e){
            Assert.assertEquals(USER_NONEXISTENT_ERROR, e.getMessage());
            return;
        }
        Assert.fail();
    }
}
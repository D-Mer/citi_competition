package nju.citix.controller.finance;

import com.alibaba.fastjson.JSON;
import nju.citix.dao.customer.CustomerMapper;
import nju.citix.dao.finance.FinanceMapper;
import nju.citix.dao.fund.FundCompositionMapper;
import nju.citix.po.Customer;
import nju.citix.po.FinanceRecord;
import nju.citix.service.finance.FinanceService;
import nju.citix.service.finance.FinanceServiceForBL;
import nju.citix.utils.AlipayUtil;
import nju.citix.utils.JWTUtil;
import nju.citix.utils.PythonUtil;
import nju.citix.vo.UserForm;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static nju.citix.controller.finance.FinanceController.*;
import static nju.citix.serviceImpl.finance.FinanceServiceImpl.*;
import static nju.citix.utils.PythonUtil.TRADE_RECORD_CSV;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class FinanceControllerTest {

    @Resource
    private WebApplicationContext wac;
    private MockMvc mvc;
    private MockHttpSession session;

    @Resource
    private FinanceService financeService;
    @Resource
    private FinanceServiceForBL financeServiceForBL;
    @Resource
    private FinanceMapper financeMapper;
    @Resource
    private CustomerMapper customerMapper;
    @Resource
    private FundCompositionMapper fundCompositionMapper;

    private Customer newCustomer;
    private String newCustomerToken;

    private LinkedList<String> before;

    @Before
    public void setUp() throws Exception {
        before = PythonUtil.getFile(TRADE_RECORD_CSV);
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        session = new MockHttpSession();
        String password = "123ad你好fA@";
        UserForm newUser = new UserForm();
        newUser.setPassword(password);
        String username = "2___sadf";
        String email = "814775538@qq.com";
        newUser.setUsername(username);
        newUser.setEmail(email);
        mvc.perform(MockMvcRequestBuilders.post("/user/register")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSON.toJSONString(newUser))
                .accept(MediaType.APPLICATION_JSON_UTF8)
        );
        newCustomer = customerMapper.selectByEmail(email);
        assert newCustomer != null;
        newCustomerToken = JWTUtil.getToken(newCustomer);
    }

    @After
    public void recover(){
        PythonUtil.write(TRADE_RECORD_CSV, before);
    }

    @Test
    public void createRechargeSuccess() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/finance/recharge")
                .param("customerId", "" + newCustomer.getCustomerId())
                .param("amount", "1.00")
                .session(session)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void createRechargeEmptyParam1() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/finance/recharge")
//                .param("customerId", "" + newCustomer.getCustomerId())
                        .param("amount", "1.00")
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .session(session)
                        .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void createRechargeEmptyParam2() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/finance/recharge")
                        .param("customerId", "" + newCustomer.getCustomerId())
//                        .param("amount", "100.00")
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .session(session)
                        .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void createRechargeErrorParam1() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/finance/recharge")
                .param("customerId", "" + newCustomer.getCustomerId())
                .param("amount", "-10.00")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(PARAMETER_ERROR));
    }

    @Test
    public void createRechargeErrorParam2() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/finance/recharge")
                .param("customerId", "-1")
                .param("amount", "10.00")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(PARAMETER_ERROR));
    }

    @Test
    public void withdrawSuccess() throws Exception {
        newCustomer.setBalance(BigDecimal.valueOf(1000));
        customerMapper.updateByPrimaryKeySelective(newCustomer);
        mvc.perform(MockMvcRequestBuilders.post("/finance/withdraw")
                .param("customerId", "" + newCustomer.getCustomerId())
                .param("amount", "15.00")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(WITHDRAW_SUCCESS));
    }

    @Test
    public void withdrawEmptyParam1() throws Exception {
        newCustomer.setBalance(BigDecimal.valueOf(1000));
        mvc.perform(MockMvcRequestBuilders.post("/finance/withdraw")
//                .param("customerId", "" + newCustomer.getCustomerId())
                        .param("amount", "10.00")
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .session(session)
                        .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void withdrawEmptyParam2() throws Exception {
        newCustomer.setBalance(BigDecimal.valueOf(1000));
        mvc.perform(MockMvcRequestBuilders.post("/finance/withdraw")
                        .param("customerId", "" + newCustomer.getCustomerId())
//                        .param("amount", "100.00")
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .session(session)
                        .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void withdrawErrorParam1() throws Exception {
        newCustomer.setBalance(BigDecimal.valueOf(1000));
        mvc.perform(MockMvcRequestBuilders.post("/finance/withdraw")
                .param("customerId", "" + newCustomer.getCustomerId())
                .param("amount", "-15.00")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(PARAMETER_ERROR));
    }

    @Test
    public void withdrawErrorParam2() throws Exception {
        newCustomer.setBalance(BigDecimal.valueOf(1000));
        mvc.perform(MockMvcRequestBuilders.post("/finance/withdraw")
                .param("customerId", "-1")
                .param("amount", "10.00")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(PARAMETER_ERROR));
    }

    @Test
    public void withdrawBalanceNotEnough() throws Exception {
        newCustomer.setBalance(BigDecimal.valueOf(10));
        customerMapper.updateByPrimaryKeySelective(newCustomer);
        mvc.perform(MockMvcRequestBuilders.post("/finance/withdraw")
                .param("customerId", "" + newCustomer.getCustomerId())
                .param("amount", "15.00")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(BALANCE_NOT_ENOUGH));
    }

    @Test
    public void getRecordsSuccess() throws Exception {
        FinanceRecord record1 = new FinanceRecord(newCustomer.getCustomerId(), BUY_REMARK, BigDecimal.valueOf(1000), FINISHED);
        financeMapper.insertRecord(record1);
        FinanceRecord record2 = new FinanceRecord(newCustomer.getCustomerId(), REDEMPTION_REMARK, BigDecimal.valueOf(1000), FINISHED);
        record2.setTradeNum(LocalDateTime.now().plusSeconds(10).format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")) + "-" + newCustomer.getCustomerId());
        financeMapper.insertRecord(record2);
        List<FinanceRecord> records = new LinkedList<>();
        records.add(record1);
        records.add(record2);
        mvc.perform(MockMvcRequestBuilders.get("/finance/records")
                .param("customerId", "" + newCustomer.getCustomerId())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[0].remark").value(record1.getRemark()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[1].remark").value(record2.getRemark()));
    }

    @Test
    public void getEmptyRecordsSuccess() throws Exception {
        List<FinanceRecord> records = financeMapper.selectAllRecordByCustomerId(newCustomer.getCustomerId());
        mvc.perform(MockMvcRequestBuilders.get("/finance/records")
                .param("customerId", "" + newCustomer.getCustomerId())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").isEmpty());
    }

    //    =================================================   //
    @Test
    public void confirmFundCompositionSuccess() throws Exception {
        Map<String, BigDecimal> composition = new HashMap<>();
        composition.put("1", new BigDecimal(0.45));
        composition.put("2", new BigDecimal(0.55));
        mvc.perform(MockMvcRequestBuilders.post("/finance/confirmFundComposition")
                .param("customerId", newCustomer.getCustomerId().toString())
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSON.toJSONString(composition))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").exists());
    }

    @Test
    public void confirmFundCompositionWrongPercent() throws Exception {
        Map<String, BigDecimal> composition = new HashMap<>();
        composition.put("1", new BigDecimal(0.45));
        composition.put("2", new BigDecimal(0.45));
        mvc.perform(MockMvcRequestBuilders.post("/finance/confirmFundComposition")
                .param("customerId", newCustomer.getCustomerId().toString())
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSON.toJSONString(composition))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(FinanceController.PARAMETER_ERROR));
    }

    @Test
    public void confirmFundCompositionWrongFundId() throws Exception {
        Map<String, BigDecimal> composition = new HashMap<>();
        composition.put("1", new BigDecimal(0.45));
        composition.put("2", new BigDecimal(0.55));
        mvc.perform(MockMvcRequestBuilders.post("/finance/confirmFundComposition")
                .param("customerId", "0")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSON.toJSONString(composition))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(FinanceController.PARAMETER_ERROR));
    }

    //    =================================================   //

    @Test
    public void buyFundCompositionSuccess() throws Exception {
        BigDecimal money = new BigDecimal(1000 * 10000);
        FinanceRecord record = financeService.recharge(newCustomer.getCustomerId(), money, AlipayUtil.getOutBizNum(newCustomer.getCustomerId()), false);
        financeService.completeTrade(record.getTradeNum());
        Map<Integer, BigDecimal> composition = new HashMap<>();
        composition.put(1, new BigDecimal(0.45));
        composition.put(2, new BigDecimal(0.55));
        Integer compositionId = financeService.confirmFundComposition(newCustomer.getCustomerId(), composition);
        mvc.perform(MockMvcRequestBuilders.get("/finance/buyFundComposition")
                .param("customerId", String.valueOf(newCustomer.getCustomerId()))
                .param("compositionId", compositionId.toString())
                .param("purchaseAmount", String.valueOf(10 * 10000))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true));
    }

    @Test
    public void buyFundCompositionWrongAmount() throws Exception {
        BigDecimal money = new BigDecimal(1000 * 10000);
        FinanceRecord record = financeService.recharge(newCustomer.getCustomerId(), money, AlipayUtil.getOutBizNum(newCustomer.getCustomerId()), false);
        financeService.completeTrade(record.getTradeNum());
        Map<Integer, BigDecimal> composition = new HashMap<>();
        composition.put(1, new BigDecimal(0.45));
        composition.put(2, new BigDecimal(0.55));
        Integer compositionId = financeService.confirmFundComposition(newCustomer.getCustomerId(), composition);
        mvc.perform(MockMvcRequestBuilders.get("/finance/buyFundComposition")
                .param("customerId", String.valueOf(newCustomer.getCustomerId()))
                .param("compositionId", compositionId.toString())
                .param("purchaseAmount", String.valueOf(10))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true));
//                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(FinanceController.PARAMETER_ERROR)); //这里本来是测试不满足最低投资份额或金额的，但是现在取消了这个限制
    }

    //    =================================================   //

    @Test
    public void sellFundCompositionSuccess() throws Exception {
        BigDecimal money = new BigDecimal(1000 * 10000);
        FinanceRecord record = financeService.recharge(newCustomer.getCustomerId(), money, AlipayUtil.getOutBizNum(newCustomer.getCustomerId()), false);
        financeService.completeTrade(record.getTradeNum());
        Map<Integer, BigDecimal> composition = new HashMap<>();
        composition.put(1, new BigDecimal(0.45));
        composition.put(2, new BigDecimal(0.55));
        Integer compositionId = financeService.confirmFundComposition(newCustomer.getCustomerId(), composition);
        BigDecimal purchaseAmount = new BigDecimal(100 * 10000);
        LinkedList<String> before = PythonUtil.getFile(TRADE_RECORD_CSV);
        financeService.buyFundComposition(newCustomer.getCustomerId(), compositionId, purchaseAmount);
        PythonUtil.write(TRADE_RECORD_CSV, before);
        mvc.perform(MockMvcRequestBuilders.post("/finance/sellFundComposition")
                .param("compositionId", String.valueOf(compositionId))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.requestTime").exists());
    }

    @Test
    public void sellFundCompositionError() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/finance/sellFundComposition")
                .param("compositionId", String.valueOf("-1"))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(PARAMETER_ERROR));
    }


}
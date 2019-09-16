package nju.citix.controller.fund;

import com.alibaba.fastjson.JSON;
import nju.citix.po.Customer;
import nju.citix.po.Fund;
import nju.citix.po.Manager;
import nju.citix.service.customer.CustomerService;
import nju.citix.service.fund.FundService;
import nju.citix.service.manager.ManagerService;
import nju.citix.utils.JWTUtil;
import nju.citix.utils.PythonUtil;
import nju.citix.vo.FundForm;
import nju.citix.vo.ManagerForm;
import nju.citix.vo.UserForm;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.LinkedList;

import static nju.citix.utils.PythonUtil.TRADE_RECORD_CSV;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class FundControllerTest {
    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private ManagerService managerService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private FundService fundService;
    private MockMvc mvc;
    private MockHttpSession session;
    private FundForm newFundForm = new FundForm();
    private Manager newManager;
    private String newManagerToken;
    private Customer newCustomer;
    private String newCustomerToken;
    private Fund newFund;

    private LinkedList<String> before;

    @Before
    public void setUp() throws Exception {
        before = PythonUtil.getFile(TRADE_RECORD_CSV);
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        session = new MockHttpSession();

        ManagerForm managerForm = new ManagerForm();
        managerForm.setUsername("gky");
        managerForm.setPassword("123123");
        newManager = managerService.addManager(managerForm);
        newManagerToken = JWTUtil.getToken(newManager);

        UserForm userForm = new UserForm();
        String username = "123abc";
        String password = "123adsfA";
        String email = "814775538@qq.com";
        userForm.setUsername(username);
        userForm.setPassword(password);
        userForm.setEmail(email);
        newCustomer = customerService.register(userForm);
        newCustomerToken = JWTUtil.getToken(newCustomer);

        String fundCode = "900075";
        newFundForm.setFundCode(fundCode);
        BigDecimal minPurchaseAmount = new BigDecimal("1.00");
        newFundForm.setMinPurchaseAmount(minPurchaseAmount);
        BigDecimal minPart = new BigDecimal("1.000000");
        newFundForm.setMinPart(minPart);
        String url = "http://quote.cfi.cn/jjzb/8711/900075.html";
        newFundForm.setUrl(url);
        mvc.perform(MockMvcRequestBuilders.post("/fund/manager/addFund")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSON.toJSONString(newFundForm))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newManagerToken)
        );
        newFund = fundService.findFundByCode(newFundForm.getFundCode());
    }

    @After
    public void recover() {
        PythonUtil.write(TRADE_RECORD_CSV, before);
    }

    //    =================================================   //

    @Test
    public void addFundSuccess() throws Exception {
        FundForm f = new FundForm();
        f.setFundCode("900057");
        mvc.perform(MockMvcRequestBuilders.post("/fund/manager/addFund")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSON.toJSONString(f))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newManagerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.fundCode").value("900057"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.minPurchaseAmount").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.minPart").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.url").isEmpty());

        FundForm f2 = new FundForm();
        f2.setFundCode("900093");
        f2.setMinPurchaseAmount(new BigDecimal("1.00"));
        f2.setMinPart(new BigDecimal("1.000000"));
        f2.setUrl("http://quote.cfi.cn/jjzb/8711/000093.html");
        mvc.perform(MockMvcRequestBuilders.post("/fund/manager/addFund")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSON.toJSONString(f2))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newManagerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.fundCode").value("900093"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.minPurchaseAmount").value(1.00))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.minPart").value(1.000000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.url").value("http://quote.cfi.cn/jjzb/8711/000093.html"));
    }

    @Test
    public void addFundDuplicatedFundCode() throws Exception {
        FundForm f = new FundForm();
        f.setFundCode(newFundForm.getFundCode());
        f.setMinPurchaseAmount(new BigDecimal("100.00"));
        f.setMinPart(new BigDecimal("100.000000"));
        f.setUrl("http://quote.cfi.cn/stockList.aspx?t=2");
        mvc.perform(MockMvcRequestBuilders.post("/fund/manager/addFund")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSON.toJSONString(f))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newManagerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(FundController.FUND_EXISTED_ERROR));
    }

    @Test
    public void addFundEmpty() throws Exception {
        FundForm f = new FundForm();
        f.setFundCode(null);
        f.setMinPurchaseAmount(new BigDecimal("100.00"));
        f.setMinPart(new BigDecimal("100.000000"));
        f.setUrl("http://quote.cfi.cn/stockList.aspx?t=2");
        mvc.perform(MockMvcRequestBuilders.post("/fund/manager/addFund")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSON.toJSONString(f))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newManagerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(FundController.CODE_SHOULD_NO_EMPTY));
    }

    @Test
    public void changeFundSuccess() throws Exception {
        Fund changeFund = new Fund();
        changeFund.setFundId(newFund.getFundId());
        changeFund.setFundCode(newFund.getFundCode());
        //已有信息的修改
        changeFund.setMinPurchaseAmount(new BigDecimal("25.00"));
        changeFund.setMinPart(null);
        changeFund.setUrl(null);
        //未添加信息的修改
        changeFund.setFundName("示例基金");
        changeFund.setAbbreviation("示例");
        changeFund.setPinyin(null);
        changeFund.setStartTime(null);
        mvc.perform(MockMvcRequestBuilders.post("/fund/manager/changeFund")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSON.toJSONString(changeFund))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newManagerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.fundCode").value(newFund.getFundCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.minPurchaseAmount").value(25.00))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.minPart").value(1.000000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.url").value(newFund.getUrl()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.fundName").value("示例基金"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.abbreviation").value("示例"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.pinyin").value(newFund.getPinyin()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.startTime").value(newFund.getStartTime()));
    }

    @Test
    public void changeFundDifferentFundCode() throws Exception {
        Fund changeFund = new Fund();
        changeFund.setFundId(newFund.getFundId());
        changeFund.setFundCode("910101");
        mvc.perform(MockMvcRequestBuilders.post("/fund/manager/changeFund")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSON.toJSONString(changeFund))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newManagerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(FundController.CODE_SHOULD_NO_CHANGE));
    }

    @Test
    public void changeFundEmptyId() throws Exception {
        Fund changeFund = new Fund();
        changeFund.setFundCode("910101");
        mvc.perform(MockMvcRequestBuilders.post("/fund/manager/changeFund")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSON.toJSONString(changeFund))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newManagerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(FundController.ID_SHOULD_NO_EMPTY));
    }

    public void changeFundEmptyFundCode() throws Exception {
        Fund changeFund = new Fund();
        changeFund.setFundId(newFund.getFundId());
        mvc.perform(MockMvcRequestBuilders.post("/fund/manager/changeFund")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSON.toJSONString(changeFund))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newManagerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(FundController.CODE_SHOULD_NO_EMPTY));
    }

    @Test
    public void changeFundErrorId() throws Exception {
        Fund changeFund = new Fund();
        changeFund.setFundId(-1234);
        changeFund.setFundCode("990101");
        mvc.perform(MockMvcRequestBuilders.post("/fund/manager/changeFund")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSON.toJSONString(changeFund))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newManagerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(FundController.FUND_NO_EXIST));
    }

    @Test
    public void viewAllFundsSuccess() throws Exception {
        String[] codes = new String[10];
        FundForm f = new FundForm();
        for (int i = 0; i < 10; i++) {
            String code = "90000" + i;
            codes[i] = code;
            f.setFundCode(code);
            fundService.addFund(f);
        }
        for (int i = 1; i < 3; i++) {
            mvc.perform(MockMvcRequestBuilders.get("/fund/viewAllFunds")
                    .param("pageNum", String.valueOf(i))
                    .param("pageSize", "5")
                    .accept(MediaType.APPLICATION_JSON_UTF8)
                    .header("token", newManagerToken)
            )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.result[4].fundCode").exists());
        }
    }

    @Test
    public void viewAllFundsParameterError1() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/fund/viewAllFunds")
                .param("pageNum", "2")
                .param("pageSize", "-1")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newManagerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(FundController.PARAMETER_ERROR));
    }

    @Test
    public void viewAllFundsParameterError2() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/fund/viewAllFunds")
                .param("pageNum", "0")
                .param("pageSize", "2")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newManagerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(FundController.PARAMETER_ERROR));
    }

    @Test
    public void viewAllFundsCrossBorder() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/fund/viewAllFunds")
                .param("pageNum", "100")
                .param("pageSize", "100")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newManagerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(FundController.FUND_SELECT_ERROR));
    }

    @Test
    public void getFundDetailedInfoSuccess() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/fund/getFundDetailedInfo")
                .param("fundId", newFund.getFundId().toString())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newManagerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.fundCode").value(newFund.getFundCode()));
    }

    @Test
    public void getFundDetailedInfoIdNotFound() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/fund/getFundDetailedInfo")
                .param("fundId", "-1")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newManagerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(FundController.FUND_NO_EXIST));
    }

    @Test
    public void recommendFund() throws Exception {
        String[] fundCodes = new String[]{"16", "15", "14", "13", "09", "10", "11", "12", "08", "07", "06", "05", "01", "02", "03", "04"};
        FundForm f = new FundForm();
        for (String i : fundCodes) {
            f.setFundCode("9990" + i);
            fundService.addFund(f);
        }
        mvc.perform(MockMvcRequestBuilders.get("/fund/user/recommendFund")
                .param("customerId", String.valueOf(newCustomer.getCustomerId()))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[0].fundCode").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[1].fundCode").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[2].fundCode").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[3].fundCode").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[4].fundCode").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[5].fundCode").isNotEmpty());
    }

    @Test
    public void recommendFundParameterError() throws Exception {
        String[] ids = new String[]{"-1", String.valueOf(newCustomer.getCustomerId() + 10)};
        mvc.perform(MockMvcRequestBuilders.get("/fund/user/recommendFund")
                .param("customerId", "-1")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(FundController.PARAMETER_ERROR));
    }

    @Test
    public void getFundNetValueSuccessTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/fund/getFundNetValue")
                .param("fundId", "1")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[0].latestValue").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[0].dailyReturn").isNotEmpty());
    }

    @Test
    public void getFundNetValueNotFound() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/fund/getFundNetValue")
                .param("fundId", "-1")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newManagerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(FundController.FUND_NET_VALUE_SELECT_ERROR));
    }

    public void getFundBuyRateSuccess() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/fund/getFundBuyRate")
                .param("fundId", "1")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newManagerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[0].descriptionType").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[0].startAmount").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[0].rate").value(1.50))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[1].descriptionType").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[1].startAmount").value(100))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[1].rate").value(1.20))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[2].descriptionType").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[2].startAmount").value(500))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[2].rate").value(0.80))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[3].descriptionType").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[3].startAmount").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[3].rate").value(1000.00));
    }

    @Test
    public void getFundBuyRateNotFund() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/fund/getFundBuyRate")
                .param("fundId", "-1")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newManagerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(FundController.FUND_RATE_SELECT_ERROR));
    }

    //    =================================================   //
    @Test
    public void getFundCompositionSuccess() throws Exception {
        Integer[] funds = {1, 2};
        mvc.perform(MockMvcRequestBuilders.post("/fund/customer/getFundComposition")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSON.toJSONString(funds))
                .param("customerId", newCustomer.getCustomerId().toString())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true));
    }

    @Test
    public void getFundCompositionWrongCustomerId() throws Exception {
        Integer[] funds = {1, 2};
        mvc.perform(MockMvcRequestBuilders.post("/fund/customer/getFundComposition")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSON.toJSONString(funds))
                .param("customerId", "-1")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(FundController.PARAMETER_ERROR));
    }

    @Test
    public void getFundCompositionEmptyFunds() throws Exception {
        Integer[] funds = {};
        mvc.perform(MockMvcRequestBuilders.post("/fund/customer/getFundComposition")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSON.toJSONString(funds))
                .param("customerId", newCustomer.getCustomerId().toString())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(FundController.PARAMETER_ERROR));
    }

    //此测试在与计科同学对接后生效
    /*@Test
    public void getFundCompositionWrongFunds() throws Exception {
        Integer[] funds = {-1, -2};
        mvc.perform(MockMvcRequestBuilders.post("/fund/customer/getFundComposition")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSON.toJSONString(funds))
                .param("customerId", newCustomer.getCustomerId().toString())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(FundController.COMPOSITION_NO_EXIST));
    }*/

    //    =================================================   //

}


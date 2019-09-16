package nju.citix.serviceImpl.fund;

import nju.citix.controller.fund.FundController;
import nju.citix.dao.fund.FundMapper;
import nju.citix.po.Customer;
import nju.citix.po.Fund;
import nju.citix.service.customer.CustomerService;
import nju.citix.service.fund.FundService;
import nju.citix.vo.FundForm;
import nju.citix.vo.UserForm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author GKY
 * @date 2019/8/20
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class FundServiceTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private FundService fundService;
    @Resource
    private FundMapper fundMapper;
    @Resource
    private CustomerService customerService;

    private Fund completeFund;//完整的输入信息
    private Fund incompleteFund;//含fundCode但含其他输入信息
    private UserForm newUser = new UserForm();
    private Customer newCustomer;
    private String requestIp = "192.168.10.12";

    @Before
    public void setUp() throws Exception {
        String username = "123abc";
        String password = "123adsfA";
        String email = "814775538@qq.com";
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setEmail(email);
        newCustomer = customerService.register(newUser);

        FundForm form = new FundForm();
        form.setFundCode("900041");
        incompleteFund = fundService.addFund(form);
        form.setFundCode("900042");
        form.setMinPurchaseAmount(new BigDecimal("1.00"));
        form.setMinPart(new BigDecimal("1.000000"));
        form.setUrl("http://quote.cfi.cn/jjzb/8711/900041.html");
        completeFund = fundService.addFund(form);
    }

    @Test
    public void addFundSuccessTest() {
        Assert.assertEquals("900041", incompleteFund.getFundCode());
        Assert.assertNull(incompleteFund.getMinPurchaseAmount());
        Assert.assertNull(incompleteFund.getMinPart());
        Assert.assertNull(incompleteFund.getUrl());
        Assert.assertEquals("900042", completeFund.getFundCode());
        Assert.assertEquals(new BigDecimal("1.00"), completeFund.getMinPurchaseAmount());
        Assert.assertEquals(new BigDecimal("1.000000"), completeFund.getMinPart());
        Assert.assertEquals("http://quote.cfi.cn/jjzb/8711/900041.html", completeFund.getUrl());
    }

    @Test(expected = DuplicateKeyException.class)
    public void addFundDuplicateFundCode() {
        FundForm f = new FundForm();
        f.setFundCode("900041");
        f.setMinPurchaseAmount(new BigDecimal("100.00"));
        f.setMinPart(new BigDecimal("100.000000"));
        f.setUrl("https://fanyi.baidu.com/#en/zh/Duplicate");
        fundService.addFund(f);
    }

    @Test
    public void changeFundSuccessTest() {
        Fund newFund = new Fund();
        newFund.setFundId(completeFund.getFundId());
        newFund.setFundCode(completeFund.getFundCode());
        //已有信息的修改
        newFund.setMinPurchaseAmount(new BigDecimal("25.00"));
        newFund.setMinPart(null);
        newFund.setUrl(null);
        //未添加信息的修改
        newFund.setFundName("示例基金");
        newFund.setAbbreviation("示例");
        newFund.setPinyin(null);
        newFund.setStartTime(null);
        fundService.changeFund(newFund);
        Fund f = fundMapper.selectFundByPrimaryKey(newFund.getFundId());//

        Assert.assertEquals(completeFund.getFundCode(), f.getFundCode());
        Assert.assertEquals(new BigDecimal("25.00"), f.getMinPurchaseAmount());
        Assert.assertEquals(completeFund.getMinPart(), f.getMinPart());
        Assert.assertEquals(completeFund.getUrl(), f.getUrl());
        Assert.assertEquals("示例基金", f.getFundName());
        Assert.assertEquals("示例", f.getAbbreviation());
        Assert.assertEquals(completeFund.getPinyin(), f.getPinyin());
        Assert.assertEquals(completeFund.getStartTime(), f.getStartTime());
    }

    @Test
    public void changeFundDifferentFundCode() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(FundController.CODE_SHOULD_NO_CHANGE);
        Fund newFund = new Fund();
        newFund.setFundId(completeFund.getFundId());
        newFund.setFundCode("910101");
        fundService.changeFund(newFund);
    }

    @Test
    public void changeFundEmpty(){
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(FundController.FUND_NO_EXIST);
        Fund newFund = new Fund();
        newFund.setFundId(1234567);
        newFund.setFundCode("911111");
        fundService.changeFund(newFund);
    }

    @Test
    public void viewAllFundsSuccessTest() {
        List<Fund> fundList = fundService.viewAllFunds(2, 2);
        Assert.assertEquals(2, fundList.size());
        Assert.assertNotNull(fundList.get(0).getFundCode());
        Assert.assertNotNull(fundList.get(1).getFundCode());
    }

    @Test
    public void viewAllFundsCrossBorder() {
        List<Fund> fundList = fundService.viewAllFunds(100, 100);
        Assert.assertEquals(true, fundList.isEmpty());
    }

    @Test
    public void findFundByIdSuccess() {
        Fund f = fundService.findFundById(completeFund.getFundId());
        Assert.assertEquals(completeFund, f);
    }

    @Test
    public void findFundByIdError() {
        Fund f = fundService.findFundById(-1);
        Assert.assertEquals(null, f);
    }

    @Test
    public void recommendFundSuccessTest() {
        String[] fundCodes = new String[]{"16", "15", "14", "13", "09", "10", "11", "12", "08", "07", "06", "05", "01", "02", "03", "04"};
        FundForm f = new FundForm();
        for (String i : fundCodes) {
            f.setFundCode("9990" + i);
            fundService.addFund(f);
        }
        List<Fund> fundList = fundService.recommendFund(newCustomer.getCustomerId());
        Assert.assertFalse(fundList.isEmpty());
//        for (int i = 0; i < 10; i++) {
//            Assert.assertEquals(String.valueOf(910016 - i), fundList.get(i).getFundCode());
//        }
    }

    @Test
    public void recommendFundCustomerIdError() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(FundController.PARAMETER_ERROR);
        List<Fund> fundList = fundService.recommendFund(newCustomer.getCustomerId() + 10);
    }
}
package nju.citix.controller.fund;

import nju.citix.annotation.Authority;
import nju.citix.annotation.UserLoginToken;
import nju.citix.po.Fund;
import nju.citix.po.FundBuyRate;
import nju.citix.po.FundNetValue;
import nju.citix.po.Recommend;
import nju.citix.service.fund.FundService;
import nju.citix.utils.JWTUtil;
import nju.citix.utils.PythonUtil;
import nju.citix.utils.ScheduleUtil;
import nju.citix.vo.FundForm;
import nju.citix.vo.Response;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * @author GKY
 * @date 2019/8/20
 */
@UserLoginToken
@RestController
@RequestMapping("/fund")
public class FundController {

    public static final String FUND_EXISTED_ERROR = "基金已存在";
    public static final String FUND_NO_EXIST = "基金不存在";
    public static final String CODE_SHOULD_NO_EMPTY = "基金代码不得为空";
    public static final String ID_SHOULD_NO_EMPTY = "基金id不得为空";
    public static final String CODE_SHOULD_NO_CHANGE = "基金代码不得修改";
    public static final String PARAMETER_ERROR = "参数无效或不合法";
    public static final String FUND_SELECT_ERROR = "找不到基金";
    public static final String FUND_NET_VALUE_SELECT_ERROR = "找不到该基金净值";
    public static final String FUND_RATE_SELECT_ERROR = "找不到基金费率";
    public static final String COMPOSITION_NO_EXIST = "无合适的基金组合";

    private FundService fundService;

    public FundController(FundService fundService) {
        this.fundService = fundService;
    }

    @Authority(JWTUtil.MANAGER)
    @PostMapping("/manager/addFund")
    public Response addFund(@Valid @RequestBody FundForm fundForm) {
        Assert.isTrue(!StringUtils.isEmpty(fundForm.getFundCode()), CODE_SHOULD_NO_EMPTY);

        try {
            return Response.buildSuccess(fundService.addFund(fundForm));
        } catch (DuplicateKeyException e) {
            return Response.buildFailure(FUND_EXISTED_ERROR);
        }
    }

    @Authority(JWTUtil.MANAGER)
    @PostMapping("/manager/changeFund")
    public Response changeFund(@Valid @RequestBody Fund fund) throws MessagingException {
        Assert.isTrue(!StringUtils.isEmpty(fund.getFundCode()), CODE_SHOULD_NO_EMPTY);
        Assert.isTrue(!StringUtils.isEmpty(fund.getFundId()), ID_SHOULD_NO_EMPTY);

        Fund newFund = fundService.changeFund(fund);
        return Response.buildSuccess(newFund);

    }

    @Authority
    @GetMapping("/viewAllFunds")
    public Response viewAllFunds(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize) {
        Assert.notNull(pageNum, PARAMETER_ERROR);
        Assert.notNull(pageSize, PARAMETER_ERROR);
        Assert.isTrue(pageNum > 0, PARAMETER_ERROR);
        Assert.isTrue(pageSize > 0, PARAMETER_ERROR);

        List<Fund> funds = fundService.viewAllFunds(pageNum, pageSize);

        if (funds.isEmpty()) {
            return Response.buildFailure(FUND_SELECT_ERROR);
        }
        return Response.buildSuccess(funds);
    }

    @Authority(JWTUtil.ALL)
    @GetMapping("/getFundDetailedInfo")
    public Response getFundDetailedInfo(@RequestParam("fundId") Integer fundId) {
        Fund fund = fundService.findFundById(fundId);
        if (fund == null) {
            return Response.buildFailure(FUND_NO_EXIST);
        }

        return Response.buildSuccess(fund);
    }

    @Authority(JWTUtil.CUSTOMER)
    @GetMapping("/user/recommendFund")
    public Response recommendFund(@RequestParam("customerId") Integer customerId) {
        Assert.notNull(customerId, PARAMETER_ERROR);
        Assert.isTrue(customerId >= 0, PARAMETER_ERROR);

        List<Fund> funds = fundService.recommendFund(customerId);

        if (funds.isEmpty()) {
            return Response.buildFailure(FUND_SELECT_ERROR);
        }
        return Response.buildSuccess(funds);

    }

    @Authority(JWTUtil.CUSTOMER)
    @GetMapping("/newUser/recommendFund")
    public Response recommendFund2New() {
        return Response.buildSuccess(fundService.getDefaultFundComposition());
    }

    @Authority(JWTUtil.ALL)
    @GetMapping("/getFundNetValue")
    public Response getFundNetValue(@RequestParam("fundId") Integer fundId) {
        Assert.notNull(fundId, ID_SHOULD_NO_EMPTY);

        List<FundNetValue> fundNetValues = fundService.getFundNetValue(fundId);

        if (fundNetValues.isEmpty()) {
            return Response.buildFailure(FUND_NET_VALUE_SELECT_ERROR);
        }
        return Response.buildSuccess(fundNetValues);
    }

    @Authority
    @GetMapping("/getFundBuyRate")
    public Response getFundBuyRate(@RequestParam("fundId") Integer fundId) {
        Assert.notNull(fundId, ID_SHOULD_NO_EMPTY);

        List<FundBuyRate> fundBuyRates = fundService.getFundBuyRate(fundId);

        if (fundBuyRates.isEmpty()) {
            return Response.buildFailure(FUND_RATE_SELECT_ERROR);
        }
        return Response.buildSuccess(fundBuyRates);
    }

    @Authority(JWTUtil.CUSTOMER)
    @PostMapping("/customer/getFundComposition")
    public Response getFundComposition(@RequestParam("customerId") Integer customerId,
                                       @RequestBody List<Integer> funds) {
        Assert.notNull(customerId, PARAMETER_ERROR);
        Assert.isTrue(customerId > 0, PARAMETER_ERROR);
        Assert.notNull(funds, PARAMETER_ERROR);
        Assert.isTrue(!funds.isEmpty(), PARAMETER_ERROR);

        Map<Integer, BigDecimal> fundComposition = fundService.getFundComposition(customerId, funds);
        if (fundComposition == null || fundComposition.isEmpty()) {
            return Response.buildFailure(COMPOSITION_NO_EXIST);
        }
        System.out.println(fundComposition.toString());
        return Response.buildSuccess(fundComposition);
    }

    @Authority(JWTUtil.SUPER)
    @GetMapping("/manager/recoverRecommend")
    public void recoverRecommend(){
        LinkedList<Recommend> recommendList = PythonUtil.readRecommendCSV(PythonUtil.RECOMMEND_CSV);
        PythonUtil.updateDatabase(recommendList);
    }

    @Authority(JWTUtil.SUPER)
    @GetMapping("/manager/updateRecommend")
    public void updateRecommend(){
        ScheduleUtil scheduleUtil = new ScheduleUtil();
        scheduleUtil.updateRecommendList();
    }

    @Authority(JWTUtil.ALL)
    @GetMapping("/test")
    public void test(){
        try {
            PythonUtil.updateRecommendList(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package nju.citix.controller.finance;

import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.alipay.api.response.AlipayTradePagePayResponse;
import nju.citix.annotation.Authority;
import nju.citix.annotation.UserLoginToken;
import nju.citix.po.CustomerComposition;
import nju.citix.po.FinanceRecord;
import nju.citix.po.PurchaseRecord;
import nju.citix.service.finance.FinanceService;
import nju.citix.utils.AlipayUtil;
import nju.citix.utils.IPUtil;
import nju.citix.utils.JWTUtil;
import nju.citix.vo.CompositionDetail;
import nju.citix.vo.Response;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author jiang hui
 * @date 2019/8/24
 */
@UserLoginToken
@RestController
@RequestMapping("/finance")
public class FinanceController {

    @Resource
    private FinanceService financeService;

    public static final String ALIPAY_NOTIFY_SUCCESS = "success";
    public static final String WITHDRAW_SUCCESS = "提现成功";

    public static final String SHOULD_NO_EMPTY = "参数不得为空";
    public static final String PARAMETER_ERROR = "参数错误";
    public static final String COMPOSITION_ERROR = "基金组合格式错误";
    public static final String ALIPAY_REQUEST_ERROR = "阿里请求执行出错";
    public static final String ALIPAY_RESPONSE_ERROR = "阿里请求回应失败";
    public static final String ALIPAY_PARAMETER_ERROR = "阿里订单参数错误";
    public static final String NO_NETWORK_NOTICE = "未联网，无法连接支付宝，使用本地支付";
    public static final String TRADE_INFO_ERROR = "订单信息有误";
    public static final String SIGNATURE_ERROR = "订单信息有误，验签失败";
    public static final String BALANCE_NOT_ENOUGH = "余额不足";
    public static final String COMPOSITION_NO_EMPTY = "基金组合不得为空";
    public static final String REPEATED_SOLD_REQUEST = "卖出申请重复";
    public static final String ALREADY_SOLD = "基金组合已卖出";

    public static final String COMPOSITIONID_INVALID = "未找到该id的基金组合";


    @Authority(JWTUtil.CUSTOMER)
    @GetMapping("/recharge")
    public void recharge(@RequestParam Integer customerId, @RequestParam BigDecimal amount, HttpServletResponse response) throws IOException {
        Assert.notNull(customerId, SHOULD_NO_EMPTY);
        Assert.notNull(amount, SHOULD_NO_EMPTY);
        Assert.isTrue(0 < amount.compareTo(BigDecimal.valueOf(0)), PARAMETER_ERROR);
        Assert.isTrue(financeService.validateCustomer(customerId), PARAMETER_ERROR);
        AlipayTradePagePayResponse alipayResponse;
        response.setContentType("text/html;charset=UTF-8");
        boolean network = IPUtil.ping(AlipayUtil.ALI_ADDRESS);
        String out_trade_no = AlipayUtil.getOutBizNum(customerId);
        if (network) {
            try {
                alipayResponse = AlipayUtil.TradePagePay(customerId, amount, out_trade_no);
            } catch (AlipayApiException e) {
                throw new AlipayUtil.AlipayException(ALIPAY_REQUEST_ERROR);
            }
            if (!alipayResponse.isSuccess()) {
                throw new AlipayUtil.AlipayException(ALIPAY_RESPONSE_ERROR);
            }
            response.getWriter().write(alipayResponse.getBody());
        } else {
            response.getWriter().write(NO_NETWORK_NOTICE);
        }
        FinanceRecord record = financeService.recharge(customerId, amount, out_trade_no, network);
    }

    @Authority(JWTUtil.CUSTOMER)
    @PostMapping("/withdraw")
    public Response withdraw(@RequestParam Integer customerId, @RequestParam BigDecimal amount) {
        Assert.notNull(customerId, SHOULD_NO_EMPTY);
        Assert.notNull(amount, SHOULD_NO_EMPTY);
        Assert.isTrue(0 < amount.compareTo(BigDecimal.valueOf(0)), PARAMETER_ERROR);
        Assert.isTrue(financeService.validateCustomer(customerId), PARAMETER_ERROR);
        AlipayFundTransToaccountTransferResponse alipayResponse;
        boolean network = IPUtil.ping(AlipayUtil.ALI_ADDRESS);
        boolean success = financeService.withdraw(customerId, amount, network);
        if (success) {
            if (network) {
                try {
                    alipayResponse = AlipayUtil.FundTransToaccountTransfer(customerId, amount);
                } catch (AlipayApiException e) {
                    throw new AlipayUtil.AlipayException(ALIPAY_REQUEST_ERROR);
                }
                if (!alipayResponse.isSuccess()) {
                    throw new AlipayUtil.AlipayException(ALIPAY_RESPONSE_ERROR);
                }
            }
            return network ? Response.buildSuccess(WITHDRAW_SUCCESS) : Response.buildSuccess(NO_NETWORK_NOTICE);
        }
        return Response.buildFailure(BALANCE_NOT_ENOUGH);
    }

    @Authority(JWTUtil.ALL)
    @PostMapping(value = "/notify")
    @ResponseBody
    public void notify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String charset = AlipayUtil.getProperty("CHARSET");
        Map<String, String> params = AlipayUtil.getParams(request);
        boolean validation = AlipayUtil.validateSignature(params);
        String result;
        if (validation) {
            if (AlipayUtil.validateParams(params)) {
                result = ALIPAY_NOTIFY_SUCCESS;
                String tradeNum = params.get("out_trade_no");
                if (!financeService.completeTrade(tradeNum)) {
                    result = TRADE_INFO_ERROR;
                }
            } else {
                result = ALIPAY_PARAMETER_ERROR;
            }
        } else {
            result = SIGNATURE_ERROR;
        }
        response.setContentType("text/html;charset=" + charset);
        response.setCharacterEncoding(charset);
        response.getWriter().write(result);
        response.getWriter().flush();
        response.getWriter().close();
    }

    @Authority(JWTUtil.ALL)
    @GetMapping("/returnHandler")
    public Response returnHandler(HttpServletRequest request) {
        Map<String, String> params = AlipayUtil.getParams(request);
        boolean validation = AlipayUtil.validateSignature(params);
        Response response;
        if (validation) {
            validation = AlipayUtil.validateParams(params);
            if (validation) {
                response = Response.buildSuccess();
                String tradeNum = params.get("out_trade_no");
                if (!financeService.completeTrade(tradeNum)) {
                    response = Response.buildFailure(TRADE_INFO_ERROR);
                }
            } else {
                response = Response.buildFailure(ALIPAY_PARAMETER_ERROR);
            }
        } else {
            response = Response.buildFailure(SIGNATURE_ERROR);
        }
        return response;
//        response.sendRedirect(request.getContextPath() + "/fx.html");
//        response.setContentType("text/html;charset=" + charset);
//        response.setCharacterEncoding(charset);
//        response.getWriter().write(result);// 直接将完整的表单html输出到页面
//        response.getWriter().flush();
//        response.getWriter().close();
    }

    @Authority(JWTUtil.CUSTOMER)
    @GetMapping("/records")
    public Response getAllFinanceRecord(@RequestParam Integer customerId) {
        Assert.notNull(customerId, SHOULD_NO_EMPTY);
        List<FinanceRecord> records = financeService.viewAllRecords(customerId);
        return Response.buildSuccess(records);
    }

    @Authority(JWTUtil.CUSTOMER)
    @PostMapping("/sellFundComposition")
    public Response sellFundComposition(@RequestParam("compositionId") Integer compositionId) {
        Assert.notNull(compositionId, PARAMETER_ERROR);
        Assert.isTrue(compositionId >= 0, PARAMETER_ERROR);
        CustomerComposition customerComposition = financeService.sellFundComposition(compositionId);
        return Response.buildSuccess(customerComposition);
    }

    @Authority(JWTUtil.CUSTOMER)
    @PostMapping("/confirmFundComposition")
    public Response confirmFundComposition(@RequestParam("customerId") Integer customerId, @RequestBody Map<Integer, BigDecimal> fundComposition) {
        Assert.notNull(customerId, PARAMETER_ERROR);
        Assert.isTrue(customerId > 0, PARAMETER_ERROR);
        Assert.notNull(fundComposition, COMPOSITION_NO_EMPTY);
        Assert.isTrue(!fundComposition.isEmpty(), COMPOSITION_NO_EMPTY);

        Integer compositionId = financeService.confirmFundComposition(customerId, fundComposition);
        if (compositionId == null) {
            return Response.buildFailure(PARAMETER_ERROR);
        }
        return Response.buildSuccess(compositionId);
    }

    @Authority(JWTUtil.CUSTOMER)
    @GetMapping("/buyFundComposition")
    public Response buyFundComposition(@RequestParam("customerId") Integer customerId, @RequestParam("compositionId") Integer compositionId, @RequestParam("purchaseAmount") BigDecimal purchaseAmount) {
        Assert.notNull(compositionId, PARAMETER_ERROR);
        Assert.isTrue(compositionId > 0, PARAMETER_ERROR);
        Assert.notNull(purchaseAmount, PARAMETER_ERROR);
        Assert.isTrue(purchaseAmount.intValue() > 0, PARAMETER_ERROR);

        boolean re = financeService.buyFundComposition(customerId, compositionId, purchaseAmount);
        if (!re) {
            return Response.buildFailure(PARAMETER_ERROR);
        }
        return Response.buildSuccess();
    }

    @Authority(JWTUtil.CUSTOMER)
    @GetMapping("/getRecord/soldTime")
    public Response getPurchaseRecordsByCustomerIdInSoldTime(@RequestParam("customerId") Integer customerId) {
        List<PurchaseRecord> purchaseRecordList = financeService.getPurchaseRecordsByCustomerIdInSoldTime(customerId);
//        如果没有购买记录，则返回空列表，前端自行判断
        return Response.buildSuccess(purchaseRecordList);
    }

    @Authority(JWTUtil.CUSTOMER)
    @GetMapping("/getRecord/purchaseAmount")
    public Response getPurchaseRecordsByCustomerIdInPurchaseAmount(@RequestParam("customerId") Integer customerId) {
        List<PurchaseRecord> purchaseRecordList = financeService.getPurchaseRecordsByCustomerIdInPurchaseAmount(customerId);
//        如果没有购买记录，则返回空列表，前端自行判断
        return Response.buildSuccess(purchaseRecordList);
    }

    @Authority(JWTUtil.CUSTOMER)
    @GetMapping("/getRecord/purchaseTime")
    public Response getPurchaseRecordsByCustomerIdInPurchaseTime(@RequestParam("customerId") Integer customerId) {
        List<PurchaseRecord> purchaseRecordList = financeService.getPurchaseRecordsByCustomerIdInPurchaseTime(customerId);
//        如果没有购买记录，则返回空列表，前端自行判断
        return Response.buildSuccess(purchaseRecordList);
    }

    @Authority(JWTUtil.CUSTOMER)
    @GetMapping("/getRecordById")
    public Response getPurchaseRecordByCompositionId(@RequestParam("compositionId") Integer compositionId) {
        CompositionDetail pr = financeService.getPurchaseRecordByCompositionId(compositionId);
        return pr == null ? Response.buildFailure(COMPOSITIONID_INVALID) : Response.buildSuccess(pr);
    }
}

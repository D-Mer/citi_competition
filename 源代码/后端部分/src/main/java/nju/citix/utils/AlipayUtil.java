package nju.citix.utils;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.alipay.api.response.AlipayTradePagePayResponse;
import nju.citix.config.AlipayConfig;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * @author jiang hui
 * @date 2019/8/28
 */
@Service
public class AlipayUtil {

    public static AlipayClient client = AlipayConfig.getAlipayClient();
    public static Properties properties = AlipayConfig.getProperties();

    /**
     * PC支付 FAST_INSTANT_TRADE_PAY, APP支付 QUICK_MSECURITY_PAY, 移动H5支付 QUICK_WAP_PAY
     */
    public static final String FAST_INSTANT_TRADE_PAY = "FAST_INSTANT_TRADE_PAY";

    public static final String ALIPAY_LOGONID = "ALIPAY_LOGONID";

    public static final String ALI_ADDRESS = "openapi.alipaydev.com";

    /**
     * 根据用户id生成订单号
     *
     * @param customerId 消费者id
     * @return 生成的订单号，下单时间 + 用户id
     */
    public static String getOutBizNum(Integer customerId) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")) + "-" + (int)(1000 * Math.random()) + "-" + customerId;
    }

    /**
     * 根据关键词获取属性
     *
     * @param key 关键词
     * @return 对应的属性
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * 生成支付宝网页支付请求
     *
     * @param customerId 消费者id
     * @param amount     消费金额
     * @return 支付宝网页支付请求对象
     */
    public static AlipayTradePagePayRequest createAlipayTradePagePayRequest(Integer customerId, BigDecimal amount, String out_trade_no) {
        JSONObject data = new JSONObject();
        data.put("out_trade_no", out_trade_no);
        data.put("product_code", FAST_INSTANT_TRADE_PAY);
        data.put("total_amount", amount);//付款金额,必填
        data.put("subject", "充值业务");//订单描述,必填
        data.put("timeout_express", "15m");//该笔订单允许的最晚付款时间，逾期将关闭交易
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();//PC支付
        //AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();//APP支付
        //AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();//移动H5支付
        request.setNotifyUrl(properties.getProperty("NOTIFY_URL"));//异步通知地址
        request.setReturnUrl(properties.getProperty("RETURN_URL"));//同步通知地址
        request.setBizContent(data.toJSONString());//业务参数
        return request;
    }

    /**
     * 向支付宝服务器发送网页支付请求
     *
     * @param customerId 消费者id
     * @param amount     消费金额
     * @return 支付宝的响应对象
     * @throws AlipayApiException 向支付宝发送请求时出现的异常
     */
    public static AlipayTradePagePayResponse TradePagePay(Integer customerId, BigDecimal amount, String out_trade_no) throws AlipayApiException {
        AlipayTradePagePayRequest request = createAlipayTradePagePayRequest(customerId, amount, out_trade_no);
        return client.pageExecute(request);
    }

    /**
     * 生成支付宝转账支付请求
     *
     * @param customerId 消费者id
     * @param amount     转账金额
     * @return 支付宝转账请求对象
     */
    public static AlipayFundTransToaccountTransferRequest createFundTransToaccountTransferRequest(Integer customerId, BigDecimal amount) {
        JSONObject data = new JSONObject();
        data.put("out_biz_no", AlipayUtil.getOutBizNum(customerId));
        data.put("payee_type", ALIPAY_LOGONID);//收款方类型
        data.put("payee_account", "btxuah1824@sandbox.com");//收款方账号，对应上面的类型
        data.put("amount", amount);
//        data.put("payer_show_name", "citix");//付款方姓名，如果实名认证了会校验
//        data.put("payee_real_name", "username");//收钱方姓名，如果实名认证了会校验
        data.put("remark", "转账");
        AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
        request.setBizContent(data.toJSONString());
        request.setNotifyUrl(properties.getProperty("NOTIFY_URL"));//异步通知地址
        request.setReturnUrl(properties.getProperty("RETURN_URL"));//同步通知地址
        return request;
    }

    /**
     * 向支付宝服务器发送转账请求
     *
     * @param customerId 消费者id
     * @param amount     转账金额
     * @return 支付宝的响应对象
     * @throws AlipayApiException 向支付宝发送请求时出现的异常
     */
    public static AlipayFundTransToaccountTransferResponse FundTransToaccountTransfer(Integer customerId, BigDecimal amount) throws AlipayApiException {
        AlipayFundTransToaccountTransferRequest request = createFundTransToaccountTransferRequest(customerId, amount);
        return client.execute(request);
    }

    /**
     * 获取支付宝回应的请求中的参数
     *
     * @param request 支付宝回应的请求
     * @return 支付宝回应的请求中的参数
     */
    public static HashMap<String, String> getParams(HttpServletRequest request) {
        Map<String, String[]> requestParams = request.getParameterMap();
        HashMap<String, String> params = new HashMap<>();
        for (String name : requestParams.keySet()) {
            params.put(name, new String(request.getParameter(name).getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
        }
        return params;
    }

    /**
     * 校验支付宝回应的请求中的参数
     *
     * @param params 支付宝回应的请求中的参数
     * @return 检验成功返回true
     */
    public static boolean validateParams(Map<String, String> params) {
        if (!params.get("app_id").equals("2016101300678473")) return false;
        if (!params.get("seller_id").equals("2088102179421595")) return false;
        if (BigDecimal.valueOf(Double.parseDouble(params.get("total_amount"))).compareTo(BigDecimal.ZERO) < 0)
            return false;
//        String pattern = "\\d*-\\d*-\\d*-\\d*-\\d*-\\d*-\\d*-\\d*";
        String pattern = "\\d*-\\d*-\\d*-\\d*-\\d*-\\d*-\\d*-\\d*";
        return Pattern.matches(pattern, params.get("out_trade_no"));
    }

    /**
     * 校验支付宝回应的请求中的签名
     *
     * @param params 支付宝回应的请求中的签名
     * @return 检验成功返回true
     */
    public static boolean validateSignature(Map<String, String> params) {
        boolean result = false;
        try {
            result = AlipaySignature.rsaCheckV1(params,
                    properties.getProperty("ALIPAY_RSA2_PUBLIC_KEY"),
                    properties.getProperty("CHARSET"),
                    properties.getProperty("SIGN_TYPE"));
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 在接入阿里服务器的方法中出现的异常
     */
    public static class AlipayException extends RuntimeException {
        public AlipayException(String message) {
            super(message);
        }
    }

}

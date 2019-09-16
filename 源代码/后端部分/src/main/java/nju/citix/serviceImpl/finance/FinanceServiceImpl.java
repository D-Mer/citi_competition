package nju.citix.serviceImpl.finance;

import nju.citix.dao.customer.CustomerCompositionMapper;
import nju.citix.dao.finance.FinanceMapper;
import nju.citix.dao.fund.FundCompositionMapper;
import nju.citix.po.*;
import nju.citix.service.customer.CustomerForService;
import nju.citix.service.finance.FinanceService;
import nju.citix.service.finance.FinanceServiceForBL;
import nju.citix.service.fund.FundForService;
import nju.citix.utils.PythonUtil;
import nju.citix.vo.CompositionDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static nju.citix.controller.finance.FinanceController.*;

/**
 * @author jiang hui
 * @date 2019/8/28
 */
@Service
public class FinanceServiceImpl implements FinanceService, FinanceServiceForBL {
    private static final Logger LOGGER = LoggerFactory.getLogger(FinanceServiceImpl.class);
    @Resource
    private FinanceMapper financeMapper;
    @Resource
    private CustomerCompositionMapper customerCompositionMapper;
    @Resource
    private FundCompositionMapper fundCompositionMapper;
    @Resource
    private FundForService fundForService;
    @Resource
    private CustomerForService customerForService;


    public static final String RECHARGE_REMARK = "充值";
    public static final String WITHDRAW_REMARK = "提现";
    public static final String BUY_REMARK = "买入";
    public static final String REDEMPTION_REMARK = "赎回";
    public static final String UNFINISHED = "未支付";
    public static final String FINISHED = "已完成";

    public static final String REMARK_ERROR = "remark should be BUY_REMARK or REDEMPTION_REMARK";
    public static final String AMOUNT_NON_POSITIVE_ERROR = "金额不能为非正数";
    public static final String INSUFFICIENT_BALANCE_ERROR = "余额不足";
    public static final String USER_NONEXISTENT_ERROR = "用户不存在";

    @Override
    public FinanceRecord recharge(Integer customerId, BigDecimal amount, String out_trade_no, boolean network) {
        FinanceRecord record;
        if (network) {
            record = new FinanceRecord(customerId, RECHARGE_REMARK, amount, out_trade_no, UNFINISHED);
        } else {
            record = new FinanceRecord(customerId, RECHARGE_REMARK, amount, out_trade_no, FINISHED);
            financeMapper.updateCustomerBalance(customerId, amount);
        }
        financeMapper.insertRecord(record);
        return record;
    }

    @Override
    public boolean completeTrade(String tradeNum) {
        FinanceRecord record = null;
        try {
            record = financeMapper.selectByTradeNum(tradeNum);
        } catch (Exception ignored) {
        }
        if (record == null) {
            return false;
        }
        if (record.getState().equals(FINISHED)) {
            return true;
        }
        record.setState(FINISHED);
        financeMapper.updateRecordSelective(record);
        return updateCustomerBalance(record.getCustomerId(), record.getAmount());
    }

    @Override
    public boolean validateCustomer(Integer customerId) {
        return customerForService.viewCustomerInfo(customerId) != null;
    }

    @Override
    public CompositionDetail getPurchaseRecordByCompositionId(Integer compositionId) {
        CompositionDetail compositionDetail = new CompositionDetail();
        compositionDetail.setCompositionId(compositionId);

        List<FundComposition> fundCompositions = fundCompositionMapper.selectFundCompositionByCompositionId(compositionId);
        List<Integer> fundIds = fundCompositions.stream().map(FundComposition::getFundId).collect(Collectors.toList());
        List<Fund> funds = fundForService.getFundFromFundIds(fundIds);

        ArrayList<CompositionDetail.FundWithPercentage> fundsWithPercentage = new ArrayList<>();
        for (Fund fund : funds) {
            CompositionDetail.FundWithPercentage fundWithPercentage = new CompositionDetail.FundWithPercentage();
            fundWithPercentage.setPercentage(fundCompositions.stream().filter(fundComposition -> fundComposition.getFundId().equals(fund.getFundId())).map(FundComposition::getFundPercentage).collect(Collectors.toList()).get(0));
            fundWithPercentage.setFund(fund);
            fundsWithPercentage.add(fundWithPercentage);
        }
        compositionDetail.setFundsWithPercentage(fundsWithPercentage);
        return compositionDetail;
    }

    @Override
    public List<PurchaseRecord> getPurchaseRecordsByCustomerIdInSoldTime(Integer customerId) {
        return financeMapper.selectPurchaseRecordsByCustomerIdInSoldTime(customerId);
    }

    @Override
    public List<PurchaseRecord> getPurchaseRecordsByCustomerIdInPurchaseTime(Integer customerId) {
        return financeMapper.selectPurchaseRecordsByCustomerIdInPurchaseTime(customerId);
    }

    @Override
    public List<PurchaseRecord> getPurchaseRecordsByCustomerIdInPurchaseAmount(Integer customerId) {
        return financeMapper.selectPurchaseRecordsByCustomerIdInPurchaseAmount(customerId);
    }

    @Override
    public boolean withdraw(Integer customerId, BigDecimal amount, boolean network) {
        FinanceRecord record = new FinanceRecord(customerId, WITHDRAW_REMARK, amount, FINISHED);
        if (updateCustomerBalance(record.getCustomerId(), record.getAmount().negate())) {
            financeMapper.insertRecord(record);
            return true;
        }
        return false;
    }

    @Override
    public List<FinanceRecord> viewAllRecords(Integer customerId) {
        return financeMapper.selectAllRecordByCustomerId(customerId);
    }

    @Override
    @Transactional
    public FinanceRecord addBalanceChangeRecord(int customerId, BigDecimal amount, String remark) throws Exception {
        if (!(remark.equals(BUY_REMARK) || remark.equals(REDEMPTION_REMARK))) {
            throw new Exception(REMARK_ERROR);
        }
        if (amount.signum() <= 0) {
            throw new Exception(AMOUNT_NON_POSITIVE_ERROR);
        }
        if (customerForService.viewCustomerInfo(customerId) == null) {
            throw new Exception(USER_NONEXISTENT_ERROR);
        }
        amount = remark.equals(BUY_REMARK) ? amount.negate() : amount;
        if (!updateCustomerBalance(customerId, amount)) {
            throw new Exception(INSUFFICIENT_BALANCE_ERROR);
        }
        FinanceRecord record = new FinanceRecord(customerId, remark, amount, FINISHED);
        financeMapper.insertRecord(record);
        return record;
    }

    /**
     * 更新用户余额
     *
     * @param customerId 用户id
     * @param amount     变动金额，可为正、负数
     * @return 成功则返回true
     */
    private boolean updateCustomerBalance(int customerId, BigDecimal amount) {
        Customer customer = customerForService.viewCustomerInfo(customerId);
        if (customer.getBalance().negate().compareTo(amount) <= 0) {
            financeMapper.updateCustomerBalance(customerId, amount);
            return true;
        }
        return false;
    }

    @Override
    public CustomerComposition sellFundComposition(Integer compositionId) {
        CustomerComposition customerComposition = customerCompositionMapper.selectCompositionOfCustomerById(compositionId);

        Assert.notNull(customerComposition, PARAMETER_ERROR);
        Assert.isTrue(customerComposition.getSoldTime() == null, ALREADY_SOLD);
        Assert.isTrue(customerComposition.getRequestTime() == null, REPEATED_SOLD_REQUEST);

        customerComposition.setRequestTime(LocalDateTime.now());
        customerCompositionMapper.updateCompositionOfCustomer(customerComposition);
        return customerCompositionMapper.selectCompositionOfCustomerById(compositionId);
    }


    @Scheduled(cron = "0 0 0 1/1 * ?")
    public void completeSold() throws Exception {
        List<CustomerComposition> customerCompositionList = customerCompositionMapper.selectPendingCompositions();//所有待卖出基金组合
        for (CustomerComposition customerComposition : customerCompositionList) {
            List<FundComposition> fundCompositionList = fundCompositionMapper.selectFundCompositionByCompositionId(customerComposition.getCompositionId());

            Assert.isTrue(fundCompositionList.isEmpty(), COMPOSITION_ERROR);

            BigDecimal total = new BigDecimal("0");//卖出总收入
            boolean canSell = true;
            for (FundComposition fundComposition : fundCompositionList) {
                FundNetValue fundNetValue = fundForService.getNewestFundNetValue(fundComposition.getFundId());
                if (date2LocalDateTime(fundNetValue.getTradingTime()).isAfter(customerComposition.getRequestTime())) {//满足可卖出条件
                    BigDecimal share = fundComposition.getFundShare();//份额
                    BigDecimal netValue = fundNetValue.getLatestValue();//基金净值
                    BigDecimal sellOutRate = new BigDecimal("0");//卖出费率
                    //计算费率
                    List<FundOut> fundOuts = fundForService.getFundOut(fundComposition.getFundId());
                    LocalDateTime now = LocalDateTime.now();
                    long days = Duration.between(customerComposition.getPurchaseTime(), now).toDays();
                    for (int i = 1; i < fundOuts.size(); i++) {
                        if (days < fundOuts.get(i).getStartDays()) {
                            sellOutRate = fundOuts.get(i - 1).getRate();
                            break;
                        }
                    }
                    if ((!fundOuts.isEmpty()) && days >= fundOuts.get(fundOuts.size() - 1).getStartDays()) {
                        sellOutRate = fundOuts.get(fundOuts.size() - 1).getRate();
                    }
                    //计算卖出金额
                    BigDecimal all = share.multiply(netValue);//总额
                    BigDecimal rateMoney = all.multiply(sellOutRate.divide(new BigDecimal("100")));//手续费
                    fundComposition.setSoldLoss(rateMoney);
                    fundCompositionMapper.updateFundCompositionItem(fundComposition);//加入基金手续费
                    total = total.add(all.subtract(rateMoney));
                } else {
                    canSell = false;
                    break;
                }
            }
            if (canSell) {
                //修改customerComposition +增加一条financeRecord
                customerComposition.setSoldAmount(total);
                customerComposition.setSoldTime(LocalDateTime.now());
                customerCompositionMapper.updateCompositionOfCustomer(customerComposition);
                addBalanceChangeRecord(customerComposition.getCustomerId(), total, REDEMPTION_REMARK);
            }
        }
    }


    @Override
    @Transactional
    public Integer confirmFundComposition(Integer customerId, Map<Integer, BigDecimal> fundComposition) {
        if (!isPercentSum100(fundComposition.values().iterator())
                || !isFundsExist(fundComposition.keySet().iterator())) {
            LOGGER.error("基金不存在或者份额和不为100");
            return null;
        }

        CustomerComposition customerComposition = new CustomerComposition(customerId);
        customerCompositionMapper.insertCompositionOfCustomer(customerComposition);
        for (Map.Entry<Integer, BigDecimal> entry : fundComposition.entrySet()) {
            FundComposition fundCompositionItem = new FundComposition(customerComposition.getCompositionId(),
                    entry.getKey(), entry.getValue());
            fundCompositionMapper.insertFundCompositionItem(fundCompositionItem);
        }
        return customerComposition.getCompositionId();
    }

    private boolean isPercentSum100(Iterator<BigDecimal> percents) {
        BigDecimal percentSum = new BigDecimal(0);
        while (percents.hasNext()) {
            percentSum = percentSum.add(percents.next());
        }
        return percentSum.multiply(new BigDecimal(100)).intValue() == 100;
    }

    private boolean isFundsExist(Iterator<Integer> funds) {
        while (funds.hasNext()) {
            if (fundForService.findFundById(funds.next()) == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = DuplicateKeyException.class)
    public boolean buyFundComposition(Integer customerId, Integer compositionId, BigDecimal purchaseAmount) {
        if (compositionId <= 0 || purchaseAmount.doubleValue() <= 0) {
            return false;
        }
        List<FundComposition> fundComposition = fundCompositionMapper.selectFundCompositionByCompositionId(compositionId);
        LinkedList<TradeRecord> tradeRecords = new LinkedList<>();
        TradeRecord tradeRecord;
        LocalDateTime purchaseTime = LocalDateTime.now();
        for (FundComposition fundCompositionItem : fundComposition) {
            Fund fund = fundForService.findFundById(fundCompositionItem.getFundId());
            //是否满足最低购买金额
//            BigDecimal minPurchaseAmount = fund.getMinPurchaseAmount();
//            if (purchaseAmount.compareTo(minPurchaseAmount) != 1) {
//                return false;
//            }
            //计算实际购买金额
            List<FundBuyRate> fundBuyRates = fundForService.getFundBuyRate(fundCompositionItem.getFundId());
            fundBuyRates.sort(Comparator.comparingInt(FundBuyRate::getStartAmount));
            BigDecimal fundPurchaseAmount = purchaseAmount.multiply(fundCompositionItem.getFundPercentage());
            BigDecimal purchaseLoss = null;
            for (int i = 1; i < fundBuyRates.size(); i++) {
                if (fundPurchaseAmount.doubleValue() < fundBuyRates.get(i).getStartAmount() * 10000) {
                    FundBuyRate fundBuyRate = fundBuyRates.get(i - 1);
                    if (fundBuyRate.getDescriptionType() == 0) {
                        BigDecimal rate = new BigDecimal(fundBuyRate.getRate().doubleValue() / 100 + 1);
                        purchaseLoss = fundPurchaseAmount.subtract(fundPurchaseAmount.divide(rate, 6, BigDecimal.ROUND_HALF_EVEN));
                    }
                    break;
                }
            }
            if (purchaseLoss == null) {
                purchaseLoss = fundBuyRates.get(fundBuyRates.size() - 1).getRate();
            }
            //是否满足最低份额
            BigDecimal minPart = fund.getMinPart();
            BigDecimal actualPart = new BigDecimal(0);
            List<FundNetValue> netValues = fundForService.getFundNetValue(fund.getFundId());
            for (FundNetValue netValue : netValues) {
                if (netValue.getTradingTime().equals(new Date())) {
                    actualPart = fundPurchaseAmount.subtract(purchaseLoss).divide(netValue.getLatestValue(), 6, BigDecimal.ROUND_HALF_EVEN);
//                    if (actualPart.compareTo(minPart) != 1) {
//                        return false;
//                    }
                    break;
                }
            }
            //计算购买收取的费率
            fundCompositionItem.setFundShare(actualPart);
            fundCompositionItem.setPurchaseLoss(purchaseLoss);
            fundCompositionMapper.updateFundCompositionItem(fundCompositionItem);
            tradeRecord = new TradeRecord(customerId, purchaseTime, actualPart, fund.getType(), fund.getFundCode());
            tradeRecords.add(tradeRecord);
        }
        //更新customer_composition表

        CustomerComposition customerComposition = customerCompositionMapper.selectCompositionOfCustomerById(compositionId);
        customerComposition.setPurchaseAmount(purchaseAmount);
        customerComposition.setPurchaseTime(purchaseTime);
        PythonUtil.addTradeRecords(tradeRecords);
        customerCompositionMapper.updateCompositionOfCustomer(customerComposition);

        try {
            addBalanceChangeRecord(customerComposition.getCustomerId(), purchaseAmount, BUY_REMARK);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void deleteComposition(Integer compositionId) {
        customerCompositionMapper.deleteCompositionOfCustomerById(compositionId);
        fundCompositionMapper.deleteFundCompositionByCompositionId(compositionId);
    }

    /**
     * Date转换为LocalDateTime
     *
     * @param date
     */
    private LocalDateTime date2LocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }
}

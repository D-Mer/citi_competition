package nju.citix.serviceImpl.fund;

import nju.citix.dao.fund.FundMapper;
import nju.citix.po.*;
import nju.citix.service.customer.CustomerForService;
import nju.citix.service.fund.FundForService;
import nju.citix.service.fund.FundService;
import nju.citix.vo.FundForm;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nju.citix.controller.fund.FundController.*;
import static nju.citix.utils.PythonUtil.EXE;
import static nju.citix.utils.PythonUtil.PYPATH;

/**
 * @author GKY
 */
@Service
public class FundServiceImpl implements FundService, FundForService {
    private static final String[] defaultFunds = {"5862", "1033", "1436", "6931", "4", "1", "8"};

    private static List<Map<Integer, BigDecimal>> defaultFundComposition;

    @Override
    public List<Map<Integer, BigDecimal>> getDefaultFundComposition() {
        if (defaultFundComposition != null) {
            return defaultFundComposition;
        } else {
            defaultFundComposition = new ArrayList<>();
            Map<Integer, BigDecimal> composition1 = new HashMap<>();
            composition1.put(Integer.parseInt(defaultFunds[0]), BigDecimal.valueOf(0.02));
            composition1.put(Integer.parseInt(defaultFunds[1]), BigDecimal.valueOf(0.18));
            composition1.put(Integer.parseInt(defaultFunds[2]), BigDecimal.valueOf(0.19));
            composition1.put(Integer.parseInt(defaultFunds[3]), BigDecimal.valueOf(0.06));
            composition1.put(Integer.parseInt(defaultFunds[4]), BigDecimal.valueOf(0.38));
            composition1.put(Integer.parseInt(defaultFunds[5]), BigDecimal.valueOf(0.10));
            composition1.put(Integer.parseInt(defaultFunds[6]), BigDecimal.valueOf(0.07));
            defaultFundComposition.add(composition1);
            Map<Integer, BigDecimal> composition2 = new HashMap<>();
            composition2.put(Integer.parseInt(defaultFunds[0]), BigDecimal.valueOf(0.02));
            composition2.put(Integer.parseInt(defaultFunds[1]), BigDecimal.valueOf(0.20));
            composition2.put(Integer.parseInt(defaultFunds[2]), BigDecimal.valueOf(0.21));
            composition2.put(Integer.parseInt(defaultFunds[3]), BigDecimal.valueOf(0.05));
            composition2.put(Integer.parseInt(defaultFunds[4]), BigDecimal.valueOf(0.37));
            composition2.put(Integer.parseInt(defaultFunds[5]), BigDecimal.valueOf(0.09));
            composition2.put(Integer.parseInt(defaultFunds[6]), BigDecimal.valueOf(0.06));
            defaultFundComposition.add(composition2);
            Map<Integer, BigDecimal> composition3 = new HashMap<>();
            composition3.put(Integer.parseInt(defaultFunds[0]), BigDecimal.valueOf(0.02));
            composition3.put(Integer.parseInt(defaultFunds[1]), BigDecimal.valueOf(0.23));
            composition3.put(Integer.parseInt(defaultFunds[2]), BigDecimal.valueOf(0.24));
            composition3.put(Integer.parseInt(defaultFunds[3]), BigDecimal.valueOf(0.05));
            composition3.put(Integer.parseInt(defaultFunds[4]), BigDecimal.valueOf(0.33));
            composition3.put(Integer.parseInt(defaultFunds[5]), BigDecimal.valueOf(0.08));
            composition3.put(Integer.parseInt(defaultFunds[6]), BigDecimal.valueOf(0.05));
            defaultFundComposition.add(composition3);
        }
        return defaultFundComposition;
    }

    @Resource
    private FundMapper fundMapper;
    @Autowired
    private CustomerForService customerForService;

    /**
     * 增加基金
     *
     * @param fundForm 前端传来的基金信息表单
     * @return 新增的基金对象
     */
    @Override
    @Transactional(rollbackFor = DuplicateKeyException.class)
    public Fund addFund(FundForm fundForm) {
        Fund fund = new Fund();
        fund.setFundCode(fundForm.getFundCode());
        fund.setMinPurchaseAmount(fundForm.getMinPurchaseAmount());
        fund.setMinPart(fundForm.getMinPart());
        fund.setUrl(fundForm.getUrl());
        fundMapper.insert(fund);
        return fund;
    }

    /**
     * 修改基金
     *
     * @param fund 前端传来的基金信息类
     * @return 修改后的基金对象，若基金代码进行了更改，返回null，否则返回更改后的fund
     */
    @Override
    public Fund changeFund(Fund fund) {
        Fund f = fundMapper.selectFundByPrimaryKey(fund.getFundId());

        Assert.notNull(f, FUND_NO_EXIST);
        Assert.isTrue(f.getFundCode().equals(fund.getFundCode()), CODE_SHOULD_NO_CHANGE);

        fundMapper.updateByPrimaryKey(fund);
        return fundMapper.selectFundByPrimaryKey(fund.getFundId());
    }

    /**
     * 查看所有基金（分页）
     *
     * @param pageNum  页码，从1开始
     * @param pageSize 每页中包含的基金信息的数量，大于等于1
     */
    @Override
    public List<Fund> viewAllFunds(Integer pageNum, Integer pageSize) {
        Integer offset = (pageNum - 1) * pageSize;
        Integer rowNums = pageSize;
        return fundMapper.selectAllFundByPage(offset, rowNums);
    }

    /**
     * 根据用户信息生成基金推荐列表（目前此方案仅针对老用户）
     *
     * @param customerId 消费者id
     * @return 基金推荐列表
     */
    @Override
    public List<Fund> recommendFund(Integer customerId) {
        Customer c = customerForService.viewCustomerInfo(customerId);
        Assert.notNull(c, PARAMETER_ERROR);
        List<Fund> recommendList = getRecommendListByUserId(customerId);
        if (recommendList.isEmpty()) {
            recommendList = recommendToNewUser();
        }
        return recommendList;
    }

    @Override
    public List<Fund> getRecommendListByUserId(Integer customerId) {
        return fundMapper.selectRecommendListByUserId(customerId);
    }

    /**
     * 该用户没有推荐信息：使用默认基金推荐列表
     * 这种情况目前算前端没有正确分辨新老用户，没时间防御式编程了
     *
     * @return 基金推荐列表
     */
    private List<Fund> recommendToNewUser() {
        List<Fund> defaultRecommend = new ArrayList<>();
        for (String fundId : defaultFunds) {
            Fund fund = fundMapper.selectFundByPrimaryKey(Integer.valueOf(fundId));
            if (fund != null) {
                defaultRecommend.add(fund);
            }
        }
        return defaultRecommend;
    }

    /**
     * 老用户：根据消费记录生成基金推荐列表；目前已废弃此方案
     *
     * @param customerId 消费者id
     * @return 基金推荐列表
     */
    private List<Fund> recommendToOldUser(Integer customerId) {
        //用户类型
        int userType = judgeUserType(customerId);

        //其他用户数据
        String otherUserData;
        //基金数据
        String fundData;

        //调用推荐算法返回推荐结果
        //一个假的排序
        List<Fund> recommendList = fundMapper.selectFundListOrderedByCodeTopTen();

        return recommendList;
    }

    /**
     * 判断用户是否为老用户（目前已废弃此方案）
     *
     * @param customerId 消费者id
     * @return 用户类型，true：老用户；false：新用户（目前返回恒为true）
     */
    private boolean isOldUser(Integer customerId) {
        return true;
    }


    /**
     * 判断用户类型（K聚类）（目前已废弃此方案）
     *
     * @param customerId 消费者id
     * @return 用户类型(K聚类)：五个用户类别
     */
    private int judgeUserType(Integer customerId) {
        int userType = 0;

//      根据消费记录处理用户数据
//      int userType = K-means(用户数据); //调用方法判断用户类别
        return userType;
    }

    /**
     * 根据id查找基金费率
     *
     * @param fundId 要查找的基金的id
     * @return 需要查找的基金费率列表
     */
    @Override
    public List<FundBuyRate> getFundBuyRate(Integer fundId) {
        return fundMapper.selectFundBuyRateById(fundId);
    }

    /**
     * 根据id查找基金
     *
     * @param fundId 要查找的基金的id
     * @return 需要查找的基金对象，若数据库中没有则返回null
     */
    @Override
    public Fund findFundById(Integer fundId) {
        return fundMapper.selectFundByPrimaryKey(fundId);
    }

    /**
     * 根据code查找基金
     *
     * @param fundCode 要查找的基金的code
     * @return 需要查找的基金对象，若数据库中没有则返回null
     */
    @Override
    public Fund findFundByCode(String fundCode) {
        return fundMapper.selectFundByCode(fundCode);
    }

    /**
     * 管理员和用户查看基金净值
     *
     * @param fundId 对应基金代码
     * @return 指定基金的净值列表
     */
    @Override
    public List<FundNetValue> getFundNetValue(Integer fundId) {
        return fundMapper.selectFundNetValue(fundId);
    }

    @Override
    public void deleteRecommendList() {
        fundMapper.deleteRecommendList();
    }

    @Override
    public void insertRecommendList(List<Recommend> recommendList) {
        fundMapper.insertRecommendList(recommendList);
    }

    @Override
    public List<Fund> getFundFromFundIds(List<Integer> fundIds) {
        return fundMapper.selectAllByFundIdIn(fundIds);
    }

    @Override
    public FundNetValue getNewestFundNetValue(Integer fundId) {
        return fundMapper.selectNewestFundNetValue(fundId);
    }

    @Override
    public List<FundOut> getFundOut(Integer fundId) {
        return fundMapper.selectFundOutById(fundId);
    }

    @Override
    public Map<Integer, BigDecimal> getFundComposition(Integer customerId, List<Integer> funds) {
        return callRecommendAlgorithm(customerId, funds);
    }

    /**
     * 调用推荐算法
     *
     * @param customerId 用户id
     * @param funds      用户选择的基金
     * @return 返回基金配比组合，key:基金id，value:基金占比
     */
    private Map<Integer, BigDecimal> callRecommendAlgorithm(Integer customerId, List<Integer> funds) {
        Map<Integer, BigDecimal> fundComposition = new HashMap<>();
        String percentages = callRecommendFunction(customerId, funds);
        if (percentages != null) {
            percentages = percentages.split("[\\[\\]]")[1];
            int i = 0;
            for (String percentage : percentages.split(",")) {
                percentage = percentage.trim();
                if (Double.parseDouble(percentage) >= 0.0001) {
                    fundComposition.put(funds.get(i), new BigDecimal(percentage));
                }
                i += 1;
            }
        } else {
            BigDecimal percentage;
            try {
                percentage = new BigDecimal(String.valueOf(1.0 / funds.size()).substring(0, 5));
            } catch (IndexOutOfBoundsException e) {
                percentage = new BigDecimal(String.valueOf(1 / funds.size()));
            }
            for (Integer fund : funds) {
                fundComposition.put(fund, percentage);
            }
            fundComposition.replace(funds.get(0), new BigDecimal(1 - Double.parseDouble(String.valueOf(percentage)) * funds.size()));
        }
        return fundComposition;
    }

    @Nullable
    private static String callRecommendFunction(Integer customerId, List funds) {
        try {
            String cmdArgs = EXE + " -u " + PYPATH + "compistinon.py " + customerId + " " + listToString(funds);
            Process process = Runtime.getRuntime().exec(cmdArgs);
            InputStream pis = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(pis, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String out = bufferedReader.readLine();
            bufferedReader.close();
            isr.close();
            pis.close();
            process.getOutputStream().close();
            return out;
        } catch (IOException e) {
            return null;
        }
    }

    public static String listToString(List list) {
        StringBuilder sb = new StringBuilder();
        for (Object o : list) {
            sb.append(o).append(" ");
        }
        return sb.toString().substring(0, sb.toString().length() - 1);
    }
}

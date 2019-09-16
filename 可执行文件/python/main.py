import pandas as pd
import pretreatment
import classifier
import Recommend
import OldRecommend as OR
import GenerateAttrs_Thread as GA_T
import CreatePoint_csv as CP
import ReadPoint_csv as RP
import Standardization as SD
import warnings
import datetime
# import sys
import os

try:
    warnings.filterwarnings('ignore')
    directory = "." + os.sep + "python" + os.sep
    charSet = "utf-8"
    recommendFile = "recommendFile.csv"
    tradeRecords = "交易记录.csv"
    # if len(sys.argv) >= 2:
    # 	tradeRecords = "交易记录_小.csv"

    print("initialized success...")
    '''
    +-----------------------------PART ONE 用户聚类------------------------------+

    '''
    print("start classifing...")

    # 读取所有交易记录
    records = pd.read_csv(directory + tradeRecords, encoding=charSet, index_col=0)
    # 计算聚类指标
    index = pretreatment.calculate_index(records)
    # 用户聚类
    client = classifier.classify(index)
    # 将用户信息写入文件
    client.to_csv(directory + '用户记录.csv', encoding=charSet)

    print("classify success...")
    '''
    +-----------------------------PART TWO 新基金推荐------------------------------+

    '''
    print("start recommend new fund...")

    # 文件读
    fundOld = pd.read_csv(directory + "旧基金2.0.csv", encoding=charSet)
    fundNew = pd.read_csv(directory + "新基金2.0.csv", encoding=charSet)
    RawData = pd.read_csv(directory + "用户记录.csv", encoding=charSet)
    TradeFrame = pd.read_csv(directory + tradeRecords, encoding=charSet)

    # 爬取基金属性
    eigenvectorOld = GA_T.main(fundOld)     # 旧基金属性
    eigenvectorNew = GA_T.main(fundNew)     # 新基金属性
    eigenvectorOld.to_csv(directory + "基金属性_旧基金2.0.csv", encoding=charSet, index=False)
    eigenvectorNew.to_csv(directory + "基金属性_新基金2.0.csv", encoding=charSet, index=False)
    # eigenvectorOld = pd.read_csv(directory + "基金属性_旧基金2.0.csv", encoding=charSet)
    # eigenvectorNew = pd.read_csv(directory + "基金属性_新基金2.0.csv", encoding="charSet)

    # 生成评分
    PointCSV = CP.createPoint(RawData, TradeFrame)  # 评分
    PointCSV = SD.Standardization_4(PointCSV)
    PointCSV.to_csv(directory + 'Point.csv', encoding=charSet, index=False)
    # PointCSV = pd.read_csv(directory + "Point.csv", encoding=charSet)

    # 生成喜好向量
    FavorFrame = RP.main(PointCSV, eigenvectorOld)  # 喜好向量
    # FavorFrame.to_csv("FavorFrame.csv", encoding=charSet, index=False)
    # FavorFrame = pd.read_csv("FavorFrame.csv", encoding=charSet)

    # 生成新基金推荐列表
    recommendFrame = Recommend.newFundRecommend(FavorFrame, eigenvectorNew)     # 新基金推荐列表
    recommendFrame.to_csv(directory + recommendFile, encoding=charSet, index=False)

    print("New Fund Recommend Success!")
    '''
    +-----------------------------PART Three 旧基金推荐------------------------------+

    '''
    print("start recommend old fund...")

    # 读取评分矩阵
    PointFrame = pd.read_csv(directory + 'Point.csv')
    # 生成旧基金推荐列表
    OldFundFrame = OR.SVDPP(PointFrame)
    OldFundFrame.to_csv(directory + "RecommendList.csv", encoding=charSet,index=False)

    print("Old Fund Recommend Success!")
    '''
    +-----------------------------PART Four 新旧基金整合------------------------------+

    '''
    print("start sort out results...")

    # 整合新旧基金
    newFundFrame = pd.read_csv(directory + "recommendFile.csv",encoding=charSet)
    Frame = Recommend.FundRecommend(OldFundFrame,newFundFrame)
    Frame.to_csv(directory + "RecommendRes.csv",encoding=charSet,index=False)

    print("Over!")
except Exception:
    print("ERROR")
    print(datetime.datetime.now())
    print("Over!")
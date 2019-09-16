# !/usr/bin/env python
# -*- coding: utf-8 -*-
# Author:DW 
# date:2019/8/21
import json

import mysql.connector
from requests import request


def get_east_money_detail(fund_id, code):
    header = {
        "DNT": "1",
        "Referer": "http://fundf10.eastmoney.com",
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                      "(KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36"
    }
    url = "http://api.fund.eastmoney.com/f10/lsjz"
    params = {
        "fundCode": code,
        "pageIndex": 1,
        "pageSize": 120,
    }
    response = request('GET', url, headers=header, params=params)
    history_values = json.loads(response.text)['Data']['LSJZList']
    detail = []
    for i in history_values:
        if i['JZZZL'] != '':
            detail.append((fund_id, i['FSRQ'], i['DWJZ'], i['JZZZL']))
    if len(detail) == 0:
        raise FileNotFoundError
    return detail


class Database:
    def __init__(self):
        ip = "jh.dwxh.xyz"
        port = 3306
        user = "root"
        psw = "123456"
        dbname = "citix"
        try:
            con = mysql.connector.connect(
                host=ip,
                user=user,
                password=psw,
                port=port,
                database=dbname,
                charset='utf8',
                buffered=True
            )
            self.con = con  # con在其他类方法中还要多次调用，所以定义为成员变量
        except mysql.connector.Error as e:
            print('连接失败', str(e))

    def insert_tb(self, sql, data):
        cursor = self.con.cursor()
        try:
            cursor.executemany(sql, data)
            self.con.commit()
            # print('数据插入成功')
        except mysql.connector.errors.IntegrityError:
            pass
        except mysql.connector.Error as e:
            self.con.rollback()
            with open('err_sql.txt', 'a') as f:
                f.write(f'插入失败 {e},数据 {data}\n\n')
            print(f'插入失败 {e},数据 {data}')
        finally:
            cursor.close()

    def select_tb(self, sql):
        cursor = self.con.cursor(dictionary=True)
        cursor = self.con.cursor()
        cursor.execute(sql)
        return cursor.fetchall()

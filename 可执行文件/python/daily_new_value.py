# !/usr/bin/env python
# -*- coding: utf-8 -*-
# Author:DW 
# date:2019/9/3
# 日常：获取基金最新净值
import datetime
import json

from bs4 import BeautifulSoup as bs
from database import Database
from mysql.connector import IntegrityError
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
        "pageSize": 10,
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


def get_cfi_detail(fund_id, code):
    url = f'http://quote.cfi.cn/quote_{code}_4.html'
    response = request('GET', url)
    soup = bs(response.text, 'html5lib')
    url = "http://quote.cfi.cn/" + soup.find('a', text='基金净值')['href']
    response = request('GET', url)
    soup = bs(response.text, 'html5lib')
    font = soup.find('td', text='交易日').parent
    current = font
    detail = []
    for i in range(20):
        current = current.next_sibling
        try:
            date = current.next_element.text
            latest = current.next_element.next_sibling.text
            daily = current.next_element.next_sibling.next_sibling.text.strip("%")
            weekly = current.next_element.next_sibling.next_sibling.next_sibling.text.strip("%")
            monthly = current.next_element.next_sibling.next_sibling.next_sibling.next_sibling.text.strip("%")
            three_month = current.next_element.next_sibling.next_sibling.next_sibling.next_sibling.next_sibling \
                .text.strip("%")
            weekly = None if weekly == '--' else weekly
            monthly = None if monthly == '--' else monthly
            three_month = None if three_month == '--' else three_month
            detail.append((fund_id, date, latest, daily, weekly, monthly, three_month))
        except AttributeError:
            if len(detail) == 0:
                raise TypeError
            else:
                detail.pop()
            break
    return detail


if __name__ == '__main__':
    print(datetime.datetime.now())
    try:
        db = Database()
        find_sql = 'select fund_id, fund_code from fund order by fund_id'
        datas = db.select_tb(find_sql)
        insert_east_money_sql = "insert into fund_netvalue(fund_id, trading_time, latest_value, daily_return) " \
                                "VALUES (%s,%s,%s,%s)"
        insert_cfi_sql = "insert into fund_netvalue" \
                         "(fund_id, trading_time, latest_value, daily_return,weekly_return,monthly_return,three_months_return) " \
                         "VALUES (%s,%s,%s,%s,%s,%s,%s)"
        i = 0
        for data in datas:
            try:
                if i % 500 == 0:
                    print(i)
                i += 1
                try:
                    for detail in get_east_money_detail(data[0], data[1]):
                        try:
                            db.insert_tb(insert_east_money_sql, [detail])
                        except IntegrityError:
                            break
                except FileNotFoundError:
                    for detail in get_cfi_detail(data[0], data[1]):
                        try:
                            db.insert_tb(insert_cfi_sql, [detail])
                        except IntegrityError:
                            break
            except TypeError:
                continue
        print(datetime.datetime.now())
        print("Over!")
    except Exception as e:
        print(f"{str(e)} ======== error")
        print(datetime.datetime.now())
        print("Over!")

# !/usr/bin/env python
# -*- coding: utf-8 -*-
# Author:DW 
# date:2019/8/22
# 3.获取基金历史净值
import json

from bs4 import BeautifulSoup as bs
from requests import request

from database import Database


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
    for i in range(120):
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
    db = Database('localhost', '3306', 'root', 'root', 'citix')
    find_sql = 'select fund_id, fund_code from fund where not  EXISTS( select fund_id from fund_netvalue where fund.fund_id = fund_netvalue.fund_id )'
    datas = db.select_tb(find_sql)
    insert_east_money_sql = "insert into fund_netvalue(fund_id, trading_time, latest_value, daily_return) " \
                            "VALUES (%s,%s,%s,%s)"
    remove_sql = 'delete from fund where fund_id = %s'
    insert_cfi_sql = "insert into fund_netvalue" \
                     "(fund_id, trading_time, latest_value, daily_return,weekly_return,monthly_return,three_months_return) " \
                     "VALUES (%s,%s,%s,%s,%s,%s,%s)"
    i = 1
    for data in datas:
        i += 1
        j = '\n' if i % 20 == 0 else ' '
        print(i, end=j)
        try:
            db.insert_tb(insert_east_money_sql, get_east_money_detail(data[0], data[1]))
        except FileNotFoundError:
            print("get from cfi")
            try:
                db.insert_tb(insert_cfi_sql, get_cfi_detail(data[0], data[1]))
            except TypeError:
                print(" ")
                print(f"delete:=== {data}")
                db.insert_tb(remove_sql, [(data[0],)])

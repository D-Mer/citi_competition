# !/usr/bin/env python
# -*- coding: utf-8 -*-
# Author:DW 
# date:2019/8/22
# 4.获取基金购入费率
from bs4 import BeautifulSoup as bs
from requests import request

from database import Database


def fee_east_money(fund_id, code):
    url = f'http://fundf10.eastmoney.com/jjfl_{code}.html'
    response = request('GET', url)
    soup = bs(response.text, 'html5lib')
    if soup.find("label", text="申购费率（前端）") is None:
        raise AttributeError
    fees = soup.find('label', text='申购费率（前端）').parent.parent.find("tbody").contents
    details = []
    for fee in fees:
        buy_range = fee.find('td', class_='th').text
        if buy_range.find("，") == -1:
            if buy_range.find("小于") != -1:
                buy_range = 0
                d_type = 0
                percentage = fee.next_element.next_sibling.next_sibling.text.split("|")[0].strip()[:-1]
            elif buy_range.find("大于") != -1:
                buy_range = fee.find('td', class_='th').text[4:-2]
                d_type = 1
                percentage = fee.next_element.next_sibling.next_sibling.text.split("|")[0].strip()[2:-1]
            else:
                buy_range = 0
                d_type = 0
                percentage = 0
        else:
            buy_range = fee.find('td', class_='th').text.split("，")[0][4:-2]
            d_type = 0
            percentage = fee.next_element.next_sibling.next_sibling.text.split("|")[0].strip()[:-1]
        details.append((fund_id, d_type, buy_range, percentage))
    return details


if __name__ == '__main__':
    db = Database('localhost', '3306', 'root', 'root', 'citix')
    find_sql = 'select fund_id,fund_code from fund order by fund_id'
    datas = db.select_tb(find_sql)
    insert_sql = 'insert into fund_buy_rates(fund_id, description_type, start_amount, rate) ' \
                 'VALUES (%s,%s,%s,%s)'
    remove_sql = 'delete from fund where fund_id = %s'
    for data in datas:
        try:
            detail = fee_east_money(data[0], data[1])
            db.insert_tb(insert_sql, detail)
        except AttributeError:
            print(" ")
            print(f"delete:=== {data}")

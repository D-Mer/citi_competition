# !/usr/bin/env python
# -*- coding: utf-8 -*-
# Author:DW 
# date:2019/8/19
# 2.获取基金详情
import json

from bs4 import BeautifulSoup as bs
from requests import request

from database import Database


def get_cfi_link(fund_code):
    source = 'http://quote.cfi.cn'
    url = f'http://quote.cfi.cn/quote_{fund_code}_4.html'
    response = request('GET', url)
    soup = bs(response.text, 'html5lib')
    links = {
        'detail': source + soup.find('a', text='基金档案')['href'],
        'manager': source + soup.find('a', text='基金经理')['href']
    }
    return links


def get_cfi_detail(link, fund_id):
    response = request('GET', link['detail'])
    soup = bs(response.text, 'html5lib')
    name = soup.find('td', text='基金名称').next_sibling.text.strip()
    pinyin = soup.find('td', text='基金简称拼音').next_sibling.text.strip()
    start_time = soup.find('td', text='设立日期').next_sibling.text.strip()
    type = soup.find('td', text='基金类型').next_sibling.text.strip()
    if type == 'ETF':
        return None
    scale = soup.find('td', text='基金设立规模(份)').next_sibling.text.strip()
    bank = soup.find('td', text='基金托管人').next_sibling.text.strip()
    history = soup.find('td', text='基金历史').next_sibling.text.strip()
    target = soup.find('td', text='投资目标').next_sibling.text.strip()
    fund_range = soup.find('td', text='基金投资范围').next_sibling.text.strip()
    min_part = soup.find('td', text='最低持有份额(份)').next_sibling.text.strip()
    min_part = 0 if min_part == '--' else min_part
    scale = 0 if scale == '--' else scale
    min_amount = 0.0

    response = request('GET', links['manager'])
    manager = bs(response.text, 'html5lib').find('table', id='tabh').next_element.next_element.next_element \
        .next_sibling.next_sibling \
        .next_element.next_sibling \
        .next_element.next_element \
        .next_sibling.next_sibling.next_sibling

    detail = (
        name, pinyin, start_time, type, scale,
        bank, history, target, fund_range, manager.text,
        manager['href'], min_amount, min_part, fund_id
    )
    return detail


def get_east_money_link(fund_code):
    return {
        'detail': f"http://fundf10.eastmoney.com/jbgk_{fund_code}.html",
        'manager': f"http://fundf10.eastmoney.com/jjjl_{fund_code}.html",
        'fee': f"http://fundf10.eastmoney.com/jjfl_{fund_code}.html",
        "pinyin": f"{fund_code}"
    }


def get_east_money_detail(link, fund_id):
    response = request('GET', link['detail'])
    soup = bs(response.text, 'html5lib')
    name = soup.find('th', text='基金全称').next_sibling.text.strip()
    pinyin = None
    start_time = soup.find('th', text='成立日期/规模').next_sibling.text.strip() \
        .split(" ")[0].replace("年", "-").replace("月", "-").replace("日", "-")
    type = soup.find('th', text='基金类型').next_sibling.text.strip()
    if type == 'ETF':
        return None
    scale_str: str = soup.find('th', text='成立日期/规模').next_sibling.text.strip() \
        .split(" ")[2]
    bank = soup.find('th', text='基金托管人').next_sibling.text.strip()
    history = None
    target = soup.find('label', text='投资目标').parent.parent.find('p').text.strip()
    fund_range = soup.find('label', text='投资范围').parent.parent.find('p').text.strip()
    if scale_str.endswith("亿份"):
        scale = float(scale_str.replace(",", "").replace("亿份", "")) * 100000000
    else:
        scale = 0

    response = request('GET', link['fee'])
    soup = bs(response.text, 'html5lib')
    min_part = soup.find('td', text='最小赎回份额').next_sibling.text.strip("份")
    min_amount = soup.find('td', text='申购起点').next_sibling.text.strip("元")
    min_part = 0 if min_part == '---' else min_part
    min_amount = '0' if min_amount == '---' else min_amount
    if min_amount.endswith("万"):
        min_amount = float(min_amount[:-1]) * 10000

    response = request('GET', links['manager'])
    manager = bs(response.text, 'html5lib').find('strong', text='姓名：').next_sibling

    url = "http://fundsuggest.eastmoney.com/FundSearch/api/FundSearchAPI.ashx"
    params = {
        "key": links['pinyin'],
        "m": 1,
    }
    try:
        response = request('GET', url, params=params)
        info = json.loads(response.text)['Datas'][0]
        pinyin = info['JP']
    except IndexError:
        pass
    # print(info['NAME'])

    detail = (
        name, pinyin, start_time, type, scale,
        bank, history, target, fund_range, manager.text,
        manager['href'], min_amount, min_part, fund_id
    )
    return detail


if __name__ == '__main__':
    db = Database('localhost', '3306', 'root', 'root', 'citix')
    find_sql = 'select fund_id,fund_code,manager_link from fund order by fund_id'
    remove_sql = 'delete from fund where fund_id = %s'
    datas = db.select_tb(find_sql)
    insert_sql = 'UPDATE fund ' \
                 'set fund_name=%s' \
                 ',pinyin=%s' \
                 ',start_time=%s' \
                 ',type=%s' \
                 ',scale=%s' \
                 ',manager_bank=%s' \
                 ',fund_history=%s' \
                 ',target=%s' \
                 ',fund_range=%s ' \
                 ',manager=%s ' \
                 ',manager_link=%s ' \
                 ',min_purchase_amount=%s ' \
                 ',min_part=%s ' \
                 'where fund_id=%s'
    details = []
    i = 0
    for data in datas:
        i += 1
        print(i, end=" ")
        if data[2] is None:
            try:
                links = get_east_money_link(data[1])
                details = [get_east_money_detail(links, data[0])]
                if details is None:
                    continue
                db.insert_tb(insert_sql, details)
            except AttributeError:
                try:
                    print(" ")
                    print(data)
                    print("get from cfi")
                    links = get_cfi_link(data[1])
                    details = [get_cfi_detail(links, data[0])]
                    if details is None:
                        continue
                    db.insert_tb(insert_sql, details)
                except TypeError:
                    print(f"delete:=== {data}")
                    db.insert_tb(remove_sql, [(data[0],)])
            except IndexError:
                print(" ")
                print(f"delete:=== {data}")
                db.insert_tb(remove_sql, [(data[0],)])

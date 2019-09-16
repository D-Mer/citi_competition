# !/usr/bin/env python
# -*- coding: utf-8 -*-
# Author:DW 
# date:2019/8/23
# 5.获取基金卖出费率

from bs4 import BeautifulSoup as bs
from requests import request

from database import Database


def fee_east_money(fund_id, code):
    url = f'http://fundf10.eastmoney.com/jjfl_{code}.html'
    response = request('GET', url)
    soup = bs(response.text, 'html5lib')
    if soup.find("th", text="赎回费率", class_='last fl') is None:
        raise AttributeError
    fees = soup.find("th", text="赎回费率", class_='last fl').parent.parent.parent.find("tbody").contents
    details = []
    for fee in fees:
        day_range = fee.find('td', class_='th').next_sibling.text
        if day_range == '---':
            details.append((fund_id, 0, 0))
            return details
        if day_range.find("，") == -1:
            if day_range.find("小于") != -1:
                day_range = 0
                percentage = fee.find('td', class_='th').next_sibling.next_sibling.text.split("|")[0].strip()[:-1]
            elif day_range.find("大于") != -1:
                day_range = day_range.replace("个", "")[2:]
                if day_range.startswith("等于"):
                    t = 0
                    day_range = day_range[2:]
                else:
                    t = 1
                if day_range[-1] == '年':
                    day_range = float(day_range[:-1]) * 365 + t
                elif day_range[-1] == '月':
                    day_range = float(day_range[:-1]) * 30 + t
                else:
                    day_range = float(day_range[:-1]) + t
                percentage = fee.find('td', class_='th').next_sibling.next_sibling.text.split("|")[0].strip()[:-1]
            else:
                with open("error_out_fee.txt", "a") as f:
                    f.writelines(f"错误基金id：{fund_id}， 错误基金代码：{code}\n")
                raise FileNotFoundError
        else:
            day_range = fee.find('td', class_='th').next_sibling.text.split("，")[0].replace("个", "")[2:]
            if day_range.startswith("等于"):
                t = 0
                day_range = day_range[2:]
            else:
                t = 1
            if day_range[len(day_range) - 1] == '年':
                day_range = float(day_range[:-1]) * 365 + t
            elif day_range[-1] == '月':
                day_range = float(day_range[:-1]) * 30 + t
            else:
                day_range = float(day_range[:-1]) + t
            percentage = fee.find('td', class_='th').next_sibling.next_sibling.text.split("|")[0].strip()[:-1]
        details.append((fund_id, day_range, percentage))
    return details


if __name__ == '__main__':
    db = Database('localhost', '3306', 'root', 'root', 'citix')
    find_sql = 'select fund_id,fund_code from fund order by fund_id'
    datas = db.select_tb(find_sql)
    insert_sql = 'insert into fund_out(fund_id, start_days, rate) ' \
                 'VALUES (%s,%s,%s)'
    for data in datas:
        try:
            detail = fee_east_money(data[0], data[1])
            db.insert_tb(insert_sql, detail)
        except FileNotFoundError:
            pass
        except Exception as e:
            with open("error_out_fee.txt", "a") as f:
                f.writelines(f"错误情况{e.__str__()}\n\n")

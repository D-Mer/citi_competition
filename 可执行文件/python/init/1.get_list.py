# !/usr/bin/env python
# -*- coding: utf-8 -*-
# Author:DW
# date:2019/8/22
# 1.获取基金列表

from bs4 import BeautifulSoup as bs
from requests import request

from database import Database

if __name__ == '__main__':
    db = Database("localhost", "3306", "root", "root", "citix")
    pattern = r"http://quote.cfi.cn/quote_\d{6}_4.html"
    sql = "insert into fund (fund_code, abbreviation,invest_type,manager_company) values (%s, %s, %s, %s)"
    for i in range(438):
        datas = []
        cfi_url = f"http://quote.cfi.cn/FundFilter.aspx?pgIndex={i}&field=ynhbl&sort=desc"
        response = request("GET", cfi_url)
        soup = bs(response.text, 'html5lib')
        table_content = soup.find("div", class_="tabResult").next_element.next_element.contents
        for j in range(1, len(table_content)):
            content = table_content[j].contents
            data = {
                'abbreviation': content[0].text,
                'fund_code': content[1].text,
                'type': content[2].text,
                'invest_type': content[3].text,
                'latest_value': content[6].text,
                'weekly_return': content[7].text,
                'monthly_return': content[8].text,
                'manager_company': content[12].text}
            if data['invest_type'] == 'ETF':
                continue
            datas.append(data)
        insert_obt = []
        for data in datas:
            obj = (
                data['fund_code'], data['abbreviation'], data['invest_type'],
                data['manager_company'])
            insert_obt.append(obj)
        db.insert_tb(sql, insert_obt)

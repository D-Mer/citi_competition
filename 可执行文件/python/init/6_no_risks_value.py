# !/usr/bin/env python
# -*- coding: utf-8 -*-
# Author:DW 
# date:2019/9/10
import json
from datetime import date

from requests import request

from database import Database

header = {
    "X-Requested-With": "XMLHttpRequest",
    "Referer": "https://cn.investing.com/rates-bonds/china-1-year-bond-yield",
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                  "(KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36"
}
url = "https://cn.investing.com/common/modules/js_instrument_chart/api/data.php"
params = {
    "pair_id": 29231,
    "chart_type": "area",
    "pair_interval": 86400,
    "candle_count": 120
}
response = request('GET', url, headers=header, params=params)
history_values = json.loads(response.text)['candles']

insert_sql = "insert into no_risk_fee(fee_date, fee) values(%s, %s)"
db = Database('localhost', '3306', 'root', 'root', 'citix')
for data in history_values:
    db.insert_tb(insert_sql, [(date.fromtimestamp(data[0] / 1000), data[1])])

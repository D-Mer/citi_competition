# !/usr/bin/env python
# -*- coding: utf-8 -*-
# Author:DW 
# date:2019/9/13

import sys

import numpy as np

from database import Database

db = Database('localhost', '3306', 'root', 'root', 'citix')


def get_values(fundId, n) -> list:
    find_sql = f'select fund_id, latest_value from fund_netvalue where fund_id={fundId} order by trading_time desc limit {n + 1}'
    value = db.select_tb(find_sql)
    return value


def get_questionnaire(userId) -> float:
    find_sql = f'select answers from questionnaire where customer_id={userId} limit 1'
    try:
        value = db.select_tb(find_sql)[0][0]
        print(value)
        score = 0
        for i in (9, 10, 13, 15, 16, 17, 18):
            score += score_cau(value[i - 1])
        score += score_cau_exp(value[10])
        score += score_cau_exp(value[11])
        return (45 - score) / (45 - 9)
    except IndexError:
        return 1


def score_cau(a_char: str):
    a_char = a_char.lower()
    if a_char == 'a':
        return 1
    if a_char == 'b':
        return 2
    if a_char == 'c':
        return 3
    if a_char == 'd':
        return 4
    if a_char == 'e':
        return 5


def score_cau_exp(a_char: str):
    a_char = a_char.lower()
    if a_char == 'a':
        return 1
    if a_char == 'b':
        return 3
    if a_char == 'c':
        return 5


# 求解公式,返回列表 N只基金，n个样本;r:基金收益期望向量；u:问卷指数
def solve(r, u):
    N = len(r)
    n = len(r[0])
    rf = 1.8
    Rf = [rf] * N
    I = [1] * N
    # 基金期望收益率
    R = []
    for r0 in r:
        R.append(sum(r0) / n)
    E = [[0] * N for i in range(N)]
    for i in range(N):
        for j in range(N):
            s = 0
            for t in range(n):
                s = s + (R[i] - r[i][t]) * (R[j] - r[j][t])
            E[i][j] = s / n
    E0 = np.mat(E)
    E_inv = np.linalg.pinv(E0)
    I0 = np.mat(I).T
    R0 = np.mat(R).T
    Rf0 = np.mat(Rf).T
    W0 = E_inv * I0 / (I0.T * E_inv * I0) + ((1.0 - u) / (2.0 - u)) * (
            E_inv * (R0 - Rf0) - E_inv * I0 * (I0.T * E_inv * (R0 - Rf0) / (I0.T * E_inv * I0)))
    W1 = W0.tolist()
    W = [w1[0] for w1 in W1]
    S = -W0.T * (R0 - Rf0) * (1.0 - u) + W0.T * E0 * W0 * u
    return (W, S)


# 把基金i的收益率全部换为0的函数
def r_zero(r, i) -> [list]:
    r_new = r
    r_new[i] = [0] * len(r[0])
    return r_new


def W_zero(W) -> [bool]:
    for w in W:
        if w < 0:
            return False
    return True


def fix(W: list) -> [list]:
    W = [round(a, 3) for a in W]
    a = 0
    for w in W:
        if w != 0:
            a = w
    W[W.index(a)] = 1 - sum(W) + a
    return W


if __name__ == '__main__':
    N = len(sys.argv) - 3
    n = 70
    r = []
    for i in range(3, N + 3):
        values = get_values(sys.argv[i], n)
        Rit = []
        for j in range(len(values) - 7):
            Rit.append(float(values[j][1] / values[j + 7][1]) - 1)
        r.append(Rit)
    u = 1

    W = solve(r, u)[0]
    while True:
        if W_zero(W):
            break
        else:
            S = []
            W_new = []
            for i in range(len(W)):
                if abs(W[i]) < 0.009 or W[i] < 0:
                    r_new = r_zero(r, i)
                    (W_, S_) = solve(r_new, u)
                    W_new.append(W_)
                    S.append(S_)
            num = S.index(min(S))
            W = W_new[num]
    W = fix(W)
    print(W)

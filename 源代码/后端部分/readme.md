# readme

## 说明

该份文件是程序的后端部分，主要使用了以下工具：

1. IDEA，作为Java开发的IDE。
2. MYSQL，作为数据库。
3. Maven，作为后端依赖的管理工具
4. Spring Boot，作为整个后端的主要框架
5. Pycharm，作为Python开发的IDE
6. pip包管理工具及相关库。

需要运行本后端程序，请确保计算机至少安装了以下程序：

- jre 8.0 或者以上版本
- maven 3.6 或者以上版本
  - 第一次运行需要连接网络下载相关依赖库
- python 3.6 或者以上版本
- pip 3 ，并安装了一下依赖：
  - pandas
  - numpy
  - surprise
  - requests
  - bs4
  - sklearn
  - mysql-connector-python
  - html5lib
  - datetime

## 准备数据库

首先使用MySQL命令行创建数据库，在`windows`系统中使用`cmd`或者`powershell`；在`Linux`系统中打开您的`terminal`，进入`sql`文件夹下，执行以下命令：

```shell
mysql -u[username] -p[password]
```

登入您的数据库；之后在交互式窗口中继续输入下列命令创建您的数据表：

```mysql
DROP DATABASE IF EXISTS citix;
CREATE DATABASE citix;
SOURCE [tableName].sql;
```

注：

1. 如果您是`Linux`系统用户，则`SOURCE`命令可以不执行，使用`exit`命令退出MySQL交互窗口后在`terminal`中使用`mysql -u[username] -p citix < [tableName].sql`来导入数据库

2. 上述内容中，`[username]`指您的数据库用户名；`[password]`指您的数据库密码；[tableName]指您将要导入的数据库表名；如果要快速开始，请导入`initTable.sql`, 这里包含了我们先前已经获得的数据，如果如此，您可以跳过下一个部分；否则请继续阅读[初始化基金表](#获取基金信息)部分内容

## 获取基金信息

如果您在上一个部分中使用的数据库是`createTable.sql`，那么该部分需要您执行相关命令，获取当前我国常见的基金数据。

请注意：**该部分由于数据所限和技术，使用了爬虫技术，所有的数据均来自于[天天基金网](http://fund.eastmoney.com/)和[中财网](http://www.cfi.net.cn/)，请勿滥用本程序影响相关网站的正常功能；用于滥用引起的一切后果，本程序编写者不承担任何责任！**

由于使用了爬虫，故该部分代码执行时间都比较久；由于没有有效的基金信息筛选，有些基金可能被存到数据库中，所以在后续程序中可能出现一些不可预料的错误。

进入到python文件里的init文件夹下。
首先打开`database.py`，将`ip = "jh.dwxh.xyz" port = 3306 user = "root" psw = "123456"`分别对应修改为自己数据库的地址，端口，用户名，密码。
然后打开命令行依次执行以下命令：

1. `python 1.get_list.py`：该python文件是获取当前市面上的基金简单情况
2. `python 2.get_detail.py`：该python文件是获取上一步获取到基金的详情情况，填充`Fund`表
3. `python 3.get_value_history.py`：该python文件是获取基金的近120天的净值
4. `python 4.get_fee_in.py`：该python文件是获取基金的(前端)申购费率
5. `python 5.get_fee_out.py`：该python文件是获基金的卖出费率
6. `python 6_no_risks_value.py`：该python文件是获取60天内无风险收益净值，这里取的是我国一年期国债的当日净值

经过这个步骤后，`fund`,`fund_buy_rates`,`fund_netvalue`,`fund_out`,`no_risk_fee`表被成功初始化。

## 修改配置文件

打开 src - main - resources - application.yml文件，按照实际需求修改以下部分：

- datasource-url：将mysql数据库地址按自己情况填入
- datasource-username：mysql用户名
- mail-username：发送邮件的用户名
- mail-protocol：发送邮件的协议
- mail-host：邮件服务器地址
- mail-port：邮件服务器端口
- mail-password：邮件供应商提供的授权码，注意此处不是邮箱密码
- profiles的prod节点
    - datasource-password：mysql的密码
- server-port : 改为自己需要发布的端口号

打开 src - nju - citix - config - AlipayConfig.java :

- 字符串`URL`改成自己本程序需要发布的`http://服务器地址:port`
- 字符串`FRONT_URL`改成对应的前端程序需要发布的`http://服务器地址:port`
- 字符串`FROM_EMAIL`改成自己发送信件邮箱

## 打包程序

回到项目的根目录下，执行`mvn clean & mvn package -P prod  -DskipTests`命令，将整个项目打包

## 部署

将打包后的target文件夹下citix-1.0.jar和根路径下python的整个文件夹部署到对应服务器上，请确保服务器有除maven之外的所有运行需要环境，使用java -jar citix-1.0.jar，项目启动成功。
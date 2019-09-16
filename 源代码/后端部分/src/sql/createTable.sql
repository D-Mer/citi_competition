create schema citix collate utf8mb4_unicode_ci;

create table customer
(
    customer_id int auto_increment comment '用户编号'
        primary key,
    username    char(20)                                   not null comment '用户名:字母或者数字开头；允许字母,数字,下划线和点(.)；5~15位之间.',
    password    char(20)                                   not null comment '密码：包含大写字母、小写字母、数字、特殊符号（不是字母，数字，下划线，汉字的字符）的8位以上组合',
    email       char(50)                                   not null comment '邮箱',
    email_valid tinyint(1)     default 0                   not null comment '邮箱是否(0)被验证',
    balance     decimal(11, 2) default 0.00                not null comment '余额',
    bonus       decimal(9, 2)  default 0.00                not null comment '收益',
    join_time   date                                       not null comment '注册时间',
    last_login  timestamp      default current_timestamp() not null on update current_timestamp() comment '最后登录时间',
    question_id int(10)        default -1                  null comment '用户填写的问卷对应id',
    ip          bigint                                     null comment '最后登录ip',
    banned      tinyint(1)     default 0                   not null comment '该账号是否被禁止',
    constraint customer_email_uindex
        unique (email),
    constraint customer_username_uindex
        unique (username)
)
    comment '消费者，即普通用户';

create table customer_composition
(
    composition_id  int auto_increment comment '基金组合id',
    customer_id     int            not null comment '消费者id',
    purchase_amount decimal(20, 4) null comment '买入金额',
    sold_amount     decimal(20, 4) null comment '卖出金额',
    purchase_time   datetime       not null comment '买入时间',
    sold_time       datetime       null comment '卖出时间',
    request_time    datetime       null comment '申请交易时间',
    constraint customer_composition_composition_id_uindex
        unique (composition_id)
);

alter table customer_composition
    add primary key (composition_id);

create table finance_record
(
    id          int auto_increment
        primary key,
    customer_id int            not null,
    remark      varchar(10)    not null,
    trade_time  datetime       null,
    amount      decimal(20, 4) not null,
    state       varchar(10)    null,
    trade_num   varchar(100)   not null,
    constraint finance_record_trade_num_uindex
        unique (trade_num),
    constraint finance_record___customer
        foreign key (customer_id) references customer (customer_id)
            on update cascade on delete cascade
);

create table find_key
(
    find_id      int auto_increment comment '找回编号'
        primary key,
    email        char(50)             not null comment '对应用户邮箱',
    request_time char(15)             not null comment '发出找回请求的时间',
    used         tinyint(1) default 0 not null comment '是否已经被使用',
    constraint find_key_customer_email_fk
        foreign key (email) references customer (email)
            on update cascade on delete cascade
);

create table fund
(
    fund_id             int auto_increment comment '基金在本数据库中的编号'
        primary key,
    fund_code           char(6)                     not null comment '基金代码',
    fund_name           varchar(100)                null comment '基金名称',
    abbreviation        varchar(50)                 null comment '基金简称',
    pinyin              varchar(25)                 null comment '基金简称拼音',
    start_time          date                        null comment '设立日期',
    type                char(10)                    null comment '基金类型',
    scale               decimal(15, 2)              null comment '基金设立规模(份）',
    manager_company     varchar(50)                 null comment '基金管理公司（基金管理人）',
    manager_bank        varchar(50)                 null comment '基金管理银行(基金托管人)',
    fund_history        text                        null comment '基金历史',
    invest_type         varchar(25)                 null comment '投资类型',
    target              text                        null comment '投资目标',
    min_purchase_amount decimal(11, 2) default 0.00 null comment '最低申购金额',
    fund_range          text                        null comment '投资范围',
    min_part            decimal(12, 6)              null comment '最低持有份额',
    manager             varchar(225)                null comment '基金经理',
    manager_link        varchar(225)                null comment '基金经理链接（基金经理详细信息）',
    url                 varchar(100)                null comment '详细信息链接（详细数据）',
    constraint fund_abbreviation_uindex
        unique (abbreviation),
    constraint fund_fund_code_uindex
        unique (fund_code)
);

create table fund_buy_rates
(
    fund_id          int                  not null comment '对应基金代码',
    description_type tinyint(1) default 0 not null comment '费率描述类型：‘0’表示按百分比描述，‘1’表示按每单xx元描述',
    start_amount     int                  not null comment ' 起步金额，万元',
    rate             decimal(11, 2)       not null comment '最高费率:百分比（%）',
    primary key (fund_id, start_amount),
    constraint fund_rates_fund_fund_id_fk
        foreign key (fund_id) references fund (fund_id)
            on update cascade on delete cascade
);

create table fund_composition
(
    composition_id  int            not null comment '基金组合id',
    fund_id         int            not null comment '基金id',
    fund_percentage decimal(11, 2) not null comment '基金占比',
    fund_share      decimal(20, 4) null comment '基金份额',
    purchase_loss   decimal(20, 4) null comment '购买基金扣除的费率',
    sold_loss       decimal(20, 4) null comment '赎回基金扣除的费率'
);

create table fund_netvalue
(
    fund_id             int                         not null comment '对应基金代码',
    trading_time        date                        not null comment '交易日',
    latest_value        decimal(11, 4)              not null comment '最新净值',
    daily_return        decimal(11, 2) default 0.00 not null comment '日回报:百分比（%）',
    weekly_return       decimal(11, 2)              null comment '周回报:百分比（%）',
    monthly_return      decimal(11, 2)              null comment '月回报:百分比（%）',
    three_months_return decimal(11, 2)              null comment '三月回报:百分比（%）',
    primary key (fund_id, trading_time),
    constraint fund_netvalue_fund_fund_id_fk
        foreign key (fund_id) references fund (fund_id)
            on update cascade on delete cascade
);

create table fund_out
(
    fund_id    int           not null,
    start_days int           not null comment '计算费率的起始天数
',
    rate       decimal(5, 2) not null comment '对应抽取的比例
',
    constraint fund_out_fund_fund_id_fk
        foreign key (fund_id) references fund (fund_id)
            on update cascade on delete cascade
);

create table manager
(
    manager_id int auto_increment
        primary key,
    username   varchar(20)          not null,
    password   varchar(20)          not null,
    banned     tinyint(1) default 0 not null,
    last_login datetime             null,
    constraint manager_username_uindex
        unique (username)
);

create table no_risk_fee
(
    fee_date date          not null
        primary key,
    fee      decimal(6, 4) null
);

create table questionnaire
(
    questionnaire_id int auto_increment
        primary key,
    customer_id      int      null,
    score            int(8)   not null,
    answers          char(18) not null,
    last_update      datetime not null,
    constraint Questionnaire_customer
        foreign key (customer_id) references customer (customer_id)
            on update cascade on delete set null
);

create table recommend
(
    customer_id int     not null comment '用户id',
    fund_code   char(6) not null comment '推荐的基金代码'
);


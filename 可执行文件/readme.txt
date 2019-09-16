前端：
用户部分：
	进入目录，执行npm run dev，项目运行在http://localhost:8080
管理员部分：
	进入目录，执行npm run dev，项目运行在http://localhost:8082

后端：
本可执行文件默认内容：
	1. 用户前端路径：http://localhost:8080
	2. 本部分发布路径：http://localhost:8081
	3. 邮箱没有提供密码，无法发送认证邮箱邮件。
	4. mysql程序已经安装，账号：root；密码：123456
	5. python版本3，pip安装以下库:pandas numpy surprise requests bs4 sklearn mysql-connector-python html5lib datetime
	6. java版本8以上
执行步骤：
	1. 导入数据库initTable.sql到mysql
	2. 确保citix-1.0.jar和python文件夹在同一个目录下
	3. java -jar citix-1.0.jar 运行后端程序
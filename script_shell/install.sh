#!/bin/bash

hello(){
	echo "   | |           | |  "  
	echo "___| |_ __ _ _ __| |_ "  
	echo "/ __| __/ _|  |  __| __|" 
	echo "\__ \ || (_| | |  | |_ " 
	echo "|___/\__\__,_|_|   \__|" 
  time3=$(date "+%Y-%m-%d %H:%M:%S")
	echo $time3
}

judge(){
 	if [ $? -eq 0 ]; then
    	echo "________do succes________"
  	else
		echo "________do error________"
	fi
}

stop_tomcat(){
	echo "________正在停止tomcat集群________"
	/root/Student/tomcat-8081/bin/./shutdown.sh
	judge
	/root/Student/tomcat-8082/bin/./shutdown.sh
	judge
	/root/Student/tomcat-8083/bin/./shutdown.sh
	judge
 sleep 5
}

clone_project(){
	if [ -d "/root/Student/linux-exam" ];then
  		echo "________项目存在,拉取更新________"
  		cd /root/Student/linux-exam/
		  git pull origin master
        judge
	else
  		echo "________项目不存在,克隆项目________"
  		git clone git@gitee.com:lplgitee/linux-exam.git
	fi
}

package_api(){
	cd /root/Student/linux-exam/api_servlet/
	echo "________mvn clean________"
	mvn clean
	judge
	echo "________mvn package________"
	mvn package
	judge
}

install_tomcat(){	
	echo "________删除有项目________"
	rm -rf /root/Student/tomcat-8081/webapps/stu
	rm -rf /root/Student/tomcat-8082/webapps/stu
	rm -rf /root/Student/tomcat-8083/webapps/stu
	rm -rf /root/Student/tomcat-8081/webapps/stu.war
	rm -rf /root/Student/tomcat-8082/webapps/stu.war
	rm -rf /root/Student/tomcat-8083/webapps/stu.war
	judge
	echo "________复制新的war包________"
	cp /root/Student/linux-exam/api_servlet/target/*.war /root/Student/tomcat-8081/webapps/
	cp /root/Student/linux-exam/api_servlet/target/*.war /root/Student/tomcat-8082/webapps/
	cp /root/Student/linux-exam/api_servlet/target/*.war /root/Student/tomcat-8083/webapps/
	judge
}

start_tomcat(){
	echo "________启动tomcat集群________"
	/root/Student/tomcat-8081/bin/./startup.sh
	judge
	/root/Student/tomcat-8082/bin/./startup.sh
	judge
	/root/Student/tomcat-8083/bin/./startup.sh
	judge
}

back_path(){
	cd /root/Student/
}

ok(){
	echo "完成部署"
}

main(){
	(( hello && stop_tomcat && package_api && install_tomcat && start_tomcat && back_path && ok ) > /root/Student/install_log.txt )  && ok
	echo "请在/root/Student/install_log.txt中查看脚本执行日志"
}

main
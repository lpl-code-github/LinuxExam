#!/bin/bash

# 检查nginx是否正常
check_nginx(){
  if [ $(ps -C nginx --no-header |wc -l) -eq 0 ];then
    # nginx尝试重启
    /usr/local/nginx/sbin/nginx
    sleep 2
    # 如果nginx启动失败
    if [ $(ps -C nginx --no-header |wc -l) -eq 0 ];then
      # 告警信息写入load_log.txt
      echo "nginx已停止，尝试重启失败，请上服务器检查" >> /root/Student/dingdingROBOT/log/load_log.txt
      sleep 2
      # 调用发送方法
      send
    fi
  fi
}

# 检查tomact是否正常
check_tomact(){
  if [ $(ps -C java --no-header |wc -l) -eq 0 ];then
    # tomact尝试重启
   	/root/Student/tomcat-8081/bin/./startup.sh
	  /root/Student/tomcat-8082/bin/./startup.sh
	  /root/Student/tomcat-8083/bin/./startup.sh
    sleep 2
    # 如果tomact启动失败
    if [ $(ps -C java --no-header |wc -l) -eq 0 ];then
      # 告警信息写入load_log.txt 
      echo "nginx尝试重启失败，已停止tomact集群，请上服务器检查" >> /root/Student/dingdingROBOT/log/load_log.txt
      sleep 2
      # 调用发送方法
      send
      # 结束脚本
      exit
    fi
  fi
}


get_info(){
  # 三个java进程，所以循环三次，分别找1，2，3行的进程pid
  for j in $(seq 1 3)
  do
    pid=`ps -ef | grep java |  egrep -v "grep|vi|tail" | sed -n ${j}p | awk '{print $2}'`
    # 获取此进程pid的cpu使用率
    CPU=`ps -p ${pid} -o pcpu |egrep -v CPU`
    # 获取此进程pid的内存使用率
    MEM=`ps -p ${pid} -o pmem |egrep -v MEM`
    
    # CPU使用率告警
    if [ ${CPU%.*} -gt 5 ];then
      # 将告警日志写入load_log.txt
      echo `date +'%H:%M:%S'` "第"${j}"个java进程的cpu使用率:"${CPU}"%,已超过5%" >> /root/Student/dingdingROBOT/log/load_log.txt
      sleep 2
      # 调用发送方法
      send
      
    fi
    
    sleep 1
    
    # 内存使用率告警
    if [ ${MEM%.*} -gt 5 ];then
      # 将告警日志写入load_log.txt
      echo `date +'%H:%M:%S'` "第"${j}"个java进程的内存使用率:"${MEM}"%,已超过5%" >> /root/Student/dingdingROBOT/log/load_log.txt
      sleep 2
      # 调用发送方法
      send
    fi
  done
}

# 钉钉机器人读取/root/Student/dingdingROBOT/load_log.txt最后一行，发送告警信息
send(){
  # 调用钉钉机器人 并将发送日至写入send_log.txt
  java -jar /root/Student/dingdingROBOT/robot_dingding.jar >> /root/Student/dingdingROBOT/log/send_log.txt
}

# main方法
main(){
  # 检查服务
  check_nginx
  check_tomact
  # 监控负载
  get_info
}

# 执行main方法
main
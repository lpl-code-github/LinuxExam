# 1.  需求分析



# 2. 系统分析

## 2.1 系统架构图



## 2.2 接口设计



## 2.3 数据库设计

### 2.3.1 数据库表设计



### 2.3.2 数据库查询语句



# 3. 设计

## 3.1 Redis缓存



## 3.2 web集群+Nginx

### 3.2.2 web集群

（1）使用tar命令解压apache-tomcat-8.5.72.tar.gz

```shell
tar -zxvf apache-tomcat-8.5.72.tar.gz
```

（2）重命名

```shell
mv apache-tomcat-8.5.72 tomact-8081
```

（3）修改端口号，在tomact目录下的conf中的server.xml，将port改为8081

![image-20211203185642175](https://gitee.com/lplgitee/blog_img/raw/master/img/image-20211203185642175.png)

（4）在webapps/ROOT/WEB-INF/目录下新建lib放使用到的jar包，新建classes放编译好的java，同时修改当前目录下的web.xml，将metadata-complete属性值改为false

![image-20211203185911040](https://gitee.com/lplgitee/blog_img/raw/master/img/image-20211203185911040.png)

（5）将tomact-8081复制两份出来，并改名为tomact-8082、tomact-8083

```shell
cp -r tomact-8081 tomact-8082
cp -r tomact-8081 tomact-8083
```

### 3.2.1 Nginx反向代理及负载均衡

（1）下载Nginx

http://nginx.org/en/download.html

![image-20211203190028667](https://gitee.com/lplgitee/blog_img/raw/master/img/image-20211203190028667.png)

（3）解压

```shell
tar -zxvf nginx-1.21.4.tar.gz
```

（4）安装依赖包

```
yum -y install gcc zlib zlib-devel pcre-devel openssl openssl-devel
```

（5）进入ngxin解压目录，编译安装

```
cd nginx-1.21.4
./configure && make && make install
```

（6）增加反向代理及负载均衡

进入安装目录

```shell
cd /usr/local/nginx/conf
```

在nginx.conf文件中的http{}中配置增加如下配置

```tex
    upstream stu {
    	server 127.0.0.1:8081;
    	server 127.0.0.1:8082;
        server 127.0.0.1:8083;
    }

    server {
        listen       8080;
        server_name  127.0.0.1:8080;

        location / {
            proxy_pass http://stu;
        }
	}
```

> 将8080端口的请求转发到8081、8082、8083端口，并实现轮询负载均衡

## 3.3 前端

### 3.1 前端设计



### 3.2 前端部署

（1）将静态页面及资源放到/usr/local/nginx/html/中

![image-20211203221204712](https://gitee.com/lplgitee/blog_img/raw/master/img/image-20211203221204712.png)

（2）启动nginx

```shell
/usr/local/nginx/sbin/nginx -c /usr/local/nginx/conf/nginx.conf
```

## 3.4 运维自动化

### 3.4.1 shell脚本发布应用



### 3.4.2 定时监控接入钉钉机器人



# 4. 测压






# 1.  需求分析



# 2. 系统分析

## 2.1 系统架构

（1）B/S结构 

浏览器----服务器结构，极少部分事务逻辑在前端（Browser）实现，主要事务逻辑在服务器端（Server）实现

（2）前后端分离

后端负责业务处理和数据持久化，提供相应的接口，前端负责页面渲染，浏览器发送axios请求，然后服务端接受该请求并将 JSON数据返回给浏览器，页面解析JSON数据，通过 dom 操作渲染页面。

## 2.2 代码设计

### 2.2.1 分层

- model层：实体

- dao层：数据业务处理：持久化操作
- service层：业务操作实现类
- controller层：对View提交的请求为其设置对应的Servlet进行特定功能的处理
- until：封装工具类，比如redis，jdbc连接池，统一返回格式Result（消息返回类也可以放在model层，或者controller）

> 目的：高内聚，低耦合

![image-20211204233930197](https://gitee.com/lplgitee/blog_img/raw/master/img/image-20211204233930197.png)

### 2.2.2 代码（省略import导包）

#### （1）model层

```java
package com.lpl.stu.pojo;

public class Student {
  private Integer id;
  private String stuId;
  private String name;
  private String sex;
  private String age;
  private String major;

  ... 这里是 get set 方法 ...
}
```

#### （2）dao层

```java
public class StuDaoImpl {
  static final String SQL_ADD_STUDENT =
      "insert into `t_student` (stu_id,name,age,sex,major) values (?,?,?,?,?);";
  static final String SQL_UPDATE_STUDENT =
      "update t_student set stu_id=?,name=?,age=?,sex=?,major=? where id=?";
  static final String SQL_DELETE_STUDENT = "delete from t_student where id=?";
  static final String SQL_QURERY_STUDENT = "SELECT * FROM t_student where 1=1";

  /**
   * 判断请求参数的数量,拼接sql进行条件查询
   * @param id
   * @param name
   * @param major
   * @return List<Student>结果集
   */
  public List<Student> getStuBy(String id, String name, String major) {
    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement ps = null;

    List<Student> studentList = new ArrayList<Student>();
    String sql = "SELECT * FROM t_student where 1=1 "; // baseSql
    int index = 1; // 填充占位符的索引
    Map<Integer, Object> map = new LinkedHashMap<Integer, Object>(); // key为索引，value为参数，共填充sql占位符时使用

    if (id != null && id.length() != 0) {
      map.put(index, id);
      index++;
      sql += " and id = ? ";
    }
    if (name != null && name.length() != 0) {
      map.put(index, name);
      index++;
      sql += " and name = ? ";
    }
    if (major != null && major.length() != 0) {
      map.put(index, major);
      index++;
      sql += " and major = ? ";
    }
    // System.out.println("拼接的sql语句：" + sql);

    try {
      conn = JdbcUtil.getConnection();
      ps = conn.prepareStatement(sql);
      // 填充占位符
      for (Integer key : map.keySet()) {
        ps.setString(key, map.get(key).toString());
        // System.out.println("第" + key + "个注入的参数：" + map.get(key).toString());
      }
      // ps.executeQuery()查询 返回查询生成的ResultSet对象
      rs = ps.executeQuery();
      while (rs.next()) {
        Student stu = new Student();
        stu.setId(rs.getInt("id"));
        stu.setStuId(rs.getString("stu_id"));
        stu.setName(rs.getString("name"));
        stu.setAge(rs.getString("age"));
        stu.setSex(rs.getString("sex"));
        stu.setMajor(rs.getString("major"));
        studentList.add(stu);
      }
    } catch (SQLException se) {
      se.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      JdbcUtil.close(conn, ps, rs);
    }

    return studentList;
  }

  /**
   * 查询全部
   * @return List<Student>结果集
   */
  public List<Student> getStu() {
    Connection conn = null;
    ResultSet rs = null;
    Statement stmt = null;
    List<Student> studentList = new ArrayList<Student>();

    try {
      conn = JdbcUtil.getConnection();
      stmt = conn.createStatement();
      rs = stmt.executeQuery(SQL_QURERY_STUDENT);
      while (rs.next()) {
        Student stu = new Student();
        stu.setId(rs.getInt("id"));
        stu.setStuId(rs.getString("stu_id"));
        stu.setName(rs.getString("name"));
        stu.setAge(rs.getString("age"));
        stu.setSex(rs.getString("sex"));
        stu.setMajor(rs.getString("major"));
        studentList.add(stu);
      }
      stmt.close();
    } catch (SQLException se) {
      se.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      JdbcUtil.close(conn, rs);
    }

    return studentList;
  }

  /**
   * 通过id删除
   * @param id
   * @return 数据库影响行数
   */
  public int delete(Integer id) {
    Connection conn = null;
    PreparedStatement ps = null;
    int delete = 0;

    try {
      conn = JdbcUtil.getConnection();
      ps = conn.prepareStatement(SQL_DELETE_STUDENT);
      // 填充占位符
      ps.setInt(1, id);
      // 操作数据库影响行数
      delete = ps.executeUpdate();
    } catch (SQLException se) {
      se.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      JdbcUtil.close(conn, ps);
    }

    return delete;
  }

  /**
   * 新增功能
   * @param student
   * @return 数据库影响行数
   */
  public int add(Student student) {
    Connection conn = null;
    PreparedStatement ps = null;
    int add = 0;

    try {
      conn = JdbcUtil.getConnection();
      ps = conn.prepareStatement(SQL_ADD_STUDENT);
      // 填充占位符
      ps.setString(1, student.getStuId());
      ps.setString(2, student.getName());
      ps.setString(3, student.getAge());
      ps.setString(4, student.getSex());
      ps.setString(5, student.getMajor());
      // 操作数据库影响行数
      add = ps.executeUpdate();
    } catch (SQLException se) {
      se.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      JdbcUtil.close(conn, ps);
    }

    return add;
  }

  /**
   * 更新
   * @param student
   * @return 数据库影响行数
   */
  public int update(Student student) {
    Connection conn = null;
    PreparedStatement ps = null;
    int update = 0;

    try {
      conn = JdbcUtil.getConnection();
      ps = conn.prepareStatement(SQL_UPDATE_STUDENT);
      // 填充占位符
      ps.setString(1, student.getStuId());
      ps.setString(2, student.getName());
      ps.setString(3, student.getAge());
      ps.setString(4, student.getSex());
      ps.setString(5, student.getMajor());
      ps.setInt(6, student.getId());
      // 操作数据库影响行数
      update = ps.executeUpdate();
    } catch (SQLException se) {
      se.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      JdbcUtil.close(conn, ps);
    }

    return update;
  }
}
```

#### （3）service层

```java
public class StuServiceImpl {
  // 注入dao层
  StuDaoImpl stuDao = new StuDaoImpl();
  RedisUtil redisUtil = new RedisUtil();

  public Result getStuBy(String id, String name, String major) {
    List<Student> studentList = stuDao.getStuBy(id, name, major);
    return new Result(true, "查询成功", studentList);
  }

  public Result delete(Integer id) {
    if (redisUtil.delete("stu")) { // 删除缓存成功
      int delete = stuDao.delete(id);
      if (delete > 0) {  // 数据库删除成功
        return new Result(true, "删除成功", null);
      } else { // 数据库删除失败
        return new Result(false, "删除失败", null);
      }
    }else { // 如果缓存删除失败，不操作数据库，防止产生脏数据
      return new Result(false, "添加失败", null);
    }
  }

  public Result getStu() {
    List<Student> studentList = stuDao.getStu();
    return new Result(true, "查询成功", studentList);
  }

  public Result add(Student student) {
    if (redisUtil.delete("stu")) { // 删除缓存成功
      int add = stuDao.add(student);
      if (add > 0) {  // 数据库添加成功
        return new Result(true, "添加成功", null);
      } else { // 数据库添加失败
        return new Result(false, "添加失败", null);
      }
    }else { // 如果缓存删除失败，不操作数据库，防止产生脏数据
      return new Result(false, "添加失败", null);
    }
  }

  public Result update(Student student) {
    if (redisUtil.delete("stu")) { // 删除缓存成功
      int update = stuDao.update(student);
      if (update > 0) {  // 数据库更新成功
        return new Result(true, "更新成功", null);
      } else { // 数据库更新失败
        return new Result(false, "更新失败", null);
      }
    }else { // 如果缓存删除失败，不操作数据库，防止产生脏数据
      return new Result(false, "更新失败", null);
    }
  }
}
```

#### （4）controller层

```java
@WebServlet("/add")
public class AddServlet extends HttpServlet {
  /**
   * 解决post请求时，预检请求的跨域
   * @param request
   * @param response
   */
  protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Access-Control-Allow-Credentials", "true");
    response.setHeader("Access-Control-Allow-Methods", "*");
    response.setHeader("Access-Control-Max-Age", "3600");
    response.setHeader("Access-Control-Allow-Headers", "Authorization,Origin,X-Requested-With,Content-Type,Accept,"
            + "content-Type,origin,x-requested-with,content-type,accept,authorization,token,id,X-Custom-Header,X-Cookie,Connection,User-Agent,Cookie,*");
    response.setHeader("Access-Control-Request-Headers", "Authorization,Origin, X-Requested-With,content-Type,Accept");
    response.setHeader("Access-Control-Expose-Headers", "*");
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // 解决跨域
    response.setHeader("Access-Control-Allow-Origin", "*");// 允许跨域的主机地址
    response.setHeader("Access-Control-Allow-Methods", "*");// 允许跨域的请求方法GET, POST, HEAD
    response.setHeader("Access-Control-Max-Age", "3600");// 重新预检验跨域的缓存时间 (s)
    response.setHeader("Access-Control-Allow-Headers", "*");// 允许跨域的请求头
    response.setContentType("application/json;charset=UTF-8");// 返回值类型
    response.setCharacterEncoding("UTF-8");

    // 注入service层
    StuServiceImpl stuService = new StuServiceImpl();

    // 获取请求体中内容
    String str = IOUtils.toString(request.getInputStream(), "UTF-8");
    Gson gson = new Gson();
    Student student = gson.fromJson(str, Student.class);

    // 调用添加的方法
    Result add = stuService.add(student);
    // 返回数据以json返回
    String json = gson.toJson(add, Result.class);

    PrintWriter out = response.getWriter();
    out.println(json);

    out.flush();
    out.close();
  }
}
```

```java
@WebServlet("/delete")
public class DeleteServlet extends HttpServlet {

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // 解决跨域
    response.setHeader("Access-Control-Allow-Origin", "*"); // 允许跨域的主机地址
    response.setHeader("Access-Control-Allow-Methods", "*"); // 允许跨域的请求方法GET, POST, HEAD 等
    response.setHeader("Access-Control-Max-Age", "3600"); // 重新预检验跨域的缓存时间 (s)
    response.setHeader("Access-Control-Allow-Headers", "*"); // 允许跨域的请求头
    response.setContentType("application/json;charset=UTF-8"); // 返回值类型
    response.setCharacterEncoding("UTF-8");

    // 获取携带参数
    int id = Integer.valueOf(request.getParameter("id"));

    // 注入service层
    StuServiceImpl stuService = new StuServiceImpl();

    // 执行删除方法
    Result delete = stuService.delete(id);
    // 返回数据以json返回
    Gson gson = new Gson();
    String json = gson.toJson(delete, Result.class);

    PrintWriter out = response.getWriter();
    out.println(json);
    out.flush();
    out.close();
  }
}
```

```java
@WebServlet("/query")
public class QueryServlet extends HttpServlet {
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // 解决跨域
    response.setHeader("Access-Control-Allow-Origin", "*");// 允许跨域的主机地址
    response.setHeader("Access-Control-Allow-Methods", "*");// 允许跨域的请求方法GET, POST, HEAD 等
    response.setHeader("Access-Control-Max-Age", "3600");// 重新预检验跨域的缓存时间 (s)
    response.setHeader("Access-Control-Allow-Headers", "*");// 允许跨域的请求头
    response.setContentType("application/json;charset=UTF-8");// 返回值类型
    response.setCharacterEncoding("UTF-8");

    // 获取请求携带的参数
    String id = request.getParameter("id");
    String name = request.getParameter("name");
    String major = request.getParameter("major");

    // 注入service层
    StuServiceImpl stuService = new StuServiceImpl();
    RedisUtil redisUtil = new RedisUtil();
    Gson gson = new Gson();
    PrintWriter out = response.getWriter();

    // 如果没有携带参数,查询全部，并放入缓存
    if ((id == null || id.length() == 0)
        && (name == null || name.length() == 0)
        && (major == null || major.length() == 0)) {
      // 通过key获取redis中的值
      String jsonValue = redisUtil.get("stu");
      if (jsonValue == null) { // 如果缓存没有，从数据库查询，并放入缓存
        // 从getStu()方法中获取查询全部的结果集
        Result result = stuService.getStu();
        String json = gson.toJson(result, Result.class);
        redisUtil.set("stu",json); // 如果放入缓存
        out.println(json);
        System.out.println("走数据库，并放入缓存");
      } else { // 如果缓存中有，从缓存查询
        out.println(jsonValue);
        System.out.println("走缓存");
      }
    } else { // 如果有参数，按条件查询，且不放入缓存
      Result result = stuService.getStuBy(id, name, major);// 动态条件查询getStuBy()
      String json = gson.toJson(result, Result.class);
      out.println(json);
    }

    out.flush();
    out.close();
  }
}
```

```java
@WebServlet("/update")
public class UpdateServlet extends HttpServlet {
  /**
   * 解决post请求时，预检请求的跨域
   * @param request
   * @param response
   */
  protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Access-Control-Allow-Credentials", "true");
    response.setHeader("Access-Control-Allow-Methods", "*");
    response.setHeader("Access-Control-Max-Age", "3600");
    response.setHeader(
        "Access-Control-Allow-Headers",
        "Authorization,Origin,X-Requested-With,Content-Type,Accept,"
            + "content-Type,origin,x-requested-with,content-type,accept,authorization,token,id,X-Custom-Header,X-Cookie,Connection,User-Agent,Cookie,*");
    response.setHeader(
        "Access-Control-Request-Headers",
        "Authorization,Origin, X-Requested-With,content-Type,Accept");
    response.setHeader("Access-Control-Expose-Headers", "*");
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // 解决跨域
    response.setHeader("Access-Control-Allow-Origin", "*");// 允许跨域的主机地址
    response.setHeader("Access-Control-Allow-Methods", "*");// 允许跨域的请求方法GET, POST, HEAD
    response.setHeader("Access-Control-Max-Age", "3600");// 重新预检验跨域的缓存时间 (s)
    response.setHeader("Access-Control-Allow-Headers", "*");// 允许跨域的请求头
    response.setContentType("application/json;charset=UTF-8");// 返回值类型
    response.setCharacterEncoding("UTF-8");

    // 注入service层
    StuServiceImpl stuService = new StuServiceImpl();

    // 获取请求体中内容
    String str = IOUtils.toString(request.getInputStream(), "UTF-8");
    Gson gson = new Gson();
    Student student = gson.fromJson(str, Student.class);

    // 调用添加的方法
    Result update = stuService.update(student);

    String json = gson.toJson(update, Result.class);
    PrintWriter out = response.getWriter();
    out.println(json);

    out.flush();
    out.close();
  }
}
```

#### （5）工具类

```java
public class JdbcUtil {
  // 创建c3p0的实例，读取src下面的配置文件中default-config里面的默认数据源
  //  private static ComboPooledDataSource ds = new ComboPooledDataSource();

  // 创建c3p0的实例，读取src下面的配置文件中named-config里面的初始数据源
  private static ComboPooledDataSource ds = new ComboPooledDataSource("mysql");
  // 从连接池里面获取连接
  public static Connection getConnection() throws SQLException {
    return ds.getConnection();
  }

  // 关闭两个参数，释放资源
  public static void close(Connection conn, PreparedStatement ps) {
    try {
      ps.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try {
      // 使用连接池后，此时的close()并不是关闭链接，而是把链接放回到了连接池中
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  // 关闭两个参数，释放资源
  public static void close(Connection conn, ResultSet rs) {
    try {
      rs.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try {
      // 使用连接池后，此时的close()并不是关闭链接，而是把链接放回到了连接池中
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  // 关闭三个参数，释放资源
  public static void close(Connection conn, PreparedStatement ps, ResultSet rs) {
    try {
      rs.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try {
      ps.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try {
      // 使用连接池后，此时的close()并不是关闭链接，而是把链接放回到了连接池中
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
```

```java
public class RedisUtil {
    Jedis jedis = new Jedis("121.199.44.171");

    /**
     * 设置 key，value 到redis
     * @param key
     * @param value
     * @return 是否成功 boolean
     */
    public boolean set(String key, String value) {
        try {
            jedis.set(key, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 通过key删除
     * @param key
     * @return 是否成功 boolean
     */
    public boolean delete(String key) {
        Boolean result = false;
        try {
            jedis.del(key);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 通过key获取value
     * @param key
     * @return
     */
    public String get(String key) {
        return jedis.get(key);
    }
}
```

```java
public class Result {
  private Boolean success;
  private String massage;
  private Object data;

  public Result(Boolean success, String massage, Object data) {
    this.success = success;
    this.massage = massage;
    this.data = data;
  }
}
```

### 2.2.3 跨域

> 跨域的产生是因为浏览器的同源策略，同源策略是指在Web浏览器中，允许某个网页脚本访问另一个网页的数据，但前提是这两个网页必须有相同的IP及端口号，一旦两个网站满足上述条件，这两个网站就被认定为具有相同来源。此策略可防止某个网页上的恶意脚本通过该页面的文档对象模型访问另一网页上的敏感数据。

Get请求中，也就是重写doGet方法时，允许跨域

```java
response.setHeader("Access-Control-Allow-Origin", "*");// 允许跨域的主机地址
response.setHeader("Access-Control-Allow-Methods", "*");// 允许跨域的请求方法GET, POST, HEAD 等
response.setHeader("Access-Control-Max-Age", "3600");// 重新预检验跨域的缓存时间 (s)
response.setHeader("Access-Control-Allow-Headers", "*");// 允许跨域的请求头
```

Post请求时，除了再重写doPost方法时允许跨域，还要再doOptions()方法解决跨域

```java
/**
 * 解决post请求时，预检请求的跨域
 * @param request
 * @param response
 */
protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
  response.setHeader("Access-Control-Allow-Origin", "*");
  response.setHeader("Access-Control-Allow-Credentials", "true");
  response.setHeader("Access-Control-Allow-Methods", "*");
  response.setHeader("Access-Control-Max-Age", "3600");
  response.setHeader(
      "Access-Control-Allow-Headers",
      "Authorization,Origin,X-Requested-With,Content-Type,Accept,"
          + "content-Type,origin,x-requested-with,content-type,accept,authorization,token,id,X-Custom-Header,X-Cookie,Connection,User-Agent,Cookie,*");
  response.setHeader(
      "Access-Control-Request-Headers",
      "Authorization,Origin, X-Requested-With,content-Type,Accept");
  response.setHeader("Access-Control-Expose-Headers", "*");
}

public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
  // 解决跨域
  response.setHeader("Access-Control-Allow-Origin", "*");// 允许跨域的主机地址
  response.setHeader("Access-Control-Allow-Methods", "*");// 允许跨域的请求方法GET, POST, HEAD
  response.setHeader("Access-Control-Max-Age", "3600");// 重新预检验跨域的缓存时间 (s)
  response.setHeader("Access-Control-Allow-Headers", "*");// 允许跨域的请求头
  
  ....
  
  }
```

因为当涉及到跨域时，并且是post请求时，本地服务器会先发送一个options预检请求到服务器，如果服务器认为options请求时无危险性且认可的，那么在允许本地服务器发送post请求

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

（3）修改端口号，在tomcat目录下的conf中的server.xml，将Connector port改为8081，将Server port改为8005

![image-20211203185642175](https://gitee.com/lplgitee/blog_img/raw/master/img/image-20211203185642175.png)

![image-20211204201101765](https://gitee.com/lplgitee/blog_img/raw/master/img/image-20211204201101765.png)

（4）在webapps/ROOT/WEB-INF/目录下新建lib放使用到的jar包，新建classes放编译好的java，同时修改当前目录下的web.xml，将metadata-complete属性值改为false

![image-20211203185911040](https://gitee.com/lplgitee/blog_img/raw/master/img/image-20211203185911040.png)

（5）将tomcat-8081复制两份出来，并改名为tomcat-8082、tomcat-8083

```shell
cp -r tomact-8081 tomact-8082
cp -r tomact-8081 tomact-8083
```

同样将新建的两个tomcat目录下的conf中的server.xml，将Connector port改为8082，8083，将Server port改为8006、8007

### 3.2.1 Nginx反向代理及负载均衡

（1）下载Nginx

http://nginx.org/en/download.html

![image-20211203190028667](https://gitee.com/lplgitee/blog_img/raw/master/img/image-20211203190028667.png)

（3）解压

```shell
tar -zxvf nginx-1.21.4.tar.gz
```

（4）安装依赖包

```shell
yum -y install gcc zlib zlib-devel pcre-devel openssl openssl-devel
```

（5）进入ngxin解压目录，编译安装

```shell
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
    	server 公网ip:8081;
    	server 公网ip:8082;
        server 公网ip:8083;
    }

    server {
        listen       8080;
        server_name  公网ip:8080;

        location / {
            proxy_pass http://stu;
        }
	}
```

> 将8080端口的请求转发到8081、8082、8083端口，并实现轮询负载均衡

## 3.3 前端

### 3.1 前端设计

![image-20211205170333585](https://gitee.com/lplgitee/blog_img/raw/master/img/image-20211205170333585.png)

![image-20211205170342964](https://gitee.com/lplgitee/blog_img/raw/master/img/image-20211205170342964.png)

![image-20211205170357012](https://gitee.com/lplgitee/blog_img/raw/master/img/image-20211205170357012.png)

![image-20211205170407754](https://gitee.com/lplgitee/blog_img/raw/master/img/image-20211205170407754.png)

### 3.2 前端代码

head，引入外部css样式

```html
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<title>学生管理</title>
		<meta content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no" name="viewport">
		<!-- 引入样式 -->
		<link rel="stylesheet" href="./plugins/elementui/index.css">
		<link rel="stylesheet" href="./plugins/font-awesome/css/font-awesome.min.css">
		<link rel="stylesheet" href="./css/style.css">
```

body，采用elment ui的组件快速搭建

```html
	<body class="hold-transition">
		<div id="app">
			<div v-loading="loading" element-loading-text="拼命加载中" element-loading-spinner="el-icon-loading"
				element-loading-background="rgba(0, 0, 0, 0.8)" style="width: 100%;height: 100vh;">
				<div class="content-header">
					<h1>学生管理</h1>
				</div>

				<div class="app-container">
					<div class="box">
						<!-- 查询 -->
						<div class="filter-container">
							<el-input placeholder="学生姓名" v-model="queryBy.name" style="width: 200px;"
								class="filter-item">
							</el-input>
							<el-input placeholder="学生专业" v-model="queryBy.major" style="width: 200px;"
								class="filter-item">
							</el-input>
							<el-button @click="getAll()" class="dalfBut">查询</el-button>
							<el-button @click="clear()" class="dalfBut">重置查询</el-button>
							<el-button type="primary" class="butT" @click="handleCreate()">新建</el-button>
						</div>

						<!-- 展示内容区域 -->
						<el-table size="small" current-row-key="id" :data="dataList" stripe highlight-current-row>
							<el-table-column type="index" align="center" label="序号"></el-table-column>
							<el-table-column prop="stuId" label="学号" align="center"></el-table-column>
							<el-table-column prop="name" label="姓名" align="center"></el-table-column>
							<el-table-column prop="age" label="年龄" align="center"></el-table-column>
							<el-table-column prop="sex" label="性别" align="center"></el-table-column>
							<el-table-column prop="major" label="专业" align="center"></el-table-column>
							<el-table-column label="操作" align="center">
								<!-- 操作按钮 -->
								<template slot-scope="scope">
									<el-button type="primary" size="mini" @click="handleUpdate(scope.row)">编辑
									</el-button>
									<el-button type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
								</template>
							</el-table-column>
						</el-table>

						<!-- 新增标签弹层 -->
						<div class="add-form">
							<el-dialog title="新增学生" :visible.sync="dialogFormVisible">
								<el-form ref="dataAddForm" :model="formData" :rules="rules" label-position="center"
									label-width="100px">
									<el-row>
										<el-col :span="12">
											<el-form-item label="姓名" prop="name">
												<el-input v-model="formData.name" />
											</el-form-item>
										</el-col>
										<el-col :span="12">
											<el-form-item label="学号" prop="stuId">
												<el-input v-model="formData.stuId" />
											</el-form-item>
										</el-col>
										<el-col :span="12">
											<el-form-item label="性别" prop="sex">
												<el-input v-model="formData.sex" />
											</el-form-item>
										</el-col>
										<el-col :span="12">
											<el-form-item label="年龄" prop="age">
												<el-input v-model="formData.age" />
											</el-form-item>
										</el-col>
										<el-col :span="12">
											<el-form-item label="专业" prop="major">
												<el-input v-model="formData.major" />
											</el-form-item>
										</el-col>
									</el-row>
								</el-form>
								<div slot="footer" class="dialog-footer">
									<el-button @click="cancel()">取消</el-button>
									<el-button type="primary" @click="handleAdd('dataAddForm')">确定</el-button>
								</div>
							</el-dialog>
						</div>

						<!-- 编辑标签弹层 -->
						<div class="add-form">
							<el-dialog title="编辑检查项" :visible.sync="dialogFormVisible4Edit">
								<el-form ref="dataEditForm" :model="formData" :rules="rules" label-position="right"
									label-width="100px">
									<el-row>
										<el-col :span="12">
											<el-form-item label="姓名" prop="name">
												<el-input v-model="formData.name" />
											</el-form-item>
										</el-col>
										<el-col :span="12">
											<el-form-item label="学号" prop="stuId">
												<el-input v-model="formData.stuId" />
											</el-form-item>
										</el-col>
										<el-col :span="12">
											<el-form-item label="性别" prop="sex">
												<el-input v-model="formData.sex" />
											</el-form-item>
										</el-col>
										<el-col :span="12">
											<el-form-item label="年龄" prop="age">
												<el-input v-model="formData.age" />
											</el-form-item>
										</el-col>
										<el-col :span="12">
											<el-form-item label="专业" prop="major">
												<el-input v-model="formData.major" />
											</el-form-item>
										</el-col>
									</el-row>
								</el-form>
								<div slot="footer" class="dialog-footer">
									<el-button @click="cancel()">取消</el-button>
									<el-button type="primary" @click="handleEdit('dataEditForm')">确定</el-button>
								</div>
							</el-dialog>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
```

script：请求、数据、dom渲染

```javascript
<!-- vue2 -->
	<script src="./js/vue.js"></script>
	<!-- element -->
	<script src="./plugins/elementui/index.js"></script>
	<!-- jquery -->
	<script type="text/javascript" src="./js/jquery.min.js"></script>
	<!-- axios -->
	<script src="./js/axios-0.18.0.js"></script>

	<script>
		var vue = new Vue({
			el: '#app',
			data: {
				dataList: [], //当前页要展示的列表数据
				dialogFormVisible: false, //添加表单是否可见
				dialogFormVisible4Edit: false, //编辑表单是否可见
				formData: {}, //表单数据
				rules: { //校验规则
					name: [{
						required: true,
						message: '姓名为必填项',
						trigger: 'blur'
					}],
					stuId: [{
						required: true,
						message: '学号为必填项',
						trigger: 'blur'
					}],
					sex: [{
						required: true,
						message: '性别为必填项',
						trigger: 'blur'
					}],
					age: [{
						required: true,
						message: '年龄为必填项',
						trigger: 'blur'
					}],
					major: [{
						required: true,
						message: '专业为必填项',
						trigger: 'blur'
					}],
				},
				queryBy: { //查询相关模型数据
					id: "",
					name: "",
					major: ""
				},
			},

			// 钩子函数，VUE对象初始化完成后自动执行
			created() {
				// 调用查询全部数据的操作
				this.getAll();
			},

			methods: {
				// 查询
				getAll() {
					axios.get("接口地址", {
						params: {
							id: this.queryBy.id,
							name: this.queryBy.name,
							major: this.queryBy.major,
						},
						headers: {
							'Content-Type': 'application/json',
						},
					}).then((res) => {
						this.dataList = res.data.data;
					});

				},

				// 重置查询
				clear() {
					// 清空表单
					this.queryBy.name = "";
					this.queryBy.major = "";
					// 重新加载数据
					this.getAll();
				},

				// 弹出添加窗口
				handleCreate() {
					this.dialogFormVisible = true;
					this.resetForm();
				},

				// 重置表单
				resetForm() {
					this.formData = {};
				},

				//  校验表单 添加
				handleAdd(formName) {
					this.$refs[formName].validate((valid) => {
						if (valid) {
							axios({
								url: "接口地址",
								method: "post",
								data: this.formData,
								headers: {
									'Content-Type': 'application/json;charset=utf-8',
								},
								changeOrigin: true,
							}).then((res) => {
								//判断当前操作是否成功
								if (res.data.success) {
									//1.关闭弹层
									this.dialogFormVisible = false;
									this.$message.success("添加成功");
								} else {
									this.$message.error("添加失败");
								}
							}).finally(() => {
								//2.重新加载数据
								this.getAll();
							});
						} else {
							console.log('error submit!!');
							return false;
						}
					});
				},

				// 取消
				cancel() {
					this.dialogFormVisible = false;
					this.dialogFormVisible4Edit = false;
					this.$message.info("当前操作取消");
				},

				// 删除
				handleDelete(row) {
					// console.log(row);
					this.$confirm("此操作永久删除当前信息，是否继续？", "提示", {
						type: "info"
					}).then(() => {
						axios({
							url: "接口地址",
							method: "get",
							params: {
								id: row.id
							},
							headers: {
								'Content-Type': 'application/json',
							},
						}).then((res) => {
							// console.log(res.data.success)
							if (res.data.success) {
								this.$message.success("删除成功");
							} else {
								this.$message.error("数据同步失败，自动刷新");
							}
						}).finally(() => {
							//2.重新加载数据
							this.getAll();
						});
					}).catch(() => {
						this.$message.info("取消操作");
					});
				},

				// 弹出编辑窗口
				handleUpdate(row) {
					axios({
						url: "接口地址",
						method: "get",
						params: {
							id: row.id
						},
						headers: {
							'Content-Type': 'application/json',
						},
					}).then((res) => {
						if (res.data != null) {
							this.dialogFormVisible4Edit = true;
							this.formData = res.data.data[0];

						} else {
							this.$message.error("数据同步失败，自动刷新");
						}
					})
				},

				// 校验表单 修改
				handleEdit(formName) {
					this.$refs[formName].validate((valid) => {
						if (valid) {
							axios({
								url: "接口地址",
								method: "post",
								data: this.formData,
								headers: {
									'Content-Type': 'application/json;charset=utf-8',
								},
							}).then((res) => {
								//判断当前操作是否成功
								if (res.data.success) {
									//1.关闭弹层
									this.dialogFormVisible4Edit = false;
									this.$message.success("修改成功");
								} else {
									this.$message.error("修改失败");
								}
							}).finally(() => {
								//2.重新加载数据
								this.getAll();
							});
						} else {
							console.log('error submit!!');
							return false;
						}
					});
				}
			},
		})
	</script>
```

### 3.2 前端部署

（1）将静态页面及资源放到/usr/local/nginx/html/中

![image-20211203221204712](https://gitee.com/lplgitee/blog_img/raw/master/img/image-20211203221204712.png)

（2）启动nginx（同时加载nginx.conf配置的负载均衡）

```shell
/usr/local/nginx/sbin/nginx -c /usr/local/nginx/conf/nginx.conf
```

## 3.4 运维自动化

### 3.4.1 shell脚本发布应用

> 三个前提：代码在Github或Gitee仓库，并且远程仓库添加Linux主机的ssh公钥；linux安装Maven；linux安装git

（1）下载并解压maven

https://maven.apache.org/download.cgi

![image-20211204201305420](https://gitee.com/lplgitee/blog_img/raw/master/img/image-20211204201305420.png)

解压

```shell
tar -zxvf apache-maven-3.8.4-bin.tar.gz
```

（2）设置环境变量，用户目录下.bash_profile加如下两行

```shell
export MAVEN_HOME=/root/Student/apache-maven-3.8.4
export PATH=$MAVEN_HOME/bin:$PATH
```

重新加载配置文件

```shell
source /root/.bash_profile
```

（3）验证maven是否配置成功

```shell
mvn -v
```

![image-20211204201922282](https://gitee.com/lplgitee/blog_img/raw/master/img/image-20211204201922282.png)

（3）自动部署脚本，不过只适用于当前项目

```sh
#!/bin/bash

#输出头部信息，日期
hello(){
	echo "   | |           | |  "  
	echo "___| |_ __ _ _ __| |_ "  
	echo "/ __| __/ _|  |  __| __|" 
	echo "\__ \ || (_| | |  | |_ " 
	echo "|___/\__\__,_|_|   \__|" 
  time3=$(date "+%Y-%m-%d %H:%M:%S")
	echo $time3
}

#判断上一条命令是否成功执行
judge(){
 	if [ $? -eq 0 ]; then
    	echo "________do succes________"
  	else
		echo "________do error________"
	fi
}

#停止tomact集群
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

#如果项目存在，从远程仓库更新项目，如果项目不存在，从远程仓库克隆项目
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

#使用maven打包项目
package_api(){
	cd /root/Student/linux-exam/api_servlet/
	echo "________mvn clean________"
	mvn clean
	judge
	echo "________mvn package________"
	mvn package
	judge
}

#部署war包项目到tomact集群
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

#启动tomact集群
start_tomcat(){
	echo "________启动tomcat集群________"
	/root/Student/tomcat-8081/bin/./startup.sh
	judge
	/root/Student/tomcat-8082/bin/./startup.sh
	judge
	/root/Student/tomcat-8083/bin/./startup.sh
	judge
}

# 回到/root/Student/目录
back_path(){
	cd /root/Student/
}

# 输出语句
ok(){
	echo "完成部署"
}

# main函数
main(){
	# 顺序执行以上函数，使用“&&”：如果某个命令执行出错了后面的函数全部不执行，执行完之后将每条命令的输出内容写入install_log.txt
	(( hello && stop_tomcat && package_api && install_tomcat && restart_tomcat && back_path && ok ) > /root/Student/install_log.txt )  && ok
	echo "请在/root/Student/install_log.txt中查看脚本执行日志"
}

# 执行main函数
main
```

> 这里没用到clone_project函数是因为远程仓库的代码删掉了数据库的配置信息

脚本执行效果（install_log.txt的内容）

```tex
   | |           | |
___| |_ __ _ _ __| |_
/ __| __/ _|  |  __| __|
\__ \ || (_| | |  | |_
|___/\__\__,_|_|   \__|
2021-12-06 13:38:48
________正在停止tomcat集群________
Using CATALINA_BASE:   /root/Student/tomcat-8081
Using CATALINA_HOME:   /root/Student/tomcat-8081
Using CATALINA_TMPDIR: /root/Student/tomcat-8081/temp
Using JRE_HOME:        /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.312.b07-1.el7_9.x86_64/jre
Using CLASSPATH:       /root/Student/tomcat-8081/bin/bootstrap.jar:/root/Student/tomcat-8081/bin/tomcat-juli                                                                                                 .jar
Using CATALINA_OPTS:
________do succes________
Using CATALINA_BASE:   /root/Student/tomcat-8082
Using CATALINA_HOME:   /root/Student/tomcat-8082
Using CATALINA_TMPDIR: /root/Student/tomcat-8082/temp
Using JRE_HOME:        /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.312.b07-1.el7_9.x86_64/jre
Using CLASSPATH:       /root/Student/tomcat-8082/bin/bootstrap.jar:/root/Student/tomcat-8082/bin/tomcat-juli                                                                                                 .jar
Using CATALINA_OPTS:
________do succes________
Using CATALINA_BASE:   /root/Student/tomcat-8083
Using CATALINA_HOME:   /root/Student/tomcat-8083
Using CATALINA_TMPDIR: /root/Student/tomcat-8083/temp
Using JRE_HOME:        /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.312.b07-1.el7_9.x86_64/jre
Using CLASSPATH:       /root/Student/tomcat-8083/bin/bootstrap.jar:/root/Student/tomcat-8083/bin/tomcat-juli                                                                                                 .jar
Using CATALINA_OPTS:
________do succes________
________mvn clean________
[INFO] Scanning for projects...
[INFO]
[INFO] ----------------------------< com.lpl:stu >-----------------------------
[INFO] Building stu Maven Webapp 1.0-SNAPSHOT
[INFO] --------------------------------[ war ]---------------------------------
[INFO]
[INFO] --- maven-clean-plugin:3.1.0:clean (default-clean) @ stu ---
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  0.890 s
[INFO] Finished at: 2021-12-06T13:38:58+08:00
[INFO] ------------------------------------------------------------------------
________do succes________
________mvn package________
[INFO] Scanning for projects...
[INFO]
[INFO] ----------------------------< com.lpl:stu >-----------------------------
[INFO] Building stu Maven Webapp 1.0-SNAPSHOT
[INFO] --------------------------------[ war ]---------------------------------
[INFO]
[INFO] --- maven-resources-plugin:3.0.2:resources (default-resources) @ stu ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] Copying 1 resource
[INFO]
[INFO] --- maven-compiler-plugin:3.8.0:compile (default-compile) @ stu ---
[INFO] Changes detected - recompiling the module!
[INFO] Compiling 10 source files to /root/Student/linux-exam/api_servlet/target/classes
[INFO]
[INFO] --- maven-resources-plugin:3.0.2:testResources (default-testResources) @ stu ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] skip non existing resourceDirectory /root/Student/linux-exam/api_servlet/src/test/resources
[INFO]
[INFO] --- maven-compiler-plugin:3.8.0:testCompile (default-testCompile) @ stu ---
[INFO] No sources to compile
[INFO]
[INFO] --- maven-surefire-plugin:2.22.1:test (default-test) @ stu ---
[INFO] No tests to run.
[INFO]
[INFO] --- maven-war-plugin:3.2.2:war (default-war) @ stu ---
[INFO] Packaging webapp
[INFO] Assembling webapp [stu] in [/root/Student/linux-exam/api_servlet/target/stu]
[INFO] Processing war project
[INFO] Copying webapp resources [/root/Student/linux-exam/api_servlet/src/main/webapp]
[INFO] Webapp assembled in [183 msecs]
[INFO] Building war: /root/Student/linux-exam/api_servlet/target/stu.war
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  5.051 s
[INFO] Finished at: 2021-12-06T13:39:05+08:00
[INFO] ------------------------------------------------------------------------
________do succes________
________删除有项目________
________do succes________
________复制新的war包________
________do succes________
________启动tomcat集群________
Using CATALINA_BASE:   /root/Student/tomcat-8081
Using CATALINA_HOME:   /root/Student/tomcat-8081
Using CATALINA_TMPDIR: /root/Student/tomcat-8081/temp
Using JRE_HOME:        /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.312.b07-1.el7_9.x86_64/jre
Using CLASSPATH:       /root/Student/tomcat-8081/bin/bootstrap.jar:/root/Student/tomcat-8081/bin/tomcat-juli                                                                                                 .jar
Using CATALINA_OPTS:
Tomcat started.
________do succes________
Using CATALINA_BASE:   /root/Student/tomcat-8082
Using CATALINA_HOME:   /root/Student/tomcat-8082
Using CATALINA_TMPDIR: /root/Student/tomcat-8082/temp
Using JRE_HOME:        /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.312.b07-1.el7_9.x86_64/jre
Using CLASSPATH:       /root/Student/tomcat-8082/bin/bootstrap.jar:/root/Student/tomcat-8082/bin/tomcat-juli                                                                                                 .jar
Using CATALINA_OPTS:
Tomcat started.
________do succes________
Using CATALINA_BASE:   /root/Student/tomcat-8083
Using CATALINA_HOME:   /root/Student/tomcat-8083
Using CATALINA_TMPDIR: /root/Student/tomcat-8083/temp
Using JRE_HOME:        /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.312.b07-1.el7_9.x86_64/jre
Using CLASSPATH:       /root/Student/tomcat-8083/bin/bootstrap.jar:/root/Student/tomcat-8083/bin/tomcat-juli                                                                                                 .jar
Using CATALINA_OPTS:
Tomcat started.
________do succes________
完成部署
```

### 3.4.2 定时监控接入钉钉机器人

（1）监控服务及尝试自启动脚本`check_service.sh`

- 检查nginx进程，如果没有nginx进程，则尝试重启，如果重启失败，写入日志，调用钉钉机器人发送告警信息
- 检查java进程，如果没有java进程，则尝试重启tomact，如果重启失败，写入日志，调用顶顶机器人发送告警信息，并exit退出脚本
- 检查java进程的cpu占用率及内存占用率，如果均超过5%，则写入日志调用钉钉机器人发送告警信息（这里写5%是为了测试时候方便）

```sh
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

# 检查tomcat是否正常
check_tomact(){
  if [ $(ps -C java --no-header |wc -l) -eq 0 ];then
    # tomcat尝试重启
   	  /root/Student/tomcat-8081/bin/./startup.sh
	  /root/Student/tomcat-8082/bin/./startup.sh
	  /root/Student/tomcat-8083/bin/./startup.sh
    sleep 2
    # 如果tomcat启动失败
    if [ $(ps -C java --no-header |wc -l) -eq 0 ];then
      # 告警信息写入load_log.txt 
      echo "tomcat尝试重启失败，已停止tomcat集群，请上服务器检查" >> /root/Student/dingdingROBOT/log/load_log.txt
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
```

**效果：**

（1）注释掉脚本中尝试重启nginx一行命令，然后手动停止nginx，执行脚本

![image-20211205164903371](https://gitee.com/lplgitee/blog_img/raw/master/img/image-20211205164903371.png)

（2）打开尝试重启，手动停止nginx，执行脚本

![image-20211205165052555](https://gitee.com/lplgitee/blog_img/raw/master/img/image-20211205165052555.png)

（3）监控cpu、内存等信息

![image-20211205164937205](https://gitee.com/lplgitee/blog_img/raw/master/img/image-20211205164937205.png)

（2）发布定时任务

每一分钟执行一次脚本

```sh
* * * * * bash /root/Student/dingdingROBOT/check_service.sh
```

![image-20211209111955740](https://gitee.com/lplgitee/blog_img/raw/master/img/image-20211209111955740.png)

（3）钉钉机器人

> 关于钉钉机器人，这篇文章详细介绍
>
> http://developer-help.cn/blog/50

getMessage()方法读取到/root/Student/dingdingROBOT/log/load_log.txt日志文件的最后一行，然后返回到主函数，发送至钉钉群聊

```java
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;
import org.apache.commons.codec.binary.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class RobotSend {
    public static void main(String[] args) throws ApiException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        //获取getUrl方法得到的url
        String url = getUrl();
        DingTalkClient client = new DefaultDingTalkClient(url);
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("text");
        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
        //获取当前系统时间并格式化
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(new Date());
        text.setContent(getMessage());
        request.setText(text);
        OapiRobotSendResponse response = client.execute(request);
        //输出日志 在Linux中将日志保存到log.txt中
        System.out.println("发送告警信息，发送时间："+date);
        ;
    }

    public static String getMessage(){
        FileReader fileReader = null;
        try {
            fileReader = new FileReader("/root/Student/dingdingROBOT/load_log.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Scanner sc = new Scanner(fileReader);
        String line = null;
        while((sc.hasNextLine()&&(line=sc.nextLine())!=null)){
            if(!sc.hasNextLine()){
                return line;
            }
        }
        sc.close();
        return line;
    }
    public static String getUrl() throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        Long timestamp = System.currentTimeMillis();
        String secret = "签名";
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
        String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)),"UTF-8");
        //定义token
        String access_token = "token";
        //拼接字符串
        String url = "https://oapi.dingtalk.com/robot/send"+
                "?access_token=%s"+
                "&timestamp=%s"+
                "&sign=%s";
        String serverUrl= String.format(
                url,
                access_token,
                timestamp,
                sign
        );
        return serverUrl;
    }
}
```

打包jar包，上传至服务器

> 一些问题：
>
> 文件内容是空的话，getMessage()方法会报空指针异常，所以需要在写脚本的时候注意，先将监控得到的信息写入load_log.txt，再执行jar包，防止出问题，我在脚本中，做了写入日志后操作，sleep 2 秒，再发送信息

# 4. 测压

效果并不好...

# 5.有待优化

## 5.1 代码方面：

- 前端没有做分页查询，以网页的形式流式查询不合适

- 插入时没有做字段校验，导致重复数据插入

- 更新时没有做校验是否与原数据相同，导致浪费资源

- 查询接口

  （不过只是一个小demo，此次考查重点不在于项目）

## 5.2 shell脚本

- 自动化部署脚本应将对应的info、error等信息封装成日志格式，便于运维找到问题
- 监控进程使用率的流程为先写入日志文件，Java读取日志文件，再发送消息，有点繁琐，但个人觉得有必要记录日志




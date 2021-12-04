package com.lpl.stu.utils;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

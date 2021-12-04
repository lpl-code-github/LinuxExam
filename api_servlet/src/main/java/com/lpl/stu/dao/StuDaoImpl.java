package com.lpl.stu.dao;

import com.lpl.stu.pojo.Student;
import com.lpl.stu.utils.JdbcUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

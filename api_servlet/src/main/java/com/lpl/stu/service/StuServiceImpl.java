package com.lpl.stu.service;

import com.lpl.stu.dao.StuDaoImpl;
import com.lpl.stu.pojo.Student;
import com.lpl.stu.utils.RedisUtil;
import com.lpl.stu.utils.Result;
import redis.clients.jedis.Jedis;

import java.util.List;

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

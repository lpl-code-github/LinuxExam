package com.lpl.stu.controller;

import com.google.gson.Gson;
import com.lpl.stu.service.StuServiceImpl;
import com.lpl.stu.utils.RedisUtil;
import com.lpl.stu.utils.Result;
import redis.clients.jedis.Jedis;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

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

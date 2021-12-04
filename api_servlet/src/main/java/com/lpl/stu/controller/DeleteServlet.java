package com.lpl.stu.controller;

import com.google.gson.Gson;
import com.lpl.stu.service.StuServiceImpl;
import com.lpl.stu.utils.Result;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

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

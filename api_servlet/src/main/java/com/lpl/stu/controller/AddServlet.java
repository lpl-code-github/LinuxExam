package com.lpl.stu.controller;

import com.google.gson.Gson;
import com.lpl.stu.service.StuServiceImpl;
import com.lpl.stu.utils.Result;
import com.lpl.stu.pojo.Student;
import org.apache.commons.io.IOUtils;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/add")
public class AddServlet extends HttpServlet {
  /**
   *  解决post请求时，预检请求的跨域
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

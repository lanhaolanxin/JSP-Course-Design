package com.xt.www.interceptor;

import com.xt.www.common.PageHelper;
import com.xt.www.dao.UserContentMapper;

import com.xt.www.entity.UserContent;
import org.springframework.context.ApplicationContext;

import org.springframework.web.context.support.WebApplicationContextUtils;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.*;
import java.io.IOException;
import java.util.List;


public class IndexJspFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("========自定义过滤器=======");
        ServletContext servletContext = servletRequest.getServletContext();
        ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        UserContentMapper userContentMapper = applicationContext.getBean(UserContentMapper.class);
        PageHelper.startPage(null,null);
        Example e = new Example(UserContent.class);
        e.setOrderByClause("rpt_time DESC");
        List<UserContent> list =  userContentMapper.select(null);
        PageHelper.Page endPage = PageHelper.endPage();
        servletRequest.setAttribute("page",endPage);
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath }"/>
<!DOCTYPE html>
<html lang="zh-Hans">

<head>
    <title>Pure</title>
    <meta name="description" content="Just 4 write.">
    <meta name="keywords" content="pure" />
    <meta charset="UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1" />
    <link href="${ctx}/css/normalize.min.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/css/pure.css" rel="stylesheet" type="text/css">
    <script src="${ctx}/js/jquery.min.js"></script>
</head>

<body>
<jsp:include page="head.jsp"/>
<div class="container">
    <header>
        <h1 class="title">Pure</h1>
        <p class="subtitle">Just 4 Write</p>
    </header>
        <div id="update-dreamland" style="height: 700px;margin-top: 50px;width: 100%;" >
            <ul style="font-size: 12px" id="update-dreamland-ul">

                <c:forEach var="cont" items="${page.result}" varStatus="i">
                    <li class="dreamland-fix">
                        <a>${cont.title}</a>
                        <span class="bar-delete">删除</span>
                        <span class="bar-update">修改</span>
                        <hr/>
                    </li>
                </c:forEach>

            </ul>


            <div style="float: left;position: absolute;bottom: 1080px;margin-left: 20px" id="update-dreamland-div">
                <ul class="pager pager-loose" id="update-dreamland-fy">
                    <c:if test="${page.pageNum <= 1}">
                        <li class="previous"><a href="javascript:void(0);">« 上一页</a></li>
                    </c:if>
                    <c:if test="${page.pageNum > 1}">
                        <li class="previous"><a onclick="turnPage(${page.pageNum-1})">« 上一页</a></li>
                    </c:if>
                    <c:forEach begin="1" end="${page.pages}" var="pn">
                        <c:if test="${page.pageNum==pn}">
                            <li class="active"><a href="javascript:void(0);">${pn}</a></li>
                        </c:if>
                        <c:if test="${page.pageNum!=pn}">
                            <li ><a onclick="turnPage(${pn})">${pn}</a></li>
                        </c:if>
                    </c:forEach>

                    <c:if test="${page.pageNum>=page.pages}">
                        <li><a href="javascript:void(0);">下一页 »</a></li>
                    </c:if>
                    <c:if test="${page.pageNum<page.pages}">
                        <li><a onclick="turnPage(${page.pageNum+1})">下一页 »</a></li>
                    </c:if>

                </ul>

            </div>
        </div>
    </article>

<%--    回到顶部--%>
    <div class="back-to-top">Top</div>
</div>

<script>


</script>
</body>

</html>
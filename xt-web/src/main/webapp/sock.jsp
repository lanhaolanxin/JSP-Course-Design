<%--
  Created by IntelliJ IDEA.
  User: ZLB_KAM
  Date: 2019/8/5
  Time: 20:23
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath }"/>
<html>
<head>
    <title>Title</title>
</head>
<body>

</body>
<script>

    var url = "ws://"+window.location.host +'/marco';
    var sock = new WebSocket(url);
    sock.onopen = function () {
        console.log('opening');
        sayMarco();
    };
    sock.onmessage = function (e) {
        console.log("Received message:",e.data);
        setTimeout(function (){sayMarco()} ,2000);
    };
    sock.onclose = function () {
        console.log("closed");
    };

    function sayMarco() {
        console.log('sending Marco');
        sock.send("Marco!")
    }


</script>

</html>

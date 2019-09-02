<%@ page import="org.springframework.web.context.request.SessionScope" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
    <link href="../../css/normalize.min.css" rel="stylesheet" type="text/css">
    <link href="../../css/pure.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/favicon.ico" rel="icon" sizes="32x32">
</head>

<body>
<%--通用导航栏引入--%>
<jsp:include page="head.jsp"/>
    <div class="container">
        <header>
            <div class="avatar-container-80 center" >
                <a  title="${user.id}" class="avatar"><img id="img-change"  src="${user.imgUrl}" onclick="selectImg();" width="100" height="100" style="border-radius:50%;margin-top: 60px;margin-left: 0px"></a>
                <form id="upload-form"   style="width:auto;" >
                    <input type="file"  id="change-img" name="uploadImg" onchange="changeImg();" style="display:none;">
                </form>
            </div>        </header>

        <article class="article">
            <h3>修改密码</h3>
            <form  method="post" class="form" >
                <%--				规定可描述输入字段预期值的简短的提示信息--%>
                <div class="item"><span>用户名:</span>
                <input disabled id="loginname" name="loginname" class="txt" value="${user.email}">
                    <input type="hidden" id="loginname" name="loginname" value="${user.password}">
                    <input type="hidden" id="userId" name="userId" value="${user.id}">

                </div>
                    <div class="item">
                        <span>密码:&nbsp;&nbsp;&nbsp;</span>
                <input type="password" id="password" name="password" class="txt" placeholder="输入密码">
                        </div><div class="item">
                    <span>验证:&nbsp;&nbsp;&nbsp;</span>
                 <input type="password" id="confirmNewPassword" name="confirmNewPassword" class="txt" placeholder="再次输入密码">
                    </div><div class="item">
                    <button type="submit" class="btn" style="cursor: pointer" formaction="updatePassword" onclick="return validate()">提交</button>
                            </div>
            </form>
        </article>
        <footer>
            <p class="copyright">Copyright© 2018-2018
        </footer>
        <div class="back-to-top">Top</div>
    </div>
    <script src="${ctx}/js/jquery.min.js"></script>
    <script src="${ctx}/js/pure.js"></script>
<script>
    function validate(){
    var word1= document.getElementById("password").value;
    var word2 = document.getElementById("confirmNewPassword").value;
    if(word1 != word2){
      window.alert("两次输入的新密码不一致！");
      return false;
    }{
    alert("密码修改成功");
            return true;
    }
   }

    //点击图片事件
    function selectImg() {
        document.getElementById("change-img").click();
    }

    //图片选择后事件
    function changeImg() {
        var formData = new FormData($( "#upload-form" )[0]);
        $.ajax({
            url: '/fileUpload' ,
            type: 'POST',
            data: formData,
            async: false,
            cache: false,
            contentType: false,
            processData: false,
            success: function (data) {
                var msg = data["error"];
                if(msg==0){
                    //上传成功
                    var url = data["url"];
                    document.getElementById("img-change").src = url;
                    saveImg(url);
                }

            }
        });
    }

    //保存个人头像
    function saveImg(url) {
        $.ajax({
            type:'post',
            data: {"url":url},
            url: '/saveImage' ,
            dataType:'json',
            success: function (data) {
                // alert(data["msg"]);
            }
        });
    }
    </script>
</body>

</html>
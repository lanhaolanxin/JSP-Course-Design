<%@ page contentType="text/html;charset=UTF-8"%>
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
    <link href="${ctx}/css/zui/css/zui.min.css" rel="stylesheet"/>
    <link href="${ctx}/css/zui/css/zui-theme.min.css" rel="stylesheet"/>
    <link href="${ctx}/css/pure.css" rel="stylesheet">
    <link href="../../css/normalize.min.css" rel="stylesheet" type="text/css">
    <link href="../../css/pure.css" rel="stylesheet" type="text/css">
    <link href="../../favicon.ico" rel="icon" sizes="32x32">
    <script type="text/javascript" src="${ctx}/css/bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="${ctx}/css/zui/js/zui.min.js"></script>
    <script src="${ctx}/css/zui/lib/kindeditor/kindeditor.min.js"></script>
    <script src="${ctx}/js/jquery.min.js"></script>
    <script type="text/javascript" src="${ctx}/css/bootstrap/js/bootstrap.min.js"></script>


</head>

<body>
<!-- 通用导航栏的引入-->
<jsp:include page="head.jsp"/>

<div class="container">
    <header>
        <h1 class="title">Pure</h1>
        <p class="subtitle">Just 4 Write</p>
        <div class="subtitle" id="time"/>
    </header>

    <!--文章编写表单 -->
    <form action="/doWritedream" method="post" id="write_form" >

            <div class="dropdown dropdown-hover" style="margin-bottom: 30px">
                <button class="btn" type="button" data-toggle="dropdown" id="dream-diff" style="background-color:#EBEBEB"><span id="fen" > <c:if test="${cont.category != null}">
                    ${cont.category}
                </c:if>
       <c:if test="${cont.category == '' || cont.category == null}">
           分类
       </c:if></span> <span class="caret"></span></button>
                <input id="hidden_cat" hidden="hidden" name="category" value="${cont.category}"/>
                <ul class="dropdown-menu" id="dreamland-category">
                    <li><a>运动</a></li>
                    <li><a>生活</a></li>
                    <li><a>日记</a></li>
                    <li><a>美食</a></li>
                    <li><a>工作</a></li>
                    <li><a>动物</a></li>
                    <li><a>其他</a></li>
                </ul>
            </div>

        <input type="text" id="txtTitle" name="txtT_itle" value="${cont.title}" class="input-file-title" maxlength="100" placeholder="&nbsp;&nbsp;&nbsp;&nbsp;输入文章标题"  style="height: 33px;width: 1080px;background-color:#EBEBEB;border: 0px" >

        <!--富文本编辑器-->
        <div style="margin-top:20px ;float: left;margin-left: 0px">
            <textarea id="content" name="content" class="form-control kindeditor" style="height:600px;width: 1170px">${cont.content}</textarea>

        </div>

        <br/>
        <div class="switch" style="float: left;margin-top: 670px;margin-left: 20px;position: absolute">
            <input type="checkbox" name="private_dream" id="private_dream" value="off">
            <label>私密</label>


            <span style="color: red">${error}</span>
        </div>
        <br/>
        <div style="float: left;margin-top: 700px;margin-left:20px;position: absolute">
            <button class="btn btn-primary"  type="button" id="sub_dream">发表</button>
            <button class="btn btn-primary" id="go_back" type="button" >返回</button>
        </div>
    </form>

    <footer>

    </footer>
    <!--返回到顶部 -->
    <div class="back-to-top">Top</div>
</div>
    <!--js文件引入-->
<script src="../../js/jquery.min.js"></script>
<script src="../../js/pure.js"></script>
<script>
    $(function () {
        editor =  KindEditor.create('#content', {
            basePath: 'css/zui/lib/kindeditor/',
            uploadJson : '${ctx}/fileUpload',
            fileManagerJson : '${ctx}/fileManager',
            allowFileManager : true,
            bodyClass : 'article-content',
            items : ['source', '|', 'undo', 'redo', '|', 'preview', 'template', 'cut', 'copy', 'paste',
                'plainpaste', 'wordpaste', '|', 'justifyleft', 'justifycenter', 'justifyright',
                'justifyfull', 'insertorderedlist', 'insertunorderedlist', 'indent', 'outdent', 'subscript',
                'superscript', 'clearhtml', 'quickformat', 'selectall', '|', 'fullscreen', '/',
                'formatblock', 'fontname', 'fontsize', '|', 'forecolor', 'hilitecolor', 'bold',
                'italic', 'underline', 'strikethrough', 'lineheight', 'removeformat', '|', 'image','multiimage',
                'flash', 'media', 'insertfile', 'table', 'hr', 'emoticons', 'baidumap', 'pagebreak',
                'anchor', 'link', 'unlink']

        });

        KindEditor.sync();

    });

    //私密开关点击事件
    $(".switch").click(function () {
        var pd = document.getElementById('private_dream');
        if(pd.checked == true){
            $("#private_dream").val("on");
        }else{
            $("#private_dream").val("off");
        }
    });

    $("#go_back").click(function () {
        location.href ="javascript:history.go(-1);"
    });

    //发表梦
    $("#sub_dream").click (function(){
        var tit = $("#txtTitle").val();
        if(tit == null || tit.trim() == ""){
            alert("请输入文章标题！");
            return;
        }
        editor.sync();
        var v1 = $("#content").val();
        if(v1 == null || v1.trim() == ""){
            alert("文章内容为空！");
            return;
        }
        $("#write_form").submit();

    });

    //li标签的点击事件
    $("#dreamland-category li").click(function(){//jquery的click事件
        var val = $(this).text();
        $("#fen").html(val);
        $("#hidden_cat").val(val);
    });

    //私密开关回显
    $(function () {
        var v = '${cont.personal}';
        if(v == "1"){
            var pd = document.getElementById('private_dream');
            pd.checked = true;
        }
    });
</script>
</body>

</html>
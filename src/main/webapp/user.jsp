<%--
  Created by IntelliJ IDEA.
  User: 17419
  Date: 2022/7/31
  Time: 19:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<html>
<head>
    <title>用户信息</title>
</head>
<body>
<h1>用户名：${requestScope.user.username}</h1>
<h1>密码：${requestScope.user.password}</h1>
</body>
</html>

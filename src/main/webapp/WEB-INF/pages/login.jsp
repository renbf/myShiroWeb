<!DOCTYPE html>
<%@ page isELIgnored="false" contentType="text/html; charset=UTF-8"  %>
<html>
<head>
    <title>Title</title>
    <script type="text/javascript">
    function check() {
    	alert($("#userform").attr("action"));
    	return true;
    }
    </script>
</head>
<body>
	<form id="userform" name="userform" method="post" action="${pageContext.request.contextPath}/adminUser/login"onsubmit="return check();">
		<div class="login_box">
			<label>用户名：</label> <input id="userName" type="text" name="userName"
				placeholder="请输入用户名" class="login_input">
		</div>
		<div class="login_box">
			<label>密码：</label> <input id="password" type="password"
				name="password" placeholder="请输入密码" class="login_input">
		</div>
		<div class="remember_me">
			<input id="rememberMe" name="rememberMe" type="checkbox" class="checked">记住我
		</div>
		<div class="login_button">
			<input type="submit" name="" value="登 录" class="login_btn">
		</div>
	</form>
</body>
</html>

<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery-1.7.2.min.js"></script>
<title>下单页面</title>
</head>
<body>
	<div id="errorDiv"></div>
	<div align="center">
		<form action="<%=request.getContextPath() %>/test/bs/pay.do" method="post">
			<table>
				<tr>
					<td align="right">商家账号：</td>
					<td align="left"><input type="text" name="biz_id" value="144"></input></td>
				</tr>
				<tr>
					<td align="right">产品ID：</td>
					<td align="left"><input type="text" name="product_id" value="9999"></input></td>
				</tr>
				<tr>
					<td align="right">合作方订单号：</td>
					<td align="left"><input type="text" name="order_id" value="${order_id }"></input></td>
				</tr>
				<tr>
					<td align="right">购买人登录账号：</td>
					<td align="left"><input type="text" name="account_id" value="2273"></input></td>
				</tr>
				<tr>
					<td align="right">终端类型：</td>
					<td align="left">
						<select name="client_type">
							<option value="wap">wap</option>
							<option value="www">www</option>
						</select>
					</td>
				</tr>
				<tr>
					<td align="right">订单类型：</td>
					<td align="left">
						<select name="order_type">
							<option value="0">新用户订购/按次</option>
							<option value="1">老用户续订</option>
						</select>
					</td>
				</tr>
				<tr>
					<td align="right">手机号：</td>
					<td align="left">
						<select name="mobile" id="mobile">
							<option value="15017568581">15017568581</option>
							<option value="13811081453">13811081453</option>
						</select>
					</td>
				</tr>
				<tr>
					<td align="right">secrtyKey：</td>
					<td align="left"><input type="text" name="secrtykey" value="f8a97340efa59e389863dd05e6ee1599"></input></td>
				</tr>
				
				<tr>
					<td align="right">
						<button type="submit">提交</button>
					</td>
					<td>
						<button type="reset">重置</button>
					</td>
				</tr>
			</table>
		</form>
	</div>
</body>
</html>
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
	<!-- 
		<form action="<%=request.getContextPath() %>/test/bs/rdo.do" method="post">
		 -->
		<form action="http://cmpay_cmcc.dalasu.com/bsrdo/MobileRdoMo" method="post">
			<table>
				<tr>
					<td align="right">博升平台号：</td>
					<td align="left"><input type="text" name="productid" value="656"></input></td>
				</tr>
				<tr>
					<td align="right">计费代码：</td>
					<td align="left"><input type="text" name="biz_productid" value="9995"></input></td>
				</tr>
				<tr>
					<td align="right">订单时间：</td>
					<td align="left"><input type="text" name="time" value="${time }"></input></td>
				</tr>
				<tr>
					<td align="right">订单号：</td>
					<td align="left"><input type="text" name="linkid" value="${linkid }"></input></td>
				</tr>
				<tr>
					<td align="right">支付通知地址：</td>
					<td align="left"><input type="text" name="redurl" value="${redurl }"></input></td>
				</tr>
				<tr>
					<td align="right">终端类型：</td>
					<td align="left">
						<select name="vt">
							<option value="1">简版</option>
							<option value="2">彩板</option>
							<option value="3">触屏版</option>
						</select>
					</td>
				</tr>
				<tr>
					<td align="right">资费类型：</td>
					<td align="left">
						<select name="fee">
							<option value="10005001">5元</option>
						</select>
					</td>
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
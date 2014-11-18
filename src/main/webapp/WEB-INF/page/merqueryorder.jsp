
<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ include file="./meta.jsp"%>
<%@ include file="./taglib.jsp"%>
<head>
	<meta name="viewport" content="initial-scale=1.0,minimum-scale=1.0,maximum-scale=2.0,user-scalable=no" />
	<meta name="MobileOptimized" content="240" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<script type="text/javascript" src="<%=request.getContextPath() %>/static/js/jquery-1.7.2.min.js"></script>
<title>商户查单页面</title>
</head>
<body>
	<div align="center">
		<form action="<%=request.getContextPath() %>/mertest/sign.do" method="post">
			<input type="hidden" name="platType" value="6"/>
			<input type="hidden" name="amttype" value="02"/>
			<input type="hidden" name="banktype" value="3"/>
			<input type="hidden" name="upversion" value="1.0"/>
			
			<table>
				<tr>
					<td align="right">商户号：</td>
					<td align="left"><input type="text" name="merid" id="merid" value="9996"></input></td>
				</tr>
				<tr>
					<td align="right">商户订单号：</td>
					<td align="left"><input type="text" name="orderid" id="orderid" value="20140930043602"></input></td>
				</tr>
				<tr>
					<td align="right">订单日期：</td>
					<td align="left"><input type="text" name="orderdate" value="20140930"></input></td>
				</tr>
				<tr>
					<td align="right">商品号：</td>
					<td align="left"><input type="text" name="goodsid" id="goodsid" value=""></input></td>
				</tr>
				<tr>
					<td align="right">商品描述：</td>
					<td align="left"><input type="text" name="goodsinfo" value=""></input></td>
				</tr>
				<tr>
					<td align="right">商品金额：</td>
					<td align="left"><input type="text" name="amount" value=""></input></td>
				</tr>
				<tr>
					<td align="right">应用ID：</td>
					<td align="left"><input type="text" name="appid" value=""></input></td>
				</tr>
				<tr>
					<td align="right">渠道ID：</td>
					<td align="left"><input type="text" name="channelid" value=""></input></td>
				</tr>
				<tr>
					<td align="right">商户私有信息：</td>
					<td align="left"><input type="text" name="merpriv" value="xx商户私有信息"></input></td>
				</tr>
				<tr>
					<td align="right">手机号：</td>
					<td align="left"><input type="text" name="mobileid" value="15017568581"></input></td>
				</tr>
				<!-- 
				<tr>
					<td align="right">充值账号：</td>
					<td align="left"><input type="text" name="accountid" id="accountid" value="umpay"></input></td>
				</tr>
				 -->
				
				<tr>
					<td align="right">商户扩展信息：</td>
					<td align="left"><input type="text" name="expand" id="expand" value=""></input></td>
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
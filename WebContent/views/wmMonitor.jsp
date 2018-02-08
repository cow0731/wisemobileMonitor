<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="org.slf4j.LoggerFactory"%><%@page import="org.slf4j.Logger"%>
<%@page import="kr.co.deotis.config.MntProperties"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>Web Monitor</title>
	
	<style type="text/css">
		#container { border:1px solid #eee; padding:10px 20px; /* overflow-y: scroll; */ max-height: 800px;	}
	</style>
	
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
	<script type="text/javascript">
		var ws = null;
		
		/* $(document).ready(function() {
			$("#connect").click(function() {
				directlyFc();
			});	
		}); */
		
		(function() {
			<% 
				Logger logger = LoggerFactory.getLogger("Main");
				String path = application.getRealPath("/WEB-INF/conf");
				logger.debug("filepath : {}", path);
				
				MntProperties prop = new MntProperties();
				String webIp = prop.getPropertiesString(path+"/property.ini", "webIp", "192.168.20.131");
				String webPort = prop.getPropertiesString(path+"/property.ini", "webport", "8080");
				String urlName = prop.getPropertiesString(path+"/property.ini", "urlName", "/wMonitor");
				logger.debug("webIp : {}, webPort : {}, urlName : {}", webIp, webPort, urlName);
			%>
			var target = "ws://<%=webIp%>:<%=webPort%><%=urlName%>/monitor";
			if('WebSocket' in window) {
				ws = new WebSocket(target);
			} else if('MozWebSocket' in window) {
				ws = new MozWebSocket(target);
			} else { 
				alert("WebSocket is not supported by this browser");
				return;
			}
		})();
			
		ws.onopen = function(event) {
			ws.send("durl");
			//document.getElementById("msg").innerText += 'Info: WebSocket connection opened.\n';
			$("#start").append('<p> monitoring Start. </p>');
		};
		ws.onmessage = function(event) {
			//document.getElementById("msg").innerText += event.data+'\n';
			$('#msg').empty();
			
			var p = event.data;
			var pArr = p.split('`');
			console.log(p);
			
			if(pArr.length == 2) {
				appendLogMsg2(pArr[0], pArr[1]);
			} else if(pArr.length == 4) {
				appendLogMsg4(pArr[0], pArr[1], pArr[2], pArr[3]);
			} else {
				appendMessage(p);
			}
			
			send();
		};
		ws.onclose = function() {
			//document.getElementById("msg").innerText += "Info: WebSocket connection closed.\n"
			$("#msg").append('<p> monitoring End. </p>');
		};
		
		function send() {
			var text = 'true';
			ws.send(text);
		}
		
		var appendMessage = function(msg) {
			$("#msg")
			.append('<p class="error"><span>'+msg+'</span></p>')
			.scrollTop(100000000);
		}
		
		var appendLogMsg2 = function(time, code) {		// 로그에 메세지 추가하기
			
			var sp1 = '<span>'+time+'</span>';
			var sp2 = '<span class="stat">'+code+'</span>';
			$("#msg")
			.append('<p class="muted">' + sp1+"&emsp;"+sp2+ '</p>')
			.scrollTop(100000000);
			
			if(code=='정상') {
				$('span[class="stat"]').css("background", "green");
			}
		}
		
		var appendLogMsg4 = function(time, code, msg, errorTime) {		// 로그에 메세지 추가하기
			var sp1 = '<span>'+time+'</span>';
			var sp2 = '<span class="stat">'+code+'</span>';
			var sp3 = '<span>'+"에러메세지 : "+msg+'</span>';
			var sp4 = '<span>'+"발생시간 : "+errorTime+'</span>';
			$("#msg")
			.append('<p class="muted">' + sp1+"&emsp;"+sp2+"&emsp;"+sp3 +"&emsp;"+sp4+ '</p>')
			.scrollTop(100000000);
			$('span[class="stat"]').css("background", "red");
		}

	</script>
</head>
<body>
	<h4>wisemobile Monitor</h4>
	<button id="connect" onclick="window.location.reload()">접속</button>
	<div id="container">
		<div id="start"></div>
		<div id="msg"></div>
	</div>
</body>
</html>
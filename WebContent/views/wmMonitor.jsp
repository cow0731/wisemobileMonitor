<!DOCTYPE html>

<%@page import="org.slf4j.LoggerFactory"%><%@page import="org.slf4j.Logger"%>
<%@page import="kr.co.deotis.config.MntProperties"%>
<html>
<head>
	<meta charset="EUC-KR">
	<title>Web Monitor</title>
	
	<style type="text/css">
		#msg { border:1px solid #eee; padding:10px 20px; overflow-y: scroll; max-height: 800px;	}
	</style>
	
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
	<script type="text/javascript">
		var ws = null;
		
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
		})()
			
		ws.onopen = function() {
			//document.getElementById("msg").innerText += 'Info: WebSocket connection opened.\n';
			$("#msg").append('<p> monitoring Start. </p>');
		};
		ws.onmessage = function(event) {
			//document.getElementById("msg").innerText += event.data+'\n';
			var p = event.data;
			var pArr = p.split('`');
			var mData = '';
			for (var i = 0; i < pArr.length; i++) {
				mData += pArr[i];
			}
			
			if(pArr.length == 2) {
				appendLogMsg(pArr[0], pArr[1]);
			} else {
				appendLogMsg(pArr[0], pArr[1], pArr[2]);
			}
			
			if(pArr[1]=='정상') {
				$('span[class="stat"]').css("background", "green");
			}else {
				$('span[class="stat"]').css("background", "red");
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
		
		var appendLogMsg = function(time, code) {		// 로그에 메세지 추가하기
			var sp1 = '<span>'+time+'</span>';
			var sp2 = '<span class="stat">'+code+'</span>';
			$("#msg")
			.append('<p class="muted">' + sp1+"&emsp;"+sp2+ '</p>')
			.scrollTop(100000000);
		}
		
		var appendLogMsg = function(time, code, msg) {		// 로그에 메세지 추가하기
			var sp1 = '<span>'+time+'</span>';
			var sp2 = '<span class="stat">'+code+'</span>';
			var sp3 = '<span>'+msg+'</span>';
			$("#msg")
			.append('<p class="muted">' + sp1+"&emsp;"+sp2+"&emsp;"+sp3 + '</p>')
			.scrollTop(100000000);
		}

	</script>
</head>
<body>
	<h4>wisemobile Monitor</h4>
	<div id="container">
		<div id="msg"></div>
	</div>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Log Übersicht</title>
    <style>
        body { font-family: sans-serif; background: #222; color: #eee; }
        a { color: #0f0; cursor: pointer; text-decoration: none; }
        a:hover { text-decoration: underline; }
        pre { background: #111; padding: 1rem; }
        .error { color: #f55; }
        .warn { color: #fa0; }
        .info { color: #0f0; }
    </style>
    <script>
    let currentFile = null;

    function loadLog(fileName) {
        currentFile = fileName; // merken, welches File gerade angezeigt wird
        fetch('log-viewer?file=' + encodeURIComponent(fileName))
            .then(response => response.text())
            .then(data => {
                const highlighted = data
                    .replace(/(ERROR.*)/g, '<span class="error">$1</span>')
                    .replace(/(WARN.*)/g, '<span class="warn">$1</span>')
                    .replace(/(INFO.*)/g, '<span class="info">$1</span>');
                document.getElementById("logContent").innerHTML = highlighted;
            });
    }

    // Auto-Refresh alle 5 Sekunden
    setInterval(() => {
        if (currentFile) {
            loadLog(currentFile);
        }
    }, 5000); // 5000ms = 5 Sekunden
</script>
</head>
<body>
	
<jsp:include page="/view/Navbar.jsp"></jsp:include>
	
    <h2>Verfügbare Log-Dateien</h2>
    <ul>
        <c:forEach var="file" items="${logFiles}">
            <li><a onclick="loadLog('${file}')">${file}</a></li>
        </c:forEach>
    </ul>

    <h3>Log Inhalt:</h3>
    <pre id="logContent">Bitte eine Log-Datei anklicken.</pre>
</body>
</html>
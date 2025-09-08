<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<html>
<head>
<meta charset="UTF-8">
<title>Admin Page</title>
</head>
<style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f2f2f2;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            height: 100vh;
            margin: 0;
        }
        h1 {
            margin-bottom: 40px;
        }
        .button-container {
            display: flex;
            flex-direction: column;
            gap: 20px;
        }
        button {
            padding: 15px 30px;
            font-size: 18px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            background-color: #007BFF;
            color: white;
            transition: background-color 0.3s ease;
        }
        button:hover {
            background-color: #0056b3;
        }
    </style>
<body>
	<body>

    <h1>Admin Menu</h1>
    <div class="button-container">
    <form method ="post">
    	<input type="hidden" name="action" value="benutzer"> 
        <button type="submit">Benutzer verwalten</button>
    
    </form>
    <form method ="post">
    	<input type="hidden" name="action" value="kamera"> 
        <button type="submit">Kameras verwalten</button>
    
    </form>
    <form method ="post">
    	<input type="hidden" name="action" value="standort"> 
        <button type="submit">Standort verwalten</button>
    
    </form>
    <form method ="post">
    	<input type="hidden" name="action" value="fehlerlog"> 
        <button type="submit">Fehlerlog anzeigen</button>
    
    </form>
    <form method ="post">
    	<input type="hidden" name="action" value="login"> 
        <button type="submit">Fehlerlog anzeigen</button>
    
    </form>
    </div>

</body>
	
</body>
</html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="./components/notifier.html" %>

<!DOCTYPE html>
<html lang="de">
<head>
    <meta charset="UTF-8">
    <title>Login</title>
    <style>
         body {
 
             font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
             background-color: #f8f9fa;
         }
         .wrapper {
             display: flex;
             align-items: center;
             justify-content: center;
             min-height: 100vh;
             padding: 20px;
         }
         .login-container {
             background-color: #ffffff;
             padding: 40px 30px;
             border-radius: 15px;
             box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
             width: 100%;
             max-width: 420px;
         }
         .login-container h2 {
             margin-bottom: 30px;
             text-align: center;
         }
         label {
             font-weight: 600;
             margin-bottom: 8px;
             display: block;
             color: #333;
         }
         input.form-control {
             padding: 12px;
             border: 1px solid #ccc;
             border-radius: 8px;
             transition: border-color 0.3s;
         }
         input.form-control:focus {
             border-color: #007bff;
             outline: none;
         }
         .btn-primary {
             padding: 12px;
             font-size: 16px;
             margin-top:6px;
             margin-bottom:12px;
         }
         .btn-secondary {
             padding: 10px;
             font-size: 14px;
             border-radius: 8px;
             background-color: #6c757d;
         }
         .btn-secondary:hover {
             background-color: #5a6268;
         }
         .error {
             background-color: #f8d7da;
             color: #842029;
             padding: 10px;
             border-radius: 8px;
             margin-top: 15px;
             text-align: center;
             border: 1px solid #f5c2c7;
         }
     </style>
</head>
<body>

<div class="wrapper">
    <div class="login-container">
        <h2>Anmeldung</h2>

        <form method="post">
            <div class="mb-custom">
                <label for="username">Benutzername</label>
                <input type="text" class="form-control" id="username" name="username" required />
            </div>

            <div class="mb-custom">
                <label for="password">Passwort</label>
                <input type="password" class="form-control" id="password" name="password" required />
            </div>

            <div class="d-grid mb-custom">
                <button type="submit" name="submit" class="btn btn-primary">Anmelden</button>
            </div>
        </form>

        <div class="password-link">
            <a href="<%= request.getContextPath() %>/changePassword">Passwort ändern</a>
        </div>

        <% String error = (String) request.getAttribute("error"); %>
        <% if (error != null) { %>
            <div class="error"><%= error %></div>
        <% } %>
    </div>
</div>

</body>
</html>
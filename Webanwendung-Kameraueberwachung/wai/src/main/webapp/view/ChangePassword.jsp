<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="de">
<head>
<meta charset="UTF-8">
<title>Passwort ändern</title>
<link rel="icon" href="../images/favicon.ico" />
<link rel="stylesheet" href="../css/bootstrap.css" />
<link rel="stylesheet" href="../css/local.css" />
<style>
.wrapper {
	font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
	background-color: #f8f9fa;
	display: flex;
	align-items: center;
	justify-content: center;
	min-height: 100vh;
	padding: 20px;
}

.wrapper .password-container {
	background-color: #ffffff;
	padding: 40px 30px;
	border-radius: 15px;
	box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
	width: 100%;
	max-width: 420px;
}

.wrapper .password-container h2 {
	margin-bottom: 30px;
	text-align: center;
}

.wrapper label {
	font-weight: 600;
	margin-bottom: 4px;
	display: block;
	color: #333;
}

.wrapper input.form-control {
	padding: 12px;
	border: 1px solid #ccc;
	border-radius: 8px;
	transition: border-color 0.3s;
	margin-bottom: 8px;
}

.wrapper input.form-control:focus {
	border-color: #007bff;
	outline: none;
}

.wrapper .btn-primary {
	padding: 12px;
	font-size: 16px;
	margin-top: 6px;
	margin-bottom: 12px;
}

.wrapper .error {
	background-color: #f8d7da;
	color: #842029;
	padding: 10px;
	border-radius: 8px;
	margin-top: 15px;
	text-align: center;
	border: 1px solid #f5c2c7;
}

.wrapper .success {
	background-color: #d1e7dd;
	color: #0f5132;
	padding: 10px;
	border-radius: 8px;
	margin-top: 15px;
	text-align: center;
	border: 1px solid #badbcc;
}
</style>
</head>
<body>

	<div class="wrapper">
		<div class="password-container">
			<h2>Passwort ändern</h2>
			<form method="post">
				<div class="mb-custom">
					<label for="username">Benutzername</label> <input type="text"
						class="form-control" id="username" name="username" required />
				</div>

				<div class="mb-custom">
					<label for="oldPassword">Aktuelles Passwort</label> <input
						type="password" class="form-control" id="oldPassword"
						name="oldPassword" required />
				</div>

				<div class="mb-custom">
					<label for="newPassword">Neues Passwort</label> <input
						type="password" class="form-control" id="newPassword"
						name="newPassword" required />
				</div>

				<div class="mb-custom">
					<label for="confirmPassword">Neues Passwort bestätigen</label> <input
						type="password" class="form-control" id="confirmPassword"
						name="confirmPassword" required />
				</div>

				<div class="d-grid mb-custom">
					<button type="submit" class="btn btn-primary">Ändern</button>
				</div>

				<div class="d-grid">
					<a href="<%= request.getContextPath() %>/login"
						class="btn btn-secondary">Zurück zur Login-Seite</a>
				</div>
			</form>

			<% String message = (String) request.getAttribute("message"); %>
			<% if (message != null) { %>
			<div
				class="<%= message.startsWith("Erfolg") ? "success" : "error" %>"><%= message %></div>
			<% } %>
		</div>
	</div>

</body>
</html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<style>
      .navbar {
      font-family: Arial, sans-serif;
        background-color: #2c3e50;
        padding: 1rem;
        color: white;
        display: flex;
        justify-content: space-between;
        align-items: center;
        flex-wrap: wrap;
        border-radius: 8px;
        box-shadow: 0 2px 5px rgba(0,0,0,0.2);
    }

    .navbar h1 {
        margin: 0;
        font-size: 1.5rem;
        color: white;
    }

    .nav-buttons {
        display: flex;
        gap: 10px;
        flex-wrap: wrap;
    }

    .nav-buttons form {
        margin: 0;
    }

    .nav-buttons button {
        background-color: #3498db;
        color: white;
        border: none;
        padding: 0.6rem 1rem;
        border-radius: 6px;
        cursor: pointer;
        transition: background-color 0.2s ease-in-out;
    }

    .nav-buttons button:hover {
        background-color: #2980b9;
    }
</style>

<div class="navbar">
    <h1>Menü</h1>

    <div class="nav-buttons">
      
        <form method="post" action="/wai/MainMenuNavbar">
            <input type="hidden" name="action" value="login">
            <button type="submit">Logout</button>
        </form>
        
        <form method="post" action="/wai/MainMenuNavbar">
         <input type="hidden" name="action" value="homepage">
            <button type="submit">Hauptmenü</button>
        
        </form>
    </div>
</div>

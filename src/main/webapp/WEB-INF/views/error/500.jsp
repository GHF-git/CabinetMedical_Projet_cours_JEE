<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Erreur 500 - Erreur serveur</title>
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background-color: #f8fafc;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            margin: 0;
        }
        .error-container {
            text-align: center;
            padding: 3rem;
        }
        h1 {
            font-size: 6rem;
            color: #ef4444;
            margin-bottom: 1rem;
        }
        h2 {
            color: #1e293b;
            margin-bottom: 1rem;
        }
        p {
            color: #64748b;
            margin-bottom: 2rem;
        }
        a {
            display: inline-block;
            padding: 0.75rem 1.5rem;
            background-color: #2563eb;
            color: white;
            text-decoration: none;
            border-radius: 4px;
        }
        a:hover {
            background-color: #1d4ed8;
        }
    </style>
</head>
<body>
    <div class="error-container">
        <h1>500</h1>
        <h2>Erreur serveur interne</h2>
        <p>Une erreur s'est produite sur le serveur. Veuillez réessayer plus tard.</p>
        <a href="${pageContext.request.contextPath}/index.jsp">Retour à l'accueil</a>
    </div>
</body>
</html>

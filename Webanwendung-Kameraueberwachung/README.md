Java-basierte Webanwendung (Servlets, JSP, JDBC, PostgreSQL) zur Verwaltung von Überwachungskameras, Benutzern und Bilddaten 
Bilder erhält man über einen Scheduled Job (Quartz), der diese herunterlädt, als Thumbnails verarbeitet und Metadaten in einer Datenbank abspeichert     
Rollenbasierte Berechtigungen sowie Datenbankzugriffe über ein DAO-Pattern mit JNDI-Connection. 
Frontend mit JSP/JavaScript inkl. Benachrichtigungen und Fehlerhandling.

In der context.xml müssen Pfade auf Server Gerät angepasst werden und Daten von einer aufgesetzten Datenbank ergänzt werden.
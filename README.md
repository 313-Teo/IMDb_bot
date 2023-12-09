# IMDb_bot by Biasiolo Matteo 5CII

Come far funzionare il bot di Telegram:
1. Aprire XAMPP e avviare sia "Apache Web Server" che "MySQL Database"
2. Tramire la pagina phpMyAdmin creare il database "IMDb"
   - Creare un nuovo database e chiamarlo "IMDb"
   - Importare la tabella "movies" tramite il file IMDb.sql nella sezione "importa" di phpMyAdmin
  
    (in caso di problemi relativi all'inserimento della tabella usare il WebCraler allegato nella repository)

3. Aprire IntelliJ e importare il progetto "TelegramBot" 
4. Scaricare tutte le librerie necessarie per far funzionare il bot 
   - Scaricare le librerie "mysql.connector.j" e "slf4j.nop" da Maven
   - Aggiungere la libreria TelegramBots6.jar allegata nella repository da Java
5. Avviare il bot nella pagina Main.java del progetto
6. Cercare "Biasiolo5CII" o @IrishIMDbbot su Telegram

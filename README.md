# IMDb_bot by Biasiolo Matteo 5CII

Come far funzionare il bot di Telegram:
1. Aprire XAMPP e avviare sia "Apache Web Server" che "MySQL Database"
2. Tramire la pagina phpMyAdmin creare il database "IMDb"
   - Creare un nuovo database e chiamarlo "IMDb"
   - Importare la tabella "movies" tramite il file IMDb.sql nella sezione "importa" di phpMyAdmin
  
    (in caso di problemi relativi all'inserimento della tabella usare il WebCraler allegato nella repository)

3. Scaricare IntelliJ
4. Scaricare il progetto "TelegramBot" e importarlo su IntelliJ
   - tutte le librerie necessarie per far funzionare il bot sono scaricate all'interno del progetto
  
     (in caso di problemi relativi alle librerie scaricare le librerie "mysql.connector.j" e "slf4j.nop" da Maven
         e TelegramBots6.jar da Java allegata nella repository)
6. Avviare il bot nella pagina Main.java del progetto
7. Cercare "Biasiolo5CII" o @IrishIMDbbot su Telegram

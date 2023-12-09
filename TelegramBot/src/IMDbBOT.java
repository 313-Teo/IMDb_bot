import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import javax.swing.*;
import java.sql.DriverManager;
import java.sql.*;

class IMDbBOT extends TelegramLongPollingBot {

    static java.sql.Connection conn1 = null;

    public String getBotUsername() {
        return "Biasiolo5CII";
    }
    @Override
    public String getBotToken() {
        return "6816736353:AAFauR2j7ya2WpZ1M_UiQtTZ9vGwEZ0yFsM";
    }
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText().trim();
            String chatId = update.getMessage().getChatId().toString();

            if (messageText.startsWith("/searchmovie ")) {
                String searchText = messageText.replace("/searchmovie ", "").trim();
                searchMovie(chatId, searchText);
            }
            else{
                switch (messageText) {
                    case "/start":
                        sendStartMessage(chatId);
                        break;
                    case "/top250movies":
                        sendMovies(chatId, "top250");
                        break;
                    case "/mostpopularmovies":
                        sendMovies(chatId, "mostpopular");
                        break;
                    case "/randommovie":
                        sendRandomMovie(chatId);
                        break;
                    default:
                        sendUnknownCommandMessage(chatId);
                        break;
                }
            }
        }

    }

    private void sendStartMessage(String chatId) {
        String startMessage = "Benvenuto nell'IMDb Bot!\n\n"
                            + "Comandi disponibili:\n"
                            + "/start - Visualizza questo messaggio\n"
                            + "/top250movies - Visualizza i 250 migliori film di IMDb\n"
                            + "/mostpopularmovies - Visualizza i 100 film più popolari di IMDb\n"
                            + "/searchmovie + NOME_FILM - Visualizza la scheda relativa al film\n"
                            + "/randommovie - Visualizza la scheda relativa ad un film casuale";

        SendMessage startSendMessage = new SendMessage();
        startSendMessage.setChatId(chatId);
        startSendMessage.setText(startMessage);

        try {
            execute(startSendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendUnknownCommandMessage(String chatId) {
        String unknownCommandMessage = "Comando sconosciuto. Usa /start per visualizzare la lista dei comandi disponibili.";

        SendMessage unknownCommandSendMessage = new SendMessage();
        unknownCommandSendMessage.setChatId(chatId);
        unknownCommandSendMessage.setText(unknownCommandMessage);

        try {
            execute(unknownCommandSendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMovies(String chatId, String list) {
        final int MAX_MESSAGE_LENGTH = 4000;
        StringBuilder moviesMessage = new StringBuilder();

        try {
            conn1 = DriverManager.getConnection("jdbc:mysql://127.0.0.1/IMDb", "root", "");
            String query = "SELECT movie FROM movies WHERE list = ? LIMIT 250";
            PreparedStatement preparedStatement = conn1.prepareStatement(query);
            preparedStatement.setString(1, list);
            ResultSet resultSet = preparedStatement.executeQuery();

            String title;
            if (list.equals("top250")) {
                title = "La Top 250 film di IMDb:\n\n";
            } else {
                title = "I film più popolari su IMDb:\n\n";
            }
            moviesMessage.append(title);

            int counter = 1;
            while (resultSet.next()) {
                String movieName = resultSet.getString("movie");
                String movieInfo = counter + ". " + movieName + "\n\n";
                counter++;

                if (moviesMessage.length() + movieInfo.length() > MAX_MESSAGE_LENGTH) {
                    SendMessage moviesSendMessage = new SendMessage();
                    moviesSendMessage.setChatId(chatId);
                    moviesSendMessage.setText(moviesMessage.toString());
                    execute(moviesSendMessage);

                    moviesMessage.setLength(0);
                }

                moviesMessage.append(movieInfo);
            }

            if (moviesMessage.length() > 0) {
                SendMessage moviesSendMessage = new SendMessage();
                moviesSendMessage.setChatId(chatId);
                moviesSendMessage.setText(moviesMessage.toString());
                execute(moviesSendMessage);
            }

            preparedStatement.close();
        } catch (SQLException | TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void searchMovie(String chatId, String movieToSearch) {
        StringBuilder movieMessage = new StringBuilder();
        String title = "Risultati della ricerca per \"" + movieToSearch + "\":\n\n";
        movieMessage.append(title);

        try {
            conn1 = DriverManager.getConnection("jdbc:mysql://127.0.0.1/IMDb", "root", "");
            String query = "SELECT movie, year, time, rating, link FROM movies WHERE movie LIKE ? LIMIT 1";
            PreparedStatement preparedStatement = conn1.prepareStatement(query);
            preparedStatement.setString(1, movieToSearch);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.isBeforeFirst()) {
                movieMessage = new StringBuilder("Nessun film trovato per la ricerca: \"" + movieToSearch + "\"");
            } else {
                while (resultSet.next()) {
                    String movieName = resultSet.getString("movie");
                    String year = resultSet.getString("year");
                    String time = resultSet.getString("time");
                    String rating = resultSet.getString("rating");
                    String movieLink = resultSet.getString("link");

                    String movieInfo =  "Titolo: " + movieName + "\n"
                                     +  "Anno: " + year + "\n"
                                     +  "Durata: " + time + "\n"
                                     +  "Valutazione: ⭐ " + rating + "\n"
                                     +  "Link IMDb: " + movieLink + "\n\n";

                    movieMessage.append(movieInfo);
                }
            }

            preparedStatement.close();

            SendMessage searchSendMessage = new SendMessage();
            searchSendMessage.setChatId(chatId);
            searchSendMessage.setText(movieMessage.toString());
            execute(searchSendMessage);

        } catch (SQLException | TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendRandomMovie(String chatId) {
        StringBuilder movieMessage = new StringBuilder();
        String title = "Ecco un film casuale da IMDb:\n\n";
        movieMessage.append(title);

        try {
            conn1 = DriverManager.getConnection("jdbc:mysql://127.0.0.1/IMDb", "root", "");
            String query = "SELECT movie, year, time, rating, link FROM movies ORDER BY RAND() LIMIT 1";
            PreparedStatement preparedStatement = conn1.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String movieName = resultSet.getString("movie");
                String year = resultSet.getString("year");
                String time = resultSet.getString("time");
                String rating = resultSet.getString("rating");
                String movieLink = resultSet.getString("link");

                String movieInfo =  "Titolo: " + movieName + "\n"
                                 +  "Anno: " + year + "\n"
                                 +  "Durata: " + time + "\n"
                                 +  "Valutazione: ⭐ " + rating + "\n"
                                 +  "Link IMDb: " + movieLink + "\n\n";

                movieMessage.append(movieInfo);
            }

            preparedStatement.close();

            SendMessage randomMovieSendMessage = new SendMessage();
            randomMovieSendMessage.setChatId(chatId);
            randomMovieSendMessage.setText(movieMessage.toString());
            execute(randomMovieSendMessage);

        } catch (SQLException | TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

import java.util.ArrayList;
import java.util.Scanner;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;

import java.sql.*;


public class Main {

    static java.sql.Connection conn1 = null;
    public static void main(String[] args) throws SQLException {
        conn1 = DriverManager.getConnection("jdbc:mysql://127.0.0.1/IMDb", "root", "");

        String url1 = "https://www.imdb.com/chart/top/";
        String url2 = "https://www.imdb.com/chart/moviemeter/";

        crawl(url1);
        crawl(url2);
    }

    public static void crawl(String url) {

        boolean isTop250 = url.contains("top");

        try {
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();

            if (connection.response().statusCode() == 200) {
                System.out.println("Link: " + url);
                System.out.println("Title: " + document.title());
            }

            if (document != null) {
                for (Element ul : document.select("ul.ipc-metadata-list.ipc-metadata-list--dividers-between.sc-9d2f6de0-0.iMNUXk.compact-list-view.ipc-metadata-list--base")) {
                    for (Element listItem : document.select("li.ipc-metadata-list-summary-item.sc-59b6048d-0.cuaJSp.cli-parent")) {

                        Element link = listItem.selectFirst("a[href]");
                        String titlelink = link.absUrl("href");

                        String title = pagecrawl(titlelink);

                        System.out.println("title: " + title);
                        System.out.println("link: " + titlelink);

                        int spancount = 0;
                        String infomovie1 = "";
                        String infomovie2 = "";

                        for (Element spanItem : listItem.select("span.sc-479faa3c-8.bNrEFi.cli-title-metadata-item")) {
                            if(spanItem != null) {
                                if (spancount == 0) {
                                    infomovie1 = spanItem.text();
                                    System.out.println("info1: " + infomovie1);
                                    spancount++;
                                } else if (spancount == 1) {
                                    infomovie2 = spanItem.text();
                                    System.out.println("info2: " + infomovie2);
                                    spancount++;
                                }
                            }
                        }

                        String rating = "";
                        Element span = listItem.selectFirst("span.ipc-rating-star.ipc-rating-star--base.ipc-rating-star--imdb.ratingGroup--imdb-rating");

                        if(span != null) {
                            rating = span.text();
                            System.out.println("rating: " + span.text());
                        }

                        String list = "";

                        if (isTop250) {
                            list = "top250";
                        } else {
                            list = "mostpopular";
                        }

                        System.out.println("list: " + list);

                        try {
                            String query = "INSERT INTO movies (movie, year, time, rating, link, list) VALUES (?, ?, ?, ?, ?, ?)";
                            PreparedStatement preparedStatement = conn1.prepareStatement(query);
                            preparedStatement.setString(1, title);
                            preparedStatement.setString(2, infomovie1);
                            preparedStatement.setString(3, infomovie2);
                            preparedStatement.setString(4, rating);
                            preparedStatement.setString(5, titlelink);
                            preparedStatement.setString(6, list);
                            preparedStatement.executeUpdate();
                            preparedStatement.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    public static String pagecrawl(String url) {
        try {
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();

            if (document != null) {
                for (Element div : document.select("div.sc-69e49b85-0.jqlHBQ")) {
                    for (Element titleElement : document.select("h1")) {

                        Element title = titleElement.selectFirst("h1");
                        return title.text();

                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return "";
    }

}
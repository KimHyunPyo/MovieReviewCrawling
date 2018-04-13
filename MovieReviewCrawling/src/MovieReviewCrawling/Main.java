package MovieReviewCrawling;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main
{
	public static String cgvurl = "http://www.cgv.co.kr";
	public static ArrayList<Movie> movieList = new ArrayList<Movie>();

	public static void main(String[] args) throws IOException
	{
		Connection.Response response = Jsoup
				.connect("http://www.cgv.co.kr/movies/?ft=0")
				.method(Connection.Method.GET).execute();
		Document cgv = response.parse();
		Elements MovieChart = cgv.select("div[class = sect-movie-chart]")
				.select("div[class = box-contents]");
		for (Element MovieChart1 : MovieChart)
		{
			Movie movie = new Movie();
			String name = MovieChart1.select("a").select("Strong").text();
			movie.setTitle(name);
			String url = MovieChart1.select("a").first().attr("href");
			url = cgvurl + url;
			movie.setUrl(url);
			movieList.add(movie);
		}
		for (Movie movie1 : movieList)
		{
			Connection.Response MoiveInfoResponse = Jsoup
					.connect(movie1.url)
					.method(Connection.Method.GET).execute();
			Document MovieInfo = MoiveInfoResponse.parse();
			System.out.println(MovieInfo.html());
			Element movieInfo = MovieInfo.select("div[class = egg-gage big]").select("Strong").first();
			String score = movieInfo.text();
			if( score.equals(""))
			{
				score = "0"; 
			}
			
			movie1.setScore(Integer.parseInt(score));
			System.out.println("영화이름 :" + movie1.getTitle() + "   영화 평점:"
			+ movie1.getScore());
		}

	}
}

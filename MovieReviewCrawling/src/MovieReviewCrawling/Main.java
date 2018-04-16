package MovieReviewCrawling;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.*;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {
	public static String cgvurl = "http://www.cgv.co.kr";
	public static String reviewurl = "/common/ajax/point.aspx/GetMoviePointVariableList";
	public static ArrayList<Movie> movieList = new ArrayList<Movie>();

	public static void main(String[] args) throws IOException, JSONException {
		Connection.Response response = Jsoup.connect("http://www.cgv.co.kr/movies/?ft=0").method(Connection.Method.GET)
				.execute();
		Document cgv = response.parse();
		Elements MovieChart = cgv.select("div[class = sect-movie-chart]").select("div[class = box-contents]");
		for (Element MovieChart1 : MovieChart) {
			Movie movie = new Movie();
			String name = MovieChart1.select("a").select("Strong").text();
			movie.setTitle(name);
			String url = MovieChart1.select("a").first().attr("href");
			url = cgvurl + url;
			movie.setUrl(url);
			movieList.add(movie);
		}
		for (Movie movie1 : movieList) {
			String result = "";
			Connection.Response MoiveInfoResponse = Jsoup.connect(movie1.url).method(Connection.Method.GET).execute();
			Document MovieInfo = MoiveInfoResponse.parse();
			Element movieInfo = MovieInfo.select("div[class = egg-gage big]").select("Strong").first();
			String score = movieInfo.text();
			if (score.equals("")) {
				score = "0";
			}
			String MovieNum = movie1.url.substring(movie1.url.indexOf("=") + 1);
			movie1.MovieNum = (Integer.parseInt(MovieNum));
			movie1.setScore(Integer.parseInt(score));
			System.out.println("영화 제목 :" + movie1.getTitle() + " 영화 평점:" + movie1.getScore());
			JSONObject reviewJson = new JSONObject();
			reviewJson.put("movieIdx", movie1.MovieNum);
			reviewJson.put("pageIndex", "1");
			reviewJson.put("pageSize", "1");
			reviewJson.put("orderType", "1");
			reviewJson.put("filterType", 1);
			reviewJson.put("isTotalCount", "false");
			reviewJson.put("isMyPoint", "false");
			System.out.println(reviewJson.toString());

			URL object = new URL("http://www.cgv.co.kr/common/ajax/point.aspx/GetMoviePointVariableList");
			try {
				HttpURLConnection con = (HttpURLConnection) object.openConnection();
				con.setDoOutput(true);
				con.setDoInput(true);
				con.setRequestProperty("Accept", "application/json");
				con.setRequestProperty("Content-Type", "application/json");
				con.setRequestMethod("POST");
				OutputStream os = con.getOutputStream();
				os.write(reviewJson.toString().getBytes());
				os.flush();
				os.close();
				int HttpResult = con.getResponseCode();
				if (HttpResult == HttpURLConnection.HTTP_OK) {
					InputStream is = con.getInputStream();
					ByteArrayOutputStream baos = null;
					baos = new ByteArrayOutputStream();
					byte[] byteBuffer = new byte[1024];
					byte[] byteData = null;
					int nLength = 0;
					while ((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
						baos.write(byteBuffer, 0, nLength);
					}

					byteData = baos.toByteArray();
					String StringRetrun;
					StringRetrun = new String(byteData);
					String ChangeReturnReview = StringRetrun.replace("\\", " ");
					JSONObject responseJSON = new JSONObject(StringRetrun);
					ObjectMapper mapper = new ObjectMapper();
					HashMap<String, Object> reviewparse = mapper.readValue(responseJSON.toString(), HashMap.class);
					Object TempObject = reviewparse.get("d");
					HashMap<String, Object> reviewparse2 = mapper.readValue(TempObject.toString(), HashMap.class);
					TempObject = reviewparse2.get("List");
					String TempString = TempObject.toString();
					System.out.println(TempString);
//					JSONObject tempJson = new JSONObject(TempString);
//					
//					System.out.println(tempJson.get("CommnetText"));
				} else {
					// System.out.println(con.getResponseMessage());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {

			}

		}

	}
}

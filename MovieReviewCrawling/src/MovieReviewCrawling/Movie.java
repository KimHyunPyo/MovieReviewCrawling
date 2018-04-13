package MovieReviewCrawling;

import java.util.ArrayList;

public class Movie
{
	public String title;
	public String url;
	public int Score;
	public ArrayList<Review> review = new ArrayList<Review>();
	
	
	
	public int getScore()
	{
		return Score;
	}
	public void setScore(int score)
	{
		Score = score;
	}
	public void setScore(String score)
	{
		Score = Integer.parseInt(score);
	}
	public String getTitle()
	{
		return title;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
	public String getUrl()
	{
		return url;
	}
	public void setUrl(String url)
	{
		this.url = url;
	}

	
	
}

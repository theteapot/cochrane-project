package populator;

import java.net.SocketTimeoutException;

import org.jsoup.Jsoup;

public class ArticleAdderMultithreaded implements Runnable {
	
	private String url;
	private String doi;
	private MySqlAccessor dbAccessor;
	
	public ArticleAdderMultithreaded(String[] hrefDoi, MySqlAccessor dbAccessor) {
		this.url = hrefDoi[1];
		this.doi = hrefDoi[0];
		this.dbAccessor = dbAccessor;
	}
	
	public void run() {
		String retry = "yes";
		ArticleObj article = new ArticleObj();
		try {
			if (!dbAccessor.duplicate(doi)) {
				while (retry == "yes") {
					try {
						article = new ArticleObj(Jsoup.connect(url).timeout(20*1000).get());
						retry = "no";
						System.out.println("Processing article: " + article.title.substring(0, Math.min(article.title.length(), 20))+"..." + article.doi);
						dbAccessor.writeDataBase(doi, article.title, article.shortAbstract, article.authors, article.keywords, article.mesh, article.pubDate);;
					} catch (SocketTimeoutException socketTimeout) {
						retry = "yes";
						System.out.println("Socket timeout for article... retrying");
					}
			
				}
			} else {
				System.out.println("Duplicate article: " + doi);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

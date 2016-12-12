package populator;

import java.net.SocketTimeoutException;
import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ResultsFilter {
	
	protected LinkedList<Element> pageContents;
	protected MySqlAccessor dbAccessor = new MySqlAccessor();
	
	
	protected ResultsFilter() {
		this.pageContents = new LinkedList<Element>();
	}
	
	/*protected LinkedList<String[]> parseTopicPage(String url) throws Exception {
		//Given the initial url of a topic page
		
		LinkedList<String> topicLinks = getTopicLinks(url);
		LinkedList<String[]> articleUrls = new LinkedList<String[]>();
		
		for (String topicLink : topicLinks) {
			System.out.println("Descending into topic: " + topicLink);
			articleUrls.addAll(parseIndexPage(topicLink));
			System.out.println("Finished topic: " + topicLink);
		}
		return articleUrls;

	}*/
	
	protected LinkedList<String[]> parsePage(String url) throws Exception {
		//Takes the url of a topic page, finds the url and doi of each article
		//in that topic.
		LinkedList<String[]> hrefUrlDoi = new LinkedList<String[]>();
		Document index = Jsoup.connect(url).timeout(10*1000).get();
		
		while (!hasNextPage(index).isEmpty()) {
			System.out.println("Seen articles: " + hrefUrlDoi.size());
			Elements indexLinks = index.getElementsByClass("results-block__link");
			//Elements indexTitles = index.getElementsByClass("mainTitle"); left over from when getting title
			
			for (int i = 0; i < indexLinks.size(); i++) {
				String[] articleUrlDoi = new String[2];
				articleUrlDoi[1] = indexLinks.get(i).attr("href");
				articleUrlDoi[0] = articleUrlDoi[1].substring(35, articleUrlDoi[1].length()-5);
				//articleUrlTitle[0] = indexTitles.get(i).text().replace('‐', '\u002d'); //Some titles use hyphens, should replace
				hrefUrlDoi.add(articleUrlDoi);                                   //with hyphen-minus to avoid errors.
			}
			
			url = hasNextPage(index);
			index = Jsoup.connect(url).timeout(10*1000).get();
		}
		return hrefUrlDoi;
	}
	
	protected void addArticlePages(LinkedList<String[]> hrefDoiArray) throws Exception {
		
		for (String[] articleUrlDoi : hrefDoiArray) {
			String url = articleUrlDoi[1];
			String doi = articleUrlDoi[0];
			String retry = "yes";
			ArticleObj article = new ArticleObj();
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
		}
	}
	
	protected LinkedList<String> getTopicLinks(String url) throws Exception {
		//Given the url of the topic page returns all the urls of each topic
		Document topicPage = Jsoup.connect(url).timeout(10*1000).get();
		LinkedList<String> urlList = new LinkedList<String>();

		Elements topicLinks = topicPage.getElementsByClass("browse-block__list-item-link");
		for (Element topicLink : topicLinks) {
			urlList.add(topicLink.attr("abs:href"));
		}
		return urlList;
	}
	
	private String hasNextPage(Document doc) {
		
		Elements paginatorLinks = doc.select(".results-block__pagination-list-item-link");
		for (Element link : paginatorLinks) {
			if (link.attr("rel").equals("next")) {
				return link.attr("abs:href");
			}
		}
		
		return "";
	}
	
	protected void parseArticlePage(Document doc) {
		Element articleSummary = doc.getElementById("en_short_abstract");
		pageContents.add(articleSummary);
	}
	
	protected LinkedList<Element> getPageContents() {
		return this.pageContents;
	}
}
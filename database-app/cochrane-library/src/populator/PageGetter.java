package populator;

import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class PageGetter {
	
	protected ResultsFilter parseDocObj;
	
	public PageGetter(String url) {
		
		try {
			ResultsFilter pageScraper = new ResultsFilter();
			LinkedList<String> topicLinks = pageScraper.getTopicLinks(url);
			for (String link : topicLinks) {
				System.out.println("Now doing topic: " + link);
				LinkedList<String[]> hrefTitleArray = pageScraper.parsePage(link);
				pageScraper.addArticlePages(hrefTitleArray);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//public static void main(String[] args) throws Exception {
	//		new PageGetter();
	//}
	
	/*protected LinkedList<Element> JsoupConnector(String url) throws Exception {
		MySqlAccessor dbAccess = new MySqlAccessor();
		
		LinkedList<String[]> hrefList = parseDocObj.parseTopicPage(url);
		
		//for (String href : hrefList) {
			//Document article = Jsoup.connect(href).timeout(10*1000).get();
			//ArticleObj artPage = new ArticleObj(article);
			//System.out.println(artPage.title + " "+artPage.pubDate + " "+artPage.authors+" " +artPage.shortAbstract + " "+ artPage.keywords.toString() + " " +artPage.mesh);
			//dbAccess.setArticleInfo(artPage.doi, artPage.title, artPage.shortAbstract, artPage.authors, artPage.keywords, artPage.pubDate);
			//dbAccess.readDataBase();
			
		//}
		
		return parseDocObj.getPageContents();
	}*/
	
	
	
	
	
}



package populator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ArticleObj {
	protected String doi;
	protected String title;
	protected Date pubDate;
	protected LinkedList<String> keywords;
	protected LinkedList<String> mesh;
	protected LinkedList<String> authors;
	protected String shortAbstract;
	
	public ArticleObj(Document articleDoc) throws Exception {
		this.pubDate = getDate(articleDoc);
		this.title = getTitle(articleDoc);
		this.keywords = getKeywords(articleDoc);
		this.shortAbstract = getShortAbstract(articleDoc);
		this.authors = getAuthors(articleDoc);
		this.doi = getDoi(articleDoc);
		this.mesh = getMesh(articleDoc);
	}
	
	public ArticleObj() {
		this.pubDate = null;
		this.title = null;
		this.keywords = null;
		this.shortAbstract = null;
		this.authors = null;
		this.doi = null;
		this.mesh = null;
	}
	
	private LinkedList<String> getMesh(Document articleDoc) {
		Elements meshElements = articleDoc.select(".article-info__mesh-item");
		LinkedList<String> meshHeaders = new LinkedList<String>();
		for (Element mesh : meshElements) {
			meshHeaders.add((mesh.text()));
		}
		return meshHeaders;
	}
	
	protected String getDoi(Document articleDoc) {
		String doi = articleDoc.select(".article-info__section-doi-data").text();
		return doi;
	}
	
	protected Date getDate(Document articleDoc) throws Exception {
		String htmlDate = articleDoc.select("#first-published-date").attr("datetime");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date = df.parse(htmlDate);
		return date;
	}
	
	protected LinkedList<String> getAuthors(Document articleDoc) {
		Elements authorElements = articleDoc.select(".article-header__authors-item");
		LinkedList<String> authors = new LinkedList<String>();
		for (Element name : authorElements) {
			authors.add((name.attr("data-author-name")));
		}
		return authors;
	}
	
	protected String getTitle(Document articleDoc) {
		String title = articleDoc.select(".article-header__title[lang='en']").text();
		return title;
	}
	
	protected String getShortAbstract(Document articleDoc) {
		String shortAbstract = articleDoc.select("#en_short_abstract p:eq(3)").text();		
		return shortAbstract;
	}
	
	protected LinkedList<String> getKeywords(Document articleDoc) {
		LinkedList<String> myKeyWords = new LinkedList<String>();
		Elements articleInfo = articleDoc.select(".article-info__topic-container");
		Elements keywords = articleInfo.select(".article-info__topic-list-link");
		for (Element element : keywords) {
			myKeyWords.add(element.text());
		}
		return myKeyWords;
	}
	
}

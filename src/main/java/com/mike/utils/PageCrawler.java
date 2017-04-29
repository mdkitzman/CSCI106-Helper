package com.mike.utils;


import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.mike.model.FinalProject;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class PageCrawler extends WebCrawler {
	
	private final static Pattern FILTERS = Pattern.compile(".*coloradomesa.edu/~([a-zA-Z0-9]+)/.*(htm|html|css)$");
	
	private Set<CrawlResult> results = new HashSet<CrawlResult>();

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		boolean matches = FILTERS.matcher(url.getURL().toLowerCase()).matches(); 
		return matches;
	}

	/**
	 * This function is called when a page is fetched and ready 
	 * to be processed by your program.
	 */
	@Override
	public void visit(Page page) {
		CrawlResult result = new CrawlResult(page.getWebURL().getURL());
		if(result.getUrl().endsWith("css"))
			result.setHTML(false);
		else if(page.getParseData() instanceof HtmlParseData){
			result.setHTML(true);
			HtmlParseData parseData = (HtmlParseData)page.getParseData();
			Document htmlDoc = Jsoup.parse(parseData.getHtml());
			result.setNumHTML5Tags((int)htmlDoc.getAllElements()
									.stream()
									.filter(e -> FinalProject.html5tags.contains(e.tagName()))
									.count());
			result.setNumInlineStyles((int)htmlDoc.getAllElements()
									.stream()
									.filter(e -> !e.attr("style").isEmpty() )
									.count());
			result.setNumEmbeddedStyles((int)htmlDoc.getAllElements()
									.stream()
									.filter(e -> "style".equals(e.tagName()))
									.filter(e -> e.attr("link").isEmpty())
									.count());
		
			result.setNumTags((int)htmlDoc.getAllElements().stream().count());
			result.setFoundFooter(htmlDoc.getAllElements().stream().filter(e -> "footer".equals(e.tagName().toLowerCase())).count() > 0);
			result.setFoundHeader(htmlDoc.getAllElements().stream().filter(e -> "header".equals(e.tagName().toLowerCase())).count() > 0);
			result.setFoundImg(htmlDoc.getAllElements().stream().filter(e -> "img".equals(e.tagName().toLowerCase())).count() > 0);
			result.setFoundNav(htmlDoc.getAllElements().stream().filter(e -> "nav".equals(e.tagName().toLowerCase())).count() > 0);
			result.setFoundTable(htmlDoc.getAllElements().stream().filter(e -> "table".equals(e.tagName().toLowerCase())).count() > 0);
		}
		results.add(result);
	}

	@Override
	public Object getMyLocalData(){
		return results;
	}
}

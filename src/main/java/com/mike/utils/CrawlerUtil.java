package com.mike.utils;

import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.mike.model.FinalProject;
import com.mike.model.ModelValidationResult;
import com.mike.validators.IsCSSValidator;
import com.mike.validators.IsHTMLValidator;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class CrawlerUtil extends ValidationUtility {

	private final Collection<Pair<String, String>> urls;
	
	public CrawlerUtil(IsCSSValidator cssValidator, IsHTMLValidator htmlValidator, Collection<Pair<String, String>> urls){
		super(cssValidator, htmlValidator);
		this.urls = urls;
	}
	
	private String url2username(String url) {
		final Pattern userPattern = Pattern.compile("org.coloradomesa.edu/~([a-zA-Z0-9]+)/");
		Matcher m = userPattern.matcher(url);
		return !m.find() ? "Unknown Student" : m.group(1);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void doWork(PrintStream output) {
		
		CrawlConfig crawlConfig = new CrawlConfig();
        crawlConfig.setCrawlStorageFolder(System.getProperty("user.dir"));
        
        PageFetcher pageFetcher = new PageFetcher(crawlConfig);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController crawlController = null;
		try {
			crawlController = new CrawlController(crawlConfig, pageFetcher, robotstxtServer);
		} catch (Exception e1) {
			output.printf("Problem encountered when creating the crawl controller: %s - %s", e1.getClass().getSimpleName(), e1.getMessage());
			return;
		}
        
        urls.stream().map(pair -> pair.getLeft()).forEach(crawlController::addSeed);
       	Map<String, String> username2name = urls.stream().collect(Collectors.toMap(urlPair -> url2username(urlPair.getLeft()), urlPair -> urlPair.getRight()));
       	
        
        crawlController.start(PageCrawler.class, 4);
        List<CrawlResult> crawledUrls = new ArrayList<CrawlResult>();
		crawlController
			.getCrawlersLocalData()
			.stream()
			.map(obj -> (Set<CrawlResult>)obj)
			.forEach(urlSet -> crawledUrls.addAll(urlSet));
		
		Map<String, List<CrawlResult>> user2Urls = crawledUrls.stream()
			.collect(Collectors.groupingBy(url -> username2name.get(url2username(url.getUrl()))));
		
		user2Urls.keySet().stream().forEach(student -> {
			
			List<CrawlResult> userUrls = user2Urls.get(student);
			
			FinalProject finalProject = new FinalProject();
			finalProject.studentName = student;
			finalProject.numCSSDocuments = (int)userUrls.stream().filter(CrawlResult::isCSS).count();
			finalProject.numHTMLPages = (int)userUrls.stream().filter(CrawlResult::isHTML).count();
			finalProject.numInlineStyles = userUrls.stream().filter(CrawlResult::isHTML).mapToInt(CrawlResult::getNumInlineStyles).sum();
			finalProject.numEmbeddedStyles = userUrls.stream().filter(CrawlResult::isHTML).mapToInt(CrawlResult::getNumEmbeddedStyles).sum();
			
			finalProject.foundFooter = userUrls.stream().filter(CrawlResult::isHTML).filter(CrawlResult::foundFooter).count() > 0;
			finalProject.foundHeader = userUrls.stream().filter(CrawlResult::isHTML).filter(CrawlResult::foundHeader).count() > 0;
			finalProject.foundImg = userUrls.stream().filter(CrawlResult::isHTML).filter(CrawlResult::foundImg).count() > 0;
			finalProject.foundTable = userUrls.stream().filter(CrawlResult::isHTML).filter(CrawlResult::foundTable).count() > 0;
			finalProject.foundNav = userUrls.stream().filter(CrawlResult::isHTML).filter(CrawlResult::foundNav).count() > 0;
			
			List<ModelValidationResult> cssResults = userUrls
				.stream()
				.parallel()
				.filter(CrawlResult::isCSS)
				.map(CrawlResult::getUrl)
				.map(urlStr -> {
					try { return new URL(urlStr); }
					catch (Exception e){
						System.err.format("Invalid CSS URL %s : %s%n", urlStr, e.getMessage());
						return null;  
					}
				})
				.filter(url -> url != null)
				.map(url -> {
					try {
						return cssValidator.validateCSS(url);
					} catch (Exception e){
						System.err.println("Problem encountered when validating url "+url.toString()+" : "+e.getMessage());
						return null;
					}
				})
				.filter(r -> r != null)
				.map(r -> new ModelValidationResult(r))
				.collect(Collectors.toList());
		
			List<ModelValidationResult> htmlResults = userUrls
				.stream()
				.parallel()
				.filter(CrawlResult::isHTML)
				.map(CrawlResult::getUrl)
				.map(urlStr -> {
					try { return new URL(urlStr); }
					catch (Exception e){
						System.err.format("Invalid HTML URL %s : %s%n", urlStr, e.getMessage());
						return null; 
					}
				})
				.filter(url -> url != null)
				.map(url -> {
						try {
							return htmlValidator.validateHTML(url);
						} catch (Exception e){
							System.err.println("Problem encountered when validating url "+url.toString()+" : "+e.getMessage());
							return null;
						}
					})
					.filter(r -> r != null)
				.map(r -> new ModelValidationResult(r))
				.collect(Collectors.toList());
			
			int numCSSValid = (int)cssResults.stream().filter(ModelValidationResult::hasNoErrors).count();
			int numHTMLValid = (int)htmlResults.stream().filter(ModelValidationResult::hasNoErrors).count();
			
			int numTags = userUrls.stream()
							.filter(CrawlResult::isHTML)
							.collect(Collectors.summingInt(CrawlResult::getNumTags));
			int numHTML5Tags = userUrls.stream()
								.filter(CrawlResult::isHTML)
								.collect(Collectors.summingInt(CrawlResult::getNumHTML5Tags));
			
			finalProject.percentHTML5 = numTags == 0 ? 0 : (double)numHTML5Tags/(double)numTags;
			finalProject.percentCSSValid = finalProject.numCSSDocuments == 0 ? 0 : (double)numCSSValid / (double)finalProject.numCSSDocuments;
			finalProject.percentHTMLValid = finalProject.numHTMLPages == 0 ? 0 : (double)numHTMLValid / (double)finalProject.numHTMLPages;
			
			finalProject.results = new ArrayList<ModelValidationResult>();
			finalProject.results.addAll(cssResults);
			finalProject.results.addAll(htmlResults);
			finalProject.print(output);
		});
	}

}

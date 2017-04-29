package com.mike.utils;

public class CrawlResult {

	private final String url;
	private boolean isHTML = false;
	
	private int numTags = 0;
	private int numHTML5Tags = 0;
	private int numInlineStyles = 0;
	private int numEmbeddedStyles = 0;
	
	private boolean foundImg = false;
	private boolean foundNav = false;
	private boolean foundTable = false;
	private boolean foundHeader = false;
	private boolean foundFooter = false;
	
	public CrawlResult(String url){
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}
	public boolean isHTML() {
		return isHTML;
	}
	public boolean isCSS() {
		return !isHTML;
	}
	public void setHTML(boolean isHTML) {
		this.isHTML = isHTML;
	}
	public int getNumTags() {
		return numTags;
	}
	public void setNumTags(int numTags) {
		this.numTags = numTags;
	}
	public int getNumHTML5Tags() {
		return numHTML5Tags;
	}
	public void setNumHTML5Tags(int numHTML5Tags) {
		this.numHTML5Tags = numHTML5Tags;
	}
	public int getNumInlineStyles() {
		return numInlineStyles;
	}
	public void setNumInlineStyles(int numInlineStyles) {
		this.numInlineStyles = numInlineStyles;
	}
	public int getNumEmbeddedStyles() {
		return numEmbeddedStyles;
	}
	public void setNumEmbeddedStyles(int numEmbeddedStyles) {
		this.numEmbeddedStyles = numEmbeddedStyles;
	}
	public boolean foundImg() {
		return foundImg;
	}
	public void setFoundImg(boolean foundImg) {
		this.foundImg = foundImg;
	}
	public boolean foundNav() {
		return foundNav;
	}
	public void setFoundNav(boolean foundNav) {
		this.foundNav = foundNav;
	}
	public boolean foundTable() {
		return foundTable;
	}
	public void setFoundTable(boolean foundTable) {
		this.foundTable = foundTable;
	}
	public boolean foundHeader() {
		return foundHeader;
	}
	public void setFoundHeader(boolean foundHeader) {
		this.foundHeader = foundHeader;
	}
	public boolean foundFooter() {
		return foundFooter;
	}
	public void setFoundFooter(boolean foundFooter) {
		this.foundFooter = foundFooter;
	}
	
	@Override
	public int hashCode(){
		return url.hashCode();
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof CrawlResult){
			return ((CrawlResult)other).url.equals(url);
		}
		return false;
	}
	
}

package com.mike.model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FinalProject {

	public static final List<String> html5tags = Arrays.asList("article", "aside", "bdi", "details", 
		"dialog", "figcaption", "figure", "footer", "header", "main", "mark", 
		"menuitem", "meter", "nav", "progress", "rp", "rt", "ruby", "section", 
		"summary", "time", "wbr");
	
	public String studentName = "";
	
	public int numHTMLPages = 0;
	public int numCSSDocuments = 0;
	public int numInlineStyles = 0;
	public int numEmbeddedStyles = 0;

	public double percentCSSValid = 0;
	public double percentHTMLValid = 0;
	public double percentHTML5 = 0;
	
	public boolean foundImg = false;
	public boolean foundNav = false;
	public boolean foundTable = false;
	public boolean foundHeader = false;
	public boolean foundFooter = false;
	
	public List<ModelValidationResult> results = new ArrayList<ModelValidationResult>();
	
	public void print(PrintStream out){
		out.println(studentName);
		out.printf("\tNum HTML Pages   : %d%n", numHTMLPages);
		out.printf("\tNum CSS Pages    : %d%n", numCSSDocuments);
		out.printf("\tNum Inline CSS   : %d%n", numInlineStyles);
		out.printf("\tNum Embedded CSS : %d%n", numEmbeddedStyles);
		out.printf("\t%% Valid CSS     : %.2f%%%n", percentCSSValid * 100);
		out.printf("\t%% Valid HTML    : %.2f%%%n", percentHTMLValid * 100);
		out.printf("\t%% HTML5 Tags    : %.2f%%%n", percentHTML5 * 100);
		out.printf("\tFound <img>      : %b%n", foundImg);
		out.printf("\tFound <nav>      : %b%n", foundNav);
		out.printf("\tFound <table>    : %b%n", foundTable);
		out.printf("\tFound <header>   : %b%n", foundHeader);
		out.printf("\tFound <footer>   : %b%n", foundFooter);
		out.printf("\tValidated %d urls%n", results.size());
		results.forEach(r -> {
			out.printf("\t\t%s%n", r.resourceName);
			out.printf("\t\t\tHas Errors   : %b%n", r.hasError);
			out.printf("\t\t\tHas Warnings : %b%n", r.hasWarning);
		});
		out.println();
		out.flush();
	}
	
}


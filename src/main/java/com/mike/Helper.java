package com.mike;


import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import org.apache.commons.io.IOUtils;

import com.mike.utils.*;
import com.mike.validators.*;


public class Helper {

	
	public static void main(String[] args) {

		
		OptionParser parser = new OptionParser("fcwdoh");

		OptionSpec<String> filesFolder = parser.accepts("f", "File location of html/css documents to check")
				.availableUnless("w", "d")
				.withRequiredArg()
				.ofType(String.class);
		
		OptionSpec<Integer> chapterNumber = parser.accepts("c", "Specify the chapter number for printing out in the feedback documents")
				.requiredIf("f")
				.withRequiredArg()
				.ofType(Integer.class);
		
		OptionSpec<String> checkWebsites = parser.accepts("w", "Specifies the file location for the index files of each website to crawl, where each line of the txt file is the url of the website")
				.availableUnless("f", "d")
				.withRequiredArg()
				.ofType(String.class);
		
		OptionSpec<String> splitDropboxZip = parser.accepts("d", "File location of downloaded dropbox zip file to split up.")
				.availableUnless("w", "f")
				.withRequiredArg()
				.ofType(String.class);
		
		
		OptionSpec<String> outputFile = parser.accepts("o", "Output file location for logging")
				.withRequiredArg()
				.defaultsTo(System.getProperty("user.dir")+File.separator+"output.log")
				.ofType(String.class);
		
		
		
		OptionSpec<Void> help = parser.accepts("h", "Show the help").forHelp();
			
		OptionSet options = null;
		try {
			options = parser.parse(args);
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
		IsHelperUtil utilToRun = null;
		
		
		if(!options.hasOptions() || options.has(help)){
			
			utilToRun = (output) -> {
				try {
					parser.printHelpOn(System.out);
				} catch (IOException e) {}
			};
			
		} else if(options.has(checkWebsites)){
			
			String urlsFilepath = options.valueOf(checkWebsites);
			if(!Files.exists(Paths.get(urlsFilepath))){
				System.err.println("Path to file for URLs does not exist");
				return;
			}
			List<String> urls;
			try {
				urls = IOUtils.readLines(Files.newInputStream(Paths.get(urlsFilepath), StandardOpenOption.READ), StandardCharsets.UTF_8);
			} catch (IOException e) {
				System.err.println("Unable to read urls file : "+e.getMessage());
				return;
			}
			W3CValidator validator = new W3CValidator();
			utilToRun = new CrawlerUtil(validator, validator, urls);
		} else if(options.has(filesFolder)){
			
			Path filesDir = Paths.get(options.valueOf(filesFolder));
			
			if(!Files.exists(filesDir) || !Files.isDirectory(filesDir)){
				System.err.println("Path to student files does not exist or is not a directory");
				return;
			}
			W3CValidator validator = new W3CValidator();
			utilToRun = new FileValidatorUtil(validator, validator, filesDir.toFile(), options.valueOf(chapterNumber));
		} else if(options.has(splitDropboxZip)) {
			Path zipFile = Paths.get(options.valueOf(splitDropboxZip));
			
			if(!Files.exists(zipFile) || !Files.isRegularFile(zipFile)){
				System.err.println("Dropbox zip file does not exists or is not a file.");
				return;
			}
			
			utilToRun = new DropboxSplitterUtil(zipFile.toFile());
		}
		
		PrintStream ps = null;
		try {
			ps = new PrintStream(new File(options.valueOf(outputFile)));
		} catch (Exception e){
			e.printStackTrace();
			return;
		}
		
		utilToRun.doWork(ps);
			
	}
	

}

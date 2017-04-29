package com.mike.utils;


import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mike.Helper;
import com.mike.model.Assignment;
import com.mike.model.ModelValidationResult;
import com.mike.validators.IsCSSValidator;
import com.mike.validators.IsHTMLValidator;

import freemarker.template.Template;

public class FileValidatorUtil extends ValidationUtility {

	private final File rootFileDir;
	private final int chapter;
	
	public FileValidatorUtil(IsCSSValidator cssValidator, IsHTMLValidator htmlValidator, File string, int chapter) {
		super(cssValidator, htmlValidator);
		this.rootFileDir = string;
		this.chapter = chapter;
	}

	public static double scale(final double valueIn, final double baseMin, final double baseMax, final double limitMin, final double limitMax) {
        return ((limitMax - limitMin) * (valueIn - baseMin) / (baseMax - baseMin)) + limitMin;
    }
	
	@Override
	public void doWork(PrintStream output) {
		
		Function<String, Boolean> copyResource = (filename) -> {
			try {
				Files.copy(Helper.class.getResourceAsStream(filename), Paths.get(rootFileDir.getAbsolutePath(), filename), StandardCopyOption.REPLACE_EXISTING);
				return true;
			} catch (Exception e){
				output.printf("Unable to copy \"%s\" : %s - %s ", filename, e.getClass().getSimpleName(), e.getMessage());
				return false;
				
			} 
		};
		
		if(!copyResource.apply("prism.css"))
			return;
		if(!copyResource.apply("prism.js"))
			return;
		if(!copyResource.apply("feedback.css"))
			return;
		
		final String feedbackFileName = "feedback.html";
		 
		Stream.of(rootFileDir.listFiles(f -> f.isDirectory()))
		.filter(studentDir -> studentDir.listFiles( file -> file.getName().equals(feedbackFileName)).length == 0 )
		.parallel()
		.forEach(studentDir -> {
			System.out.println("checking student files for "+studentDir.getName());
			
			Assignment assignment = new Assignment();
			assignment.chapterNum = chapter;
			assignment.studentName = studentDir.getName();

			assignment.results = Stream.of(studentDir.listFiles())
				.filter(file -> file.getName().endsWith("html") || file.getName().endsWith("htm") || file.getName().endsWith("css"))
				.map(file ->  {
					try {
						return file.getName().endsWith("css") ? cssValidator.validateCSS(file) : htmlValidator.validateHTML(file);
					} catch (Exception e){
						System.err.println("Problem encountered when validating file "+file.getName()+" : "+e.getMessage());
						return null;
					}
				})
				.filter(r -> r != null)
				.map(validationResult -> new ModelValidationResult(validationResult))
				.collect(Collectors.toCollection(ArrayList::new));
			
			long numOK = assignment.results.stream().filter(r -> !r.hasError()).count();
			assignment.validationPoints = Double.valueOf(scale(numOK, 0, assignment.results.size(), 0, 6)).intValue();;
			try {
				Gson gson = new GsonBuilder().create();
				JSONParser jsonParser = new JSONParser();
				
				assignment.results.sort((r1, r2) -> r1.getResourceType().compareTo(r2.getResourceType()));
				Template template = cfg.getTemplate("feedbackTemplate.ftl");
				Writer out = new FileWriter(new File(studentDir.getAbsolutePath()+File.separator+feedbackFileName));
				JSONObject jsoData = (JSONObject) jsonParser.parse(gson.toJson(assignment));
				template.process(jsoData, out);  
			} catch (Exception e) {
				e.printStackTrace();
			}  
			
			output.println();
		});

	}

}

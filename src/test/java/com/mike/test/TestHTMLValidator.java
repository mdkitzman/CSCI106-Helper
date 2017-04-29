package com.mike.test;

import java.io.File;
import java.net.URL;

import org.junit.Test;

import com.mike.model.IsValidationResult;
import com.mike.validators.IsHTMLValidator;
import com.mike.validators.W3CValidator;

import junit.framework.TestCase;

public class TestHTMLValidator extends TestCase{

	public TestHTMLValidator(){
		super("Test HTML Validator");
	}
	
	@Test
	public void testGoodHTMLFile() {
		IsHTMLValidator htmlValidator = new W3CValidator();
		IsValidationResult result = null;
		try {
			result = htmlValidator.validateHTML(new File(TestHTMLValidator.class.getResource("html/good.html").getPath()));
		} catch (Exception e) {
			fail(e.getMessage());
		}
		assertNotNull(result);
		assertFalse(result.hasError());
	}
	
	@Test
	public void testBadHTMLFile() {
		IsHTMLValidator htmlValidator = new W3CValidator();
		IsValidationResult result = null;
		try {
			result = htmlValidator.validateHTML(new File(TestHTMLValidator.class.getResource("html/bad.html").getPath()));
		} catch (Exception e) {
			fail(e.getMessage());
		}
		assertNotNull(result);
		assertTrue(result.hasError());
	}
	
	@Test
	public void testGoodHTMLURL(){
		IsHTMLValidator htmlValidator = new W3CValidator();
		IsValidationResult result = null;
		try {
			result = htmlValidator.validateHTML(new URL("http://org.coloradomesa.edu/~mkitzman/test/good.html"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
		assertNotNull(result);
		assertFalse(result.hasError());
	}
	
	@Test
	public void testBadHTMLURL(){

		IsHTMLValidator htmlValidator = new W3CValidator();
		IsValidationResult result = null;
		try {
			result = htmlValidator.validateHTML(new URL("http://org.coloradomesa.edu/~mkitzman/test/bad.html"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
		assertNotNull(result);
		assertTrue(result.hasError());
	}
	
}

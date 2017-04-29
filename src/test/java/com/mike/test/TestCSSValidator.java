package com.mike.test;

import java.io.File;
import java.net.URL;

import org.junit.Test;

import com.mike.model.IsValidationResult;
import com.mike.validators.IsCSSValidator;
import com.mike.validators.W3CValidator;

import junit.framework.TestCase;

public class TestCSSValidator extends TestCase{
	
	public TestCSSValidator(){
		super("Test CSS Validator");
	}

	@Test
	public void testGoodCSSFile() {
		IsCSSValidator cssValidator = new W3CValidator();
		IsValidationResult result = null;
		try {
			result = cssValidator.validateCSS(new File(TestCSSValidator.class.getResource("css/good.css").getPath()));
		} catch (Exception e) {
			fail(e.getMessage());
		}
		assertNotNull(result);
		assertFalse(result.hasError());
	}
	
	@Test
	public void testBadCSSFile() {
		IsCSSValidator cssValidator = new W3CValidator();
		IsValidationResult result = null;
		try {
			result = cssValidator.validateCSS(new File(TestCSSValidator.class.getResource("css/bad.css").getPath()));
		} catch (Exception e) {
			fail(e.getMessage());
		}
		assertNotNull(result);
		assertTrue(result.hasError());
	}
	
	@Test
	public void testGoodCSSUrl() {
		IsCSSValidator cssValidator = new W3CValidator();
		IsValidationResult result = null;
		try {
			result = cssValidator.validateCSS(new URL("http://org.coloradomesa.edu/~mkitzman/test/good.css"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
		assertNotNull(result);
		assertFalse(result.hasError());
	}
	
	@Test
	public void testBadCSSURL() {
		IsCSSValidator cssValidator = new W3CValidator();
		IsValidationResult result = null;
		try {
			result = cssValidator.validateCSS(new URL("http://org.coloradomesa.edu/~mkitzman/test/bad.css"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
		assertNotNull(result);
		assertTrue(result.hasError());
	}

}

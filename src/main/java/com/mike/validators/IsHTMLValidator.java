package com.mike.validators;

import java.io.File;
import java.net.URL;

import com.mike.model.IsValidationResult;

public interface IsHTMLValidator {

	public IsValidationResult validateHTML(File file) throws Exception;
	public IsValidationResult validateHTML(URL url) throws Exception;
	
}

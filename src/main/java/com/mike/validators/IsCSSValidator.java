package com.mike.validators;

import java.io.File;
import java.net.URL;

import com.mike.model.IsValidationResult;

public interface IsCSSValidator {

	public IsValidationResult validateCSS(File file) throws Exception;
	public IsValidationResult validateCSS(URL url) throws Exception;
	
}

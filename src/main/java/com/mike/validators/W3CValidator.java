package com.mike.validators;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.commons.io.IOUtils;
import com.jcabi.w3c.ValidationResponse;
import com.jcabi.w3c.Validator;
import com.jcabi.w3c.ValidatorBuilder;
import com.mike.model.IsValidationResult;
import com.mike.model.IsValidationResult.Type;

public class W3CValidator implements IsHTMLValidator, IsCSSValidator {
	
	private static class ValidationResult implements IsValidationResult {

		Type type;
		ValidationResponse response;
		String name;
		
		public ValidationResult(ValidationResponse response, Type type, String name){
			this.type = type;
			this.response = response;
			this.name = name;
		}
		@Override
		public boolean hasError() { return response.errors() != null && response.errors().size() > 0; }
		@Override
		public boolean hasWarning() { return response.warnings() != null && response.warnings().size() > 0; }
		@Override
		public String getResourceName() { return name; }
		@Override
		public Type getResourceType() { return type; }
		
		
	}

	private static final Validator cssValidator = new ValidatorBuilder().css();
	private static final Validator htmlValidator = new ValidatorBuilder().html();
	
	@Override
	public IsValidationResult validateHTML(File file)  throws Exception{
		byte[] data = Files.readAllBytes(file.toPath());
		final ValidationResponse response = htmlValidator.validate(new String(data));
		final String filename = file.getName();
		return new ValidationResult(response, Type.HTML, filename);
	}

	@Override
	public IsValidationResult validateHTML(URL url) throws Exception {
		String data = IOUtils.toString(url.openConnection().getInputStream(), StandardCharsets.UTF_8);
		final ValidationResponse response = htmlValidator.validate(data);
		final String strUrl = url.toString();
		return new ValidationResult(response, Type.HTML, strUrl);
	}

	@Override
	public IsValidationResult validateCSS(File file) throws Exception{
		byte[] data = Files.readAllBytes(file.toPath());
		final ValidationResponse response = cssValidator.validate(new String(data));
		final String filename = file.getName();
		return new ValidationResult(response, Type.CSS, filename);
	}

	@Override
	public IsValidationResult validateCSS(URL url) throws Exception{
		String data = IOUtils.toString(url.openConnection().getInputStream(), StandardCharsets.UTF_8);
		final ValidationResponse response = cssValidator.validate(data);
		final String strUrl = url.toString();
		return new ValidationResult(response, Type.CSS, strUrl);
	}

}

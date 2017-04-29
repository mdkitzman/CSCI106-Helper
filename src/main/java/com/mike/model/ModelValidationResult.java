package com.mike.model;

public class ModelValidationResult implements IsValidationResult {

	public boolean hasError;
	public boolean hasWarning;
	public String resourceName;
	public Type	resourceType;
	
	public ModelValidationResult(IsValidationResult otherResult){
		copy(otherResult);
	}
	
	public void copy(IsValidationResult otherResult){
		hasError = otherResult.hasError();
		hasWarning = otherResult.hasWarning();
		resourceName = otherResult.getResourceName();
		resourceType = otherResult.getResourceType();
	}
	
	public boolean hasNoErrors() {
		return !hasError;
	}
	
	@Override
	public boolean hasError() {
		return hasError;
	}

	@Override
	public boolean hasWarning() {
		return hasWarning;
	}

	@Override
	public String getResourceName() {
		return resourceName;
	}

	@Override
	public Type getResourceType() {
		return resourceType;
	}

}

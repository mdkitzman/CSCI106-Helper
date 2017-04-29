package com.mike.model;

public interface IsValidationResult {
	
	public static enum Type { HTML, CSS }
	
	public boolean hasError();
	
	public boolean hasWarning();
	
	public String getResourceName();
	
	public Type getResourceType(); 
}

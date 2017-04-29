package com.mike.utils;

import com.mike.Helper;
import com.mike.validators.IsCSSValidator;
import com.mike.validators.IsHTMLValidator;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.TemplateExceptionHandler;

abstract class ValidationUtility implements IsHelperUtil{
	
	protected final Configuration cfg = new Configuration(Configuration.VERSION_2_3_0);
	protected final IsHTMLValidator htmlValidator;
	protected final IsCSSValidator  cssValidator;
	
	protected ValidationUtility(IsCSSValidator cssValidator, IsHTMLValidator htmlValidator){
		this.htmlValidator = htmlValidator;
		this.cssValidator = cssValidator;
		
		cfg.setClassForTemplateLoading(Helper.class, "");
		cfg.setObjectWrapper(new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_0).build());
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
		
	}
	
}

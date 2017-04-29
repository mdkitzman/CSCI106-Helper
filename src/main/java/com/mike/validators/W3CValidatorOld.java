package com.mike.validators;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.mike.model.IsValidationResult;
import com.mike.model.IsValidationResult.Type;

public class W3CValidatorOld implements IsHTMLValidator, IsCSSValidator {

	public static class ValidationResult implements IsValidationResult {

		private final String	m_resourceName;
		private boolean m_hasErrors		= false;
		private boolean m_hasWarnings	= false;
		private final Type m_type;
		
		public ValidationResult(String resourceName, Type type){
			m_resourceName = resourceName;
			m_type = type;
		}
		
		@Override
		public boolean hasError(){
			return m_hasErrors;
		}
		
		@Override
		public boolean hasWarning(){
			return m_hasWarnings;
		}
		
		@Override
		public String getResourceName(){
			return m_resourceName;
		}

		@Override
		public Type getResourceType() {
			return m_type;
		}		
	}
	
	protected static final int	s_maxReadBufferSize		= 5120;

	
	protected byte[] getInputAsBytes(BufferedInputStream inputStream) throws IOException{
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int len;
		byte[] data = new byte[s_maxReadBufferSize];
		while ((len = inputStream.read(data, 0, data.length)) != -1) 
			buffer.write(data, 0, len);
		inputStream.close();
		buffer.flush();
		return buffer.toByteArray();
	}
	
	@Override
	public IsValidationResult validateHTML(File file)  throws Exception{
		ValidationResult result = new ValidationResult(file.getName(), Type.HTML);
		
		try {
			ClientHttpRequest request = new ClientHttpRequest("http://validator.w3.org/check");
			request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			request.setHeader("Accept-Encoding", "gzip, deflate");
			request.setHeader("Accept-Language", "en-US,en;q=0.5");
			request.setHeader("Connection", "keep-alive");
			request.setHeader("Host", "validator.w3.org");
			request.setHeader("Referer", "http://validator.w3.org/");
			request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0");
			request.setParameter("uploaded_file", file);
			request.setParameter("doctype", "Inline");
			request.setParameter("charset", "(detect automatically)");
			request.setParameter("group", 0);
			InputStream response = request.post();
			String responseStr = new String(getInputAsBytes(new BufferedInputStream(response)));
			Document doc = Jsoup.parse(responseStr);
			doc.select("#results_container h2").stream().findFirst().ifPresent(element -> {
				String styleClass = element.attr("class");
				result.m_hasErrors = !"valid".equals(styleClass);
			});
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServerResponseException e) {
			System.err.println(e.getMessage() + " : "+e.getStatusCode());
			
		}
		
		return result;
	}

	@Override
	public IsValidationResult validateHTML(URL url) throws Exception {
		ValidationResult result = new ValidationResult(url.toString(), Type.HTML);
		
		final String checkURL = "http://validator.w3.org/check?uri=%s&charset=%%28detect+automatically%%29&doctype=Inline&group=0";
		HttpResponse response = Request.Get(String.format(checkURL, URLEncoder.encode(url.toString(), "UTF-8")))
						        .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
					            .addHeader("Accept-Encoding", "gzip, deflate")
					            .addHeader("Accept-Language", "en-US,en;q=0.5")
					            .addHeader("Connection", "keep-alive")
					            .addHeader("Host", "validator.w3.org")
					            .addHeader("Referer", "http://validator.w3.org/")
					            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0")
						        .execute().returnResponse();	
		
		if(HttpStatus.SC_OK != response.getStatusLine().getStatusCode())
        	throw new Exception(response.getStatusLine().getReasonPhrase());

		String encoding = response.getFirstHeader("Content Encoding") == null ? "UTF-8" : response.getFirstHeader("Content Encoding").getValue();
		
		Document doc = Jsoup.parse(response.getEntity().getContent(), encoding, "");
		doc.select("#results li.error").stream().findFirst().ifPresent(element -> {
			result.m_hasErrors = true;
		});
			
		return result;
	}

	@Override
	public IsValidationResult validateCSS(File file) throws Exception{
		ValidationResult result = new ValidationResult(file.getName(), Type.CSS);
		
		ClientHttpRequest request = new ClientHttpRequest("http://jigsaw.w3.org/css-validator/validator");
		request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		request.setHeader("Accept-Encoding", "gzip, deflate");
		request.setHeader("Accept-Language", "en-US,en;q=0.5");
		request.setHeader("Connection", "keep-alive");
		request.setHeader("Host", "jigsaw.w3.org");
		request.setHeader("Referer", "http://jigsaw.w3.org/css-validator/");
		request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0");
		
		request.setParameter("file", file);
		request.setParameter("profile", "css3");
		request.setParameter("usermedium", "all");
		request.setParameter("warning", "1");
		request.setParameter("vextwarning", "");
		InputStream response = request.post();
		String responseStr = new String(getInputAsBytes(new BufferedInputStream(response)));
		Document doc = Jsoup.parse(responseStr);
		result.m_hasErrors = doc.select("#results_container #errors").stream().count() > 0;
		
		return result;
	}

	@Override
	public IsValidationResult validateCSS(URL url) throws Exception{
		
		ValidationResult result = new ValidationResult(url.toString(), Type.CSS);
		
		final String checkURL = "http://jigsaw.w3.org/css-validator/validator?uri=%s&profile=css3&usermedium=all&warning=1&vextwarning=&lang=en";
		HttpResponse response = Request.Get(String.format(checkURL, URLEncoder.encode(url.toString(), "UTF-8")))
		        .setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
				.setHeader("Accept-Encoding", "gzip, deflate")
				.setHeader("Accept-Language", "en-US,en;q=0.5")
				.setHeader("Connection", "keep-alive")
				.setHeader("Host", "jigsaw.w3.org")
				.setHeader("Referer", "http://jigsaw.w3.org/css-validator/")
				.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0")
		        .execute().returnResponse();
		
		if(HttpStatus.SC_OK != response.getStatusLine().getStatusCode())
        	throw new Exception(response.getStatusLine().getReasonPhrase());

		String encoding = response.getFirstHeader("Content Encoding") == null ? "UTF-8" : response.getFirstHeader("Content Encoding").getValue();
		
		Document doc = Jsoup.parse(response.getEntity().getContent(), encoding, "");
		result.m_hasErrors = doc.select("#results_container #errors").stream().count() > 0;
			
		return result;
	}

}

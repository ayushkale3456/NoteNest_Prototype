package com.dimsum.notenest20;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
	    configurer
	        .favorParameter(false)
	        .ignoreAcceptHeader(true)
	        .defaultContentType(MediaType.MULTIPART_FORM_DATA); // allow multipart by default
	}

}

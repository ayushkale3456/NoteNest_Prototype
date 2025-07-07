package com.dimsum.notenest20;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// No @PostConstruct for deployment (assuming you confirmed it loads)
// import jakarta.annotation.PostConstruct;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	/*
	 * // Remove @PostConstruct after confirming it loads on Render.com
	 * 
	 * @PostConstruct public void init() {
	 * System.out.println("====== WebConfig is being initialized. ======"); }
	 */

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.favorParameter(false) // Don't use URL parameters (e.g., ?format=json) for content negotiation
				// .ignoreAcceptHeader(true) // DO NOT ignore Accept headers for a REST API
				.defaultContentType(MediaType.APPLICATION_JSON) // Default to JSON if Accept header is not specified
				.mediaType("json", MediaType.APPLICATION_JSON) // Optional: allow .json extension for content type
				.mediaType("xml", MediaType.APPLICATION_XML); // Optional: allow .xml extension for content type
	}

}
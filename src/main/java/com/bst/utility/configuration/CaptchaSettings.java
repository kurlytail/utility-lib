package com.bst.utility.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "google.recaptcha")
public class CaptchaSettings {

	private String key;
	private String secret;
	private String url;

	public String getKey() {
		return this.key;
	}

	public String getSecret() {
		return this.secret;
	}

	public String getUrl() {
		return this.url;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	public void setSecret(final String secret) {
		this.secret = secret;
	}

	public void setUrl(final String url) {
		this.url = url;
	}
}

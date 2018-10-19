package com.bst.utility.services;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;

import com.bst.utility.configuration.CaptchaSettings;
import com.bst.utility.dto.ReCaptchaResponse;

@Service
public class ReCaptchaService {

	private static final Logger log = LoggerFactory.getLogger(ReCaptchaService.class);

	@Autowired
	private CaptchaSettings captchaSettings;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private RestOperations restTemplate;

	public boolean validate(final String reCaptchaResponse) {
		final URI verifyUri = URI
				.create(String.format(this.captchaSettings.getUrl() + "?secret=%s&response=%s&remoteip=%s",
						this.captchaSettings.getSecret(), reCaptchaResponse, this.request.getRemoteAddr()));

		try {
			final ReCaptchaResponse response = this.restTemplate.getForObject(verifyUri, ReCaptchaResponse.class);
			return response.isSuccess();
		} catch (final Exception ignored) {
			ReCaptchaService.log.error("", ignored);
			// ignore when google services are not available
			// maybe add some sort of logging or trigger that'll alert the administrator
		}

		return true;
	}

}
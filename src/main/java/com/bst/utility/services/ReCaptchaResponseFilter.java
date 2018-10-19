package com.bst.utility.services;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

@Service
public class ReCaptchaResponseFilter implements Filter {

	private static class ReCaptchaHttpServletRequest extends HttpServletRequestWrapper {

		final Map<String, String[]> params;

		ReCaptchaHttpServletRequest(final HttpServletRequest request) {
			super(request);
			this.params = new HashMap<>(request.getParameterMap());
			this.params.put(ReCaptchaResponseFilter.RE_CAPTCHA_ALIAS,
					request.getParameterValues(ReCaptchaResponseFilter.RE_CAPTCHA_RESPONSE));
		}

		@Override
		public String getParameter(final String name) {
			return this.params.containsKey(name) ? this.params.get(name)[0] : null;
		}

		@Override
		public Map<String, String[]> getParameterMap() {
			return this.params;
		}

		@Override
		public Enumeration<String> getParameterNames() {
			return Collections.enumeration(this.params.keySet());
		}

		@Override
		public String[] getParameterValues(final String name) {
			return this.params.get(name);
		}
	}

	private static final String RE_CAPTCHA_ALIAS = "reCaptchaResponse";

	private static final String RE_CAPTCHA_RESPONSE = "g-recaptcha-response";

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
			final FilterChain chain) throws IOException, ServletException {

		final HttpServletRequest request = (HttpServletRequest) servletRequest;
		final HttpServletResponse response = (HttpServletResponse) servletResponse;

		if (request.getParameter(ReCaptchaResponseFilter.RE_CAPTCHA_RESPONSE) != null) {
			final ReCaptchaHttpServletRequest reCaptchaRequest = new ReCaptchaHttpServletRequest(request);
			chain.doFilter(reCaptchaRequest, response);
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
	}
}

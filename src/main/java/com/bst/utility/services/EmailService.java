package com.bst.utility.services;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Service
public class EmailService implements ApplicationListener<ServletWebServerInitializedEvent> {

	@Autowired
	private MailSender mailSender;

	private String prefix;

	@Autowired
	private ServletContext servletContext;

	@Autowired
	private SpringTemplateEngine templateEngine;

	private void checkPrefix() throws MalformedURLException {
		final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes instanceof ServletRequestAttributes) {
			final HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			int port = request.getServerPort();

			if (request.getScheme().equals("http") && (port == 80)) {
				port = -1;
			} else if (request.getScheme().equals("https") && (port == 443)) {
				port = -1;
			}

			final URL serverURL = new URL(request.getScheme(), request.getServerName(), port,
					this.servletContext.getContextPath());
			this.prefix = serverURL.toString();
		}
	}

	public MailSender getMailSender() {
		return this.mailSender;
	}

	@Override
	public void onApplicationEvent(final ServletWebServerInitializedEvent event) {
		final int port = event.getWebServer().getPort();
		try {
			final String serverName = InetAddress.getLocalHost().getHostName();
			this.prefix = "http://" + serverName;
			if (port == 443) {
				this.prefix = "https://" + serverName;
			} else if ((port != 22) && (port != 0)) {
				this.prefix = this.prefix + ":" + port;
			}
			this.prefix = this.prefix + this.servletContext.getContextPath();
		} catch (final UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(final String[] recepients, final String template, final String from, final String subject,
			final String dtoName, final Object dto, final Locale locale)
			throws MessagingException, MalformedURLException {

		this.checkPrefix();

		final JavaMailSender javaMailSender = (JavaMailSender) this.getMailSender();
		final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");

		final Context context = new Context(locale);
		context.setVariable(dtoName, dto);
		context.setVariable("DomainURL", this.prefix);

		final String htmlContent = this.templateEngine.process(template, context);

		helper.setText(htmlContent, true);
		helper.setSubject(subject);
		helper.setFrom(from);
		helper.setTo(recepients);

		javaMailSender.send(mimeMessage);
	}

	public void setMailSender(final MailSender mailSender) {
		this.mailSender = mailSender;
	}
}

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

	@Autowired
	private SpringTemplateEngine templateEngine;
	
	@Autowired
	private ServletContext servletContext;
	
	private String prefix;

	public MailSender getMailSender() {
		return mailSender;
	}

	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void sendMessage(final String[] recepients, final String template, final String from, final String subject,
			final String dtoName, final Object dto, final Locale locale) throws MessagingException, MalformedURLException {
		
		checkPrefix();

		final JavaMailSender javaMailSender = (JavaMailSender) getMailSender();
		final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");

		final Context context = new Context(locale);
		context.setVariable(dtoName, dto);
		context.setVariable("DomainURL", prefix);

		final String htmlContent = this.templateEngine.process(template, context);

		helper.setText(htmlContent, true);
		helper.setSubject(subject);
		helper.setFrom(from);
		helper.setTo(recepients);

		javaMailSender.send(mimeMessage);
	}

	private void checkPrefix() throws MalformedURLException {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
	    if (requestAttributes instanceof ServletRequestAttributes) {
	    	HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
	    	int port = request.getServerPort();

	    	if (request.getScheme().equals("http") && port == 80) {
	    	    port = -1;
	    	} else if (request.getScheme().equals("https") && port == 443) {
	    	    port = -1;
	    	}

	    	URL serverURL = new URL(request.getScheme(), request.getServerName(), port, servletContext.getContextPath());
	    	this.prefix = serverURL.toString();
	    }
	}

	@Override
	public void onApplicationEvent(ServletWebServerInitializedEvent event) {
		int port = event.getWebServer().getPort();
		try {
			String serverName = InetAddress.getLocalHost().getHostName();
			prefix = "http://" + serverName;
			if (port == 443) {
				prefix = "https://" + serverName;
			} else if (port != 22 && port != 0) {
				prefix = prefix + ":" + port;
			}
			prefix = prefix + servletContext.getContextPath();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}

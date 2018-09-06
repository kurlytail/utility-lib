package com.bst.utility.components;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
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

	private String serverName;
	private int port;

	public MailSender getMailSender() {
		return mailSender;
	}

	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void sendMessage(final String[] recepients, final String template, final String from, final String subject,
			final String dtoName, final Object dto, final Locale locale) throws MessagingException {

		final JavaMailSender javaMailSender = (JavaMailSender) getMailSender();
		final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");

		final Context context = new Context(locale);
		context.setVariable(dtoName, dto);
		
		String prefix = "http://" + serverName;
		if (this.port == 443) {
			prefix = "https://" + serverName;
		} else if (this.port != 22) {
			prefix = prefix + ":" + this.port;
		}
		prefix = prefix + servletContext.getContextPath();
		
		context.setVariable("DomainURL", prefix);

		final String htmlContent = this.templateEngine.process(template, context);

		helper.setText(htmlContent, true);
		helper.setSubject(subject);
		helper.setFrom(from);
		helper.setTo(recepients);

		javaMailSender.send(mimeMessage);
	}

	@Override
	public void onApplicationEvent(ServletWebServerInitializedEvent event) {
		this.port = event.getWebServer().getPort();
		try {
			this.serverName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}

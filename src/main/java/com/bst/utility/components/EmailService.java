package com.bst.utility.components;

import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Service
public class EmailService {

	@Autowired
	private MailSender mailSender;
	
	@Autowired
	private SpringTemplateEngine templateEngine;

	public MailSender getMailSender() {
		return mailSender;
	}

	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void sendMessage(final String[] recepients, final String template, 
			final String from, final String subject,
			final Object dto, final Locale locale)
			throws MessagingException {

		final Context ctx = new Context(locale);
		ctx.setVariable("dto", dto);
		
		final JavaMailSender javaMailSender = (JavaMailSender)getMailSender();
		final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
	    final String htmlContent = this.templateEngine.process(template, ctx);
	    
		helper.setText(htmlContent, true);
		helper.setSubject(subject);
		helper.setFrom(from);
		helper.setTo(recepients);
		
		javaMailSender.send(mimeMessage);
	}
}

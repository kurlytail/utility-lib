package com.bst.utility.components;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Service
public class EmailService {

	@Autowired
	private MailSender mailSender;
	
	@Autowired
	private SpringTemplateEngine templateEngine;
	
	@Autowired
	private ServletContext servletContext; 

	public MailSender getMailSender() {
		return mailSender;
	}

	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void sendMessage(final String[] recepients, final String template, 
			final String from, final String subject, final String dtoName,
			final Object dto, final Locale locale)
			throws MessagingException {


		IContext context;
		
		final JavaMailSender javaMailSender = (JavaMailSender)getMailSender();
		final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
		
	    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
	    if (requestAttributes instanceof ServletRequestAttributes) {
	        HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
	        WebContext webContext = new WebContext(request, new MockHttpServletResponse(), servletContext);
			webContext.setVariable(dtoName, dto);
	        context = webContext;
	    } else {
			final Context ctx = new Context(locale);
			ctx.setVariable(dtoName, dto);
			context = ctx;
	    }
		
	    final String htmlContent = this.templateEngine.process(template, context);
	    
		helper.setText(htmlContent, true);
		helper.setSubject(subject);
		helper.setFrom(from);
		helper.setTo(recepients);
		
		javaMailSender.send(mimeMessage);
	}
}

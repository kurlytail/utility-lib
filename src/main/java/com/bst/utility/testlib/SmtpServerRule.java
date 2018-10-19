package com.bst.utility.testlib;

import javax.mail.internet.MimeMessage;

import org.junit.rules.ExternalResource;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

public class SmtpServerRule extends ExternalResource {

	private final int port;
	private GreenMail smtpServer;

	public SmtpServerRule(final int port) {
		this.port = port;
	}

	@Override
	protected void after() {
		super.after();
		this.smtpServer.stop();
	}

	@Override
	protected void before() throws Throwable {
		super.before();
		this.smtpServer = new GreenMail(new ServerSetup(this.port, null, "smtp"));
		this.smtpServer.start();
	}

	public MimeMessage[] getMessages() {
		return this.smtpServer.getReceivedMessages();
	}
}
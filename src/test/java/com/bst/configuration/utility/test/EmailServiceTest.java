package com.bst.configuration.utility.test;

import static io.github.jsonSnapshot.SnapshotMatcher.expect;
import static io.github.jsonSnapshot.SnapshotMatcher.start;
import static io.github.jsonSnapshot.SnapshotMatcher.validateSnapshots;

import java.io.IOException;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bst.configuration.utility.UtilityConfiguration;
import com.bst.utility.components.EmailService;
import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = UtilityConfiguration.class)
@EnableAutoConfiguration
public class EmailServiceTest {
	
	@Autowired
	private EmailService emailService;

	@Rule
	public GreenMailRule smtpServerRule = new GreenMailRule(ServerSetupTest.ALL);

	@After
	public void cleanup() {
		this.smtpServerRule.stop();
	}

	@BeforeClass
	public static void startSnapshot() {
		start();
	}

	@AfterClass
	public static void stopSnapshot() {
		validateSnapshots();
	}

	@Test
	public void sendAndReceiveEmailTest() throws IOException, MessagingException {
		
		emailService.sendMessage(new String[] { "some@email" }, "test-email.html", "my@email", "MySubject", "dto", new Object() {
			public String testField1 = "test1";
			public String testField2 = "test2";
		}, Locale.ENGLISH);

		MimeMessage[] messages = smtpServerRule.getReceivedMessages();

		assert (messages.length == 1);

		for (MimeMessage msg : messages) {
			expect(msg.getContent(), msg.getAllHeaders(), msg.getAllRecipients(), msg.getFrom()).toMatchSnapshot();
		}
	}
}

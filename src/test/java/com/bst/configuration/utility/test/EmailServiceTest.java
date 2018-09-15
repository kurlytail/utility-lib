package com.bst.configuration.utility.test;

import static com.bst.utility.testlib.SnapshotListener.expect;

import java.util.Locale;

import javax.mail.internet.MimeMessage;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bst.configuration.utility.UtilityConfiguration;
import com.bst.utility.components.EmailService;
import com.bst.utility.testlib.SnapshotListener;
import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EmailService.class)
@EnableAutoConfiguration
@TestExecutionListeners(listeners = SnapshotListener.class, mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
public class EmailServiceTest {

	@Autowired
	private EmailService emailService;

	@Rule
	public GreenMailRule smtpServerRule = new GreenMailRule(ServerSetupTest.ALL);

	@After
	public void cleanup() {
		this.smtpServerRule.stop();
	}

	@Test
	public void sendAndReceiveEmailTest() throws Exception {

		emailService.sendMessage(new String[] { "some@email" }, "test-email.html", "my@email", "MySubject", "dto",
				new Object() {
					@SuppressWarnings("unused")
					public String testField1 = "test1";
					@SuppressWarnings("unused")
					public String testField2 = "test2";
				}, Locale.ENGLISH);

		MimeMessage[] messages = smtpServerRule.getReceivedMessages();

		assert (messages.length == 1);

		for (MimeMessage msg : messages) {
			expect(msg.getContent()).toMatchSnapshot();
			expect(msg.getAllRecipients()).toMatchSnapshot();
			expect(msg.getFrom()).toMatchSnapshot();
		}
	}
}

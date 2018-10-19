package com.bst.configuration.utility.test;

import java.util.Locale;

import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bst.utility.services.EmailService;
import com.bst.utility.testlib.SnapshotListener;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(classes = EmailService.class)
@EnableAutoConfiguration
@TestExecutionListeners(listeners = SnapshotListener.class, mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
@SpringBootTest
@TestPropertySource("classpath:application.properties")
public class EmailServiceTest {

	@Autowired
	private EmailService emailService;

	@Test
	public void sendAndReceiveEmailTest() throws Exception {

		final GreenMail greenMail = new GreenMail(ServerSetupTest.ALL);
		greenMail.start();

		this.emailService.sendMessage(new String[] { "some@email" }, "test-email.html", "my@email", "MySubject", "dto",
				new Object() {
					@SuppressWarnings("unused")
					public String testField1 = "test1";
					@SuppressWarnings("unused")
					public String testField2 = "test2";
				}, Locale.ENGLISH);

		final MimeMessage[] messages = greenMail.getReceivedMessages();

		assert (messages.length == 1);

		for (final MimeMessage msg : messages) {
			SnapshotListener.expect(msg.getContent()).toMatchSnapshot();
			SnapshotListener.expect(msg.getAllRecipients()).toMatchSnapshot();
			SnapshotListener.expect(msg.getFrom()).toMatchSnapshot();
		}

		greenMail.stop();
	}
}

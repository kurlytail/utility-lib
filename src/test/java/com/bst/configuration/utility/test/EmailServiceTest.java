package com.bst.configuration.utility.test;

import static com.bst.utility.testlib.SnapshotListener.expect;

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

import com.bst.utility.components.EmailService;
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
		
		emailService.sendMessage(new String[] { "some@email" }, "test-email.html", "my@email", "MySubject", "dto",
				new Object() {
					@SuppressWarnings("unused")
					public String testField1 = "test1";
					@SuppressWarnings("unused")
					public String testField2 = "test2";
				}, Locale.ENGLISH);

		MimeMessage[] messages = greenMail.getReceivedMessages();

		assert (messages.length == 1);

		for (MimeMessage msg : messages) {
			expect(msg.getContent()).toMatchSnapshot();
			expect(msg.getAllRecipients()).toMatchSnapshot();
			expect(msg.getFrom()).toMatchSnapshot();
		}
		
		greenMail.stop();
	}
}

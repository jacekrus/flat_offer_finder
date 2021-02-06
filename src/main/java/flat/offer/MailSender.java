package flat.offer;

import java.util.Collection;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

public class MailSender {

	private static final Logger LOG = Logger.getLogger(MailSender.class);
	private static final String USER_NAME = "dater.noreply";
	private static final String PASSWORD = System.getenv("APP_ENCRYPTION_PASSWORD");
	private static final String RECIPIENT = System.getenv("MY_EMAIL");
	private static final String HOST = "smtp.gmail.com";

	private final Properties props;

	public MailSender() {
		props = System.getProperties();
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", HOST);
		props.put("mail.smtp.user", USER_NAME);
		props.put("mail.smtp.password", PASSWORD);
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", true);
	}

	public void sendEmail(Collection<Offer> offers) throws OfferFinderException {
		Session session = Session.getDefaultInstance(props);
		MimeMessage message = new MimeMessage(session);
		Transport transport = null;
		try {
			message.setFrom(new InternetAddress(USER_NAME));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(RECIPIENT));
			message.setSubject("Nowe mieszkania dostepne");
			message.setText(createMessageBody(offers));
			transport = session.getTransport("smtp");
			transport.connect(HOST, USER_NAME, PASSWORD);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (MessagingException e) {
			throw new OfferFinderException("Exception occured while sending email", e);
		}
		finally {
			if(transport != null) {
				try {
					transport.close();
				}
				catch(MessagingException e) {
					LOG.warn("Exception occured while closing transport. ", e);
				}
			}
		}
	}

	private String createMessageBody(Collection<Offer> offers) {
		StringBuilder bld = new StringBuilder();
		bld.append("Nowe oferty: \n");
		for (Offer offer : offers) {
			bld.append(offer.getId());
			bld.append(" \n");
			bld.append(offer.getLink());
			bld.append(" \n\n");
		}
		return bld.toString();
	}

}

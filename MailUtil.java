package learn;

//参考
//java_tutorial.pdf - Simply Easy Learning by tutorialspoint.com
//http://redleaf.iteye.com/blog/78217
//http://www.cnblogs.com/yejg1212/archive/2013/06/01/3112702.html

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class MailUtil {
	private String to;
	private String from;
	private String host;
	private String username;
	private String password;
	private String subject = "";
	private String content = "";
	private String filename;

	public MailUtil() {

	}

	public MailUtil(String from, String host, String username, String password) {
		this.from = from;
		this.host = host;
		this.username = username;
		this.password = password;
	}
	
	public MailUtil(String to, String from, String host, String username, String password, String subject,
			String content) {
		this.to = to;
		this.from = from;
		this.host = host;
		this.username = username;
		this.password = password;
		this.subject = subject;
		this.content = content;
	}
	
	public MailUtil(String to, String from, String host, String username, String password, String subject,
			String content, String filename) {
		this.to = to;
		this.from = from;
		this.host = host;
		this.username = username;
		this.password = password;
		this.subject = subject;
		this.content = content;
		this.filename = filename;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void sendMail() {
		// Get system properties
		Properties properties = System.getProperties();
		// Setup mail server
		properties.setProperty("mail.smtp.host", host);
		// Get the default Session object.
		Session session = Session.getDefaultInstance(properties);

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);
			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));
			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			// Set Subject: header field
			message.setSubject(subject);

			// Create a multipar message
			Multipart multipart = new MimeMultipart();

			// Create the message part
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(content, "text/html;charset=UTF-8");
			multipart.addBodyPart(messageBodyPart);

			// Part two is attachment
			messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(filename);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(MimeUtility.encodeText(filename));	//解决附件名为中文时接收乱码
			multipart.addBodyPart(messageBodyPart);

			// Send the complete message parts
			message.setContent(multipart);
			// Send message
			Transport transport = session.getTransport("smtp");
			transport.connect(host, username, password);
			transport.sendMessage(message, message.getAllRecipients());

			System.out.println("Sent " + filename + " successfully.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

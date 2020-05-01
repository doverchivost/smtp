import java.io.*;
import org.apache.commons.io.*;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

public class Main {

    public static String target = "";
    public static String subject = "";
    public static String attachments = "";
    public static String user_name = "s.t4sk@yandex.com";
    public static String password = "SMTPtask20";

    public static String host_addr = "smtp.yandex.ru";
    public static String port = "465";

    public static void main(String[] args) throws Exception {
        read_config();
        Properties props = new Properties();
        props.put("mail.smtp.host", host_addr); //SMTP Host
        props.put("mail.smtp.socketFactory.port", port); //SSL Port
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); //SSL Factory Class
        props.put("mail.smtp.auth", "true"); //Enabling SMTP Authentication
        props.put("mail.smtp.port", port); //SMTP Port

        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user_name, password);
            }
        };

        Session session = Session.getDefaultInstance(props, auth);

        try{
            MimeMessage msg = new MimeMessage(session);
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(user_name);
            msg.setSubject(subject, "UTF-8");
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(target, false));

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(read_msg());

            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            if (attachments.length() > 0) {
                for (String atch : attachments.split(", ")) {
                    messageBodyPart = new MimeBodyPart();
                    String filename = "Files\\" + atch;
                    DataSource source = new FileDataSource(filename);
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(filename);
                    multipart.addBodyPart(messageBodyPart);

                }
            }

            msg.setContent(multipart);

            Transport.send(msg);
            System.out.println("Success!");
        }catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public static void read_config() {
        File file = new File("Files\\config.txt");
        String[] config = new String[3];
        try {
            config = FileUtils.readFileToString(file).split("\n");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        target = config[0].substring(12);
        subject = config[1].substring(9);
        attachments = config[2].substring(13);
    }

    public static String read_msg() {
        File file = new File("Files\\msg.txt");
        String msg = "";
        try {
            msg = FileUtils.readFileToString(file);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return msg;
    }


}

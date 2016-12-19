import java.util.Properties;
import javax.mail.Session;

public class SendMail {

    public static void main() {

        System.out.println("SimpleEmail Start");

        String smtpHostServer = "mail.mentor.com";
        String emailID = "nick_demarco@mentor.com";

        Properties props = System.getProperties();

        props.put("mail.smtp.host", smtpHostServer);

        Session session = Session.getInstance(props, null);

        EmailUtil.sendEmail(session, emailID,"SimpleEmail Testing Subject", "SimpleEmail Testing Body");
    }

}
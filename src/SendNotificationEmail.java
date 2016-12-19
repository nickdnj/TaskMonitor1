/*
 * Copyright 2016 Mentor Graphics Corporation. All Rights Reserved.
 * <p>
 * Recipients who obtain this code directly from Mentor Graphics use it solely
 * for internal purposes to serve as example Java web services.
 * This code may not be used in a commercial distribution. Recipients may
 * duplicate the code provided that all notices are fully reproduced with
 * and remain in the code. No part of this code may be modified, reproduced,
 * translated, used, distributed, disclosed or provided to third parties
 * without the prior written consent of Mentor Graphics, except as expressly
 * authorized above.
 * <p>
 * THE CODE IS MADE AVAILABLE "AS IS" WITHOUT WARRANTY OR SUPPORT OF ANY KIND.
 * MENTOR GRAPHICS OFFERS NO EXPRESS OR IMPLIED WARRANTIES AND SPECIFICALLY
 * DISCLAIMS ANY WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR WARRANTY OF NON-INFRINGEMENT. IN NO EVENT SHALL MENTOR GRAPHICS OR ITS
 * LICENSORS BE LIABLE FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING LOST PROFITS OR SAVINGS) WHETHER BASED ON CONTRACT, TORT
 * OR ANY OTHER LEGAL THEORY, EVEN IF MENTOR GRAPHICS OR ITS LICENSORS HAVE BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * <p>
 */


import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class SendNotificationEmail {

    public static void main(String  EmailMessage ) {
      //  public  SendNotificationEmail() {

        //String sendText = EmailMessage;
        String to = "";
        String from = "";
        String smtpHost = "";
        String subject = "";
        String configFileName = System.getenv("TASKMON_CONFIG");


        //System.out.println(sendText);

        try {
            //File file = new File("C:\\Users\\demo\\Google Drive\\CapitalPlugins\\TaskMonitor\\out\\artifacts\\CEATaskMonitor\\config\\TaskMonConfig.xml");
            File file = new File(configFileName);

            FileInputStream fileInput = new FileInputStream(file);
            Properties properties = new Properties();
            properties.loadFromXML(fileInput);
            fileInput.close();

            Enumeration enuKeys = properties.keys();
            while (enuKeys.hasMoreElements()) {
                String key = (String) enuKeys.nextElement();
                //String value = properties.getProperty(key);
                //System.out.println(key + ": " + value);

                if ("to".equalsIgnoreCase(key)){
                    to = properties.getProperty(key);
                }
                else if ("from".equalsIgnoreCase(key)){
                    from = properties.getProperty(key);
                }
                else if ("smtpHost".equalsIgnoreCase(key)){
                    smtpHost = properties.getProperty(key);
                }
                else if ("subject".equalsIgnoreCase(key)){
                    subject = properties.getProperty(key);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    // Recipient's email ID needs to be mentioned.
    //    to = "nick_demarco@mentor.com";

        // Sender's email ID needs to be mentioned
     //   from = "nick_demarco@mentor.com";

        // Assuming you are sending email from localhost
     //   smtpHost = "mail.mentorg.com";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.setProperty("mail.smtp.host", smtpHost);

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            List<String> items = Arrays.asList(to.split("\\s*,\\s*"));

            for (int i = 0; i < items.size(); i++) {
                //System.out.println(items.get(i));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(items.get(i)));
            }
            
            //message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            //message.addRecipient(Message.RecipientType.TO, new InternetAddress("nickd@demarconet.com") );


            // Set Subject: header field
            message.setSubject(subject);

            // Now set the actual message
           // message.setText("This is actual message");
            message.setText(EmailMessage);


            // Send message
            Transport.send(message);
            //System.out.println("Sent message successfully....");
        }catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}
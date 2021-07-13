package com.test.demo.mail;

public class MailManager {
    public void sendMail(String senderAddress,
                         String recipientAddress,
                         String senderAccount,
                         String senderPassword,
                         String title,
                         String text) throws Exception {
        JavaxMailHandler javaxMailHandler = new JavaxMailHandler();
        javaxMailHandler.sendMail(senderAddress,recipientAddress,senderAccount,senderPassword,title,text);
    }
}

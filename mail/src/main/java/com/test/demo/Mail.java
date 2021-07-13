package com.test.demo;


import com.test.demo.mail.MailManager;

public class Mail {
        public static void main(String[] args) throws Exception {
            String address = "xxx@xx.com";
            String senderPassword = "Aa123456";
            String title = "邮件主题";
            String text = "邮件内容";
            MailManager mailManager = new MailManager();
            mailManager.sendMail(address,address,address,senderPassword,title,text);
        }

}

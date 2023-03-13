package com.niecf.community;

import com.niecf.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail(){
        mailClient.sendMail("602065880@qq.com","TEST","Welcome.");
    }

    @Test
    public void testHtmlMail(){
        Context context=new Context();
        context.setVariable("username","赵剑鹏");
        String content=templateEngine.process("/mail/demo",context);
        System.out.println(content);
        mailClient.sendMail("cszjpzhao@qq.com","HTML",content);
    }
}

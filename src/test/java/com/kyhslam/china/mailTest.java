package com.kyhslam.china;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class mailTest {

    public static void main(String[] args) {

        // SMTP 서버 설정
        String host = "10.111.243.25"; // SMTP 서버 주소 (예: smtp.gmail.com)
        int port = 25; // SMTP 포트 (보통 587 또는 465)
        String username = ""; // 보내는 사람 이메일
        String password = ""; // 이메일 비밀번호 또는 앱 비밀번호

        // 수신자 이메일
        String toEmail = "younghwan.kim@hyundaielevator.com";
        String subject = "PLM-VAULT 연계 테스트 메일";
        String content = "안녕하세요.\n\nSMTP를 이용한 Java 메일 발송 테스트입니다.";

        // SMTP 설정 속성 구성
        Properties props = new Properties();
        props.put("mail.smtp.auth", false);
        props.put("mail.smtp.starttls.enable", "true"); // TLS
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));

        //

        // 인증 및 세션 생성
        /*Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });*/

        // 세션 생성 (인증 없음)
        Session session = Session.getInstance(props);

        try {

            //msg.setFrom(new InternetAddress("helco7000002@hdel.co.kr", "PLM Admin"));

            // 이메일 메시지 구성
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(toEmail)
            );
            message.setSubject(subject);
            message.setText(content);

            // 메일 전송
            Transport.send(message);
            System.out.println("이메일이 성공적으로 전송되었습니다.");

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("이메일 전송 실패: " + e.getMessage());
        }
    }


}

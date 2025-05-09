package com.kyhslam.china;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class sendChinaPart {

    public static void main(String[] args) {



        // SMTP 서버 설정
        String host = "10.111.243.25"; // SMTP 서버 주소 (예: smtp.gmail.com)
        int port = 25; // SMTP 포트 (보통 587 또는 465)
        String username = "Vault_Admin@hyundaielevator.com"; // 보내는 사람 이메일
        //String password = "1111"; // 이메일 비밀번호 또는 앱 비밀번호

        // 수신자 이메일
        String toEmail = "younghwan.kim@hyundaielevator.com";
        //String subject = "PLM-VAULT 연계 테스트 메일";
        String subject = """
                📢 중국자재번호 모니터링
                """;

        String htmlContent = """
                <html>
                <body style="font-family: Arial, sans-serif; line-height: 1.6;">
                    <h2 style="color: #2F5597;">🔔 PLM-VAULT 연계 알림</h2>
                    <p>안녕하세요,</p>
                    <p>법인PDM에서 아래의 자재들이 릴리즈 처리 되었습니다.</p>
                    <ul>
                        <li><strong>연계 일시:</strong> 2025-03-24</li>
                        <li><strong>적용 시스템:</strong> PLM → VAULT</li>
                        <li><strong>적용 내용:</strong> 도면 라이프사이클 동기화</li>
                    </ul>
                    <p>자세한 내용은
                        <a href="#" style="color: #1a73e8;">여기</a>
                        를 참고해주세요.
                    </p>
                                
                    <hr style="margin-top: 30px;"/>
                                
                    <h4 style="color: #2F5597; margin-bottom: 10px;">📌 3D 업무 담당자 안내</h4>
                    <table style="border-collapse: collapse; width: 100%;">
                        <thead>
                            <tr>
                                <th style="border: 1px solid #ccc; padding: 8px; background-color: #f2f2f2;">자재번호</th>
                                <th style="border: 1px solid #ccc; padding: 8px; background-color: #f2f2f2;">버전</th>
                                <th style="border: 1px solid #ccc; padding: 8px; background-color: #f2f2f2;"></th>
                            </tr>
                        </thead>
                        <tbody>
                        """;

        htmlContent += """
                            <tr>
                                <td style="border: 1px solid #ccc; padding: 8px;">김영환</td>
                                <td style="border: 1px solid #ccc; padding: 8px;">VAULT시스템</td>
                                <td style="border: 1px solid #ccc; padding: 8px;">hong.gd@yourcompany.com</td>
                            </tr>
                        """;

        htmlContent += """
                        </tbody>
                    </table>
                                
                    <p style="margin-top: 30px;">감사합니다.<br><strong>수배로직설계팀</strong></p>
                                
                    <hr style="margin-top: 40px;" />
                    <small style="color: #888;">※ 본 메일은 시스템에서 자동 발송된 메일입니다.</small>
                </body>
                </html>
                """;

        // SMTP 설정 속성 구성
        Properties props = new Properties();
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.starttls.enable", "true"); // TLS
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));

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
            //message.setText(content);
            message.setContent(htmlContent, "text/html; charset=UTF-8");


            // 메일 전송
            Transport.send(message);
            System.out.println("이메일이 성공적으로 전송되었습니다.");

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("이메일 전송 실패: " + e.getMessage());
        }


    }
}

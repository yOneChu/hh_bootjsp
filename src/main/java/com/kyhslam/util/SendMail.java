package com.kyhslam.util;

import com.kyhslam.dto.PartInfoDTO;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.ArrayList;
import java.util.Properties;

public class SendMail {


    //법인PDM-PLM 모니터링 결과 메일 발송
    public static void sendChinaPart(ArrayList<PartInfoDTO> data) {

        //dongki.seo@hyundaielevator.com


        // SMTP 서버 설정
        String host = "10.111.243.25"; // SMTP 서버 주소 (예: smtp.gmail.com)
        int port = 25; // SMTP 포트 (보통 587 또는 465)
        String username = "SUBAEAdmin@hyundaielevator.com"; // 보내는 사람 이메일
        //String password = "1111"; // 이메일 비밀번호 또는 앱 비밀번호


        // 수신자 이메일
        //서동기: dongki.seo@hyundaielevator.com
        //박효현: hh.park@hyundaielevator.com
        //안대환: daehwan.ahn@hyundaielevator.com
        //정선우: sunwoo.jung@hyundaielevator.com
        //이선욱: seonuk.lee@hyundaielevator.com
        String toEmail = "jihyun.kim@hyundaielevator.com, jeongwon.shon@hyundaielevator.com, dongki.seo@hyundaielevator.com, hh.park@hyundaielevator.com, sunwoo.jung@hyundaielevator.com, daehwan.ahn@hyundaielevator.com";
        String ccEmail = "younghwan.kim@hyundaielevator.com, seonuk.lee@hyundaielevator.com";
        //String toEmail = "seonuk.lee@hyundaielevator.com, younghwan.kim@hyundaielevator.com, jihyun.kim@hyundaielevator.com, jeongwon.shon@hyundaielevator.com, dongki.seo@hyundaielevator.com, hh.park@hyundaielevator.com, sunwoo.jung@hyundaielevator.com, daehwan.ahn@hyundaielevator.com";
        //String toEmail = "younghwan.kim@hyundaielevator.com";

        //String subject = "PLM-VAULT 연계 테스트 메일";
        String subject = """
                📢 법인PDM 모니터링 결과 메일 입니다.
                """;

        String htmlContent = """
                <html>
                <body style="font-family: Arial, sans-serif; line-height: 1.6;">
                    <h2 style="color: #2F5597;">🔔 법인PDM-PLM 비교 모니터링 알림</h2>
                    <p>안녕하세요,</p>
                    <p>법인PDM과 PLM 정합성 비교 결과, 상이한 부품 목록 입니다.</p>

                    <hr style="margin-top: 30px;"/>
                                
                    <h4 style="color: #2F5597; margin-bottom: 10px;">📌 PLM 자재 목록 </h4>
                    <table style="border-collapse: collapse; width: 100%;">
                        <thead>
                            <tr>
                                <th style="border: 1px solid #ccc; padding: 8px; background-color: #f2f2f2;">자재번호</th>
                                <th style="border: 1px solid #ccc; padding: 8px; background-color: #f2f2f2;">자재명</th>
                                <th style="border: 1px solid #ccc; padding: 8px; background-color: #f2f2f2;">버전</th>
                            </tr>
                        </thead>
                        <tbody>
                        """;

        /*htmlContent += """
                            <tr>
                                <td style="border: 1px solid #ccc; padding: 8px;">김영환</td>
                                <td style="border: 1px solid #ccc; padding: 8px;">VAULT시스템</td>
                                <td style="border: 1px solid #ccc; padding: 8px;">hong.gd@yourcompany.com</td>
                            </tr>
                        """;*/

        String str = "";

        for (int i = 0; i < data.size(); i++) {
            String partNo = data.get(i).getPartNo();
            String partName = data.get(i).getPartName();
            String eName = data.get(i).getEName();
            String cName = data.get(i).getCName();
            String version = data.get(i).getVersion();

            str += "<tr>";
            str += "<td style='text-align: center; border: 1px solid #ccc; padding: 8px;'>" + partNo + "</td>";
            str += "<td style='text-align: center; border: 1px solid #ccc; padding: 8px;'>" + partName + "</td>";
            str += "<td style='text-align: center; border: 1px solid #ccc; padding: 8px;'>" + version + "</td>";
            str += "</tr>";
        }

        htmlContent += str;

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
            ); //수신자

            message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmail)); //참조자
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


    /**
     * BlockNo 기준정보 변경되었을 경우 메일 발송
     * @param data
     */
    public static void sendBlockHistory(ArrayList<PartInfoDTO> data) {

        // SMTP 서버 설정
        String host = "10.111.243.25"; // SMTP 서버 주소 (예: smtp.gmail.com)
        int port = 25; // SMTP 포트 (보통 587 또는 465)
        String username = "SUBAEAdmin@hyundaielevator.com"; // 보내는 사람 이메일
        //String password = "1111"; // 이메일 비밀번호 또는 앱 비밀번호

        // 수신자 이메일
        //서동기: dongki.seo@hyundaielevator.com
        //박효현: hh.park@hyundaielevator.com
        //안대환: daehwan.ahn@hyundaielevator.com
        //정선우: sunwoo.jung@hyundaielevator.com
        //이선욱: seonuk.lee@hyundaielevator.com
        String toEmail = "jihyun.kim@hyundaielevator.com, jeongwon.shon@hyundaielevator.com, dongki.seo@hyundaielevator.com, hh.park@hyundaielevator.com, sunwoo.jung@hyundaielevator.com, daehwan.ahn@hyundaielevator.com";
        String ccEmail = "younghwan.kim@hyundaielevator.com, seonuk.lee@hyundaielevator.com";
        //String toEmail = "seonuk.lee@hyundaielevator.com, younghwan.kim@hyundaielevator.com, jihyun.kim@hyundaielevator.com, jeongwon.shon@hyundaielevator.com, dongki.seo@hyundaielevator.com, hh.park@hyundaielevator.com, sunwoo.jung@hyundaielevator.com, daehwan.ahn@hyundaielevator.com";
        //String toEmail = "younghwan.kim@hyundaielevator.com";
        String subject = """
                📢 BlockNo 기준정보 변경 모니터링 결과 메일
                """;

        String htmlContent = """
                <html>
                <body style="font-family: Arial, sans-serif; line-height: 1.6;">
                    <h2 style="color: #2F5597;">🔔 법인PDM-PLM 비교 모니터링 알림</h2>
                    <p>안녕하세요,</p>
                    <p>BlockNo 기준정보 변경 모니터링 결과</p>

                    <hr style="margin-top: 30px;"/>
                                
                    <h4 style="color: #2F5597; margin-bottom: 10px;">📌 PLM 자재 목록 </h4>
                    <table style="border-collapse: collapse; width: 100%;">
                        <thead>
                            <tr>
                                <th style="border: 1px solid #ccc; padding: 8px; background-color: #f2f2f2;">자재번호</th>
                                <th style="border: 1px solid #ccc; padding: 8px; background-color: #f2f2f2;">자재명</th>
                                <th style="border: 1px solid #ccc; padding: 8px; background-color: #f2f2f2;">버전</th>
                            </tr>
                        </thead>
                        <tbody>
                        """;

        /*htmlContent += """
                            <tr>
                                <td style="border: 1px solid #ccc; padding: 8px;">김영환</td>
                                <td style="border: 1px solid #ccc; padding: 8px;">VAULT시스템</td>
                                <td style="border: 1px solid #ccc; padding: 8px;">hong.gd@yourcompany.com</td>
                            </tr>
                        """;*/

        String str = "";

        for (int i = 0; i < data.size(); i++) {
            String partNo = data.get(i).getPartNo();
            String partName = data.get(i).getPartName();
            String eName = data.get(i).getEName();
            String cName = data.get(i).getCName();
            String version = data.get(i).getVersion();

            str += "<tr>";
            str += "<td style='text-align: center; border: 1px solid #ccc; padding: 8px;'>" + partNo + "</td>";
            str += "<td style='text-align: center; border: 1px solid #ccc; padding: 8px;'>" + partName + "</td>";
            str += "<td style='text-align: center; border: 1px solid #ccc; padding: 8px;'>" + version + "</td>";
            str += "</tr>";
        }

        htmlContent += str;

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
            ); //수신자

            message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmail)); //참조자
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

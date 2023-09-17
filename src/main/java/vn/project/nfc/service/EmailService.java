package vn.project.nfc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendEmailRegisterAccount(String nickName, String email) {
        try {
            String content = "<h3>Xin chào " + nickName + "</h3>" +
                    "<p>LIAM xin gửi lời cảm ơn chân thành đến " + nickName + " vì bạn tin tưởng và lựa chọn sản phẩm của chúng mình. LIAM rất mong rằng thẻ cá nhân mà bạn đã lựa chọn sẽ đem lại cho bạn một trải nghiệm tuyệt vời cùng người thương, gia đình và bạn bè!\n" +
                    "Nếu " + nickName + " có bất kỳ thắc mắc hoặc góp ý nào xin đừng ngại ngần liên hệ với LIAM, chúng mình sẽ luôn luôn sẵn sàng hỗ trợ bạn</p>" +
                    "\n" +
                    "<p>Một lần nữa, LIAM xin chân thành cảm ơn bạn vì đã tin tưởng và ủng hộ sản phẩm của chúng mình. Chúc bạn một ngày tốt lành!</p>" +
                    "\n" +
                    "<p> Trân trọng </p>" +
                    "<p>LIAM</p>" +
                    "<p>-------------</p>" +
                    "<p>Thông tin liên hệ</p>" +
                    "<a href=\"https://www.google.com/\">Facebook</a>" +
                    "<a href=\"https://www.google.com/\">Instagram</a>" +
                    "<a href=\"https://www.google.com/\">Tiktok</a>" +
                    "<p>Hotline: 0364688581</p>";
            String subject = "Cảm ơn bạn đã mua hàng của chúng tôi";
            MimeMessage message = javaMailSender.createMimeMessage();
            message.setFrom("info.liamcompany@gmail.com");
            message.setRecipients(MimeMessage.RecipientType.TO, email);
            message.setSubject(subject);
            message.setContent(content, "text/html; charset=utf-8");
            javaMailSender.send(message);
        } catch (Exception e) {
            log.info("Error Send Email Register ==================== " + e.getMessage());
        }
    }

}

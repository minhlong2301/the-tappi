package vn.project.nfc.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
public class GenericService {

    public String generateQRCode(String url, int width, int height) {
//        StringBuilder result = new StringBuilder();
//        if (StringUtils.hasText(url)) {
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            try {
//                QRCodeWriter qrCodeWriter = new QRCodeWriter();
//                BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, width, height);
//                BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
//                ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
//                result.append("data:image/png;base64,");
//                String qrCodeBase64 = Arrays.toString(Base64.getEncoder().encode(byteArrayOutputStream.toByteArray()));
//                result.append(qrCodeBase64);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return result.toString();
//    }
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, width, height);
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();
            return Base64.getEncoder().encodeToString(pngData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}

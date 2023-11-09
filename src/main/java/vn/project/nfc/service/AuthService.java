package vn.project.nfc.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import vn.project.nfc.jwt.JwtProvider;
import vn.project.nfc.model.User;
import vn.project.nfc.repository.UserRepository;
import vn.project.nfc.request.CheckUserRequest;
import vn.project.nfc.request.LoginRequest;
import vn.project.nfc.request.RegisterRequest;
import vn.project.nfc.response.GlobalResponse;
import vn.project.nfc.response.GlobalUserResponse;
import vn.project.nfc.response.LoginResponse;
import vn.project.nfc.sercurity.impl.UserDetailsImpl;
import vn.project.nfc.utils.GenericService;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoderAndDecode;

    private final AuthenticationManager authenticationManager;

    private final JwtProvider jwtProvider;

    private final GenericService genericService;

    private final EmailService emailService;


    @Transactional
    public GlobalResponse<Object> registerAccount(RegisterRequest registerRequest) throws MessagingException {
        Optional<User> user = userRepository.findByUuid(registerRequest.getUuid());
        if (!user.isPresent()) {
            return GlobalResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Bạn chưa có mã thẻ để đăng kí tài khoản")
                    .data(null)
                    .build();
        }
        if (StringUtils.hasText(user.get().getEmail())) {
            return GlobalResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Thẻ này đã được đăng kí")
                    .data(null)
                    .build();
        }
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            return GlobalResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Email đã tồn tại")
                    .data(null)
                    .build();
        }
        if (userRepository.findByNickName(registerRequest.getNickName()).isPresent()) {
            return GlobalResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Nickname đã tồn tại")
                    .data(null)
                    .build();
        }
        user.get().setNickName(registerRequest.getNickName());
        user.get().setEmail(registerRequest.getEmail());
        user.get().setTelephone(registerRequest.getTelephone());
        user.get().setPassWord(passwordEncoderAndDecode.encode(registerRequest.getPassWord()));
        user.get().setCreateAt(new Date());
        userRepository.save(user.get());
        emailService.sendEmailRegisterAccount(registerRequest.getNickName(), registerRequest.getEmail());
        return GlobalResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Thành công")
                .data(null)
                .build();
    }

    public GlobalResponse<Object> userLogin(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassWord()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
//        Boolean isPasswordMatch = passwordEncoderAndDecode.matches(loginRequest.getPassWord(), userDetailsImpl.getPassword());
        String token = jwtProvider.createJwtToken(userDetailsImpl);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken(token);
        loginResponse.setUserName(userDetailsImpl.getUsername());
        return GlobalResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Thành công")
                .data(loginResponse)
                .build();
    }

    public GlobalResponse<Object> checkUser(CheckUserRequest checkUserRequest) {
        Optional<User> user = userRepository.findByUuid(checkUserRequest.getUuid());
        GlobalUserResponse globalUserResponse = new GlobalUserResponse();
        if (user.isPresent()) {
            BeanUtils.copyProperties(user.get(), globalUserResponse);
            return GlobalResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("Thành công")
                    .data(globalUserResponse)
                    .build();
        } else {
            return GlobalResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Chưa tồn tại mã thẻ")
                    .data(null)
                    .build();
        }
    }

    public GlobalResponse<Object> getUserByNickName(String nickName) {
        Optional<User> user = userRepository.findByNickName(nickName);
        GlobalUserResponse globalUserResponse = new GlobalUserResponse();
        if (!user.isPresent()) {
            return GlobalResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Nickname không tồn tại trên hệ thống")
                    .data(null)
                    .build();
        } else {
            user.get().setUpdateAt(new Date());
            userRepository.save(user.get());
            BeanUtils.copyProperties(user.get(), globalUserResponse);
            return GlobalResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("Thành công")
                    .data(globalUserResponse)
                    .build();
        }
    }

    public GlobalResponse<Object> getQrCode(String uuid) {
        Optional<User> user = userRepository.findByUuid(uuid);
        if (user.isPresent()) {
            String qrCodeBase64 = genericService.generateQRCode(user.get().getUrl(), 300, 300);
            if (StringUtils.hasText(qrCodeBase64)) {
                return GlobalResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Thành công")
                        .data(qrCodeBase64)
                        .build();
            }
        }
        return GlobalResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Uuid không tồn tại trên hệ thống")
                .data(null)
                .build();
    }

    @Transactional
    public GlobalResponse<Object> generateUuidAndUrl(Integer number, Integer createTime) {
        String uuid;
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            User user = new User();
            user.setUuid(UUID.randomUUID().toString());
            uuid = user.getUuid();
            user.setUrl("https://liamtap.site/" + uuid);
            user.setNumberTimeCreate(createTime);
            userList.add(user);
        }
        userRepository.saveAll(userList);
        return GlobalResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Thành công")
                .data(null)
                .build();
    }

    public byte[] generateQRCode(Integer createTime) throws IOException, WriterException {
        ByteArrayOutputStream zipOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(zipOutputStream);
        int width = 42;
        int height = 42;
        Map<String, String> userList = this.getDataUrls(createTime);
        Map<String, String> uuidAndUrl = new HashMap<>();
        for (var url : userList.entrySet()) {
            EnumMap<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8);
            BitMatrix bitMatrix = new MultiFormatWriter().encode(url.getValue(), BarcodeFormat.QR_CODE, width, height, hints);
            BufferedImage qrImage = toBufferedImage(bitMatrix);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "PNG", outputStream);
            byte[] pngData = outputStream.toByteArray();
            String base64QR = Base64.getEncoder().encodeToString(pngData);
            uuidAndUrl.put(url.getKey(), base64QR);
        }
        for (var item : uuidAndUrl.entrySet()) {
            byte[] decodedDataPDF = Base64Utils.decodeFromString(item.getValue());
            ZipEntry pdfEntry = new ZipEntry(item.getKey() + ".png");
            zip.putNextEntry(pdfEntry);
            zip.write(decodedDataPDF);
            zip.closeEntry();
        }
        zip.close();
        zipOutputStream.close();
        return zipOutputStream.toByteArray();
    }

    private Map<String, String> getDataUrls(Integer createTime) {
        Map<String, String> uuidList = new HashMap<>();
        List<User> userList = userRepository.findByNumberTimeCreate(createTime);
        if (!CollectionUtils.isEmpty(userList)) {
            for (User item : userList) {
                uuidList.put(item.getUuid(), item.getUrl());
            }
        }
        return uuidList;
    }

    private static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int customColorRGB = new Color(0, 0, 0).getRGB();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? customColorRGB : Color.TRANSLUCENT);
            }
        }
        return image;
    }

    private void createCell(Row row, int columnCount, Object valueOfCell, CellStyle style, XSSFSheet sheet) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (valueOfCell instanceof Integer) {
            cell.setCellValue((Integer) valueOfCell);
        } else if (valueOfCell instanceof Long) {
            cell.setCellValue((Long) valueOfCell);
        } else if (valueOfCell instanceof String) {
            cell.setCellValue((String) valueOfCell);
        } else if (valueOfCell instanceof Double) {
            cell.setCellValue((Double) valueOfCell);
        }
        cell.setCellStyle(style);
    }

    public byte[] exportExcel(Integer createTime) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("URL thẻ cá nhân");
        Row rowHeader = sheet.createRow(0);
        CellStyle styleHeader = workbook.createCellStyle();
        XSSFFont fontHeader = workbook.createFont();
        fontHeader.setBold(true);
        fontHeader.setFontHeight(13);
        fontHeader.setFontName("Times New Roman");
        styleHeader.setFont(fontHeader);
        createCell(rowHeader, 0, "STT", styleHeader, sheet);
        createCell(rowHeader, 1, "URL", styleHeader, sheet);
        CellStyle styleData = workbook.createCellStyle();
        XSSFFont fontData = workbook.createFont();
        fontData.setFontHeight(13);
        fontData.setFontName("Times New Roman");
        styleData.setFont(fontData);
        List<User> userList = userRepository.findByNumberTimeCreate(createTime);
        int rowCount = 1;
        if (!CollectionUtils.isEmpty(userList)) {
            int stt = 1;
            for (User item : userList) {
                Row row = sheet.createRow(rowCount++);
                int columnCount = 0;
                createCell(row, columnCount++, stt++, styleData, sheet);
                createCell(row, columnCount++, item.getUrl(), styleData, sheet);

            }
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

}

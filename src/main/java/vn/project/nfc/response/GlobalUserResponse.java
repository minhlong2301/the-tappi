package vn.project.nfc.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalUserResponse {

    private String avatar;

    private String nickName;

    private String content;

    private String email;

    private String telephone;

    private String typeCard;



}

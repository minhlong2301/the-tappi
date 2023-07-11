package vn.project.nfc.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "USER")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, unique = true)
    private Long id;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "UUID")
    private String uuid;

    @Column(name = "PASSWORD")
    private String passWord;

    @Column(name = "URL")
    private String url;

    @Column(name = "AVATAR")
    private String avatar;

    @Column(name = "NICKNAME")
    private String nickName;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "TELEPHONE")
    private String telephone;

    @Column(name = "TEMPLATES")
    private String templates;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "USER_NAME")
    private String userName;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    @Column(name = "CREATE_AT")
    private Date createAt;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    @Column(name = "UPDATE_AT")
    private Date updateAt;

}

package vn.project.nfc.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "USER")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, unique = true)
    private Long id;

    @Column(name = "USERNAME")
    private String userName;

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


}

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


}

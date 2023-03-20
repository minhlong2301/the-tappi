package vn.project.nfc.model;

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

    @Column(name = "USERNAME")
    private String userName;

    @Column(name = "UUID")
    private String uuid;

    @Column(name = "AVATAR")
    private String avatar;

    @Column(name = "FB")
    private String fb;

    @Column(name = "TIKTOK")
    private String tiktok;

    @Column(name = "IG")
    private String ig;

    @Column(name = "DOMAIN")
    private String domain;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "PAGE")
    private String page;

    @Column(name = "CREATE_AT")
    private Date createAt;

}

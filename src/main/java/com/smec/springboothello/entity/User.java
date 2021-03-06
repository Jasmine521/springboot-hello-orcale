package com.smec.springboothello.entity;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class User {

    private Long id;

    private String email;
    private String password;
    private String name;
    private String imageUrl;

    private long createdAt;

    public Long getId(){return id;}

    public void setId(Long id){this.id = id;}

    public long getCreatedAt(){return createdAt;}

    public void setCreatedAt(long createdAt){this.createdAt = createdAt;}

    public String getCreatedDateTime(){
        return Instant.ofEpochMilli(this.createdAt).atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }

    public String getEmail(){return email;}

    public void setEmail(String email){this.email = email;}

    public String getPassword(){return password;}

    public void setPassword(String password){this.password = password;}

    public String getName(){return name;}

    public void setName(String name){this.name = name;}

    @Override
    public String toString(){
        return String.format("User[id=%s, email=%s, name=%s, password=%s, createdAt=%s, createdDateTime=%s]",getId(),
                getEmail(),getName(),getPassword(),getCreatedAt(),getCreatedDateTime());
    }

    public String getImageUrl(){return imageUrl;}

    public void setImageUrl(String imageUrl){this.imageUrl=imageUrl;}

}

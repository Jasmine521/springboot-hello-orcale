package com.smec.springboothello.service;

import com.smec.springboothello.entity.User;
import com.sun.source.tree.ReturnTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.rmi.registry.Registry;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Map;


@Component
@Transactional
public class UserService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    JdbcTemplate jdbcTemplate;

    RowMapper<User> userRowMapper = new BeanPropertyRowMapper<>(User.class);

    public User getUserById(long id) {
        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE id = ?",userRowMapper,new Object[]{id});
    }

    public User getUserByEmail(String email){
        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email = ?",userRowMapper,new Object[] {email});
    }

    public User signin(String email, String password){
        logger.info("try login by {}...", email);
        User user = (User) getUserByEmail(email);
        if(user.getPassword().equals(password)){
            return user;
        }
        throw new RuntimeException("login failed.");
    }

    public User register(String email,String password,String name,String imageUrl){
        logger.info("try register by {}...",email);
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        user.setCreatedAt(System.currentTimeMillis());
        user.setImageUrl(imageUrl);
        KeyHolder holder = new GeneratedKeyHolder();
        if(1 != jdbcTemplate.update((conn)->{
            PreparedStatement ps = conn.prepareStatement("INSERT INTO users(email,password,name,createdAt,imageUrl) VALUES (?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1,user.getEmail());
            ps.setObject(2,user.getPassword());
            ps.setObject(3,user.getName());
            ps.setObject(4,user.getCreatedAt());
            ps.setObject(5,user.getImageUrl());
            return ps;
        },holder)){

            throw new RuntimeException("Insert failed.");
        }//if end
        Map<String,Object> keys = holder.getKeys();
        user.setId((Long) keys.get("ID"));
        return user;
    }

    public void updateUser(User user){
        if(1 !=jdbcTemplate.update("UPDATE users SET password = ? WHERE  id = ?",user.getPassword(),user.getId())){
            throw new RuntimeException("User not found by id.");
        }
    }
}

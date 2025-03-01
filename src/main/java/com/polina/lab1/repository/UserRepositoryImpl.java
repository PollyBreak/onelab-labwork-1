package com.polina.lab1.repository;

import com.polina.lab1.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository{

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<UserDTO> userMapper = (rs, rowNum) ->
            UserDTO.builder()
                    .id(rs.getLong("id"))
                    .username(rs.getString("username"))
                    .email(rs.getString("email"))
                    .build();

    @Override
    public void save(UserDTO user) {
        String sql = "INSERT INTO users (username, email) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() == null) {
            throw new IllegalStateException("Failed to retrieve generated ID.");
        }

        user.setId(keyHolder.getKey().longValue());
    }




    @Override
    public void delete(Long userId) {
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", userId);
    }

    @Override
    public UserDTO findById(Long userId) {
        List<UserDTO> users = jdbcTemplate.query("SELECT * FROM users WHERE id = ?", userMapper, userId);
        return users.isEmpty() ? null : users.get(0);
    }

    @Override
    public List<UserDTO> findAll() {
        return jdbcTemplate.query("SELECT * FROM users", userMapper);
    }

    @Override
    public UserDTO findByUsername(String username) {
        List<UserDTO> users = jdbcTemplate.query("SELECT * FROM users WHERE username = ?", userMapper, username);
        return users.isEmpty() ? null : users.get(0);
    }

    @Override
    public UserDTO findByEmail(String email) {
        List<UserDTO> users = jdbcTemplate.query("SELECT * FROM users WHERE email = ?", userMapper, email);
        return users.isEmpty() ? null : users.get(0);
    }
}

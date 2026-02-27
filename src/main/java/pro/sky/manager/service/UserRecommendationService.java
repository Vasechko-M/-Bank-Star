package pro.sky.manager.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Сервис для чтения ФИО из БД
 */
@Service
public class UserRecommendationService {

    private final JdbcTemplate jdbcTemplate;

    public UserRecommendationService(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getFullNameById(String userId) {
        String sql = "SELECT FIRST_NAME, LAST_NAME FROM USERS WHERE ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{userId}, (rs, rowNum) -> {
                String firstName = rs.getString("FIRST_NAME");
                String lastName = rs.getString("LAST_NAME");
                return firstName + " " + lastName;
            });
        } catch (EmptyResultDataAccessException e) {
            return null; // Надо подумать
        }
    }
}

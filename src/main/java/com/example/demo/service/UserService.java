package com.example.demo.service;

import com.example.demo.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserService implements IService<User> {
    private final Connection connection;

    public UserService(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(User user) throws SQLException {
        String query = "INSERT INTO user (username, email, password, role, token, otp, avatar) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword()); // Already hashed
            statement.setString(4, user.getRole());
            statement.setString(5, user.getToken());
            statement.setString(6, user.getOtp());
            statement.setString(7, user.getAvatar());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(User user) throws SQLException {
        String query = "UPDATE user SET username = ?, email = ?, password = ?, role = ?, " +
                "token = ?, otp = ?, avatar = ? " +
                "WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getRole());
            statement.setString(5, user.getToken());
            statement.setString(6, user.getOtp());
            statement.setString(7, user.getAvatar());
            statement.setInt(8, user.getId());

            statement.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM user WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    @Override
    public List<User> getAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user";
        System.out.println("Executing query: " + query);

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                User user = mapResultSetToUser(resultSet);
                System.out.println("Mapped user from database: " + user.getId() + " - " + user.getUsername());
                users.add(user);
            }
        }
        System.out.println("Total users retrieved: " + users.size());
        return users;
    }

    @Override
    public User getOneById() throws SQLException {
        return null;
    }

    public User getOneById(int id) throws SQLException {
        String query = "SELECT * FROM user WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                }
            }
        }
        return null;
    }

    // Additional useful methods
    public User getByUsername(String username) throws SQLException {
        String query = "SELECT * FROM user WHERE username = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                }
            }
        }
        return null;
    }

    public User getByEmail(String email) throws SQLException {
        String query = "SELECT * FROM user WHERE email = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                }
            }
        }
        return null;
    }

    public boolean verifyUserCredentials(String usernameOrEmail, String plainPassword) throws SQLException {
        User user = getByUsername(usernameOrEmail);
        if (user == null) {
            user = getByEmail(usernameOrEmail);
        }

        return user != null && BCrypt.checkpw(plainPassword, user.getPassword());
    }

    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setUsername(resultSet.getString("username"));
        user.setEmail(resultSet.getString("email"));
        user.setPassword(resultSet.getString("password"));
        user.setRole(resultSet.getString("role"));
        user.setToken(resultSet.getString("token"));
        user.setOtp(resultSet.getString("otp"));
        user.setAvatar(resultSet.getString("avatar"));
        return user;
    }
}
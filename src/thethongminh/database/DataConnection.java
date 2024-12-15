package thethongminh.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataConnection {

    public static Connection conn() throws ClassNotFoundException {
        Connection connection = null;
        try {
            String url = "jdbc:mysql://localhost:3306/smartcard";
            String user = "root";
            String password = "";
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Kết nối thành công!");
        } catch (ClassNotFoundException e) {
            System.err.println("Lỗi: Không tìm thấy driver JDBC. " + e.getMessage());
            throw e;
        } catch (SQLException e) {
            System.err.println("Lỗi: Không thể kết nối tới database. " + e.getMessage());
        }
        return connection;
    }

    public static void main(String[] args) {
        try {
            // Kết nối tới database
            Connection conn = DataConnection.conn();
            if (conn != null) {
                List<String[]> transHistory = fetchTransactionHistory();
                List<String[]> flightHistory = fetchFlightHistory();
                List<String[]> ticketMember = fetchTicketOfMember();
                conn.close();
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Lỗi trong quá trình load driver: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Lỗi trong quá trình xử lý kết nối: " + e.getMessage());
        }
    }

    public static List<String[]> fetchTransactionHistory() throws ClassNotFoundException, SQLException {
        List<String[]> transHistory = new ArrayList<>();
        String query = "SELECT * FROM giaodich";
        try (Connection connection = conn(); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String[] row = new String[]{
                    resultSet.getString("idGiaoDich"),
                    resultSet.getString("idThe"),
                    resultSet.getString("time"),
                    resultSet.getString("soTien"),
                    resultSet.getString("loai"),
                    resultSet.getString("maVeDaDat")
                };
                transHistory.add(row);
            }
        }
        System.out.println("Dữ liệu lịch sử giao dịch:");
        for (String[] row : transHistory) {
            System.out.println(String.join(", ", row));
        }
        return transHistory;
    }

    public static List<String[]> fetchFlightHistory() throws ClassNotFoundException, SQLException {
        List<String[]> flightHistory = new ArrayList<>();
        String query = "SELECT * FROM chuyenbay";
        try (Connection connection = conn(); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String[] row = new String[]{
                    resultSet.getString("gioDat"),
                    resultSet.getString("gioDi"),
                    resultSet.getString("gioDen"),
                    resultSet.getString("diaDiemDen"),
                    resultSet.getString("diaDiemDi"),
                    resultSet.getString("giaVe")
                };
                flightHistory.add(row);
            }
        }
        System.out.println("Dữ liệu lịch sử chuyến bay:");
        for (String[] row : flightHistory) {
            System.out.println(String.join(", ", row));
        }

        return flightHistory;
    }

    public static List<String[]> fetchTicketOfMember() throws ClassNotFoundException, SQLException {
        List<String[]> ticketMember = new ArrayList<>();
        String query = "SELECT * FROM vedadat";
        try (Connection connection = conn(); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String[] row = new String[]{
                    resultSet.getString("maVeDaDat"),
                    resultSet.getString("maVe"),
                    resultSet.getString("maCB"),      
                    resultSet.getString("idUser"),
                    resultSet.getString("soLuongDat"),
                    resultSet.getString("tongThanhToan"),
                    resultSet.getBoolean("daThanhToan") ? "Đã thanh toán" : "Chưa thanh toán"
                };
                ticketMember.add(row);
            }
        }
        System.out.println("Dữ liệu vé của tôi:");
        for (String[] row : ticketMember) {
            System.out.println(String.join(", ", row));
        }
        return ticketMember;
    }
    
    public static void payTicket(String maVeDaDat) throws ClassNotFoundException, SQLException {
    // Sử dụng PreparedStatement để tránh SQL Injection
    String query = "UPDATE vedadat SET daThanhToan = 1 WHERE maVeDaDat = ?";
    
    try (Connection connection = conn(); PreparedStatement statement = connection.prepareStatement(query)) {
        // Thiết lập giá trị cho tham số trong câu lệnh SQL
        statement.setString(1, maVeDaDat);
        
        // Thực thi câu lệnh UPDATE
        int rowsAffected = statement.executeUpdate();
        
        if (rowsAffected > 0) {
            System.out.println("Thanh toán thành công cho mã vé " + maVeDaDat);
        } else {
            System.out.println("Không tìm thấy mã vé " + maVeDaDat);
        }
    }
}
}
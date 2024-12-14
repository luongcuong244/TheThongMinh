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
            String password = "Matkhau@123";
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
                // Tạo bảng và dữ liệu mẫu nếu chưa tồn tại
                createTablesAndInsertSampleData(conn);

                List<String[]> transHistory = fetchTransactionHistory();
                List<String[]> flightHistory = fetchFlightHistory();
                List<String[]> ticketAll = fetchTicket();
                List<String[]> ticketMember = fetchTicketOfMember();
                conn.close();
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Lỗi trong quá trình load driver: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Lỗi trong quá trình xử lý kết nối: " + e.getMessage());
        }
    }

    // Hàm tạo bảng và chèn dữ liệu mẫu
    public static void createTablesAndInsertSampleData(Connection conn) throws SQLException {
        // Tạo bảng giaodich
        String createTransactionTable = "CREATE TABLE IF NOT EXISTS giaodich ("
                + "idGiaoDich INT PRIMARY KEY AUTO_INCREMENT, "
                + "idThe VARCHAR(255), "
                + "time DATETIME, "
                + "soTien DECIMAL(10, 2), "
                + "loai VARCHAR(50), "
                + "maVeDaDat VARCHAR(255))";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTransactionTable);
            System.out.println("Tạo bảng giaodich thành công!");
        }

        // Tạo bảng chuyenbay
        String createFlightTable = "CREATE TABLE IF NOT EXISTS chuyenbay ("
                + "gioDat DATETIME, "
                + "gioDi DATETIME, "
                + "gioDen DATETIME, "
                + "diaDiemDen VARCHAR(255), "
                + "diaDiemDi VARCHAR(255), "
                + "giaVe DECIMAL(10, 2))";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createFlightTable);
            System.out.println("Tạo bảng chuyenbay thành công!");
        }

        // Tạo bảng ve
        String createTicketTable = "CREATE TABLE IF NOT EXISTS ve ("
                + "maVe VARCHAR(255) PRIMARY KEY, "
                + "soLuong INT, "
                + "hangVe VARCHAR(50))";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTicketTable);
            System.out.println("Tạo bảng ve thành công!");
        }

        // Tạo bảng vedadat
        String createBookedTicketTable = "CREATE TABLE IF NOT EXISTS vedadat ("
                + "maVeDaDat VARCHAR(255) PRIMARY KEY, "
                + "maVe VARCHAR(255), "
                + "maCB VARCHAR(255), "
                + "idUser VARCHAR(255), "
                + "soLuongDat INT, "
                + "tongThanhToan DECIMAL(10, 2))";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createBookedTicketTable);
            System.out.println("Tạo bảng vedadat thành công!");
        }

        // Chèn dữ liệu mẫu vào các bảng
        insertSampleData(conn);
    }

    // Hàm chèn dữ liệu mẫu
    public static void insertSampleData(Connection conn) throws SQLException {
        // Chèn dữ liệu mẫu vào bảng giaodich
        String insertTransactionData = "INSERT INTO giaodich (idThe, time, soTien, loai, maVeDaDat) VALUES "
                + "('The123', NOW(), 500000, 'Mua Vé', 'VD123'), "
                + "('The456', NOW(), 200000, 'Nạp Tiền', 'VD456')";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(insertTransactionData);
            System.out.println("Dữ liệu mẫu vào bảng giaodich thành công!");
        }

        // Chèn dữ liệu mẫu vào bảng chuyenbay
        String insertFlightData = "INSERT INTO chuyenbay (gioDat, gioDi, gioDen, diaDiemDen, diaDiemDi, giaVe) VALUES "
                + "(NOW(), NOW(), NOW(), 'Hà Nội', 'TP.HCM', 1000000), "
                + "(NOW(), NOW(), NOW(), 'Đà Nẵng', 'Hà Nội', 800000)";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(insertFlightData);
            System.out.println("Dữ liệu mẫu vào bảng chuyenbay thành công!");
        }

        // Chèn dữ liệu mẫu vào bảng ve
        String insertTicketData = "INSERT INTO ve (maVe, soLuong, hangVe) VALUES "
                + "('V123', 10, 'VIP'), "
                + "('V456', 20, 'Economy')";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(insertTicketData);
            System.out.println("Dữ liệu mẫu vào bảng ve thành công!");
        }

        // Chèn dữ liệu mẫu vào bảng vedadat
        String insertBookedTicketData = "INSERT INTO vedadat (maVeDaDat, maVe, maCB, idUser, soLuongDat, tongThanhToan) VALUES "
                + "('VD123', 'V123', 'CB123', 'U123', 2, 2000000), "
                + "('VD456', 'V456', 'CB456', 'U456', 1, 800000)";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(insertBookedTicketData);
            System.out.println("Dữ liệu mẫu vào bảng vedadat thành công!");
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

    public static List<String[]> fetchTicket() throws ClassNotFoundException, SQLException {
        List<String[]> ticketAll = new ArrayList<>();
        String query = "SELECT * FROM ve";
        try (Connection connection = conn(); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String[] row = new String[]{
                    resultSet.getString("maVe"),
                    resultSet.getString("soLuong"),
                    resultSet.getString("hangVe"),                  
                };
                ticketAll.add(row);
            }
        }
        System.out.println("Dữ liệu tất cả vé:");
        for (String[] row : ticketAll) {
            System.out.println(String.join(", ", row));
        }
        return ticketAll;
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
}
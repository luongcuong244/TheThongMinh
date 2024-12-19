package thethongminh.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import thethongminh.model.Card;
import thethongminh.model.User;
import thethongminh.utils.DateUtil;
import thethongminh.utils.ImageUtils;

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
//                List<String[]> transHistory = fetchTransactionHistory();
//                List<String[]> flightHistory = fetchFlightHistory();
//                List<String[]> ticketMember = fetchTicketOfMember();
                conn.close();
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Lỗi trong quá trình load driver: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Lỗi trong quá trình xử lý kết nối: " + e.getMessage());
        }
    }

    public static List<String[]> fetchTransactionHistory(String cardId) throws ClassNotFoundException, SQLException {
        List<String[]> transHistory = new ArrayList<>();
        String query = "SELECT * FROM giaodich WHERE idThe  = ?";

        Connection connection = conn();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, cardId);
        ResultSet resultSet = statement.executeQuery();

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

        System.out.println("Dữ liệu lịch sử giao dịch:");
        for (String[] row : transHistory) {
            System.out.println(String.join(", ", row));
        }
        return transHistory;
    }

    // fetch dữ liệu lịch sử chuyến bay
    public static List<String[]> fetchFlightHistory() throws ClassNotFoundException, SQLException {
        List<String[]> flightHistory = new ArrayList<>();
        String query = "SELECT * FROM chuyenbay";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        try ( Connection connection = conn();  Statement statement = connection.createStatement();  ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String gioDenString = resultSet.getString("gioDen");
                LocalDateTime gioDen = LocalDateTime.parse(gioDenString, formatter);
                // Kiểm tra nếu chuyến bay đã hoàn thành
                if (gioDen.isBefore(now)) {
                    String[] row = new String[]{
                        resultSet.getString("gioDat"),
                        resultSet.getString("gioDi"),
                        gioDenString,
                        resultSet.getString("diaDiemDen"),
                        resultSet.getString("diaDiemDi"),
                        resultSet.getString("giaVe")
                    };
                    flightHistory.add(row);
                }
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
        try ( Connection connection = conn();  Statement statement = connection.createStatement();  ResultSet resultSet = statement.executeQuery(query)) {
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

    public static void payTicket(String maVeDaDat, String idThe, double soTien) throws ClassNotFoundException, SQLException {
        // Sử dụng PreparedStatement để tránh SQL Injection
        String query = "UPDATE vedadat SET daThanhToan = 1 WHERE maVeDaDat = ?";
        String insertQuery = "INSERT INTO giaodich (idThe, time, soTien, loai, maVeDaDat) VALUES (?, ?, ?, ?, ?)";

        try ( Connection connection = conn();  PreparedStatement statement = connection.prepareStatement(query);  PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
            // Thiết lập giá trị cho tham số trong câu lệnh SQL
            statement.setString(1, maVeDaDat);

            // Thực thi câu lệnh UPDATE
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Thanh toán thành công cho mã vé " + maVeDaDat);

                // Lấy thời gian hiện tại
                Timestamp currentTime = Timestamp.valueOf(LocalDateTime.now());

                // Set các tham số cho câu lệnh INSERT
                insertStatement.setString(1, idThe);  // id thẻ
                insertStatement.setTimestamp(2, currentTime);  // thời gian giao dịch
                insertStatement.setDouble(3, soTien);  // số tiền thanh toán
                insertStatement.setString(4, "Mua vé");  // loại giao dịch
                insertStatement.setString(5, maVeDaDat);  // mã vé đã đặt

                // Thực thi câu lệnh INSERT
                int insertResult = insertStatement.executeUpdate();
                if (insertResult > 0) {
                    System.out.println("Thông tin giao dịch đã được ghi lại vào bảng giaodich.");
                } else {
                    System.out.println("Không thể ghi thông tin vào bảng giaodich.");
                }
            } else {
                System.out.println("Không tìm thấy mã vé " + maVeDaDat);
            }
        }
    }

    // Hàm hủy vé - Xóa giao dịch trong bảng giaodich trước rồi xóa vé trong bảng vedadat
    public static void cancelTicket(String maVeDaDat) {
        String deleteTransactionQuery = "DELETE FROM giaodich WHERE maVeDaDat = ?";
        String deleteTicketQuery = "DELETE FROM vedadat WHERE maVeDaDat = ?";

        try ( Connection connection = conn();  PreparedStatement deleteTransactionStatement = connection.prepareStatement(deleteTransactionQuery);  PreparedStatement deleteTicketStatement = connection.prepareStatement(deleteTicketQuery)) {

            // Bước 1: Xóa giao dịch liên quan đến vé trong bảng giaodich
            deleteTransactionStatement.setString(1, maVeDaDat);
            int transactionDeleteResult = deleteTransactionStatement.executeUpdate();

            if (transactionDeleteResult > 0) {
                System.out.println("Giao dịch liên quan đến mã vé " + maVeDaDat + " đã được xóa.");
            } else {
                System.out.println("Không tìm thấy giao dịch với mã vé " + maVeDaDat);
            }

            // Bước 2: Xóa vé trong bảng vedadat
            deleteTicketStatement.setString(1, maVeDaDat);
            int ticketDeleteResult = deleteTicketStatement.executeUpdate();

            if (ticketDeleteResult > 0) {
                System.out.println("Vé " + maVeDaDat + " đã được xóa khỏi bảng vedadat.");
            } else {
                System.out.println("Không tìm thấy vé với mã " + maVeDaDat);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi khi hủy vé: " + e.getMessage());
        }
    }

    // Hàm nạp tiền vào tài khoản
    public static void topupMoney(String userId, String idThe, int topupAmount) {
        // Sử dụng PreparedStatement để tránh SQL Injection
        String query = "UPDATE user SET soDu = soDu + ? WHERE idUser = ?";
        String insertQuery = "INSERT INTO giaodich (idThe, time, soTien, loai) VALUES (?, ?, ?, ?)";

        try ( Connection connection = conn();  PreparedStatement statement = connection.prepareStatement(query);  PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {

            // Thiết lập giá trị cho câu lệnh UPDATE (cộng thêm số tiền nạp vào tài khoản người dùng)
            statement.setInt(1, topupAmount);  // Số tiền nạp
            statement.setString(2, userId);    // Mã người dùng

            // Thực thi câu lệnh UPDATE
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Nạp tiền thành công cho người dùng " + userId);

                // Lấy thời gian hiện tại
                Timestamp currentTime = Timestamp.valueOf(LocalDateTime.now());

                // Set các tham số cho câu lệnh INSERT (ghi lại giao dịch nạp tiền)
                insertStatement.setString(1, idThe);       // ID thẻ
                insertStatement.setTimestamp(2, currentTime); // Thời gian giao dịch
                insertStatement.setInt(3, topupAmount);    // Số tiền nạp
                insertStatement.setString(4, "Nạp tiền"); // Loại giao dịch

                // Thực thi câu lệnh INSERT
                int insertResult = insertStatement.executeUpdate();
                if (insertResult > 0) {
                    System.out.println("Thông tin giao dịch đã được ghi lại vào bảng giaodich.");
                } else {
                    System.out.println("Không thể ghi thông tin vào bảng giaodich.");
                }
            } else {
                System.out.println("Không tìm thấy người dùng với ID " + userId);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DataConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DataConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static int addUser(User user) {
        // Sử dụng PreparedStatement để tránh SQL Injection

        ResultSet generatedKeys = null;

        int generatedId = 0;

        String insertQuery = "INSERT INTO user (name, ngaySinh, diaChi, sdt, soDu, image) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            Connection connection = conn();

            //PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);

            insertStatement.setString(1, user.getName());
            insertStatement.setDate(2, DateUtil.convertStringToSqlDate(user.getDateOfBirth(), "dd-MM-yyyy"));
            insertStatement.setString(3, user.getAddress());
            insertStatement.setString(4, user.getPhoneNumber());
            insertStatement.setString(5, "0");
            insertStatement.setBytes(6, ImageUtils.bufferedImageToByteArray(user.getAvatar()));

            int insertResult = insertStatement.executeUpdate();
            if (insertResult > 0) {
                generatedKeys = insertStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    generatedId = generatedKeys.getInt(1); // Cột đầu tiên chứa khóa tự tăng
                    System.out.println("ID của hàng vừa được chèn là: " + generatedId);
                }
            } else {
                System.out.println("Lỗi thêm user.");
            }

        } catch (ClassNotFoundException | SQLException | IOException | ParseException ex) {
            Logger.getLogger(DataConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        return generatedId;
    }

    public static void addCard(int userID, String cardID, String publicKey) {

        String insertQuery = "INSERT INTO card (idThe, idUser, publicKey) VALUES (?, ?, ?)";
        try {
            Connection connection = conn();

            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            insertStatement.setString(1, cardID);
            insertStatement.setInt(2, userID);
            insertStatement.setString(3, publicKey);

            int insertResult = insertStatement.executeUpdate();
            if (insertResult > 0) {
                System.out.println("Đã thêm card mới");
            } else {
                System.out.println("Lỗi thêm card.");
            }

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DataConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DataConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void updatePublickey(Card card) throws ClassNotFoundException, SQLException {

        String query = "UPDATE card SET publicKey = ? WHERE idThe = ?";

        try ( Connection connection = conn();  PreparedStatement statement = connection.prepareStatement(query);) {

            statement.setString(1, String.valueOf(card.getModulus()) + "." + String.valueOf(card.getExponent()));
            statement.setString(2, card.getCardId());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Cập nhật public key thành công" + rowsAffected);

            } else {
                System.out.println("Cập nhật public key thất bại" + rowsAffected);
            }
        }
    }

}

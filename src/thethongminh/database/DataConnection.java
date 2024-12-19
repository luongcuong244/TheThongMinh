package thethongminh.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
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

    public static List<String[]> getTransactionHistoryByCardId(String idThe) throws ClassNotFoundException, SQLException {
        List<String[]> transactionHistory = new ArrayList<>();
        String query = "SELECT idGiaoDich, idThe, time, soTien, loai, maVe FROM giaodich WHERE idThe = ?";

        try (Connection connection = conn(); // Kết nối cơ sở dữ liệu
                 PreparedStatement statement = connection.prepareStatement(query)) {  // Sử dụng PreparedStatement để tránh SQL injection
            statement.setString(1, idThe);  // Đặt giá trị idThe vào câu truy vấn

            try (ResultSet resultSet = statement.executeQuery()) {  // Thực thi truy vấn
                while (resultSet.next()) {
                    // Lấy từng dòng kết quả từ ResultSet
                    String[] row = new String[]{
                        resultSet.getString("idGiaoDich"), // Lấy idGiaoDich
                        resultSet.getString("idThe"), // Lấy idThe
                        resultSet.getString("time"), // Lấy time
                        resultSet.getString("soTien"), // Lấy soTien
                        resultSet.getString("loai"), // Lấy loai giao dịch
                        resultSet.getString("maVe") // Lấy maVe (nếu có)
                    };
                    transactionHistory.add(row);  // Thêm dòng vào danh sách
                }
            }
        }

        // In ra lịch sử giao dịch để kiểm tra
        System.out.println("Lịch sử giao dịch của thẻ " + idThe + ":");
        for (String[] row : transactionHistory) {
            System.out.println(String.join(", ", row));
        }

        return transactionHistory;  // Trả về danh sách lịch sử giao dịch
    }

    // fetch dữ liệu lịch sử chuyến bay
    public static List<String[]> fetchFlightHistory() throws ClassNotFoundException, SQLException {
        List<String[]> flightHistory = new ArrayList<>();
        String query = "SELECT * FROM chuyenbay";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        try (Connection connection = conn(); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
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

    public static double getBalanceByCard(String idThe) throws ClassNotFoundException, SQLException {
        double soDu = 0.0;  // Biến để lưu số dư của người dùng
        String query = "SELECT u.soDu "
                + "FROM user u "
                + "JOIN card c ON u.idUser = c.idUser "
                + "WHERE c.idThe = ?";  // Lọc theo mã thẻ (idThe)

        try (Connection connection = conn(); // Kết nối với cơ sở dữ liệu
                 PreparedStatement statement = connection.prepareStatement(query)) {

            // Gán giá trị idThe vào PreparedStatement
            statement.setString(1, idThe);  // Thay thế tham số ? với idThe

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Lấy giá trị số dư từ ResultSet
                    soDu = resultSet.getDouble("soDu");
                }
            }
        }

        // Trả về số dư của người dùng
        return soDu;
    }

    public static List<String[]> fetchAllFlights() throws ClassNotFoundException, SQLException {
        List<String[]> flights = new ArrayList<>();
        String query = "SELECT maChuyenBay, diemDi, diemDen, gioDi, gioDen FROM chuyenbay";  // Lấy tất cả chuyến bay

        try (Connection connection = conn(); // Kết nối với cơ sở dữ liệu
                 Statement statement = connection.createStatement(); // Tạo Statement để thực thi câu lệnh SQL
                 ResultSet resultSet = statement.executeQuery(query)) {  // Thực thi câu lệnh và lấy kết quả

            // Duyệt qua tất cả kết quả trong ResultSet
            while (resultSet.next()) {
                // Lấy các giá trị từ ResultSet và lưu vào mảng String[]
                String[] row = new String[]{
                    resultSet.getString("maChuyenBay"),
                    resultSet.getString("diemDi"),
                    resultSet.getString("diemDen"),
                    resultSet.getString("gioDi"),
                    resultSet.getString("gioDen")
                };
                flights.add(row);  // Thêm vào danh sách flights
            }
        }

        // In dữ liệu các chuyến bay
        System.out.println("Danh sách tất cả các chuyến bay:");
        for (String[] row : flights) {
            System.out.println(String.join(", ", row));  // In ra từng chuyến bay
        }

        return flights;  // Trả về danh sách các chuyến bay
    }

    public static List<String[]> fetchTickets() throws ClassNotFoundException, SQLException {
        List<String[]> ticketMember = new ArrayList<>();
        String query = "SELECT v.maVe, v.maChuyenBay, c.diemDi, c.diemDen, c.gioDi, c.gioDen, v.maSoGhe, v.giaVe "
                + "FROM ve v "
                + "JOIN chuyenbay c ON v.maChuyenBay = c.maChuyenBay "
                + "WHERE v.userId IS NULL";  // Lọc theo userId là NULL

        try (Connection connection = conn(); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                // Tạo một mảng String để lưu các giá trị từ ResultSet
                String[] row = new String[]{
                    resultSet.getString("maVe"),
                    resultSet.getString("maChuyenBay"),
                    resultSet.getString("diemDi"),
                    resultSet.getString("diemDen"),
                    resultSet.getString("gioDi"),
                    resultSet.getString("gioDen"),
                    resultSet.getString("maSoGhe"),
                    resultSet.getString("giaVe")
                };
                ticketMember.add(row);
            }
        }

        // In dữ liệu vé không có userId
        System.out.println("Dữ liệu vé không có userId:");
        for (String[] row : ticketMember) {
            System.out.println(String.join(", ", row));
        }

        return ticketMember;
    }

    public static List<String[]> fetchTicketOfMember(String idThe) throws ClassNotFoundException, SQLException {
        List<String[]> ticketMember = new ArrayList<>();
        String query = "SELECT v.maVe, v.maChuyenBay, c.diemDi, c.diemDen, c.gioDi, c.gioDen, v.maSoGhe, v.giaVe "
                + "FROM ve v "
                + "JOIN chuyenbay c ON v.maChuyenBay = c.maChuyenBay "
                + "JOIN card t ON v.userId = t.idUser "
                + "WHERE t.idThe = ?";  // Lọc theo idThe

        try (Connection connection = conn(); PreparedStatement statement = connection.prepareStatement(query)) {

            // Set tham số idThe vào PreparedStatement
            statement.setString(1, idThe);  // Truyền idThe vào câu truy vấn

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    // Tạo một mảng String để lưu các giá trị từ ResultSet
                    String[] row = new String[]{
                        resultSet.getString("maVe"),
                        resultSet.getString("maChuyenBay"),
                        resultSet.getString("diemDi"),
                        resultSet.getString("diemDen"),
                        resultSet.getString("gioDi"),
                        resultSet.getString("gioDen"),
                        resultSet.getString("maSoGhe"),
                        resultSet.getString("giaVe")
                    };
                    ticketMember.add(row);
                }
            }
        }

        // In dữ liệu vé của người dùng
        System.out.println("Dữ liệu vé của tôi:");
        for (String[] row : ticketMember) {
            System.out.println(String.join(", ", row));
        }

        return ticketMember;
    }

    public static boolean bookTicket(String idThe, String maVe) throws ClassNotFoundException, SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = conn();  // Thiết lập kết nối cơ sở dữ liệu

            // Bắt đầu transaction để đảm bảo tính toàn vẹn của dữ liệu
            connection.setAutoCommit(false);

            // Lấy idUser từ idThe
            String getUserQuery = "SELECT idUser FROM card WHERE idThe = ?";
            statement = connection.prepareStatement(getUserQuery);
            statement.setString(1, idThe);
            resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                // Nếu không tìm thấy userId với idThe, trả về false
                System.out.println("Thẻ không hợp lệ hoặc không tồn tại.");
                return false;
            }

            String idUser = resultSet.getString("idUser");  // Lấy idUser từ thẻ

            // Lấy thông tin vé (giaVe) từ bảng ve
            String getTicketQuery = "SELECT maVe, giaVe FROM ve WHERE maVe = ? AND userId IS NULL"; // Lọc vé chưa có userId
            statement = connection.prepareStatement(getTicketQuery);
            statement.setString(1, maVe);
            resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                // Nếu vé đã có người đặt hoặc không tồn tại
                System.out.println("Vé không còn hoặc đã được đặt.");
                return false;
            }

            // Lấy giá vé của vé cần đặt
            double giaVe = resultSet.getDouble("giaVe");

            // Cập nhật bảng vé, gán userId cho vé
            String bookTicketQuery = "UPDATE ve SET userId = ? WHERE maVe = ?";
            statement = connection.prepareStatement(bookTicketQuery);
            statement.setString(1, idUser);
            statement.setString(2, maVe);
            statement.executeUpdate();  // Cập nhật userId cho vé

            // Cập nhật số dư của người dùng
            String updateBalanceQuery = "UPDATE user SET soDu = soDu - ? WHERE idUser = ?";
            statement = connection.prepareStatement(updateBalanceQuery);
            statement.setDouble(1, giaVe);
            statement.setString(2, idUser);
            statement.executeUpdate();  // Cập nhật số dư người dùng

            // Thêm giao dịch vào bảng giaodich
            String addTransactionQuery = "INSERT INTO giaodich (idThe, time, soTien, loai, maVe) VALUES (?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(addTransactionQuery);
            statement.setString(1, idThe);  // Mã thẻ
            statement.setString(2, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));  // Thời gian hiện tại
            statement.setDouble(3, giaVe);  // Số tiền giao dịch
            statement.setString(4, "Đặt vé");  // Loại giao dịch
            statement.setString(5, maVe);  // Mã vé
            statement.executeUpdate();  // Thực hiện thêm giao dịch

            // Commit transaction để lưu tất cả thay đổi vào cơ sở dữ liệu
            connection.commit();

            System.out.println("Đặt vé thành công.");
            return true;  // Đặt vé thành công
        } catch (SQLException e) {
            if (connection != null) {
                // Rollback nếu có lỗi
                connection.rollback();
            }
            e.printStackTrace();
            return false;  // Xử lý lỗi nếu có
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.setAutoCommit(true);  // Quay lại chế độ tự động commit
                connection.close();
            }
        }
    }

    public static boolean cancelTicket(String idThe, String maVe) throws ClassNotFoundException, SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = conn();  // Thiết lập kết nối cơ sở dữ liệu
            connection.setAutoCommit(false);  // Bắt đầu giao dịch

            // Bước 1: Lấy idUser từ idThe
            String getUserQuery = "SELECT idUser FROM card WHERE idThe = ?";
            statement = connection.prepareStatement(getUserQuery);
            statement.setString(1, idThe);
            resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                System.out.println("Thẻ không hợp lệ hoặc không tồn tại.");
                return false;  // Nếu không tìm thấy thẻ, trả về false
            }

            String idUser = resultSet.getString("idUser");  // Lấy idUser từ thẻ

            // Bước 2: Kiểm tra xem vé có thuộc về người dùng này không (bảng ve)
            String getTicketQuery = "SELECT maVe, giaVe, userId FROM ve WHERE maVe = ? AND userId = ?";
            statement = connection.prepareStatement(getTicketQuery);
            statement.setString(1, maVe);
            statement.setString(2, idUser);  // Kiểm tra vé có thuộc về người dùng này không
            resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                System.out.println("Vé không tồn tại hoặc không thuộc về người dùng này.");
                return false;  // Nếu vé không thuộc về người dùng hoặc không tồn tại
            }

            // Bước 3: Lấy giá vé và hủy vé
            double giaVe = resultSet.getDouble("giaVe");

            // Cập nhật bảng vé: Hủy vé bằng cách đặt userId = NULL
            String cancelTicketQuery = "UPDATE ve SET userId = NULL WHERE maVe = ? AND userId = ?";
            statement = connection.prepareStatement(cancelTicketQuery);
            statement.setString(1, maVe);
            statement.setString(2, idUser);
            statement.executeUpdate();  // Cập nhật vé để hủy

            // Bước 4: Trả lại tiền cho người dùng (cập nhật số dư trong bảng user)
            String updateBalanceQuery = "UPDATE user SET soDu = soDu + ? WHERE idUser = ?";
            statement = connection.prepareStatement(updateBalanceQuery);
            statement.setDouble(1, giaVe);  // Cộng tiền vào số dư người dùng
            statement.setString(2, idUser);
            statement.executeUpdate();  // Cập nhật số dư người dùng

            // Bước 5: Thêm giao dịch vào bảng giaodich (loại "Hủy vé")
            String addTransactionQuery = "INSERT INTO giaodich (idThe, time, soTien, loai, maVe) VALUES (?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(addTransactionQuery);
            statement.setString(1, idThe);  // Mã thẻ
            statement.setString(2, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));  // Thời gian hiện tại
            statement.setDouble(3, giaVe);  // Số tiền giao dịch (giá vé)
            statement.setString(4, "Hủy vé");  // Loại giao dịch là "Hủy vé"
            statement.setString(5, maVe);  // Mã vé
            statement.executeUpdate();  // Thực hiện thêm giao dịch

            // Commit transaction để lưu tất cả thay đổi vào cơ sở dữ liệu
            connection.commit();

            System.out.println("Hủy vé thành công và đã hoàn tiền.");
            return true;  // Hủy vé thành công
        } catch (SQLException e) {
            if (connection != null) {
                // Rollback nếu có lỗi
                connection.rollback();
            }
            e.printStackTrace();
            return false;  // Nếu có lỗi, trả về false
        } finally {
            // Đảm bảo đóng kết nối và các tài nguyên
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.setAutoCommit(true);  // Quay lại chế độ tự động commit
                connection.close();
            }
        }
    }

    public static boolean rechargeMoney(String idThe, double amount) throws ClassNotFoundException, SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = conn();  // Thiết lập kết nối cơ sở dữ liệu
            connection.setAutoCommit(false);  // Bắt đầu giao dịch

            // Bước 1: Lấy idUser từ idThe
            String getUserQuery = "SELECT idUser FROM card WHERE idThe = ?";
            statement = connection.prepareStatement(getUserQuery);
            statement.setString(1, idThe);
            resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                System.out.println("Thẻ không hợp lệ hoặc không tồn tại.");
                return false;  // Nếu không tìm thấy thẻ, trả về false
            }

            String idUser = resultSet.getString("idUser");  // Lấy idUser từ thẻ

            // Bước 2: Cập nhật số dư người dùng trong bảng user
            String updateBalanceQuery = "UPDATE user SET soDu = soDu + ? WHERE idUser = ?";
            statement = connection.prepareStatement(updateBalanceQuery);
            statement.setDouble(1, amount);  // Cộng số tiền vào số dư người dùng
            statement.setString(2, idUser);
            statement.executeUpdate();  // Cập nhật số dư người dùng

            // Bước 3: Thêm giao dịch vào bảng giaodich
            String addTransactionQuery = "INSERT INTO giaodich (idThe, time, soTien, loai, maVe) VALUES (?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(addTransactionQuery);
            statement.setString(1, idThe);  // Mã thẻ
            statement.setString(2, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));  // Thời gian hiện tại
            statement.setDouble(3, amount);  // Số tiền giao dịch
            statement.setString(4, "Nạp tiền");  // Loại giao dịch là "Nạp tiền"
            statement.setString(5, null);  // `maVe` là null vì đây là giao dịch nạp tiền
            statement.executeUpdate();  // Thực hiện thêm giao dịch

            // Commit transaction để lưu tất cả thay đổi vào cơ sở dữ liệu
            connection.commit();

            System.out.println("Nạp tiền thành công.");
            return true;  // Nạp tiền thành công
        } catch (SQLException e) {
            if (connection != null) {
                // Rollback nếu có lỗi
                connection.rollback();
            }
            e.printStackTrace();
            return false;  // Nếu có lỗi, trả về false
        } finally {
            // Đảm bảo đóng kết nối và các tài nguyên
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.setAutoCommit(true);  // Quay lại chế độ tự động commit
                connection.close();
            }
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

        try (Connection connection = conn(); PreparedStatement statement = connection.prepareStatement(query);) {

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

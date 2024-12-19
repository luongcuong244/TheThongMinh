/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package thethongminh.user;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.smartcardio.CardException;
import javax.smartcardio.ResponseAPDU;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import thethongminh.database.DataConnection;
import thethongminh.model.Constants;
import thethongminh.model.User;
import thethongminh.utils.CardManager;
import thethongminh.utils.CardUtils;
import thethongminh.utils.ImageUtils;
import thethongminh.view.UserInfoEditingDialog;

/**
 *
 * @author nlcuong
 */
public class Home extends javax.swing.JFrame {

    private User user = new User("Vũ Xuân Nam", "10-11-2002", "HN", "0123456789", null);
    private int userMoney = 2500000;
    private boolean canEditUserInfo = false;
    String cardId;

    CardManager cardManager;
    private static ResponseAPDU response;

    private int selectedRowMyTicketTable = -1;
    private int selectedRowTicketTable = -1;

    /**
     * Creates new form MainFrame
     */
    public Home() {
        initComponents();
        setLocationRelativeTo(null);

        cardManager = CardManager.getInstance();
        getUserData();

        updateMoneyUI();

        edtName.setEditable(false);
        edtDateOfBirth.setEditable(false);
        edtPhoneNumber.setEditable(false);
        edtAddressCard.setEditable(false);

        if (Login.image != null) {
            jLabel4.setIcon(new ImageIcon(Login.image));
        }
        updateUserInfo(user);
        setDataForTransactionHistoryTable();
        setDataForFlightHistoryTable();
        setDataForFlightTable();
        setDataForBookTicketTable();
        setDataForMyTicketTable();
    }

    public void setDataForTransactionHistoryTable() {
        try {

            List<String[]> transactionHistoryData = DataConnection.getTransactionHistoryByCardId("001");

            Vector<String> columnNames = new Vector<String>();
            columnNames.add("Mã giao dịch");
            columnNames.add("Mã thẻ");
            columnNames.add("Thời gian");
            columnNames.add("Số tiền");
            columnNames.add("Loại");
            columnNames.add("Mã vé");

            Vector<Vector<String>> data = new Vector<Vector<String>>();
            for (int i = 0; i < transactionHistoryData.size(); i++) {
                String[] item = transactionHistoryData.get(i);
                Vector<String> row = new Vector<String>();
                row.add(item[0]);
                row.add(item[1]);
                row.add(item[2]);
                row.add(item[3]);
                row.add(item[4]);
                row.add(item[5]);
                data.add(row);
            }

            DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            if (jTableTransactionHistory != null) {
                jTableTransactionHistory.setModel(tableModel);
            };
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setDataForFlightHistoryTable() {
        try {
            List<String[]> flightHistoryData = DataConnection.fetchFlightHistory();

            Vector<String> columnNames = new Vector<String>();
            columnNames.add("Giờ đặt");
            columnNames.add("Giờ đi");
            columnNames.add("Giờ đến");
            columnNames.add("Điểm đến");
            columnNames.add("Điểm đi");
            columnNames.add("Giá vé");

            Vector<Vector<String>> data = new Vector<Vector<String>>();
            for (int i = 0; i < flightHistoryData.size(); i++) {
                String[] item = flightHistoryData.get(i);
                Vector<String> row = new Vector<String>();
                row.add(item[0]);
                row.add(item[1]);
                row.add(item[2]);
                row.add(item[3]);
                row.add(item[4]);
                row.add(item[5]);
                data.add(row);
            }

            DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            if (jTableFlightHistory != null) {
                jTableFlightHistory.setModel(tableModel);
            };
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setDataForFlightTable() {
        try {
            List<String[]> myTicketData = DataConnection.fetchAllFlights();

            Vector<String> columnNames = new Vector<String>();
            columnNames.add("Mã chuyến bay");
            columnNames.add("Điểm đi");
            columnNames.add("Điểm đến");
            columnNames.add("Giờ đi");
            columnNames.add("Giờ đến");

            Vector<Vector<String>> data = new Vector<Vector<String>>();
            for (int i = 0; i < myTicketData.size(); i++) {
                String[] item = myTicketData.get(i);
                Vector<String> row = new Vector<String>();
                row.add(item[0]);
                row.add(item[1]);
                row.add(item[2]);
                row.add(item[3]);
                row.add(item[4]);
                data.add(row);
            }

            DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            if (jTableMyTicket2 != null) {
                jTableMyTicket2.setModel(tableModel);
            };
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setDataForBookTicketTable() {
        try {
            List<String[]> myTicketData = DataConnection.fetchTickets();

            Vector<String> columnNames = new Vector<String>();
            columnNames.add("Mã vé");
            columnNames.add("Mã chuyến bay");
            columnNames.add("Điểm đi");
            columnNames.add("Điểm đến");
            columnNames.add("Giờ đi");
            columnNames.add("Giờ đến");
            columnNames.add("Mã số ghế");
            columnNames.add("Giá vé");

            Vector<Vector<String>> data = new Vector<Vector<String>>();
            for (int i = 0; i < myTicketData.size(); i++) {
                String[] item = myTicketData.get(i);
                Vector<String> row = new Vector<String>();
                row.add(item[0]);
                row.add(item[1]);
                row.add(item[2]);
                row.add(item[3]);
                row.add(item[4]);
                row.add(item[5]);
                row.add(item[6]);
                row.add(item[7]);
                data.add(row);
            }

            DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            if (jTableMyTicket1 != null) {
                jTableMyTicket1.setModel(tableModel);
                jTableMyTicket1.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        if (event.getButton() != MouseEvent.BUTTON1) {
                            return;
                        }
                        if (event.getClickCount() == 1) {
                            Point pnt = event.getPoint();
                            int row = jTableMyTicket1.rowAtPoint(pnt);
                            selectedRowTicketTable = row;
                            return;
                        }
                    }
                });
            };
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setDataForMyTicketTable() {
        try {
            List<String[]> myTicketData = DataConnection.fetchTicketOfMember("001");

            Vector<String> columnNames = new Vector<String>();
            columnNames.add("Mã vé");
            columnNames.add("Mã chuyến bay");
            columnNames.add("Điểm đi");
            columnNames.add("Điểm đến");
            columnNames.add("Giờ đi");
            columnNames.add("Giờ đến");
            columnNames.add("Mã số ghế");
            columnNames.add("Giá vé");

            Vector<Vector<String>> data = new Vector<Vector<String>>();
            for (int i = 0; i < myTicketData.size(); i++) {
                String[] item = myTicketData.get(i);
                Vector<String> row = new Vector<String>();
                row.add(item[0]);
                row.add(item[1]);
                row.add(item[2]);
                row.add(item[3]);
                row.add(item[4]);
                row.add(item[5]);
                row.add(item[6]);
                row.add(item[7]);
                data.add(row);
            }

            DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            if (jTableMyTicket != null) {
                jTableMyTicket.setModel(tableModel);
                jTableMyTicket.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        if (event.getButton() != MouseEvent.BUTTON1) {
                            return;
                        }
                        if (event.getClickCount() == 1) {
                            Point pnt = event.getPoint();
                            int row = jTableMyTicket.rowAtPoint(pnt);
                            selectedRowMyTicketTable = row;
                            return;
                        }
                    }
                });
            };
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelRound1 = new thethongminh.view.PanelRound();
        panelRound2 = new thethongminh.view.PanelRound();
        panelRound3 = new thethongminh.view.PanelRound();
        panelRound4 = new thethongminh.view.PanelRound();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        edtName = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        edtDateOfBirth = new javax.swing.JTextField();
        jPanel9 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        edtPhoneNumber = new javax.swing.JTextField();
        jPanel10 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        edtAddressCard = new javax.swing.JTextField();
        panelRound5 = new thethongminh.view.PanelRound();
        jLabel4 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableTransactionHistory = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jTextField2 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableFlightHistory = new javax.swing.JTable();
        jPanel14 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jTextField7 = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTableMyTicket2 = new javax.swing.JTable();
        jPanel16 = new javax.swing.JPanel();
        btnCheckin = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jTextField6 = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTableMyTicket1 = new javax.swing.JTable();
        jPanel13 = new javax.swing.JPanel();
        btnBookTicket = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jTextField5 = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableMyTicket = new javax.swing.JTable();
        jButton7 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        btnCancelTicket = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        confirmPassInput = new javax.swing.JPasswordField();
        newPassInput = new javax.swing.JPasswordField();
        jLabel6 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(244, 247, 250));

        jTabbedPane1.setBackground(new java.awt.Color(244, 247, 250));

        jPanel1.setBackground(new java.awt.Color(244, 247, 250));

        jPanel7.setBackground(new java.awt.Color(244, 247, 250));
        jPanel7.setBorder(new MatteBorder(0, 0, 1, 0, new java.awt.Color(222, 222, 222)));

        jLabel3.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(106, 106, 106));
        jLabel3.setText("Họ và tên");

        edtName.setBackground(new java.awt.Color(244, 247, 250));
        edtName.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        edtName.setText("Vũ Xuân Nam");
        edtName.setBorder(null);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(218, 218, 218)
                .addComponent(edtName, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(edtName, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        jPanel8.setBackground(new java.awt.Color(244, 247, 250));
        jPanel8.setBorder(new MatteBorder(0, 0, 1, 0, new java.awt.Color(222, 222, 222)));

        jLabel5.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(106, 106, 106));
        jLabel5.setText("Ngày sinh");

        edtDateOfBirth.setBackground(new java.awt.Color(244, 247, 250));
        edtDateOfBirth.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        edtDateOfBirth.setText("10/11/2002");
        edtDateOfBirth.setBorder(null);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addGap(216, 216, 216)
                .addComponent(edtDateOfBirth, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(261, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(edtDateOfBirth)
                .addContainerGap())
        );

        jPanel9.setBackground(new java.awt.Color(244, 247, 250));
        jPanel9.setBorder(new MatteBorder(0, 0, 1, 0, new java.awt.Color(222, 222, 222)));

        jLabel7.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(106, 106, 106));
        jLabel7.setText("Số điện thoại");

        edtPhoneNumber.setBackground(new java.awt.Color(244, 247, 250));
        edtPhoneNumber.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        edtPhoneNumber.setText("097 207 56 32");
        edtPhoneNumber.setBorder(null);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addGap(189, 189, 189)
                .addComponent(edtPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(edtPhoneNumber)
                .addContainerGap())
        );

        jPanel10.setBackground(new java.awt.Color(244, 247, 250));
        jPanel10.setBorder(new MatteBorder(0, 0, 1, 0, new java.awt.Color(222, 222, 222)));

        jLabel9.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(106, 106, 106));
        jLabel9.setText("Địa chỉ");

        edtAddressCard.setBackground(new java.awt.Color(244, 247, 250));
        edtAddressCard.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        edtAddressCard.setText("097 207 56 32");
        edtAddressCard.setBorder(null);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addGap(238, 238, 238)
                .addComponent(edtAddressCard, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(edtAddressCard)
                .addContainerGap())
        );

        panelRound5.setBackground(new java.awt.Color(217, 217, 217));
        panelRound5.setRoundBottomLeft(150);
        panelRound5.setRoundBottomRight(150);
        panelRound5.setRoundTopLeft(150);
        panelRound5.setRoundTopRight(150);

        javax.swing.GroupLayout panelRound5Layout = new javax.swing.GroupLayout(panelRound5);
        panelRound5.setLayout(panelRound5Layout);
        panelRound5Layout.setHorizontalGroup(
            panelRound5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
        );
        panelRound5Layout.setVerticalGroup(
            panelRound5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
        );

        jButton1.setText("Sửa");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(98, 98, 98)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(378, 378, 378)
                        .addComponent(panelRound5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(126, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(15, 15, 15))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelRound5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62))
        );

        jTabbedPane1.addTab("Thông tin cá nhân", jPanel1);

        jPanel2.setBackground(new java.awt.Color(244, 247, 250));

        jTextField1.setBackground(new java.awt.Color(231, 251, 255));
        jTextField1.setToolTipText("");
        jTextField1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(107, 228, 255)));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/thethongminh/image/ic_search.png"))); // NOI18N

        jTableTransactionHistory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"001", "001", "20:00 - 29/07/2024", "50.000 VNĐ", "Nạp tiền", ""},
                {"002", "001", "21:04 - 23/06/2024", "2.000.000 VNĐ", "Mua vé", "003"},
                {"003", "002", "13:34 - 12/04/2024", "5.000.000 VNĐ", "Nạp tiền", null},
                {null, null, "", null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Mã giao dịch", "Mã thẻ", "Thời gian", "Số tiền", "Loại", "Mã vé"
            }
        ));
        jScrollPane1.setViewportView(jTableTransactionHistory);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addContainerGap(591, Short.MAX_VALUE))
            .addComponent(jScrollPane1)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField1))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Lịch sử giao dịch", jPanel2);

        jPanel3.setBackground(new java.awt.Color(244, 247, 250));

        jTextField2.setBackground(new java.awt.Color(231, 251, 255));
        jTextField2.setToolTipText("");
        jTextField2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(107, 228, 255)));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/thethongminh/image/ic_search.png"))); // NOI18N

        jTableFlightHistory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"09:57:34  23/08/2023", "09:57:34  23/08/2023", "09:57:34  23/08/2023", "Hà Nội", "Đà Nẵng", "2.400.000 VNĐ"},
                {null, null, null, "", null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Giờ đặt", "Giờ đi", "Giờ đến", "Điểm đến", "Điểm đi", "Giá vé"
            }
        ));
        jScrollPane2.setViewportView(jTableFlightHistory);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addContainerGap(591, Short.MAX_VALUE))
            .addComponent(jScrollPane2)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField2))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Lịch sử chuyến bay", jPanel3);

        jPanel15.setBackground(new java.awt.Color(244, 247, 250));

        jTextField7.setBackground(new java.awt.Color(231, 251, 255));
        jTextField7.setToolTipText("");
        jTextField7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(107, 228, 255)));

        jLabel23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/thethongminh/image/ic_search.png"))); // NOI18N

        jTableMyTicket2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"09:57:34  23/08/2023", "09:57:34  23/08/2023", "09:57:34  23/08/2023", "Hà Nội", "Đà Nẵng", "2.400.000 VNĐ"},
                {null, null, null, "", null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Ngày đặt", "Ngày đi", "Ngày đến", "Điểm đi", "Điểm đến", "Giá vé"
            }
        ));
        jScrollPane6.setViewportView(jTableMyTicket2);

        jPanel16.setBackground(new java.awt.Color(244, 247, 250));

        btnCheckin.setBackground(new java.awt.Color(0, 153, 153));
        btnCheckin.setForeground(new java.awt.Color(255, 255, 255));
        btnCheckin.setText("Checkin");
        btnCheckin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckinActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addGap(0, 126, Short.MAX_VALUE)
                .addComponent(btnCheckin))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnCheckin, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 994, Short.MAX_VALUE)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField7)
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 994, Short.MAX_VALUE)
            .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel14Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 465, Short.MAX_VALUE)
            .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel14Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("Chuyến bay", jPanel14);

        jPanel12.setBackground(new java.awt.Color(244, 247, 250));

        jTextField6.setBackground(new java.awt.Color(231, 251, 255));
        jTextField6.setToolTipText("");
        jTextField6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(107, 228, 255)));

        jLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/thethongminh/image/ic_search.png"))); // NOI18N

        jTableMyTicket1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"09:57:34  23/08/2023", "09:57:34  23/08/2023", "09:57:34  23/08/2023", "Hà Nội", "Đà Nẵng", "2.400.000 VNĐ"},
                {null, null, null, "", null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Ngày đặt", "Ngày đi", "Ngày đến", "Điểm đi", "Điểm đến", "Giá vé"
            }
        ));
        jScrollPane4.setViewportView(jTableMyTicket1);

        jPanel13.setBackground(new java.awt.Color(244, 247, 250));

        btnBookTicket.setBackground(new java.awt.Color(0, 153, 153));
        btnBookTicket.setForeground(new java.awt.Color(255, 255, 255));
        btnBookTicket.setText("Đặt vé");
        btnBookTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBookTicketActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addGap(0, 126, Short.MAX_VALUE)
                .addComponent(btnBookTicket))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnBookTicket, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 994, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField6)
                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 994, Short.MAX_VALUE)
            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel11Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 465, Short.MAX_VALUE)
            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel11Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("Đặt vé", jPanel11);

        jPanel6.setBackground(new java.awt.Color(244, 247, 250));

        jTextField5.setBackground(new java.awt.Color(231, 251, 255));
        jTextField5.setToolTipText("");
        jTextField5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(107, 228, 255)));

        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/thethongminh/image/ic_search.png"))); // NOI18N

        jTableMyTicket.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"09:57:34  23/08/2023", "09:57:34  23/08/2023", "09:57:34  23/08/2023", "Hà Nội", "Đà Nẵng", "2.400.000 VNĐ"},
                {null, null, null, "", null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Ngày đặt", "Ngày đi", "Ngày đến", "Điểm đi", "Điểm đến", "Giá vé"
            }
        ));
        jScrollPane3.setViewportView(jTableMyTicket);

        jButton7.setBackground(new java.awt.Color(0, 153, 153));
        jButton7.setForeground(new java.awt.Color(255, 255, 255));
        jButton7.setText("Đặt vé");

        jPanel4.setBackground(new java.awt.Color(244, 247, 250));

        btnCancelTicket.setBackground(new java.awt.Color(0, 153, 153));
        btnCancelTicket.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelTicket.setText("Huỷ vé");
        btnCancelTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelTicketActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(0, 126, Short.MAX_VALUE)
                .addComponent(btnCancelTicket))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnCancelTicket, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 994, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addGap(461, 461, 461)
                    .addComponent(jButton7)
                    .addContainerGap(461, Short.MAX_VALUE)))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField5)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE))
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addGap(215, 215, 215)
                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(215, 215, 215)))
        );

        jTabbedPane1.addTab("Vé của tôi", jPanel6);

        jPanel5.setBackground(new java.awt.Color(244, 247, 250));

        jLabel15.setFont(new java.awt.Font("Helvetica Neue", 0, 36)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(44, 62, 80));
        jLabel15.setText("Đổi mã PIN");

        jLabel16.setText("Nhập mã pin mới");

        jLabel19.setText("Xác nhận mã pin mới");

        jButton4.setBackground(new java.awt.Color(0, 123, 255));
        jButton4.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Xác nhận");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(331, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(82, 82, 82)
                                .addComponent(jLabel15))
                            .addComponent(jLabel19)
                            .addComponent(confirmPassInput, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(newPassInput, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(288, 288, 288))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(414, 414, 414))))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(jLabel15)
                .addGap(28, 28, 28)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(newPassInput, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(confirmPassInput, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(115, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Mã pin", jPanel5);

        jLabel6.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        jLabel6.setText("250.000 VNĐ");

        jButton2.setText("Nạp tiền");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addGap(18, 18, 18)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(2, 12, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 496, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        JTextField amountOfMoney = new JTextField();

        ((AbstractDocument) amountOfMoney.getDocument()).setDocumentFilter(new DocumentFilter() {
            Pattern regEx = Pattern.compile("\\d+");

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                Matcher matcher = regEx.matcher(text);
                if (!matcher.matches()) {
                    return;
                }
                super.replace(fb, offset, length, text, attrs);
            }
        });

        final JComponent[] inputs = new JComponent[]{
            new JLabel("Nạp tiền"), amountOfMoney,};
        int result = JOptionPane.showConfirmDialog(null, inputs, "Nạp tiền", JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                if (amountOfMoney.getText().trim().isEmpty()) {
                    return;
                }
                int amount = Integer.parseInt(amountOfMoney.getText().trim());
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "Số tiền phải lớn hơn 0!",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                DataConnection.rechargeMoney("001", amount);
                updateMoneyUI();
                setDataForTransactionHistoryTable();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        new UserInfoEditingDialog(this, user).setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnCancelTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelTicketActionPerformed
        if (selectedRowMyTicketTable > -1) {
            try {
                String maVe = jTableMyTicket.getValueAt(selectedRowMyTicketTable, 0).toString();
                boolean isSuccessfully = DataConnection.cancelTicket("001", maVe);
                
                if (isSuccessfully) {
                    JOptionPane.showMessageDialog(null,
                            "Hủy vé thành công",
                            "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                    setDataForMyTicketTable();
                    setDataForBookTicketTable();
                    setDataForTransactionHistoryTable();
                    updateMoneyUI();
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Đặt vé thất bại",
                            "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                selectedRowMyTicketTable = -1;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnCancelTicketActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        char[] passInput = newPassInput.getPassword();
        char[] confirmInput = confirmPassInput.getPassword();

        String newPin = new String(passInput).trim();
        String confirmPin = new String(confirmInput).trim();

        System.out.println("newPin: " + newPin);
        System.out.println("confirmPin: " + confirmPin);

        if (newPin.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Mã PIN mới không được để trống!",
                    "Thông báo",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (confirmPin.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Mã PIN xác nhận không được để trống!",
                    "Thông báo",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (newPin.length() != 6 || confirmPin.length() != 6) {
            JOptionPane.showMessageDialog(null,
                    "Mã PIN phải có đúng 6 chữ số!",
                    "Thông báo",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPin.matches("\\d{6}") || !confirmPin.matches("\\d{6}")) {
            JOptionPane.showMessageDialog(null,
                    "Mã PIN chỉ được chứa các chữ số từ 0 đến 9!",
                    "Thông báo",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPin.equals(confirmPin)) {
            JOptionPane.showMessageDialog(null,
                    "Mã PIN xác nhận không khớp!",
                    "Thông báo",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

//        try {
//
//            response = cardManager.sendApduCommand(Constants.CLA, Constants.INS_UPDATE_PIN, Constants.PARAM_DEFAULT, Constants.PARAM_DEFAULT, CardUtils.convertPinToByte(newPin));
//            int sw = response.getSW();
//            System.out.println("sw UPDATE_PIN res: " + CardUtils.convertSWToHex(sw));
//            System.out.println("data UPDATE_PIN res: " + CardUtils.converBytesToHex(response.getData()));
//
//            switch (sw) {
//                case Constants.SW_INVALID_PIN_LENGTH:
//                    JOptionPane.showMessageDialog(null,
//                            "Độ dài PIN không chính xác!",
//                            "Thông báo",
//                            JOptionPane.ERROR_MESSAGE);
//                    break;
//                case Constants.SW_PIN_NOT_VALIDATED:
//                    JOptionPane.showMessageDialog(null,
//                            "Thẻ chưa xác thực",
//                            "Thông báo",
//                            JOptionPane.ERROR_MESSAGE);
//                    break;
//                case Constants.SW_SUCCESS:
//                    JOptionPane.showMessageDialog(null,
//                            "Mã PIN đã được cập nhật",
//                            "Thông báo",
//                            JOptionPane.INFORMATION_MESSAGE);
//                    break;
//                default:
//                    System.out.println("Unknown res SW");
//                    break;
//            }
//
//        } catch (CardException ex) {
//            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void btnBookTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBookTicketActionPerformed
        if (selectedRowTicketTable > -1) {
            try {
                String maVe = jTableMyTicket1.getValueAt(selectedRowTicketTable, 0).toString();
                boolean isSuccessfully = DataConnection.bookTicket("001", maVe);

                if (isSuccessfully) {
                    JOptionPane.showMessageDialog(null,
                            "Đặt vé thành công",
                            "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                    setDataForMyTicketTable();
                    setDataForBookTicketTable();
                    setDataForTransactionHistoryTable();
                    updateMoneyUI();
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Đặt vé thất bại",
                            "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                }

                selectedRowTicketTable = -1;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnBookTicketActionPerformed

    private void btnCheckinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckinActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCheckinActionPerformed

    public void getUserData() {
//        try {
//            response = cardManager.sendApduCommand(Constants.CLA, Constants.INS_PRINT, Constants.PARAM_DEFAULT, Constants.PARAM_DEFAULT, null);
//            int sw = response.getSW();
//            byte[] resData = response.getData();
//            System.out.println("sw INS_PRINT res: " + CardUtils.convertSWToHex(sw));
//            System.out.println("data INS_PRINT res: " + CardUtils.converBytesToHex(resData));
//            if(sw == Constants.SW_SUCCESS) {
//                byte[][] splitedData = CardUtils.splitData(resData, Constants.SEPARATOR, 6);
//
//                byte[] cardIdByte = splitedData[0];
//                byte[] nameByte = splitedData[1];
//                byte[] birthdayByte = splitedData[2];
//                byte[] addressByte = splitedData[3];
//                byte[] phoneByte = splitedData[4];
//                byte[] imageLenByte = splitedData[5];
//                
//                System.out.println("sw cardIdByte res: " + CardUtils.convertBytesToStringUTF8(cardIdByte));
//                System.out.println("sw nameByte res: " + CardUtils.convertBytesToStringUTF8(nameByte));
//                System.out.println("sw birthdayByte res: " + CardUtils.convertBytesToStringUTF8(birthdayByte));
//                System.out.println("sw addressByte res: " + CardUtils.convertBytesToStringUTF8(addressByte));
//                System.out.println("sw phoneByte res: " + CardUtils.convertBytesToStringUTF8(phoneByte));
//                System.out.println("sw imageLenByte res: " + CardUtils.convertBytesToStringUTF8(imageLenByte));
//                
//                ByteBuffer buffer = ByteBuffer.wrap(imageLenByte);
//                short imageLen = buffer.getShort();
//                System.out.println("image length: " + imageLen);
//                byte[] imageByte = new byte[imageLen];  
//                int offset = 0;
//                int count = 0;
//                while(imageLen > 0) {
//                    int ne = imageLen > Constants.MAX_LENGTH ? Constants.MAX_LENGTH : imageLen;
//                    response = cardManager.sendApduCommand(Constants.CLA, Constants.INS_PRINT, Constants.PARAM_P1_PRINT, count, ne);
//                    sw = response.getSW();
//                    if(sw == Constants.SW_SUCCESS) {
//                        System.arraycopy(response.getData(), 0, imageByte, offset, ne);
//                        offset += ne;
//                        imageLen -= ne;
//                        count++;
//                    }else {
//                        System.out.println("Error");
//                        return;
//                    }
//                }
//                System.out.println("image byte: " + CardUtils.converBytesToHex(imageByte));
//                this.user = new User(CardUtils.convertBytesToStringUTF8(nameByte),
//                            CardUtils.convertBytesToStringUTF8(birthdayByte), 
//                                CardUtils.convertBytesToStringUTF8(addressByte),
//                            CardUtils.convertBytesToStringUTF8(phoneByte),
//                            ImageUtils.byteArrayToBufferedImage(imageByte));
//            }
//
//        } catch (CardException ex) {
//            Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
//        }

    }

    public void updateUserInfo(User user) {
        this.user = user;
        edtName.setText(user.getName());
        edtDateOfBirth.setText(user.getDateOfBirth());
        edtPhoneNumber.setText(user.getPhoneNumber());
        edtAddressCard.setText(user.getAddress());
        if (user.getAvatar() != null) {
            jLabel4.setIcon(new ImageIcon(user.getAvatar()));
        }
    }

    public void updateMoneyUI() {
        try {
            userMoney = (int) DataConnection.getBalanceByCard("001");
        } catch (Exception e) {

        }
        NumberFormat formatter = NumberFormat.getInstance(Locale.getDefault());
        jLabel6.setText(formatter.format(userMoney) + " VNĐ");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Home().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBookTicket;
    private javax.swing.JButton btnCancelTicket;
    private javax.swing.JButton btnCheckin;
    private javax.swing.JPasswordField confirmPassInput;
    private javax.swing.JTextField edtAddressCard;
    private javax.swing.JTextField edtDateOfBirth;
    private javax.swing.JTextField edtName;
    private javax.swing.JTextField edtPhoneNumber;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableFlightHistory;
    private javax.swing.JTable jTableMyTicket;
    private javax.swing.JTable jTableMyTicket1;
    private javax.swing.JTable jTableMyTicket2;
    private javax.swing.JTable jTableTransactionHistory;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JPasswordField newPassInput;
    private thethongminh.view.PanelRound panelRound1;
    private thethongminh.view.PanelRound panelRound2;
    private thethongminh.view.PanelRound panelRound3;
    private thethongminh.view.PanelRound panelRound4;
    private thethongminh.view.PanelRound panelRound5;
    // End of variables declaration//GEN-END:variables
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thethongminh.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import thethongminh.model.User;
import thethongminh.user.Home;
import thethongminh.user.Login;
import thethongminh.utils.ImageUtils;

/**
 *
 * @author ADMIN
 */
public class UserInfoEditingDialog extends JDialog {

    private int WIDTH = 360, HEIGHT = 380;
    private JPanel container;
    private int fontSize = 12;

    private JComboBox jCBCategory;
    private JTextField edtName, edtDateOfBirth, edtPhoneNumber, edtIdentifyCard;
    private BufferedImage avatar;
    private JButton btnConfirm, btnCancel;

    private User productItem;
    private Home homeFrame;

    public UserInfoEditingDialog(Home serviceTab, User user) {
        this.homeFrame = serviceTab;
        this.productItem = user;
        Toolkit toolkit = this.getToolkit();
        Dimension dimension = toolkit.getScreenSize();
        this.setBounds(dimension.width / 2 - WIDTH / 2, dimension.height / 2 - HEIGHT / 2, WIDTH, HEIGHT);
        this.setModal(true);
        this.setTitle("Sửa");
        this.setResizable(false);
        this.setLayout(new BorderLayout());
        container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        this.add(container, BorderLayout.CENTER);

        JPanel jPanelProductName = new JPanel();
        JLabel label1 = new JLabel("Tên");
        label1.setFont(label1.getFont().deriveFont(Font.BOLD, fontSize));
        label1.setPreferredSize(new Dimension(100, 25));
        edtName = new JTextField(user != null ? user.getName() : "");
        edtName.setFont(edtName.getFont().deriveFont(fontSize));
        edtName.setPreferredSize(new Dimension(200, 25));
        jPanelProductName.add(label1);
        jPanelProductName.add(edtName);

        JPanel jPanelDateOfBirth = new JPanel();
        JLabel label2 = new JLabel("Ngày sinh");
        label2.setFont(label1.getFont().deriveFont(Font.BOLD, fontSize));
        label2.setPreferredSize(new Dimension(100, 25));
        edtDateOfBirth = new JTextField(user != null ? user.getDateOfBirth(): "");
        edtDateOfBirth.setFont(edtDateOfBirth.getFont().deriveFont(fontSize));
        edtDateOfBirth.setPreferredSize(new Dimension(200, 25));
        jPanelDateOfBirth.add(label2);
        jPanelDateOfBirth.add(edtDateOfBirth);
        
        JPanel jPanelPhoneNumber = new JPanel();
        JLabel label3 = new JLabel("Số điện thoại");
        label3.setFont(label1.getFont().deriveFont(Font.BOLD, fontSize));
        label3.setPreferredSize(new Dimension(100, 25));
        edtPhoneNumber = new JTextField(user != null ? user.getPhoneNumber(): "");
        edtPhoneNumber.setFont(edtPhoneNumber.getFont().deriveFont(fontSize));
        edtPhoneNumber.setPreferredSize(new Dimension(200, 25));
        jPanelPhoneNumber.add(label3);
        jPanelPhoneNumber.add(edtPhoneNumber);
        
        JPanel jPanelIdentifyCard = new JPanel();
        JLabel label4 = new JLabel("Căn cước");
        label4.setFont(label1.getFont().deriveFont(Font.BOLD, fontSize));
        label4.setPreferredSize(new Dimension(100, 25));
        edtIdentifyCard = new JTextField(user.getIdentityCard());
        edtIdentifyCard.setFont(edtIdentifyCard.getFont().deriveFont(fontSize));
        edtIdentifyCard.setPreferredSize(new Dimension(200, 25));
        jPanelIdentifyCard.add(label4);
        jPanelIdentifyCard.add(edtIdentifyCard);

        JPanel jPanelAvatar = new JPanel();
        JLabel label5 = new JLabel("Ảnh");
        label5.setFont(label1.getFont().deriveFont(Font.BOLD, fontSize));
        label5.setPreferredSize(new Dimension(100, 25));
        jPanelAvatar.add(label5);
        JPanel avatarContainer = new JPanel();
        avatarContainer.setPreferredSize(new Dimension(140, 140));
        avatarContainer.setBackground(Color.lightGray);
        avatar = user.getAvatar();
        JLabel avatarLabel;
        if (avatar != null) {
            avatarLabel = new JLabel(new ImageIcon(avatar));
        } else {
            avatarLabel = new JLabel("");
        }
        avatarLabel.setPreferredSize(new Dimension(140, 140));
        avatarLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Khi người dùng nhấp chuột, mở cửa sổ chọn file
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Chọn một hình ảnh");
                
                // Chỉ lọc các file hình ảnh (JPG, PNG, GIF)
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "gif");
                fileChooser.setFileFilter(filter);
                
                // Mở cửa sổ chọn file
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    try {
                        // Lấy file hình ảnh người dùng chọn
                        File selectedFile = fileChooser.getSelectedFile();
                        
                        // Load the image from a file (replace with your image path)
                        BufferedImage originalImage = ImageIO.read(selectedFile);
                        
                        // Resize the image to a new width and height
                        int newWidth = 140; // Set desired width
                        int newHeight = 140; // Set desired height
                        BufferedImage resizedImage = ImageUtils.resizeImage(originalImage, newWidth, newHeight);
                        avatarLabel.setIcon(new ImageIcon(resizedImage));
                        avatar = resizedImage;
                    } catch (IOException ex) {
                        Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        avatarContainer.add(avatarLabel);
        
        jPanelAvatar.add(avatarContainer);
        
        
        JPanel jPanelButtonContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        setupConfirmButton();
        setupCancelButton();
        jPanelButtonContainer.add(btnConfirm);
        jPanelButtonContainer.add(btnCancel);

        container.add(jPanelProductName);
        container.add(jPanelDateOfBirth);
        container.add(jPanelPhoneNumber);
        container.add(jPanelIdentifyCard);
        container.add(jPanelAvatar);
        container.add(jPanelButtonContainer);
    }

    private void setupConfirmButton() {
        btnConfirm = new JButton("Xong");
        btnConfirm.setPreferredSize(new Dimension(70, 25));
        btnConfirm.setBackground(Color.green);
        btnConfirm.setForeground(Color.black);
        btnConfirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String name = edtName.getText().trim().toString();
                String dateOfBirth = edtDateOfBirth.getText().trim().toString();
                String phoneNumber = edtPhoneNumber.getText().trim().toString();
                String identifyCard = edtIdentifyCard.getText().trim().toString();

                if (name.isEmpty() || dateOfBirth.isEmpty() || phoneNumber.isEmpty() || identifyCard.isEmpty()) {
                    JOptionPane.showMessageDialog(UserInfoEditingDialog.this,
                            "Không được bỏ trống!",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                homeFrame.updateUserInfo(new User(name, dateOfBirth, phoneNumber, identifyCard, avatar));
                UserInfoEditingDialog.this.setVisible(false);
            }
        });
    }

    private void setupCancelButton() {
        btnCancel = new JButton("Hủy");
        btnCancel.setPreferredSize(new Dimension(70, 25));
        btnCancel.setBackground(Color.gray);
        btnCancel.setForeground(Color.white);
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UserInfoEditingDialog.this.setVisible(false);
            }
        });
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package thethongminh.model;

/**
 *
 * @author ACER
 */
public class Constants {

    public static final byte[] AID_APPLET = {(byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x04, (byte) 0x01};
    public static final byte CLA = (byte) 0x00;

    // INS
    public static final byte INS_VERIFY_PIN = (byte) 0x01;  // Verify PIN
    public static final byte INS_UPDATE_PIN = (byte) 0x02;  // Update PIN
    public static final byte INS_RESET_PIN = (byte) 0x03;    // Reset PIN
    public static final byte INS_UNBLOCK_PIN = (byte) 0x04;   // Unblock PIN
    public static final byte INS_ENTER = (byte) 0x05; // Init data
    
    public static final byte INS_PRINT_LENGTH = (byte) 0x06;
    public static final byte INS_PRINT = (byte) 0x07;
    public static final byte INS_CHANGE = (byte) 0x08;

    // SW
    public static final int SW_SUCCESS = 0x9000; // Success
    public static final int SW_AID_NOT_FOUND = 0x6A82; // AID not exist
    public static final int SW_VERIFICATION_FAILED = 0x7700; // Wrong PIN
    public static final int SW_PIN_BLOCKED = 0x7701; // PIN block
    public static final int SW_PIN_VALIDATED = 0x7702; // PIN validate
    public static final int SW_PIN_NOT_VALIDATED = 0x7703; // PIN not validated
    public static final int SW_INVALID_PIN_LENGTH = 0x7704; //Wrong length
    public static final int SW_WRONG_LENGTH = 0x6700; 
    public static final int SW_DATA_INVALID = 0x6984; 

    // P1 - P2
    public static final int PARAM_DEFAULT = 0x00;
    public static final int PARAM_P1_PRINT = 0x01;

    // Separator
    public static final byte SEPARATOR = (byte) 0x2E;
    
    // Field ID
    public static final byte ID_NAME = (byte) 0x71;
    public static final byte ID_BIRTHDAY = (byte) 0x72;
    public static final byte ID_ADDRESS = (byte) 0x73;
    public static final byte ID_PHONE = (byte) 0x74;
    public static final byte ID_IMAGE = (byte) 0x75;
    
    // MAX LENGTH RECEIVE
    public static final int MAX_LENGTH = 8190;
    

}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package thethongminh.utils;

import javax.smartcardio.*;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author ACER
 */
public class CardManager {

    private static CardManager instance;
    private CardChannel cardChannel;

    public CardManager() {
    }

    ;
    
    public static CardManager getInstance() {
        if (instance == null) {
            instance = new CardManager();
        }
        return instance;
    }

    public boolean getCardChannel() {
        try {

            TerminalFactory factory = TerminalFactory.getDefault();
            List<CardTerminal> terminals = factory.terminals().list();
            CardTerminal terminal = terminals.get(0);
            Card card = terminal.connect("T=1");
            cardChannel = card.getBasicChannel();
            System.out.println("getCardChannel success");
            return true;
        } catch (Exception e) {
            System.out.println("getCardChannel ~ error: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Kết nối thất bại",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public ResponseAPDU sendApduCommand(int cla, int ins, int p1, int p2, byte[] data) throws CardException {
        try {
            CommandAPDU apdu;

            if (data == null || data.length == 0) {
                apdu = new CommandAPDU(cla, ins, p1, p2);
            } else {
                apdu = new CommandAPDU(cla, ins, p1, p2, data);
            }

            System.out.println("APDU command: " + CardUtils.converBytesToHex(apdu.getBytes()));

            ResponseAPDU response = cardChannel.transmit(apdu);

            return response;
        } catch (CardException e) {
            System.err.println("sendApduCommand ~ error: " + e.getMessage());
            throw e;
        }
    }
    
    public ResponseAPDU sendApduCommand(int cla, int ins, int p1, int p2, int ne) throws CardException {
        try {
            CommandAPDU apdu;

            apdu = new CommandAPDU(cla, ins, p1, p2, ne);

            System.out.println("APDU command: " + CardUtils.converBytesToHex(apdu.getBytes()));

            ResponseAPDU response = cardChannel.transmit(apdu);

            return response;
        } catch (CardException e) {
            System.err.println("sendApduCommand ~ error: " + e.getMessage());
            throw e;
        }
    }

    public void disconnect() {
        if (cardChannel != null) {
            try {
                cardChannel.getCard().disconnect(false);
            } catch (CardException e) {
                System.out.println("disconnect ~ error: " + e.getMessage());
            }
        }
    }

}

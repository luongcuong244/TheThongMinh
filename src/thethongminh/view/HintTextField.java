/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package thethongminh.view;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
/**
 *
 * @author nlcuong
 */
public class HintTextField extends JTextField implements FocusListener
{

    private String hint;

    public HintTextField ()
    {
        this("");
    }

    public HintTextField(final String hint)
    {
        setHint(hint);
        super.addFocusListener(this);
    }

    public void setHint(String hint)
    {
        this.hint = hint;
        setUI(new HintTextFieldUI(hint, true));
        //setText(this.hint);
    }


    public void focusGained(FocusEvent e)
    {
        if(this.getText().length() == 0)
        {
            super.setText("");
        }
    }

    public void focusLost(FocusEvent e)
    {
        if(this.getText().length() == 0)
        {
            setHint(hint);
        }
    }

    public String getText()
    {
        String typed = super.getText();
        return typed.equals(hint)?"":typed;
    }
}

class HintTextFieldUI extends javax.swing.plaf.basic.BasicTextFieldUI implements FocusListener
{

    private String hint;
    private boolean hideOnFocus;
    private Color color;

    public Color getColor()
    {
        return color;
    }

    public void setColor(Color color)
    {
        this.color = color;
        repaint();
    }

    private void repaint()
    {
        if(getComponent() != null)
        {
            getComponent().repaint();
        }
    }

    public boolean isHideOnFocus()
    {
        return hideOnFocus;
    }

    public void setHideOnFocus(boolean hideOnFocus)
    {
        this.hideOnFocus = hideOnFocus;
        repaint();
    }

    public String getHint()
    {
        return hint;
    }

    public void setHint(String hint)
    {
        this.hint = hint;
        repaint();
    }

    public HintTextFieldUI(String hint)
    {
        this(hint, false);
    }

    public HintTextFieldUI(String hint, boolean hideOnFocus)
    {
        this(hint, hideOnFocus, null);
    }

    public HintTextFieldUI(String hint, boolean hideOnFocus, Color color)
    {
        this.hint = hint;
        this.hideOnFocus = hideOnFocus;
        this.color = color;
    }


    protected void paintSafely(Graphics g)
    {
        super.paintSafely(g);
        JTextComponent comp = getComponent();
        if(hint != null && comp.getText().length() == 0 && (!(hideOnFocus && comp.hasFocus())))
        {
            if(color != null)
            {
                g.setColor(color);
            }
            else
            {
                g.setColor(Color.gray);
            }
            int padding = (comp.getHeight() - comp.getFont().getSize()) / 2;
            g.drawString(hint, 5, comp.getHeight() - padding - 1);
        }
    }


    public void focusGained(FocusEvent e)
    {
        if(hideOnFocus) repaint();

    }


    public void focusLost(FocusEvent e)
    {
        if(hideOnFocus) repaint();
    }

    protected void installListeners()
    {
        super.installListeners();
        getComponent().addFocusListener(this);
    }

    protected void uninstallListeners()
    {
        super.uninstallListeners();
        getComponent().removeFocusListener(this);
    }
}

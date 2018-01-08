/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sysvap.gui.helper;

import java.awt.Color;
import java.awt.EventQueue;
import java.io.*;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

//public class SysGUIConsoleOutput extends OutputStream {
public class SysGUIConsoleOutput extends OutputStream {

    private JTextComponent textComponent;
    private Style style;
    private int typeMSG;

    public SysGUIConsoleOutput(JTextComponent textComponent, Style style, int typeMSG) {
        this.textComponent = textComponent;
        this.style = style;
        this.typeMSG = typeMSG;
    }

    public void println(String text) {
        print(text + "\n");
    }

    public void print(String text) {
        try {
            write(text.getBytes());
        } catch (IOException e) {
            //Faça o log da exceção aqui
        }
    }

    @Override
    public void write(final int b) throws IOException {
        write(new byte[]{(byte) b}, 0, 1);
    }

    @Override
    public void write(final byte[] b, final int off, final int len)
            throws IOException {
        final String str = new String(b, off, len);

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Style newStyle = null;
                    String msg = str;
                    if (typeMSG == SysConstants.MSG_PRINTOUT) {
                        if (str.startsWith(SysConstants.OUT_ALERT)) {
                            newStyle = style;
                            msg = msg.substring(SysConstants.OUT_ALERT.length(),msg.length());
                        }
                    } else {
                        newStyle = style;
                    }

                    textComponent.getDocument().insertString(textComponent.getDocument().getLength(), msg, newStyle);
                } catch (BadLocationException e) {
                    //Faça o log da exceção aqui
                }
            }
        });
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

    public static Style getErrorStyle(JTextPane txtOutput) {
        Style style = txtOutput.getStyledDocument().addStyle("error",
                null);
        StyleConstants.setForeground(style, Color.red);

        return style;
    }

    public static Style getAlertStyle(JTextPane txtOutput) {
        Style style = txtOutput.getStyledDocument().addStyle("alert",
                null);
        StyleConstants.setForeground(style, Color.blue);

        return style;
    }

    public static void redirectOutput(JTextPane txtOutput) {
        SysGUIConsoleOutput textOut = new SysGUIConsoleOutput(txtOutput, getAlertStyle(txtOutput), SysConstants.MSG_PRINTOUT);
        PrintStream outStream = new PrintStream(textOut, false);
        System.setOut(outStream);

        SysGUIConsoleOutput textErr = new SysGUIConsoleOutput(txtOutput, getErrorStyle(txtOutput), SysConstants.MSG_PRINTERR);
        PrintStream errStream = new PrintStream(textErr, true);
        System.setErr(errStream);
    }
}

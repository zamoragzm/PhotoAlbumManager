package photoalbum.ui;

import javax.swing.*;
import java.awt.*;

// Utility class for showing pop-ups to user
public class PopUps {

    // EFFECTS: displays given error message to user
    public static void errorPopup(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    // EFFECTS: displays Yes/No confirmation dialog with given message to user,
    //          returns true if user selects the "Yes" option, false otherwise
    public static boolean confirmPopup(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Confirm action",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}

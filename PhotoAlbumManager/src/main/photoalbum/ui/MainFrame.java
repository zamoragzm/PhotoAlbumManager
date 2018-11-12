package photoalbum.ui;

import photoalbum.photo.PhotoManager;
import photoalbum.photo.TagManager;
import photoalbum.utility.FileManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@SuppressWarnings("serial")
// The application's main frame
public class MainFrame extends JFrame {

    // The file manager
    private FileManager fileManager;

    // The underlying library
    private PhotoManager photoMan;
    private TagManager tagMan;

    // UI components
    private BrowsePanel browsePanel;
    private ThumbnailsPanel thumbnailsPanel;

    // EFFECTS: constructs main frame
    public MainFrame() {
        photoMan = new PhotoManager();
        tagMan = new TagManager();
        fileManager = new FileManager(this);
        browsePanel = new BrowsePanel(this);
        PhotoPanel photoPanel = new PhotoPanel(this);
        thumbnailsPanel = new ThumbnailsPanel(photoPanel, browsePanel);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);

                if (PopUps.confirmPopup(MainFrame.this, "Save metadata to file?"))
                    fileManager.writeMetadataToFile();

                dispose();
            }
        });

        getContentPane().add(browsePanel, BorderLayout.WEST);

        getContentPane()
                .add(new JSplitPane(JSplitPane.VERTICAL_SPLIT, thumbnailsPanel, photoPanel),
                        BorderLayout.CENTER);

        setJMenuBar(createMenuBar());
        populateLibrary();
        updateDisplayedPhotoSet();
        pack();
        setVisible(true);
    }

    public TagManager getTagManager() {
        return tagMan;
    }

    public PhotoManager getPhotoManager() {
        return photoMan;
    }

    FileManager getFileManager() {
        return fileManager;
    }

    // MODIFIES: this
    // EFFECTS:  updates the UI to display the set of photos selected in the browse panel
    void updateDisplayedPhotoSet() {
        // Ignore events fired during initialisation before the window is set up
        if (browsePanel == null)
            return;

        setTitle("Photo Library: " + browsePanel.getSelectionDescription());
        thumbnailsPanel.refresh();
    }

    // EFFECTS: returns menu bar
    private JMenuBar createMenuBar() {
        JMenuItem addPhotoMenuItem = new JMenuItem("Add Photos...");
        addPhotoMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                browsePanel.showAddPhotoDialog();
            }
        });

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.dispatchEvent(new WindowEvent(MainFrame.this, WindowEvent.WINDOW_CLOSING));
            }
        });

        JMenu fileMenu = new JMenu("File");
        fileMenu.add(addPhotoMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);

        return menuBar;
    }

    // MODIFIES: this
    // EFFECTS:  populates library with albums and photos read from disk
    private void populateLibrary() {
        fileManager.loadLibrary();
        browsePanel.refresh();
    }
}

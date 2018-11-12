package photoalbum.ui;

import photoalbum.photo.Album;
import photoalbum.photo.Photo;
import photoalbum.photo.Tag;
import photoalbum.photo.TagManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

// The panel for displaying a photo and managing its associated information
class PhotoPanel extends JPanel {
    private static final int BORDER = 40;
    private static final int IMAGE_PANEL_WIDTH = Photo.MAX_WIDTH + BORDER;
    private static final int IMAGE_PANEL_HEIGHT = Photo.MAX_HEIGHT + BORDER;
    private static final int INFO_PANEL_WIDTH = 200;
    private static final int INFO_PANEL_HEIGHT = 300;

    static final int TOTAL_WIDTH = IMAGE_PANEL_WIDTH + INFO_PANEL_WIDTH;

    private TagManager tagMan;
    private Photo selectedPhoto;

    private JPanel imagePanel;
    private JPanel infoPanel;
    private JLabel lblAdded;
    private JLabel lblAlbum;
    private JTextArea txtDescription;
    private JPanel descriptionBtnPanel;

    private java.util.List<JButton> editButtons;
    private Box imageEditBtnBox;

    private DefaultListModel<Tag> photoTagsModel;
    private JList<Tag> tagList;

    // EFFECTS: constructs photo panel
    PhotoPanel(final MainFrame mainFrame) {
        super(new BorderLayout());

        tagMan = mainFrame.getTagManager();
        imagePanel = new JPanel();
        imagePanel.setLayout(new BorderLayout());
        infoPanel = new JPanel();
        lblAdded = new JLabel();
        lblAlbum = new JLabel();
        txtDescription = new JTextArea();
        descriptionBtnPanel = new JPanel();
        photoTagsModel = new DefaultListModel<Tag>();
        tagList = new JList<Tag>(photoTagsModel);
        editButtons = new ArrayList<JButton>();

        imagePanel.setPreferredSize(new Dimension(IMAGE_PANEL_WIDTH, IMAGE_PANEL_HEIGHT));
        imageEditBtnBox = createImageEditButtons();

        JScrollPane scrollPane = new JScrollPane(imagePanel);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // Set up the info pane
        // Description

        JButton btnSaveDescription = new JButton("Save");
        btnSaveDescription.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedPhoto.setDescription(txtDescription.getText());
                refreshInfoPanel();
            }
        });

        JButton btnResetDescription = new JButton("Reset");
        btnResetDescription.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshInfoPanel();
            }
        });

        descriptionBtnPanel.add(btnSaveDescription);
        descriptionBtnPanel.add(btnResetDescription);

        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setFont(lblAlbum.getFont());

        txtDescription.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                descriptionBtnPanel.setVisible(true);
            }
        });

        // Tags

        JMenuItem removeTag = new JMenuItem("Remove");
        removeTag.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Tag tag = tagList.getSelectedValue();
                if (tag != null) {
                    selectedPhoto.removeTag(tag);
                    refreshInfoPanel();
                }
            }
        });

        JPopupMenu tagPopup = new JPopupMenu();
        tagPopup.add(removeTag);
        tagList.addMouseListener(new PopupMouseListener(tagList, tagPopup));

        JButton btnAddTag = new JButton("Add tag");
        btnAddTag.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tagName = JOptionPane
                        .showInputDialog(mainFrame, "Enter the name of the tag to add:");
                if (tagName != null) {
                    Tag tag = tagMan.findTag(tagName);
                    if (tag == null) {
                        PopUps.errorPopup(mainFrame, "The specified tag does not exist.");
                        return;
                    }
                    selectedPhoto.addTag(tag);
                    refreshInfoPanel();
                }
            }
        });

        // Add the components to the panel

        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        infoPanel.add(lblAdded);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(lblAlbum);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(Box.createVerticalStrut(5));

        JPanel descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel
                .add(new JLabel("Description:"), BorderLayout.NORTH);
        descriptionPanel.add(new JScrollPane(txtDescription),
                BorderLayout.CENTER);
        descriptionPanel.add(descriptionBtnPanel, BorderLayout.SOUTH);
        infoPanel.add(descriptionPanel);

        JPanel tagsPanel = new JPanel(new BorderLayout());
        tagsPanel.add(new JLabel("Tags:"), BorderLayout.NORTH);
        tagsPanel.add(new JScrollPane(tagList), BorderLayout.CENTER);
        tagsPanel.add(btnAddTag, BorderLayout.SOUTH);
        infoPanel.add(tagsPanel);

        // center everything
        for (Component c : infoPanel.getComponents())
            ((JComponent) c).setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.setPreferredSize(new Dimension(INFO_PANEL_WIDTH, INFO_PANEL_HEIGHT));
        add(infoPanel, BorderLayout.EAST);
    }

    // MODIFIES: this
    // EFFECTS:  display given photo and associated information in panel
    public void displayPhoto(Photo photo) {
        selectedPhoto = photo;

        // Un-focus the description text area
        imagePanel.requestFocusInWindow();

        // Add the image
        imagePanel.removeAll();
        if (photo != null) {
            imagePanel.add(new JLabel(new ImageIcon(photo.getImage())), BorderLayout.CENTER);
        } else {
            Box hBox = Box.createHorizontalBox();
            hBox.add(Box.createHorizontalGlue());
            hBox.add(new JLabel("No photo selected."));
            hBox.add(Box.createHorizontalGlue());
            imagePanel.add(hBox, BorderLayout.CENTER);
        }

        imagePanel.add(imageEditBtnBox, BorderLayout.SOUTH);

        // Update the info panel
        refreshInfoPanel();

        repaint();
        revalidate();
    }

    // MODIFIES: this
    // EFFECTS: returns box containing buttons for editing image
    private Box createImageEditButtons() {
        createEditButton("Flip Horizontal", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedPhoto.flipHorizontal();
                repaint();
                revalidate();
            }
        });

        createEditButton("Black & White", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedPhoto.toBlackAndWhite();
                repaint();
                revalidate();
            }
        });

        createEditButton("Blur", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedPhoto.blur();
                repaint();
                revalidate();
            }
        });

        createEditButton("Flip Vertically", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedPhoto.flipVertically();
                repaint();
                revalidate();
            }
        });

        Box hBox = Box.createHorizontalBox();
        hBox.setBorder(BorderFactory.createEtchedBorder());
        hBox.add(Box.createHorizontalGlue());

        for (JButton btn : editButtons)
            hBox.add(btn);

        hBox.add(Box.createHorizontalGlue());
        return hBox;
    }

    // MODIFIES: this
    // EFFECTS: creates edit button with given title and listener and adds it to list of edit buttons
    private void createEditButton(String title, ActionListener actionListener) {
        JButton btn = new JButton(title);
        btn.addActionListener(actionListener);
        editButtons.add(btn);
    }

    // MODIFIES: this
    // EFFECTS:  updates info panel to display information for selected photo
    private void refreshInfoPanel() {
        if (selectedPhoto != null) {
            infoPanel.setBorder(BorderFactory.createTitledBorder("Photo: " + selectedPhoto.getName()));
            lblAdded.setText("Created: "
                    + new SimpleDateFormat("EEE, d MMM yyyy").format(selectedPhoto.getDateCreated()));
            Album album = selectedPhoto.getAlbum();
            lblAlbum.setText("Album: " + (album == null ? "(none)" : album.getName()));

            txtDescription.setText(selectedPhoto.getDescription());
            descriptionBtnPanel.setVisible(false);

            photoTagsModel.clear();
            Set<Tag> tags = new TreeSet<Tag>(BrowsePanel.tagsByName);
            tags.addAll(selectedPhoto.getTags());
            for (Tag tag : tags)
                photoTagsModel.addElement(tag);

            enableEditButtons(true);
            infoPanel.setVisible(true);
        }
        else {
            enableEditButtons(false);
            infoPanel.setVisible(false);
        }
    }

    // MODIFIES: this
    // EFFECTS: sets enabled status of all edit buttons to isEnabled
    private void enableEditButtons(boolean isEnabled) {
        for (JButton btn : editButtons)
            btn.setEnabled(isEnabled);
    }
}

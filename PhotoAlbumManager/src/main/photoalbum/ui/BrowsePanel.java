package photoalbum.ui;

import photoalbum.photo.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

// The left-side browse-by-album/tag/date tabbed panel
class BrowsePanel extends JTabbedPane {
    private static final int WIDTH = 200;
    private static final int HEIGHT = 500;

    // Indices of the tabs
    private static final int ALBUM = 0;
    private static final int TAG = 1;
    private static final int DATE = 2;

    private MainFrame mainFrame;
    private PhotoManager photoMan;
    private TagManager tagMan;
    private AlbumListModel albumsModel;
    private TagListModel tagsModel;
    private JList<Album> albumList;
    private JList<Tag> tagList;
    private PhotoFileChooser photoFileChooser;
    private Date startDate;
    private Date endDate;
    private JLabel lblStartDate;
    private JLabel lblEndDate;

    // EFFECTS: constructs browse panel
    BrowsePanel(final MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        photoMan = mainFrame.getPhotoManager();
        tagMan = mainFrame.getTagManager();
        albumsModel = new AlbumListModel();
        tagsModel = new TagListModel();
        albumList = new JList<Album>(albumsModel);
        tagList = new JList<Tag>(tagsModel);
        photoFileChooser = new PhotoFileChooser();
        lblStartDate = new JLabel();
        lblEndDate = new JLabel();

        // Album right-click popup
        JMenuItem addPhotos = new JMenuItem("Add photos...");
        addPhotos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                photoFileChooser.showAddPhotoDialog();
            }
        });

        JPopupMenu albumPopup = new JPopupMenu();
        albumPopup.add(addPhotos);
        albumList.addMouseListener(new PopupMouseListener(albumList,
                albumPopup));

        // Tag right-click popup

        JMenuItem renameTag = new JMenuItem("Rename");
        renameTag.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Tag tag = tagList.getSelectedValue();
                if (tag != null) {
                    String newName = JOptionPane
                            .showInputDialog(mainFrame, "Enter a new name for the tag.");
                    if (newName != null) {
                        tagMan.renameTag(tag.getName(), newName);
                        tagsModel.refresh();
                    }
                }
            }
        });

        JMenuItem removeTag = new JMenuItem("Remove");
        removeTag.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Tag tag = tagList.getSelectedValue();
                if (tag != null
                        && PopUps.confirmPopup(mainFrame, "Remove tag " + tag
                        + " from the system?")) {
                    tagMan.removeTag(tag.toString());
                    tagsModel.refresh();
                }
            }
        });

        JPopupMenu tagPopup = new JPopupMenu();
        tagPopup.add(renameTag);
        tagPopup.add(removeTag);
        tagList.addMouseListener(new PopupMouseListener(tagList, tagPopup));

        // Update the displayed photo set when the user switches tabs
        addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JTabbedPane selectedPane = (JTabbedPane) e.getSource();
                int selectedIndex = selectedPane.getSelectedIndex();
                if (selectedIndex == DATE) {
                    setEndDate(new Date());
                }
                mainFrame.updateDisplayedPhotoSet();
            }
        });

        // Update the displayed photo set when the tag or album selection
        // changes
        ListSelectionListener updatePhotoDisplay = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                mainFrame.updateDisplayedPhotoSet();
            }
        };

        albumList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        albumList.addListSelectionListener(updatePhotoDisplay);
        tagList.addListSelectionListener(updatePhotoDisplay);

        JButton btnNewTag = new JButton("New Tag");
        btnNewTag.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tagName = JOptionPane
                        .showInputDialog(mainFrame, "Enter a name for the new tag.");
                if (tagName != null) {
                    Tag tag;
                    tag = tagMan.createTag(tagName);
                    tagsModel.refresh();
                    selectTag(tag);
                }
            }
        });

        // Date range controls

        final JButton btnChangeStartDate = new JButton("Change...");
        final JButton btnChangeEndDate = new JButton("Change...");

        ActionListener changeDate = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean start = e.getSource() == btnChangeStartDate;
                String newDateString = JOptionPane.showInputDialog(
                        mainFrame, "Enter a date:", new SimpleDateFormat()
                                .format(start ? startDate : endDate));
                if (newDateString != null) {
                    Date date;
                    DateFormat df = new SimpleDateFormat();
                    try {
                        date = df.parse(newDateString);
                    } catch (ParseException ex) {
                        PopUps.errorPopup(mainFrame, "Unrecognized date format.");
                        return;
                    }
                    if (start)
                        setStartDate(date);
                    else
                        setEndDate(date);
                    mainFrame.updateDisplayedPhotoSet();
                }
            }
        };

        btnChangeStartDate.addActionListener(changeDate);
        btnChangeEndDate.addActionListener(changeDate);

        setStartDate(new Date(0));
        setEndDate(new Date());

        JPanel albumsPanel = new JPanel(new BorderLayout());
        albumsPanel.add(new JScrollPane(albumList), BorderLayout.CENTER);
        albumsPanel.setName("Albums");

        JPanel tagsPanel = new JPanel(new BorderLayout());
        tagsPanel.add(new JScrollPane(tagList), BorderLayout.CENTER);
        tagsPanel.add(btnNewTag, BorderLayout.PAGE_END);
        tagsPanel.setName("Tags");

        JPanel datePanel = new JPanel();
        datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.Y_AXIS));
        datePanel.add(lblStartDate);
        datePanel.add(btnChangeStartDate);
        datePanel.add(Box.createVerticalStrut(5));
        datePanel.add(lblEndDate);
        datePanel.add(btnChangeEndDate);
        datePanel.setName("Date range");
        datePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add the tabs
        add(albumsPanel, ALBUM);
        add(tagsPanel, TAG);
        add(datePanel, DATE);

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }

    // EFFECTS: returns a displayable description of current selection
    String getSelectionDescription() {
        switch (getSelectedIndex()) {
            case ALBUM:
                Album album = getSelectedAlbum();
                return album == null ? "No album selected" : "Album: "
                        + album.getName();
            case TAG:
                return tagList.getSelectedValuesList().size() + " tag(s) selected";
            case DATE:
                return "Photos added between "
                        + new SimpleDateFormat().format(startDate) + " and "
                        + new SimpleDateFormat().format(endDate);
            default:
                return "";
        }
    }

    // EFFECTS: returns the set of photos corresponding to selected album, tags or date range
    Set<Photo> getPhotoSelection() {
        Set<Photo> photos = new TreeSet<Photo>(photosByDate);

        switch (getSelectedIndex()) {
            case ALBUM:
                Album album = getSelectedAlbum();
                if (album != null)
                    photos.addAll(album.getPhotos());
                break;
            case TAG:
                for (Tag tag : getSelectedTags())
                    photos.addAll(tag.getPhotos());
                break;
            case DATE:
                photos.addAll(photoMan.findPhotosInDateRange(startDate, endDate));
                break;
        }

        return photos;
    }

    // MODIFIES: this
    // EFFECTS:  refresh the album and tags models from the photo library;
    //           select first album in list (if any)
    void refresh() {
        albumsModel.refresh();
        tagsModel.refresh();

        if (albumList.getModel().getSize() > 0)
            albumList.setSelectedIndex(0);
    }

    // MODIFIES: this
    // EFFECTS:  display photo file chooser dialog
    void showAddPhotoDialog() {
        photoFileChooser.showAddPhotoDialog();
    }

    // EFFECTS: returns the index of the currently selected album, or -1
    //          if no album is selected
    private int getSelectedAlbumIndex() {
        return albumList.getSelectedIndex();
    }

    // EFFECTS: returns the currently selected album, or null if no album is selected
    private Album getSelectedAlbum() {
        Album album = albumList.getSelectedValue();
        return album == null ? null : album;
    }

    // EFFECTS: returns set of currently selected tags
    private Set<Tag> getSelectedTags() {
        Set<Tag> tags = new HashSet<Tag>();
        for (Object tle : tagList.getSelectedValuesList())
            tags.add((Tag) tle);
        return tags;
    }

    // MODIFIES: this
    // EFFECTS:  display albums tab and select the given album
    private void selectAlbum(Album album) {
        for (int i = 0; i < albumsModel.size(); i++) {
            if (albumsModel.get(i) == album) {
                albumList.setSelectedIndex(i);
                setSelectedIndex(ALBUM);
                return;
            }
        }
    }

    // MODIFIES: this
    // EFFECTS:  display the tags tab and select the given tag
    private void selectTag(Tag tag) {
        for (int i = 0; i < tagsModel.size(); i++) {
            if (tagsModel.get(i) == tag) {
                tagList.setSelectedIndex(i);
                setSelectedIndex(TAG);
                return;
            }
        }
    }

    // MODIFIES: this
    // EFFECTS:  set the start date to given date and update corresponding label
    private void setStartDate(Date date) {
        startDate = date;
        lblStartDate.setText("Start date: "
                + new SimpleDateFormat().format(date));
    }

    // MODIFIES: this
    // EFFECTS:  set the end date to given date and update corresponding label
    private void setEndDate(Date date) {
        endDate = date;
        lblEndDate.setText("End date: "
                + new SimpleDateFormat().format(date));
    }

    // Orders albums by name, case-insensitive
    private static Comparator<Album> albumsByName = new Comparator<Album>() {
        @Override
        public int compare(Album a, Album b) {
            return a.getName().compareToIgnoreCase(b.getName());
        }
    };

    // Orders tags by name, case-insensitive
    static Comparator<Tag> tagsByName = new Comparator<Tag>() {
        @Override
        public int compare(Tag a, Tag b) {
            return a.getName().compareToIgnoreCase(b.getName());
        }
    };

    // Orders photos by date
    private static Comparator<Photo> photosByDate = new Comparator<Photo>() {
        @Override
        public int compare(Photo a, Photo b) {
            return a.getDateCreated().compareTo(b.getDateCreated());
        }
    };

    /**
     * ListModel representing the tags in the system
     */
    private class TagListModel extends DefaultListModel<Tag> {
        public void refresh() {
            clear();
            Set<Tag> tags = new TreeSet<Tag>(tagsByName);
            tags.addAll(tagMan.getTags());
            for (Tag tag : tags)
                addElement(tag);
        }
    }

    /**
     * ListModel representing the albums in the system
     */
    private class AlbumListModel extends DefaultListModel<Album> {
        public void refresh() {
            clear();
            Set<Album> albums = new TreeSet<Album>(albumsByName);
            albums.addAll(photoMan.getAlbums());
            for (Album a : albums)
                addElement(a);
        }
    }

    // File chooser for selecting jpg files
    private class PhotoFileChooser extends JFileChooser {

        private JList<Album> albumsList;

        // EFFECTS: constructs file chooser with panel to show current set of albums
        public PhotoFileChooser() {
            albumsList = new JList<Album>(albumsModel);

            JPanel accessory = new JPanel(new BorderLayout());
            accessory.add(new JLabel("Add to album:"), BorderLayout.NORTH);
            accessory.add(new JScrollPane(albumsList), BorderLayout.CENTER);
            accessory.setPreferredSize(new Dimension(150, 275));
            accessory.setBorder(new EmptyBorder(0, 10, 0, 0));
            setAccessory(accessory);

            setMultiSelectionEnabled(true);
            setAcceptAllFileFilterUsed(false);
            setApproveButtonText("Add Photos");

            setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if (f.isDirectory())
                        return true;
                    String name = f.getName().toLowerCase();
                    return name.endsWith(".jpg") || name.endsWith(".jpeg");
                }

                @Override
                public String getDescription() {
                    return "JPEG images (*.jpg, *.jpeg)";
                }
            });
        }

        // EFFECTS: show the dialog and add the selected photo files to the selected album
        void showAddPhotoDialog() {
            if (albumsModel.size() == 0) {
                PopUps.errorPopup(mainFrame, "You must create an album before adding photos.");
                return;
            }

            albumsList.setSelectedIndex(getSelectedAlbumIndex());

            if (showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
                Album album = albumsList.getSelectedValue();

                mainFrame.getFileManager().loadFromFilesToAlbum(getSelectedFiles(), album);

                selectAlbum(album);
                mainFrame.updateDisplayedPhotoSet();
            }
        }
    }
}


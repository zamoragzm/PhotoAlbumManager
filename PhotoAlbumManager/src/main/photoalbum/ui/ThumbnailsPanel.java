package photoalbum.ui;

import photoalbum.photo.Photo;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;

// A scroll pane that contains thumbnail images
class ThumbnailsPanel extends JScrollPane {
    private static final int MAX_WIDTH = PhotoPanel.TOTAL_WIDTH;
    private static final int MAX_HEIGHT = 135;
    private static final int THUMBNAIL_BORDER = 2;
    private static final int DEFAULT_LABEL_WIDTH = 200;
    private static final int DEFAULT_LABEL_HEIGHT = 100;

    private PhotoPanel photoPanel;
    private BrowsePanel browsePanel;
    private JPanel thumbnailContainer;

    // EFFECTS: constructs thumbnails panel
    ThumbnailsPanel(PhotoPanel photoPanel, BrowsePanel browsePanel) {
        this.photoPanel = photoPanel;
        this.browsePanel = browsePanel;
        setMaximumSize(new Dimension(MAX_WIDTH, MAX_HEIGHT));
        setPreferredSize(getMaximumSize());
        setBorder(null);
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_ALWAYS);
    }

    // MODIFIES: this
    // EFFECTS:  display thumbnails for photos currently selected in browse panel;
    //           select first thumbnail (if any) and display corresponding photo in photo panel
    void refresh() {
        Set<Photo> photos = browsePanel.getPhotoSelection();
        thumbnailContainer = new JPanel();

        if (photos.isEmpty()) {
            JLabel label = new JLabel("No photos to display.");
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setPreferredSize(new Dimension(DEFAULT_LABEL_WIDTH, DEFAULT_LABEL_HEIGHT));
            thumbnailContainer.add(label);
        }

        LabelledThumbnail firstLabel = null;
        for (Photo p : photos) {
            thumbnailContainer.add((firstLabel == null ? firstLabel = new LabelledThumbnail(p) : new LabelledThumbnail(p)));
        }

        setViewportView(thumbnailContainer);
        repaint();
        revalidate();

        if (firstLabel != null) {
            firstLabel.setSelectedBorder(true);
            firstLabel.scrollRectToVisible(new Rectangle(0, 0, 5, 5));
        }

        photoPanel.displayPhoto(firstLabel == null ? null : firstLabel.photo);
    }

    // Panel in which thumbnail is displayed
    private class LabelledThumbnail extends JPanel {
        private Photo photo;

        // EFFECTS: constructs labelled thumbnail for given photo;
        //          when thumbnail is selected, corresponding photo is displayed in photo panel
        public LabelledThumbnail(Photo p) {
            super(new BorderLayout());
            photo = p;

            JLabel nameLabel = new JLabel(p.getName());
            nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(nameLabel, BorderLayout.SOUTH);
            add(new JLabel(new ImageIcon(p.getThumbnailImage())), BorderLayout.CENTER);
            setSelectedBorder(false);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    // update the thumbnail borders indicating the selected photo
                    for (Component c : ThumbnailsPanel.this.thumbnailContainer.getComponents())
                        ((LabelledThumbnail) c).setSelectedBorder(c == LabelledThumbnail.this);

                    // display the photo in the lower area
                    photoPanel.displayPhoto(photo);
                }
            });
        }

        // MODIFIES: this
        // EFFECTS:  if selected, set blue border, otherwise set empty border
        private void setSelectedBorder(boolean selected) {
            setBorder(selected ?
                    new CompoundBorder(new LineBorder(Color.BLUE, THUMBNAIL_BORDER),
                            new EmptyBorder(THUMBNAIL_BORDER, THUMBNAIL_BORDER, THUMBNAIL_BORDER, THUMBNAIL_BORDER))
                    : new EmptyBorder(2 * THUMBNAIL_BORDER, 2 * THUMBNAIL_BORDER, 2 * THUMBNAIL_BORDER, 2 * THUMBNAIL_BORDER));
        }
    }
}

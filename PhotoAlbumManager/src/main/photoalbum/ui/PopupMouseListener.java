package photoalbum.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.JPopupMenu;


// MouseListener that adds a right-click popup menu to JList items
class PopupMouseListener extends MouseAdapter {
	private JList list;
	private JPopupMenu menu;

    // EFFECTS: constructs popup menu listener for given list and popup menu
    PopupMouseListener(JList parentList, JPopupMenu popupMenu){
		list = parentList;
		menu = popupMenu;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		maybePopup(e);
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		maybePopup(e);
	}

    // MODIFIES: this
    // EFFECTS:  show popup menu if event is a popup trigger over an item in the list
	private void maybePopup(MouseEvent e){
		if(e.isPopupTrigger()){
			int index = list.locationToIndex(e.getPoint());
			if(index != -1){
				list.setSelectedIndex(index);
				menu.show(list, e.getX(), e.getY());
			}
		}
	}
}

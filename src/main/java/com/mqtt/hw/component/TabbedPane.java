package com.mqtt.hw.component;

import com.common.util.JarFileHWUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>TabbedPane<code>
 *
 * @author Tom
 */
public class TabbedPane extends JPanel {

    /**
     * The default Hand cursor.
     */
    public static final Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);
    /**
     * The default Text Cursor.
     */
    public static final Cursor DEFAULT_CURSOR = new Cursor(
            Cursor.DEFAULT_CURSOR);
    private static final long serialVersionUID = -9007068462231539973L;
    private static final String NAME = "TabbedPane";
    /***
     * 判断是否是在jar包中
     */
    private static boolean isInJar = JarFileHWUtil.isInJar(TabbedPane.class);
    private List<TabbedPaneListener> listeners = new ArrayList<TabbedPaneListener>();
    private JTabbedPane pane = null;
    private Icon closeInactiveButtonIcon;
    private Icon closeActiveButtonIcon;
    /***
     * 是否允许关闭标签页
     */
    private boolean closeEnabled = true;
    private int dragTabIndex = -1;

    public TabbedPane() {
        this(JTabbedPane.TOP);
    }

    public TabbedPane(final int type) {

        pane = new JTabbedPane(type);
        pane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);

        setLayout(new BorderLayout());
        add(pane);
        ChangeListener changeListener = new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent
                        .getSource();
                int index = sourceTabbedPane.getSelectedIndex();
                if (index >= 0) {
                    fireTabSelected(getTabAt(index), getTabAt(index)
                            .getComponent(), index);
                }
            }
        };
        pane.addChangeListener(changeListener);
        if (closeEnabled) {
            System.out.println("isInJar:" + isInJar);
            String picPath = null;
            if (isInJar) {
                picPath = "/com/mqtt/hw/img/close_white.png";
            } else {
                picPath = "../img/close_white.png";
            }
            URL url = TabbedPane.class.getResource(picPath);
            System.out.println("url  ../img/close_white.png:\n" + url);
//			if (url == null) {
//				url = this.getClass().getResource(
//						"/com/mqtt/hw/img/close_white.png");
//			}
            closeInactiveButtonIcon = new ImageIcon(url);
            if (isInJar) {
                picPath = "/com/mqtt/hw/img/close_dark.png";
            } else {
                picPath = "../img/close_dark.png";
            }
            url = TabbedPane.class.getResource(picPath);
//			if (ValueWidget.isNullOrEmpty(url)) {
//				url = this.getClass().getResource(
//						"/com/mqtt/hw/img/close_dark.png");
//			}
            closeActiveButtonIcon = new ImageIcon(url);
        }
    }

    public MyTab getTabContainingComponent(Component component) {
        for (Component comp : pane.getComponents()) {
            if (comp instanceof MyTab) {
                MyTab tab = (MyTab) comp;
                if (tab.getComponent() == component)
                    return tab;
            }
        }
        return null;
    }

    public MyTab addTab(String title, Icon icon, final Component component) {
        return addTab(title, icon, component, null);
    }

    public MyTab addTab(String title, Icon icon, final Component component,
                        String tip) {
        final MyTab tab = new MyTab(this, component);

        TabPanel tabpanel = new TabPanel(tab, title, icon);
        pane.addTab(null, null, tab, tip);

        pane.setTabComponentAt(pane.getTabCount() - 1, tabpanel);
        fireTabAdded(tab, component, getTabPosition(tab));

        return tab;
    }

    public MyTab getTabAt(int index) {
        return ((MyTab) pane.getComponentAt(index));
    }

    public int getTabPosition(MyTab tab) {
        return pane.indexOfComponent(tab);
    }

    public Component getComponentInTab(MyTab tab) {
        return tab.getComponent();
    }

    public void setIconAt(int index, Icon icon) {
        Component com = pane.getTabComponentAt(index);
        if (com instanceof TabPanel) {
            TabPanel panel = (TabPanel) com;
            panel.setIcon(icon);
        }
    }

    public void setTitleAt(int index, String title) {
        if (index > 0) {
            Component com = pane.getTabComponentAt(index);
            if (com instanceof TabPanel) {
                TabPanel panel = (TabPanel) com;
                panel.setTitle(title);
            }
        }
    }

    public void setTitleColorAt(int index, Color color) {

        Component com = pane.getTabComponentAt(index);
        if (com instanceof TabPanel) {
            TabPanel panel = (TabPanel) com;
            panel.setTitleColor(color);
        }
    }

    public void setTitleBoldAt(int index, boolean bold) {
        Component com = pane.getTabComponentAt(index);
        if (com instanceof TabPanel) {
            TabPanel panel = (TabPanel) com;
            panel.setTitleBold(bold);
        }
    }

    public void setTitleFontAt(int index, Font font) {
        Component com = pane.getTabComponentAt(index);
        if (com instanceof TabPanel) {
            TabPanel panel = (TabPanel) com;
            panel.setTitleFont(font);
        }
    }

    public Font getDefaultFontAt(int index) {
        Component com = pane.getTabComponentAt(index);
        if (com instanceof TabPanel) {
            TabPanel panel = (TabPanel) com;
            return panel.getDefaultFont();
        }
        return null;
    }

    public String getTitleAt(int index) {
        return pane.getTitleAt(index);
    }

    public int getTabCount() {
        return pane.getTabCount();
    }

    public int indexOfComponent(Component component) {
        for (Component comp : pane.getComponents()) {
            if (comp instanceof MyTab) {
                MyTab tab = (MyTab) comp;
                if (tab.getComponent() == component)
                    return pane.indexOfComponent(tab);
            }
        }
        return -1;
    }

    public Component getComponentAt(int index) {
        return ((MyTab) pane.getComponentAt(index)).getComponent();
    }

    public Component getTabComponentAt(int index) {
        return pane.getTabComponentAt(index);
    }

    public Component getTabComponentAt(MyTab tab) {
        return pane.getTabComponentAt(indexOfComponent(tab));
    }

    public Component getSelectedComponent() {
        if (pane.getSelectedComponent() instanceof MyTab) {
            MyTab tab = (MyTab) pane.getSelectedComponent();
            return tab.getComponent();
        }
        return null;
    }

    public void removeTabAt(int index) {
        if (index > 2) {
            pane.remove(index);
        }
    }

    public int getSelectedIndex() {
        return pane.getSelectedIndex();
    }

    public void setSelectedIndex(int index) {
        pane.setSelectedIndex(index);
    }

    public void setCloseButtonEnabled(boolean enable) {
        closeEnabled = enable;
    }

    public void addTabbedPaneListener(TabbedPaneListener listener) {
        listeners.add(listener);
    }

    public void removeTabbedPaneListener(TabbedPaneListener listener) {
        listeners.remove(listener);
    }

    public void fireTabAdded(MyTab tab, Component component, int index) {
        for (TabbedPaneListener listener : listeners) {
            listener.tabAdded(tab, component, index);
        }
    }

    public JPanel getMainPanel() {
        return this;
    }

    public void removeComponent(Component comp) {
        int index = indexOfComponent(comp);
        if (index != -1) {
            removeTabAt(index);
        }
    }

    public void fireTabRemoved(MyTab tab, Component component, int index) {
        for (TabbedPaneListener listener : listeners) {
            listener.tabRemoved(tab, component, index);
        }
    }

    public void fireTabSelected(MyTab tab, Component component, int index) {
        for (TabbedPaneListener listener : listeners) {
            listener.tabSelected(tab, component, index);
        }
    }

    public void allTabsClosed() {
        for (TabbedPaneListener listener : listeners) {
            listener.allTabsRemoved();
        }
    }

    public void close(MyTab tab) {
        System.out.println("关闭标签页");
        int closeTabNumber = pane.indexOfComponent(tab);
        pane.removeTabAt(closeTabNumber);
        fireTabRemoved(tab, tab.getComponent(), closeTabNumber);

        if (pane.getTabCount() == 0) {
            allTabsClosed();
        }
    }

    /**
     * Drag and Drop
     */
    public void enableDragAndDrop() {
        final DragSourceListener dsl = new DragSourceListener() {

            @Override
            public void dragDropEnd(DragSourceDropEvent event) {
                dragTabIndex = -1;
            }

            @Override
            public void dragEnter(DragSourceDragEvent event) {
                event.getDragSourceContext().setCursor(
                        DragSource.DefaultMoveDrop);
            }

            @Override
            public void dragExit(DragSourceEvent event) {
            }

            @Override
            public void dragOver(DragSourceDragEvent event) {
            }

            @Override
            public void dropActionChanged(DragSourceDragEvent event) {
            }

        };

        final Transferable t = new Transferable() {
            private final DataFlavor FLAVOR = new DataFlavor(
                    DataFlavor.javaJVMLocalObjectMimeType, NAME);

            @Override
            public Object getTransferData(DataFlavor flavor)
                    throws UnsupportedFlavorException, IOException {
                return pane;
            }

            @Override
            public DataFlavor[] getTransferDataFlavors() {
                DataFlavor[] f = new DataFlavor[1];
                f[0] = this.FLAVOR;
                return f;
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return flavor.getHumanPresentableName().equals(NAME);
            }

        };

        final DragGestureListener dgl = new DragGestureListener() {

            @Override
            public void dragGestureRecognized(DragGestureEvent event) {
                dragTabIndex = pane.indexAtLocation(event.getDragOrigin().x,
                        event.getDragOrigin().y);
                try {
                    event.startDrag(DragSource.DefaultMoveDrop, t, dsl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };

        final DropTargetListener dtl = new DropTargetListener() {

            @Override
            public void dragEnter(DropTargetDragEvent event) {
            }

            @Override
            public void dragExit(DropTargetEvent event) {
            }

            @Override
            public void dragOver(DropTargetDragEvent event) {
            }

            @Override
            public void drop(DropTargetDropEvent event) {
                int dropTabIndex = getTargetTabIndex(event.getLocation());
                moveTab(dragTabIndex, dropTabIndex);
            }

            @Override
            public void dropActionChanged(DropTargetDragEvent event) {
            }

        };

        new DropTarget(pane, DnDConstants.ACTION_COPY_OR_MOVE, dtl, true);
        new DragSource().createDefaultDragGestureRecognizer(pane,
                DnDConstants.ACTION_COPY_OR_MOVE, dgl);
    }

    private void moveTab(int prev, int next) {
        if (next < 0 || prev == next) {
            return;
        }
        Component cmp = pane.getComponentAt(prev);
        Component tab = pane.getTabComponentAt(prev);
        String str = pane.getTitleAt(prev);
        Icon icon = pane.getIconAt(prev);
        String tip = pane.getToolTipTextAt(prev);
        boolean flg = pane.isEnabledAt(prev);
        int tgtindex = prev > next ? next : next - 1;
        pane.remove(prev);
        pane.insertTab(str, icon, cmp, tip, tgtindex);
        pane.setEnabledAt(tgtindex, flg);

        if (flg)
            pane.setSelectedIndex(tgtindex);

        pane.setTabComponentAt(tgtindex, tab);
    }

    private int getTargetTabIndex(Point point) {
        Point tabPt = SwingUtilities.convertPoint(pane, point, pane);
        boolean isTB = pane.getTabPlacement() == JTabbedPane.TOP
                || pane.getTabPlacement() == JTabbedPane.BOTTOM;
        for (int i = 0; i < getTabCount(); i++) {
            Rectangle r = pane.getBoundsAt(i);
            if (isTB)
                r.setRect(r.x - r.width / 2, r.y, r.width, r.height);
            else
                r.setRect(r.x, r.y - r.height / 2, r.width, r.height);
            if (r.contains(tabPt))
                return i;
        }
        Rectangle r = pane.getBoundsAt(getTabCount() - 1);
        if (isTB)
            r.setRect(r.x + r.width / 2, r.y, r.width, r.height);
        else
            r.setRect(r.x, r.y + r.height / 2, r.width, r.height);
        return r.contains(tabPt) ? getTabCount() : -1;
    }

    private class TabPanel extends JPanel {
        private static final long serialVersionUID = -8249981130816404360L;
        private final BorderLayout layout = new BorderLayout();
        private final Font defaultFont = new Font("Dialog", Font.PLAIN, 11);
        private JLabel iconLabel;
        private JLabel titleLabel;

        public TabPanel(final MyTab tab, String title, Icon icon) {
            setOpaque(false);
            this.setLayout(layout);
            titleLabel = new JLabel(title);
            titleLabel.setFont(defaultFont);
            if (icon != null) {
                iconLabel = new JLabel(icon);
                add(iconLabel, BorderLayout.WEST);
            }
            add(titleLabel, BorderLayout.CENTER);
            if (closeEnabled) {
                final JLabel tabCloseButton = new JLabel(
                        closeInactiveButtonIcon);
                tabCloseButton.addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent mouseEvent) {
                        tabCloseButton.setIcon(closeActiveButtonIcon);
                        setCursor(HAND_CURSOR);
                    }

                    public void mouseExited(MouseEvent mouseEvent) {
                        tabCloseButton.setIcon(closeInactiveButtonIcon);
                        setCursor(DEFAULT_CURSOR);
                    }

                    public void mousePressed(MouseEvent mouseEvent) {
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                close(tab);
                            }
                        }.start();
                    }
                });
                add(tabCloseButton, BorderLayout.EAST);
            }
        }

        public Font getDefaultFont() {
            return defaultFont;
        }

        public void setIcon(Icon icon) {
            iconLabel.setIcon(icon);
        }

        public void setTitle(String title) {
            titleLabel.setText(title);
        }

        public void setTitleColor(Color color) {
            titleLabel.setForeground(color);
            titleLabel.validate();
            titleLabel.repaint();
        }

        public void setTitleBold(boolean bold) {
            Font oldFont = titleLabel.getFont();
            Font newFont;
            if (bold) {
                newFont = new Font(oldFont.getFontName(), Font.BOLD,
                        oldFont.getSize());
            } else {
                newFont = new Font(oldFont.getFontName(), Font.PLAIN,
                        oldFont.getSize());
            }

            titleLabel.setFont(newFont);
            titleLabel.validate();
            titleLabel.repaint();
        }

        public void setTitleFont(Font font) {
            titleLabel.setFont(font);
            titleLabel.validate();
            titleLabel.repaint();
        }

    }

}

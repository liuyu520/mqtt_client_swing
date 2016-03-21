package com.mqtt.hw.component;

import java.awt.*;

/**
 * 该通知是在状态改变时监听 <code>TabbedPane</code>
 *
 * @author Tom
 */
public interface TabbedPaneListener {

    /**
     * Called when a <code>Tab</code> is removed from the tab pane.
     *
     * @param tab       the tab that is being removed.
     * @param component the child component of the tab.
     * @param index     the index of the tab.
     */
    void tabRemoved(MyTab tab, Component component, int index);

    /**
     * Called when a new <code>Tab</code> has been added.
     *
     * @param tab       the new Tab added.
     * @param component the child component of the tab.
     * @param index     the index of the tab.
     */
    void tabAdded(MyTab tab, Component component, int index);

    /**
     * Called when the tab is selected by the user.
     *
     * @param tab       the Tab selected.
     * @param component the child component of the tab.
     * @param index     the index of the tab.
     */
    void tabSelected(MyTab tab, Component component, int index);

    /**
     * Called when all tabs are closed.
     */
    void allTabsRemoved();

    /**
     * Implementations of this method allow users to have more fine grained control
     * on closing of individual tabs depending on component state.
     *
     * @param tab       the Tab that will be closing.
     * @param component the child component of the tab.
     * @return true to allow closing, otherwise returning false will stop closing of this tab.
     */
    boolean canTabClose(MyTab tab, Component component);


}

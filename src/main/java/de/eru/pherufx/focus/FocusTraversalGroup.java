package de.eru.pherufx.focus;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author Philipp Bruckner
 */
public class FocusTraversalGroup {

    private final ObservableList<Node> nodes = FXCollections.observableArrayList();
    private final ObservableList<EventHandler<KeyEvent>> eventHandlers = FXCollections.observableArrayList();

    public FocusTraversalGroup() {
    }

    public FocusTraversalGroup(Node... nodes) {
        for (Node node : nodes) {
            add(node);
        }
    }

    public void add(Node node) {
        EventHandler<KeyEvent> handler = createEventHandlerForNode(node);
        node.addEventHandler(KeyEvent.KEY_PRESSED, handler);
        eventHandlers.add(handler);
        nodes.add(node);
    }

    public void add(int index, Node node) {
        EventHandler<KeyEvent> handler = createEventHandlerForNode(node);
        node.addEventHandler(KeyEvent.KEY_PRESSED, handler);
        eventHandlers.add(index, handler);
        nodes.add(index, node);
    }

    public void remove(int index) {
        nodes.get(index).removeEventHandler(KeyEvent.KEY_PRESSED, eventHandlers.get(index));
        nodes.remove(index);
        eventHandlers.remove(index);
    }

    public void remove(Node node) {
        int index = nodes.indexOf(node);
        remove(index);
    }

    private EventHandler<KeyEvent> createEventHandlerForNode(Node node) {
        return (KeyEvent event) -> {
            if (event.getCode() == KeyCode.TAB) {
                event.consume();
                int nextIndex;
                if (event.isShiftDown()) { //Shift -> Rückwärts
                    nextIndex = nodes.indexOf(node) - 1;
                    if (nextIndex < 0) {
                        nextIndex = nodes.size() - 1;
                    }
                } else { //Kein Shift -> Vorwärts
                    nextIndex = nodes.indexOf(node) + 1;
                    if (nextIndex >= nodes.size()) {
                        nextIndex = 0;
                    }
                }
                nodes.get(nextIndex).requestFocus();
            }
        };
    }

    public void clear() {
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).removeEventHandler(KeyEvent.KEY_PRESSED, eventHandlers.get(i));
        }
        nodes.clear();
        eventHandlers.clear();
    }
}

package de.pheru.fx.util.focus;

import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
    private final Map<Node, EventHandler<KeyEvent>> eventHandlers = FXCollections.observableHashMap();

    public FocusTraversalGroup() {
        nodes.addListener((ListChangeListener.Change<? extends Node> c) -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (Node addedNode : c.getAddedSubList()) {
                        EventHandler<KeyEvent> handler = createEventHandlerForNode(addedNode);
                        addedNode.addEventHandler(KeyEvent.KEY_PRESSED, handler);
                        eventHandlers.put(addedNode, handler);
                    }
                } else if (c.wasRemoved()) {
                    for (Node removedNode : c.getRemoved()) {
                        removedNode.removeEventHandler(KeyEvent.KEY_PRESSED, eventHandlers.get(removedNode));
                        eventHandlers.remove(removedNode);
                    }
                }
            }
        });
    }

    public FocusTraversalGroup(Node... nodes) {
        this();
        this.nodes.addAll(nodes);
    }

    protected EventHandler<KeyEvent> createEventHandlerForNode(Node node) {
        return (KeyEvent event) -> {
            if (event.getCode() == KeyCode.TAB) {
                event.consume();
                int index = nodes.indexOf(node);
                //requestFocus funktioniert nur, wenn die Node sichtbar und nicht disabled ist.
                //Damit auch bei unsichtbaren/disabled-ten Nodes der Fokus wechselt, müssen diese übersprungen werden.
                //Um eine eventuell auftretende Endlos-Schleife zu verhindern (wenn alle Nodes unsichtbar oder disabled sind),
                //wird die Schleife maximal so oft durchlaufen, wie Nodes in der Gruppe vorhanden sind.
                int tries = 0;
                do {
                    if (event.isShiftDown()) { //Shift -> Rückwärts
                        index--;
                        if (index < 0) {
                            index = nodes.size() - 1;
                        }
                    } else { //Kein Shift -> Vorwärts
                        index++;
                        if (index >= nodes.size()) {
                            index = 0;
                        }
                    }
                    nodes.get(index).requestFocus();
                    tries++;
                } while (!nodes.get(index).isFocused() && tries < nodes.size());
            }
        };
    }

    public void cleanUp() {
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).removeEventHandler(KeyEvent.KEY_PRESSED, eventHandlers.get(nodes.get(i)));
        }
        nodes.clear();
        eventHandlers.clear();
    }

    public ObservableList<Node> getNodes() {
        return nodes;
    }

//    public void setName(String name) {
//        FocusTraversal.validateGroup(name, this);
//        FocusTraversal.groups.put(name, this);
//    }
}

package de.pheru.fx.util.focus;

import javafx.beans.NamedArg;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashMap;
import java.util.Map;

public class FocusTraversalGroup {

    private static final String DEFAULT_GROUP_NAME = "FocusTraversalGroup_Default_";
    private static final Map<String, FocusTraversalGroup> groups = new HashMap<>();
    private static int defaultCounter = 0;

    private final ObservableList<Node> nodes = FXCollections.observableArrayList();
    private final Map<Node, EventHandler<KeyEvent>> eventHandlers = FXCollections.observableHashMap();

    private final String name;

    public FocusTraversalGroup() {
        this(DEFAULT_GROUP_NAME + defaultCounter);
        defaultCounter++;
    }

    public FocusTraversalGroup(final Node... nodes) {
        this(DEFAULT_GROUP_NAME + defaultCounter);
        defaultCounter++;
        this.nodes.addAll(nodes);
    }

    public FocusTraversalGroup(@NamedArg("name") final String name, final Node... nodes) {
        this(name);
        this.nodes.addAll(nodes);
    }

    public FocusTraversalGroup(@NamedArg("name") final String name) {
        this.name = name;
        groups.put(name, this);
        nodes.addListener((ListChangeListener.Change<? extends Node> c) -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (Node addedNode : c.getAddedSubList()) {
                        EventHandler<KeyEvent> handler = createEventHandlerForNode(addedNode);
                        FocusTraversal.getFocusableForNode(addedNode).addEventHandler(KeyEvent.KEY_PRESSED, handler);
                        eventHandlers.put(addedNode, handler);
                    }
                } else if (c.wasRemoved()) {
                    for (Node removedNode : c.getRemoved()) {
                        FocusTraversal.getFocusableForNode(removedNode).removeEventHandler(KeyEvent.KEY_PRESSED, eventHandlers.get(removedNode));
                        eventHandlers.remove(removedNode);
                    }
                }
            }
        });
    }

    private EventHandler<KeyEvent> createEventHandlerForNode(final Node node) {
        return (KeyEvent event) -> {
            if (event.getCode() == KeyCode.TAB) {
                event.consume();
                int index = nodes.indexOf(node);
                //requestFocus funktioniert nur, wenn die Node sichtbar und nicht disabled ist.
                //Damit auch bei unsichtbaren/disabled-ten Nodes der Fokus wechselt, müssen diese übersprungen werden.
                //Um eine eventuell auftretende Endlos-Schleife zu verhindern (wenn alle Nodes unsichtbar oder disabled sind),
                //wird die Schleife maximal so oft durchlaufen, wie Nodes in der Gruppe vorhanden sind.
                int tries = 0;
                Node focusableForNode;
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
                    focusableForNode = FocusTraversal.getFocusableForNode(nodes.get(index));
                    focusableForNode.requestFocus();
                    tries++;
                } while (!focusableForNode.isFocused() && tries < nodes.size());
            }
        };
    }

    private void cleanUp() {
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).removeEventHandler(KeyEvent.KEY_PRESSED, eventHandlers.get(nodes.get(i)));
        }
        nodes.clear();
        eventHandlers.clear();
    }

    public ObservableList<Node> getNodes() {
        return nodes;
    }

    public String getName() {
        return name;
    }

    public static FocusTraversalGroup getFocusTraversalGroup(final String name) {
        return groups.get(name);
    }

    public static void removeFocusTraversalGroup(final String name) {
        groups.get(name).cleanUp();
        groups.remove(name);
    }
}

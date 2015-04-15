package de.eru.pherufx.focus;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author Philipp Bruckner
 */
public final class FocusTraversal {

    protected static final Map<String, FocusTraversalGroup> groups = new HashMap<>();

    private FocusTraversal() {
    }

    public static FocusTraversalGroup createFocusTraversalGroup(String name) {
        FocusTraversalGroup group = new FocusTraversalGroup();
        validateGroup(name, group);
        groups.put(name, group);
        return group;
    }

    public static FocusTraversalGroup createFocusTraversalGroup(String name, Node... nodes) {
        FocusTraversalGroup group = createFocusTraversalGroup(name);
        group.getNodes().addAll(nodes);
        return group;
    }

    public static FocusTraversalGroup getFocusTraversalGroup(String name) {
        return groups.get(name);
    }

    public static void removeFocusTraversalGroup(String name) {
        groups.get(name).clear();
        groups.remove(name);
    }

    public static void createSingleFocusTraversal(Node from, Node to) {
        from.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (event.getCode() == KeyCode.TAB) {
                event.consume();
                to.requestFocus();
            }
        });
    }
    
    protected static void validateGroup(String name, FocusTraversalGroup group){
        if (FocusTraversal.groups.containsValue(group)) {
            throw new GroupAlreadyRegisteredException("The name of an already registered FocusTraversalGroup must not be changed!");
        } else if (FocusTraversal.groups.containsKey(name)) {
            throw new GroupNameAlreadyExistsException("FocusTraversalGroup-name \"" + name + "\" already exists!");
        }
    }
}

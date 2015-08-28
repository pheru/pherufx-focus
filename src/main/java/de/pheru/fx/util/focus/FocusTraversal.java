package de.pheru.fx.util.focus;

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
        groups.get(name).cleanUp();
        groups.remove(name);
    }

    public static void setSingleFocusTraversalForNode(Node node, Node focusForwardTarget, Node focusBackwardTarget) {
        node.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (event.getCode() == KeyCode.TAB) {
                event.consume();
                if (event.isShiftDown() && focusBackwardTarget != null) {
                    focusBackwardTarget.requestFocus();
                } else if (focusForwardTarget != null) {
                    focusForwardTarget.requestFocus();
                }
            }
        });
    }

    public static void setTabKeyEventHandlerForNode(Node node, Runnable tabForward, Runnable tabBackwards) {
        node.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (event.getCode() == KeyCode.TAB) {
                event.consume();
                if (event.isShiftDown() && tabBackwards != null) {
                    tabBackwards.run();
                } else if (tabForward != null) {
                    tabForward.run();
                 }
            }
        });
    }

    protected static void validateGroup(String name, FocusTraversalGroup group) {
        if (FocusTraversal.groups.containsValue(group)) {
            throw new GroupAlreadyRegisteredException("The name of an already registered FocusTraversalGroup must not be changed!");
        } else if (FocusTraversal.groups.containsKey(name)) {
            throw new GroupNameAlreadyExistsException("FocusTraversalGroup-name \"" + name + "\" already exists!");
        }
    }
}

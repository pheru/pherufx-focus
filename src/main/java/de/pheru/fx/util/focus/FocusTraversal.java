package de.pheru.fx.util.focus;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author Philipp Bruckner
 */
public final class FocusTraversal {

    private FocusTraversal() {
    }

    public static void setSingleFocusTraversalForNode(Node node, Node focusForwardTarget, Node focusBackwardTarget) {
        getFocusableForNode(node).addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (event.getCode() == KeyCode.TAB) {
                event.consume();
                if (event.isShiftDown() && focusBackwardTarget != null) {
                    getFocusableForNode(focusBackwardTarget).requestFocus();
                } else if (focusForwardTarget != null) {
                    getFocusableForNode(focusForwardTarget).requestFocus();
                }
            }
        });
    }

    public static void setTabKeyEventHandlerForNode(Node node, Runnable tabForward, Runnable tabBackwards) {
        getFocusableForNode(node).addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
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

    protected static Node getFocusableForNode(Node node) {
        if (node instanceof ComboBox) {
            ComboBox comboBox = (ComboBox) node;
            if (comboBox.isEditable()) {
                return comboBox.getEditor();
            }
        }
        return node;
    }
}

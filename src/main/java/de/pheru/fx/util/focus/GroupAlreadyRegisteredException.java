package de.pheru.fx.util.focus;

/**
 *
 * @author Philipp Bruckner
 */
public class GroupAlreadyRegisteredException extends RuntimeException {

    public GroupAlreadyRegisteredException() {
    }

    public GroupAlreadyRegisteredException(String msg) {
        super(msg);
    }
}

package de.pheru.fx.focus;

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

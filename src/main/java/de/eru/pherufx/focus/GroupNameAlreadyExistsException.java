package de.eru.pherufx.focus;

/**
 *
 * @author Philipp Bruckner
 */
public class GroupNameAlreadyExistsException extends RuntimeException {

    public GroupNameAlreadyExistsException() {
    }

    public GroupNameAlreadyExistsException(String msg) {
        super(msg);
    }
}

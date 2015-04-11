package de.eru.pherufx.focus;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.Node;

/**
 *
 * @author Philipp Bruckner
 */
public final class FocusTraversalGroups {
    
    private static final Map<String, FocusTraversalGroup> groups = new HashMap<>();
    
    private FocusTraversalGroups(){
    }
    
    public static FocusTraversalGroup create(String name){
        FocusTraversalGroup focusTraversalGroup = new FocusTraversalGroup();
        groups.put(name, focusTraversalGroup);
        return focusTraversalGroup;
    }
    
    public static FocusTraversalGroup createNewGroup(String name, Node... nodes){
        FocusTraversalGroup group = create(name);
        for (Node node : nodes) {
            group.add(node);
        }
        return group;
    }
    
    public static FocusTraversalGroup get(String name){
        return groups.get(name);
    }

    public static void remove(String name){
        groups.get(name).clear();
        groups.remove(name);
    }
}

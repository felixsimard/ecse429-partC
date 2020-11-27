package APITests;

import java.util.*;

/**
 * Singleton context used to pass variables between steps
 */
public class Context {

    private static Context instance = null;

    private Map<String, ContextElement> variables;

    private Map<String, List<ContextElement>> listVariables;

    public Context() {
        this.listVariables = new HashMap<>();
        this.variables = new HashMap<>();
    }

    public static Context getContext() {
        if (instance == null) {
            instance = new Context();
        }
        return instance;
    }

    public static void resetContext() {
        instance.variables.clear();
        instance = null;
    }

    public int get(String key) {
        return this.variables.get(key).id;
    }

    public void set(String key, int id, ContextElement.ElementType type) {
        ContextElement el = new ContextElement(id, type);
        this.variables.put(key, el);
    }

    public List<ContextElement> getAllElementsToDelete() {
        List<ContextElement> result = new ArrayList<>();
        Iterator<Map.Entry<String, ContextElement>> it = this.variables.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, ContextElement> pair = it.next();
            if (pair.getValue().type == ContextElement.ElementType.TODO ||
                    pair.getValue().type == ContextElement.ElementType.PROJECT ||
                    pair.getValue().type == ContextElement.ElementType.CATEGORY) {

                result.add(pair.getValue());
            }
        }
        it.remove(); // avoids a ConcurrentModificationException

        Iterator<Map.Entry<String, List<ContextElement>>> it2 = this.listVariables.entrySet().iterator();
        while (it2.hasNext()) {
            Map.Entry<String, List<ContextElement>> pair = it2.next();
            for (ContextElement e : pair.getValue()) {
                if (e.type == ContextElement.ElementType.TODO ||
                        e.type == ContextElement.ElementType.PROJECT ||
                        e.type == ContextElement.ElementType.CATEGORY) {

                    result.add(e);
                }
            }
        }
        return result;
    }

}

package APITests;

public class ContextElement {

    public int id;
    public ElementType type;

    public enum ElementType {
        TODO, CATEGORY, PROJECT, OTHER
    }

    public ContextElement(int id, ElementType type) {
        this.id = id;
        this.type = type;
    }
}

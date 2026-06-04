public class ServiceWagon extends Wagon {
    private String typeName;

    public ServiceWagon(int number, String typeName) {
        super(number);
        this.typeName = typeName;
    }

    public String getTypeName() { return typeName; }

    public String getCategory() { return "Служебный"; }

    public String getState() { return "в эксплуатации"; }
}

public class Station {
    private String name;
    private double x;
    private double y;

    public Station(String name, double x, double y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public String getName() { return name; }

    public double distanceTo(Station other) {
        double dx = x - other.x;
        double dy = y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public String toString() { return name; }
}

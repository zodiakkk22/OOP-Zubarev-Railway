public abstract class Wagon {
    private int number;

    public Wagon(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public abstract String getTypeName();
    public abstract String getCategory();
    public abstract String getState();
}

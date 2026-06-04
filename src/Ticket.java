public class Ticket {
    private String passenger;
    private int trainNumber;
    private String departureStation;
    private String wagonType;
    private double price;
    private boolean usedEquipment;

    public Ticket(String passenger, int trainNumber, String departureStation,
                  String wagonType, double price, boolean usedEquipment) {
        this.passenger = passenger;
        this.trainNumber = trainNumber;
        this.departureStation = departureStation;
        this.wagonType = wagonType;
        this.price = price;
        this.usedEquipment = usedEquipment;
    }

    public String getPassenger() { return passenger; }
    public int getTrainNumber() { return trainNumber; }
    public String getDepartureStation() { return departureStation; }
    public String getWagonType() { return wagonType; }
    public double getPrice() { return price; }
    public boolean isUsedEquipment() { return usedEquipment; }
}

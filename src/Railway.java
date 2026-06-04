import java.util.ArrayList;
import java.util.Random;

public class Railway {
    private ArrayList<Station> stations = new ArrayList<>();
    private ArrayList<Train> trains = new ArrayList<>();
    private ArrayList<Ticket> sold = new ArrayList<>();
    private ArrayList<String> failures = new ArrayList<>(); // "Имя -> причина"
    private Random rnd = new Random();

    private String[] names = {
        "Иванов", "Петров", "Сидоров", "Кузнецов", "Смирнов",
        "Попов", "Васильев", "Морозов", "Новиков", "Фёдоров"
    };

    private String[] wagonTypes = {"Сидячий", "Плацкартный", "Купейный"};

    private String[] reasons = {
        "Поезд с указанным номером не найден",
        "Поезд не следует до указанного пункта назначения",
        "В составе нет вагона-ресторана",
        "В составе нет вагона-буфета",
        "В составе нет вагонов запрошенного типа",
        "Нет вагонов с требуемым оборудованием",
        "Нет свободных мест"
    };

    public ArrayList<Station> getStations() { return stations; }
    public ArrayList<Train> getTrains() { return trains; }

    private Train findTrain(int number) {
        for (Train t : trains) {
            if (t.getNumber() == number) return t;
        }
        return null;
    }

    public String buyTicket(String passenger, int trainNumber, Station destination,
                            String wagonType, boolean needRestaurant, boolean needBuffet,
                            boolean needTv, boolean needPhone, boolean wantBedding) {
        if (passenger == null || passenger.isEmpty()) passenger = "Пассажир";

        Train train = findTrain(trainNumber);
        if (train == null) return fail(passenger, reasons[0]);
        if (!train.getDestination().getName().equals(destination.getName()))
            return fail(passenger, reasons[1]);
        if (needRestaurant && !train.hasWagonNamed("Вагон-ресторан"))
            return fail(passenger, reasons[2]);
        if (needBuffet && !train.hasWagonNamed("Вагон-буфет"))
            return fail(passenger, reasons[3]);

        boolean typeExists = false;
        boolean equipOk = false;
        PassengerWagon chosen = null;

        for (PassengerWagon w : train.getPassengerWagons()) {
            if (!wagonType.equals("Любой") && !w.getTypeName().equals(wagonType)) continue;
            typeExists = true;
            if (needTv && !w.hasTv()) continue;
            if (needPhone && !w.hasPhone()) continue;
            equipOk = true;
            if (!w.hasFreeSeats()) continue;
            chosen = w;
            break;
        }

        if (chosen == null) {
            String reason;
            if (!typeExists) reason = reasons[4];
            else if (!equipOk) reason = reasons[5];
            else reason = reasons[6];
            return fail(passenger, reason);
        }

        chosen.occupySeat();
        double price = chosen.calculatePrice(train.getRouteDistance(), wantBedding);
        sold.add(new Ticket(passenger, train.getNumber(),
                train.getDeparture().getName(), chosen.getTypeName(),
                price, needTv || needPhone));

        return "УСПЕХ: " + passenger + " — поезд №" + train.getNumber()
             + ", вагон №" + chosen.getNumber() + " (" + chosen.getTypeName() + "), "
             + String.format("%.2f руб.", price);
    }

    private String fail(String passenger, String reason) {
        failures.add(passenger + " -> " + reason);
        return "ОТКАЗ: " + reason;
    }

    public String simulateRandom(int n) {
        int ok = 0;
        int bad = 0;
        for (int i = 0; i < n; i++) {
            Train t = trains.get(rnd.nextInt(trains.size()));
            Station dest = t.getDestination();
            if (rnd.nextInt(100) >= 80) {
                dest = stations.get(rnd.nextInt(stations.size()));
            }
            String type = "Любой";
            int r = rnd.nextInt(4);
            if (r < 3) type = wagonTypes[r];

            String name = names[rnd.nextInt(names.length)] + "-" + (i + 1);
            String res = buyTicket(name, t.getNumber(), dest, type,
                    rnd.nextInt(100) < 15, rnd.nextInt(100) < 15,
                    rnd.nextInt(100) < 25, rnd.nextInt(100) < 15,
                    rnd.nextInt(100) < 40);
            if (res.startsWith("УСПЕХ")) ok++; else bad++;
        }
        return "Сгенерировано " + n + " пассажиров.\nПродано билетов: " + ok
             + "\nОтказов: " + bad + "\n";
    }

    // ===================== ОТЧЁТЫ =====================

    public String reportTrains() {
        String s = "=== ПОЕЗДА И СОСТАВ ===\n";
        for (Train t : trains) {
            s += t + ", расстояние " + Math.round(t.getRouteDistance()) + " км\n";
            for (Wagon w : t.getWagons()) {
                String extra = "";
                if (w instanceof PassengerWagon) {
                    PassengerWagon p = (PassengerWagon) w;
                    extra = " | мест: " + p.getTotalSeats()
                          + " | ТВ:" + (p.hasTv() ? "да" : "нет")
                          + " телефон:" + (p.hasPhone() ? "да" : "нет");
                }
                s += "   №" + w.getNumber() + " " + w.getTypeName() + extra + "\n";
            }
            s += "\n";
        }
        return s;
    }

    public String reportTrainsState() {
        String s = "=== СОСТОЯНИЕ ПОЕЗДОВ ===\n";
        for (Train t : trains) {
            s += t + "\n";
            for (Wagon w : t.getWagons()) {
                s += "   №" + w.getNumber() + " " + w.getTypeName()
                   + " [" + w.getCategory() + "] " + w.getState() + "\n";
            }
            s += "   ИТОГО пассажиров: " + t.getTotalPassengers()
               + " из " + t.getTotalSeats() + " мест\n\n";
        }
        return s;
    }

    public String reportEquipmentByStation() {
        String s = "=== ПАССАЖИРЫ С ДОП. ОБОРУДОВАНИЕМ (ТВ/телефон) ===\n";
        int total = 0;
        for (Station st : stations) {
            int count = 0;
            for (Ticket tk : sold) {
                if (tk.isUsedEquipment() && tk.getDepartureStation().equals(st.getName())) {
                    count++;
                }
            }
            s += "   " + st.getName() + ": " + count + "\n";
            total += count;
        }
        s += "   ВСЕГО: " + total + "\n";
        return s;
    }

    public String reportWagonLoadByType() {
        String s = "=== ЗАГРУЖЕННОСТЬ ВАГОНОВ ПО ТИПАМ ===\n";
        for (String type : wagonTypes) {
            int occ = 0;
            int tot = 0;
            for (Train t : trains) {
                for (PassengerWagon w : t.getPassengerWagons()) {
                    if (w.getTypeName().equals(type)) {
                        occ += w.getOccupiedSeats();
                        tot += w.getTotalSeats();
                    }
                }
            }
            double pct = (tot == 0) ? 0 : 100.0 * occ / tot;
            s += "   " + type + ": " + occ + "/" + tot
               + String.format(" (%.1f%%)", pct) + "\n";
        }
        return s;
    }

    public String reportRouteLoad() {
        String s = "=== ЗАГРУЖЕННОСТЬ МАРШРУТОВ ===\n";
        for (Train t : trains) {
            int occ = t.getTotalPassengers();
            int tot = t.getTotalSeats();
            double pct = (tot == 0) ? 0 : 100.0 * occ / tot;
            s += "   №" + t.getNumber() + " " + t.getDeparture().getName()
               + " -> " + t.getDestination().getName()
               + " : " + occ + "/" + tot + String.format(" (%.1f%%)", pct) + "\n";
        }
        return s;
    }

    public String reportRevenue() {
        String s = "=== ВЫРУЧКА ===\n По поездам:\n";
        double total = 0;
        for (Train t : trains) {
            double sum = 0;
            for (Ticket tk : sold) {
                if (tk.getTrainNumber() == t.getNumber()) sum += tk.getPrice();
            }
            s += "   №" + t.getNumber() + " : " + String.format("%.2f руб.", sum) + "\n";
            total += sum;
        }
        s += " По станциям отправления:\n";
        for (Station st : stations) {
            double sum = 0;
            for (Ticket tk : sold) {
                if (tk.getDepartureStation().equals(st.getName())) sum += tk.getPrice();
            }
            if (sum > 0) s += "   " + st.getName() + " : " + String.format("%.2f руб.", sum) + "\n";
        }
        s += " По типам вагонов:\n";
        for (String type : wagonTypes) {
            double sum = 0;
            for (Ticket tk : sold) {
                if (tk.getWagonType().equals(type)) sum += tk.getPrice();
            }
            s += "   " + type + " : " + String.format("%.2f руб.", sum) + "\n";
        }
        s += " ОБЩАЯ ВЫРУЧКА: " + String.format("%.2f руб.", total) + "\n";
        return s;
    }

    public String reportFailures() {
        String s = "=== ОТКАЗЫ В ПРОДАЖЕ БИЛЕТОВ ===\n";
        if (failures.isEmpty()) {
            return s + "   Отказов не было.\n";
        }
        for (String f : failures) {
            s += "   " + f + "\n";
        }
        s += " Итого по причинам:\n";
        for (String reason : reasons) {
            int count = 0;
            for (String f : failures) {
                if (f.endsWith(reason)) count++;
            }
            if (count > 0) s += "   " + reason + ": " + count + "\n";
        }
        s += " ВСЕГО ОТКАЗОВ: " + failures.size() + "\n";
        return s;
    }

    public void loadSampleData() {
        Station moscow = new Station("Москва", 0, 0);
        Station spb = new Station("Санкт-Петербург", 0, 650);
        Station kazan = new Station("Казань", 820, 100);
        Station sochi = new Station("Сочи", 300, -1600);
        Station ekb = new Station("Екатеринбург", 1800, 250);
        stations.add(moscow);
        stations.add(spb);
        stations.add(kazan);
        stations.add(sochi);
        stations.add(ekb);

        Train t1 = new Train(1, moscow, spb, "23:00", "07:00");
        t1.addWagon(new PassengerWagon(1, "Сидячий", 1.0, 60, 2.5, false, false, 0));
        t1.addWagon(new PassengerWagon(2, "Плацкартный", 1.3, 54, 2.5, true, false, 0));
        t1.addWagon(new PassengerWagon(3, "Купейный", 1.7, 36, 2.5, true, true, 150));
        t1.addWagon(new ServiceWagon(4, "Вагон-ресторан"));
        trains.add(t1);

        Train t2 = new Train(2, moscow, kazan, "18:30", "06:00");
        t2.addWagon(new PassengerWagon(1, "Сидячий", 1.0, 60, 2.2, false, false, 0));
        t2.addWagon(new PassengerWagon(2, "Плацкартный", 1.3, 54, 2.2, false, false, 0));
        t2.addWagon(new ServiceWagon(3, "Вагон-буфет"));
        t2.addWagon(new ServiceWagon(4, "Почтовый"));
        trains.add(t2);

        Train t3 = new Train(3, moscow, sochi, "12:00", "10:00");
        t3.addWagon(new PassengerWagon(1, "Плацкартный", 1.3, 54, 3.0, false, false, 0));
        t3.addWagon(new PassengerWagon(2, "Купейный", 1.7, 36, 3.0, true, true, 200));
        t3.addWagon(new PassengerWagon(3, "Купейный", 1.7, 36, 3.0, false, false, 200));
        t3.addWagon(new ServiceWagon(4, "Вагон-ресторан"));
        trains.add(t3);

        Train t4 = new Train(4, kazan, ekb, "09:00", "20:00");
        t4.addWagon(new PassengerWagon(1, "Сидячий", 1.0, 60, 2.0, true, false, 0));
        t4.addWagon(new PassengerWagon(2, "Купейный", 1.7, 36, 2.0, false, false, 120));
        t4.addWagon(new ServiceWagon(3, "Вагон-буфет"));
        trains.add(t4);
    }
}

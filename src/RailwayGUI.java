import javax.swing.*;
import java.awt.*;

public class RailwayGUI extends JFrame {
    private Railway railway = new Railway();
    private JTextArea output = new JTextArea();

    public RailwayGUI() {
        super("Железная дорога — имитация перевозок");
        railway.loadSampleData();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(740, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        JPanel buttons = new JPanel(new GridLayout(0, 2, 5, 5));

        JButton b1 = new JButton("Показать поезда");
        b1.addActionListener(e -> showText(railway.reportTrains()));
        buttons.add(b1);

        JButton b2 = new JButton("Купить билет");
        b2.addActionListener(e -> buyTicket());
        buttons.add(b2);

        JButton b3 = new JButton("Случайные пассажиры");
        b3.addActionListener(e -> simulate());
        buttons.add(b3);

        JButton b4 = new JButton("Состояние поездов");
        b4.addActionListener(e -> showText(railway.reportTrainsState()));
        buttons.add(b4);

        JButton b5 = new JButton("Оборудование по станциям");
        b5.addActionListener(e -> showText(railway.reportEquipmentByStation()));
        buttons.add(b5);

        JButton b6 = new JButton("Загрузка вагонов");
        b6.addActionListener(e -> showText(railway.reportWagonLoadByType()));
        buttons.add(b6);

        JButton b7 = new JButton("Загрузка маршрутов");
        b7.addActionListener(e -> showText(railway.reportRouteLoad()));
        buttons.add(b7);

        JButton b8 = new JButton("Выручка");
        b8.addActionListener(e -> showText(railway.reportRevenue()));
        buttons.add(b8);

        JButton b9 = new JButton("Отказы");
        b9.addActionListener(e -> showText(railway.reportFailures()));
        buttons.add(b9);

        JButton b10 = new JButton("Очистить");
        b10.addActionListener(e -> output.setText(""));
        buttons.add(b10);

        add(buttons, BorderLayout.NORTH);
        add(new JScrollPane(output), BorderLayout.CENTER);

        showText("Нажмите «Случайные пассажиры», чтобы запустить имитацию,\n"
               + "затем смотрите отчёты. Или купите билет вручную.");
    }

    private void showText(String text) {
        output.setText(text);
        output.setCaretPosition(0);
    }

    private void buyTicket() {
        JTextField name = new JTextField("Пассажир");
        JComboBox<Train> train = new JComboBox<>(railway.getTrains().toArray(new Train[0]));
        JComboBox<Station> dest = new JComboBox<>(railway.getStations().toArray(new Station[0]));
        String[] types = {"Любой", "Сидячий", "Плацкартный", "Купейный"};
        JComboBox<String> type = new JComboBox<>(types);
        JCheckBox restaurant = new JCheckBox("нужен вагон-ресторан");
        JCheckBox buffet = new JCheckBox("нужен вагон-буфет");
        JCheckBox tv = new JCheckBox("нужен телевизор");
        JCheckBox phone = new JCheckBox("нужен телефон");
        JCheckBox bedding = new JCheckBox("заказать постель (купе)");

        JPanel panel = new JPanel(new GridLayout(0, 1, 3, 3));
        panel.add(new JLabel("Пассажир:"));
        panel.add(name);
        panel.add(new JLabel("Поезд:"));
        panel.add(train);
        panel.add(new JLabel("Пункт назначения:"));
        panel.add(dest);
        panel.add(new JLabel("Тип вагона:"));
        panel.add(type);
        panel.add(restaurant);
        panel.add(buffet);
        panel.add(tv);
        panel.add(phone);
        panel.add(bedding);

        int result = JOptionPane.showConfirmDialog(this, panel, "Покупка билета",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        Train t = (Train) train.getSelectedItem();
        String message = railway.buyTicket(
                name.getText().trim(),
                t.getNumber(),
                (Station) dest.getSelectedItem(),
                (String) type.getSelectedItem(),
                restaurant.isSelected(), buffet.isSelected(),
                tv.isSelected(), phone.isSelected(), bedding.isSelected());

        output.append(message + "\n");
    }

    private void simulate() {
        String input = JOptionPane.showInputDialog(this,
                "Сколько пассажиров сгенерировать?", "30");
        if (input == null) return;
        try {
            int n = Integer.parseInt(input.trim());
            showText(railway.simulateRandom(n));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Введите целое число.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RailwayGUI().setVisible(true));
    }
}

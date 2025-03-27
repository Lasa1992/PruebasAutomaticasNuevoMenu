package Suite;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MonitorDeHilos {
    private final JFrame frame;
    private final Map<Integer, JPanel> paneles = new HashMap<>();
    private final Map<Integer, JLabel> etiquetas = new HashMap<>();
    private final Map<Integer, JTextArea> logs = new HashMap<>();

    public MonitorDeHilos(int numeroHilos) {
        frame = new JFrame("Monitoreo de Pruebas");
        frame.setLayout(new GridLayout(2, 2));
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        for (int i = 0; i < numeroHilos; i++) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createTitledBorder("Hilo " + i));
            JLabel etiqueta = new JLabel("Esperando...");
            JTextArea log = new JTextArea();
            log.setEditable(false);
            log.setFont(new Font("Monospaced", Font.PLAIN, 11));

            panel.add(etiqueta, BorderLayout.NORTH);
            panel.add(new JScrollPane(log), BorderLayout.CENTER);

            paneles.put(i, panel);
            etiquetas.put(i, etiqueta);
            logs.put(i, log);

            frame.add(panel);
        }

        frame.setVisible(true);
    }

    public void actualizar(int hilo, String estado, Color color) {
        SwingUtilities.invokeLater(() -> {
            etiquetas.get(hilo).setText(estado);
            paneles.get(hilo).setBackground(color);
        });
    }

    public void log(int hilo, String mensaje) {
        SwingUtilities.invokeLater(() -> {
            logs.get(hilo).append(mensaje + "\n");
        });
    }
}

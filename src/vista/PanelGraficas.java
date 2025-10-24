package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Panel de gráfica de líneas interactiva sin dependencias externas.
 * - Muestra el valor al pasar el mouse por encima del punto más cercano.
 * - Ajusta el rango automáticamente según los valores.
 * - Dibuja ejes y etiquetas de referencia.
 */
public class PanelGraficas extends JPanel {
    private List<Double> datos;
    private String titulo;
    private Color colorLinea;

    private int puntoSeleccionado = -1; // índice del punto bajo el mouse
    private static final int RADIO_PUNTO = 4;

    public PanelGraficas(List<Double> datos, String titulo, Color colorLinea) {
        this.datos = datos;
        this.titulo = titulo;
        this.colorLinea = colorLinea;
        setPreferredSize(new Dimension(280, 140));
        setBackground(Color.WHITE);

        // Interactividad con el mouse
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                puntoSeleccionado = encontrarPuntoCercano(e.getX(), e.getY());
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (datos == null || datos.isEmpty()) {
            g.setColor(Color.GRAY);
            g.drawString("Sin datos aún", getWidth() / 2 - 30, getHeight() / 2);
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int n = datos.size();
        int margin = 30;

        double max = datos.stream().mapToDouble(Double::doubleValue).max().orElse(1);
        double min = datos.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double range = Math.max(1, max - min);

        // Fondo del título
        g2.setColor(new Color(240, 240, 255));
        g2.fillRect(0, 0, w, 20);
        g2.setColor(Color.BLACK);
        g2.drawString(titulo, 10, 15);

        // Ejes
        g2.drawLine(margin, h - margin, w - margin, h - margin);
        g2.drawLine(margin, margin, margin, h - margin);

        // Etiquetas de ejes
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2.drawString(String.format("%.2f", max), 5, margin + 5);
        g2.drawString(String.format("%.2f", min), 5, h - margin);
        g2.drawString("Ciclos", w / 2 - 15, h - 5);

        // Línea de datos
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(colorLinea);

        int prevX = margin;
        int prevY = escalarY(datos.get(0), h, margin, range, min);
        for (int i = 1; i < n; i++) {
            int x = escalarX(i, w, n, margin);
            int y = escalarY(datos.get(i), h, margin, range, min);
            g2.drawLine(prevX, prevY, x, y);
            prevX = x;
            prevY = y;
        }

        // Puntos
        for (int i = 0; i < n; i++) {
            int x = escalarX(i, w, n, margin);
            int y = escalarY(datos.get(i), h, margin, range, min);
            g2.fillOval(x - RADIO_PUNTO / 2, y - RADIO_PUNTO / 2, RADIO_PUNTO, RADIO_PUNTO);
        }

        // Mostrar valor del punto seleccionado
        if (puntoSeleccionado >= 0 && puntoSeleccionado < n) {
            int x = escalarX(puntoSeleccionado, w, n, margin);
            int y = escalarY(datos.get(puntoSeleccionado), h, margin, range, min);

            // Dibuja círculo resaltado
            g2.setColor(Color.BLACK);
            g2.fillOval(x - RADIO_PUNTO, y - RADIO_PUNTO, RADIO_PUNTO * 2, RADIO_PUNTO * 2);

            // Dibuja cuadro flotante con valor
            String texto = String.format("Ciclo %d: %.3f", puntoSeleccionado + 1, datos.get(puntoSeleccionado));
            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(texto);
            int textH = fm.getHeight();

            int boxX = Math.min(x + 10, w - textW - 10);
            int boxY = Math.max(y - textH - 5, 10);

            g2.setColor(new Color(255, 255, 225));
            g2.fillRoundRect(boxX, boxY, textW + 6, textH, 8, 8);
            g2.setColor(Color.DARK_GRAY);
            g2.drawRoundRect(boxX, boxY, textW + 6, textH, 8, 8);
            g2.drawString(texto, boxX + 3, boxY + textH - 4);
        }
    }

    private int escalarX(int i, int ancho, int total, int margen) {
        if (total <= 1) return margen;
        return margen + (i * (ancho - 2 * margen)) / (total - 1);
    }

    private int escalarY(double valor, int alto, int margen, double rango, double min) {
        return alto - margen - (int) (((valor - min) / rango) * (alto - 2 * margen));
    }

    private int encontrarPuntoCercano(int mouseX, int mouseY) {
        if (datos == null || datos.isEmpty()) return -1;
        int w = getWidth(), h = getHeight(), n = datos.size(), margin = 30;
        double max = datos.stream().mapToDouble(Double::doubleValue).max().orElse(1);
        double min = datos.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double range = Math.max(1, max - min);

        int puntoCercano = -1;
        double distMin = Double.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            int x = escalarX(i, w, n, margin);
            int y = escalarY(datos.get(i), h, margin, range, min);
            double dist = Math.hypot(mouseX - x, mouseY - y);
            if (dist < distMin && dist < 10) {
                distMin = dist;
                puntoCercano = i;
            }
        }
        return puntoCercano;
    }
}


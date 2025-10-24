/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// vista/GraficasManager.java (Versión temporal sin JFreeChart)
// vista/GraficasManager.java
// vista/GraficasManager.java - LÍNEA CORREGIDA

package vista;

import hilos.SimuladorHilo;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Muestra estadísticas de rendimiento del simulador con texto y gráficas en tiempo real.
 */
public class GraficasManager {
    private JFrame frameGraficas;
    private JTextArea areaEstadisticas;
    private List<Double> historialThroughput;
    private List<Double> historialUtilizacionCPU;
    private List<Double> historialTiempoRespuesta;
    private List<Double> historialEquidad;

    // Paneles de gráficas
    private PanelGraficas panelThroughput;
    private PanelGraficas panelCPU;
    private PanelGraficas panelTiempo;
    private PanelGraficas panelEquidad;

    public GraficasManager() {
        this.historialThroughput = new ArrayList<>();
        this.historialUtilizacionCPU = new ArrayList<>();
        this.historialTiempoRespuesta = new ArrayList<>();
        this.historialEquidad = new ArrayList<>();
        crearInterfazTemporal();
    }

    private void crearInterfazTemporal() {
        frameGraficas = new JFrame("📈 Estadísticas de Rendimiento - Historial");
        frameGraficas.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameGraficas.setLayout(new BorderLayout());
        frameGraficas.setSize(950, 600);

        // === 1️⃣ Área de texto ===
        areaEstadisticas = new JTextArea();
        areaEstadisticas.setEditable(false);
        areaEstadisticas.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaEstadisticas.setBackground(new Color(240, 240, 240));

        JScrollPane scrollPane = new JScrollPane(areaEstadisticas);
        frameGraficas.add(scrollPane, BorderLayout.CENTER);

        // === 2️⃣ Panel de gráficas dinámicas ===
        panelThroughput = new PanelGraficas(historialThroughput, "Throughput", Color.BLUE);
        panelCPU = new PanelGraficas(historialUtilizacionCPU, "Utilización CPU (%)", Color.RED);
        panelTiempo = new PanelGraficas(historialTiempoRespuesta, "Tiempo de Respuesta", Color.GREEN);
        panelEquidad = new PanelGraficas(historialEquidad, "Equidad del Sistema", Color.MAGENTA);

        JPanel panelGraficos = new JPanel(new GridLayout(4, 1, 5, 5));
        panelGraficos.add(panelThroughput);
        panelGraficos.add(panelCPU);
        panelGraficos.add(panelTiempo);
        panelGraficos.add(panelEquidad);
        frameGraficas.add(panelGraficos, BorderLayout.EAST);

        // === 3️⃣ Etiqueta superior ===
        JLabel lblAdvertencia = new JLabel("📊 Visualización de Métricas a lo Largo del Tiempo", JLabel.CENTER);
        lblAdvertencia.setForeground(Color.BLUE);
        lblAdvertencia.setFont(new Font("Arial", Font.BOLD, 14));
        frameGraficas.add(lblAdvertencia, BorderLayout.NORTH);

        // === 4️⃣ Botón inferior ===
        JButton btnLimpiar = new JButton("Limpiar Historial");
        btnLimpiar.addActionListener(e -> limpiarGraficas());
        frameGraficas.add(btnLimpiar, BorderLayout.SOUTH);
    }

    public void actualizarGraficas(SimuladorHilo simulador) {
        if (simulador == null) return;

        // Agregar nuevos datos al historial
        historialThroughput.add(simulador.getThroughput());
        historialUtilizacionCPU.add(simulador.getUtilizacionCPU() * 100);
        historialTiempoRespuesta.add(simulador.getTiempoRespuestaPromedio());
        historialEquidad.add(simulador.getEquidad());

        // Actualizar texto
        actualizarAreaEstadisticas();

        // Redibujar paneles de gráficas
        frameGraficas.repaint();
    }

    private void actualizarAreaEstadisticas() {
        StringBuilder sb = new StringBuilder();
        sb.append("ESTADÍSTICAS HISTÓRICAS - ÚLTIMOS 20 CICLOS\n");
        sb.append("=============================================\n\n");

        if (historialThroughput.isEmpty()) {
            sb.append("Esperando datos de la simulación...\n");
            sb.append("Inicia la simulación y agrega procesos para ver las métricas.\n");
        } else {
            sb.append("THROUGHPUT (Procesos Completados / Ciclo):\n");
            sb.append(generarHistorialTexto(historialThroughput));
            sb.append("\nUTILIZACIÓN CPU (%):\n");
            sb.append(generarHistorialTexto(historialUtilizacionCPU));
            sb.append("\nTIEMPO RESPUESTA PROMEDIO (Ciclos):\n");
            sb.append(generarHistorialTexto(historialTiempoRespuesta));
            sb.append("\nEQUIDAD DEL SISTEMA (0-1):\n");
            sb.append(generarHistorialTexto(historialEquidad));
            sb.append("\n").append(getEstadisticasResumen()).append("\n");
        }

        areaEstadisticas.setText(sb.toString());
        areaEstadisticas.setCaretPosition(areaEstadisticas.getDocument().getLength());
    }

    private String generarHistorialTexto(List<Double> datos) {
        if (datos.isEmpty()) return "  No hay datos\n";

        StringBuilder sb = new StringBuilder();
        int start = Math.max(0, datos.size() - 20);
        for (int i = start; i < datos.size(); i++) {
            sb.append(String.format("  Ciclo %3d: %8.4f\n", i + 1, datos.get(i)));
        }
        return sb.toString();
    }

    public String getEstadisticasResumen() {
        if (historialThroughput.isEmpty()) return "No hay datos suficientes";

        return String.format(
            "RESUMEN ESTADÍSTICO:\n" +
            "────────────────────\n" +
            "Throughput Promedio:      %8.4f procesos/ciclo\n" +
            "Utilización CPU Promedio: %8.2f%%\n" +
            "Tiempo Respuesta Promedio:%8.2f ciclos\n" +
            "Equidad Promedio:         %8.2f\n" +
            "Muestras Analizadas:      %8d ciclos",
            calcularPromedio(historialThroughput),
            calcularPromedio(historialUtilizacionCPU),
            calcularPromedio(historialTiempoRespuesta),
            calcularPromedio(historialEquidad),
            historialThroughput.size()
        );
    }

    private double calcularPromedio(List<Double> datos) {
        return datos.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    public void mostrarGraficas() {
        frameGraficas.setVisible(true);
    }

    public void ocultarGraficas() {
        frameGraficas.setVisible(false);
    }

    public void limpiarGraficas() {
        historialThroughput.clear();
        historialUtilizacionCPU.clear();
        historialTiempoRespuesta.clear();
        historialEquidad.clear();
        areaEstadisticas.setText("Historial limpiado. Los nuevos datos aparecerán aquí.\n");
        frameGraficas.repaint();
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// vista/PanelEstadisticas.java
package vista;

import hilos.SimuladorHilo;
import javax.swing.*;
import java.awt.*;

public class PanelEstadisticas extends JPanel {
    private JLabel lblThroughput;
    private JLabel lblUtilizacionCPU;
    private JLabel lblTiempoRespuesta;
    private JLabel lblEquidad;
    private JLabel lblProcesosCompletados;
    private JLabel lblCiclosTotales;
    private JLabel lblProcesosEnSistema;
    
    public PanelEstadisticas() {
        initComponents();
        setupLayout();
    }
    
    private void initComponents() {
        setBorder(BorderFactory.createTitledBorder("📊 Estadísticas en Tiempo Real"));
        setPreferredSize(new Dimension(300, 200));
        
        lblThroughput = new JLabel("Throughput: 0.0000");
        lblUtilizacionCPU = new JLabel("Utilización CPU: 0%");
        lblTiempoRespuesta = new JLabel("Tiempo Respuesta: 0.00");
        lblEquidad = new JLabel("Equidad: 1.00");
        lblProcesosCompletados = new JLabel("Procesos Completados: 0");
        lblCiclosTotales = new JLabel("Ciclos Totales: 0");
        lblProcesosEnSistema = new JLabel("Procesos en Sistema: 0");
        
        // Estilo para mejor visualización
        Font fontMetricas = new Font("Monospaced", Font.BOLD, 12);
        lblThroughput.setFont(fontMetricas);
        lblUtilizacionCPU.setFont(fontMetricas);
        lblTiempoRespuesta.setFont(fontMetricas);
        lblEquidad.setFont(fontMetricas);
        lblProcesosCompletados.setFont(fontMetricas);
        lblCiclosTotales.setFont(fontMetricas);
        lblProcesosEnSistema.setFont(fontMetricas);
    }
    
    private void setupLayout() {
        setLayout(new GridLayout(7, 1, 5, 5));
        add(lblThroughput);
        add(lblUtilizacionCPU);
        add(lblTiempoRespuesta);
        add(lblEquidad);
        add(lblProcesosCompletados);
        add(lblProcesosEnSistema);
        add(lblCiclosTotales);
    }
    
    public void actualizarEstadisticas(SimuladorHilo simulador) {
        if (simulador == null || !simulador.isEjecutando()) {
            setValoresPorDefecto();
            return;
        }
        
        lblThroughput.setText(String.format("Throughput: %.4f", simulador.getThroughput()));
        lblUtilizacionCPU.setText(String.format("Utilización CPU: %.1f%%", 
            simulador.getUtilizacionCPU() * 100));
        lblTiempoRespuesta.setText(String.format("Tiempo Respuesta: %.2f", 
            simulador.getTiempoRespuestaPromedio()));
        lblEquidad.setText(String.format("Equidad: %.2f", simulador.getEquidad()));
        lblProcesosCompletados.setText(String.format("Procesos Completados: %d", 
            simulador.getProcesosCompletados()));
        lblProcesosEnSistema.setText(String.format("Procesos en Sistema: %d", 
            simulador.getProcesos().size()));
        lblCiclosTotales.setText(String.format("Ciclos Totales: %d", 
            simulador.getCicloGlobal()));
    }
    
    private void setValoresPorDefecto() {
        lblThroughput.setText("Throughput: 0.0000");
        lblUtilizacionCPU.setText("Utilización CPU: 0%");
        lblTiempoRespuesta.setText("Tiempo Respuesta: 0.00");
        lblEquidad.setText("Equidad: 1.00");
        lblProcesosCompletados.setText("Procesos Completados: 0");
        lblProcesosEnSistema.setText("Procesos en Sistema: 0");
        lblCiclosTotales.setText("Ciclos Totales: 0");
    }
}

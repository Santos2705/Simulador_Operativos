/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// vista/VistaPrincipal.java
// vista/VistaPrincipal.java
package vista;

import modelo.*;
import hilos.SimuladorHilo;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VistaPrincipal extends JFrame {
    // Componentes principales
    private JTable tablaProcesos;
    private JTextArea logEventos;
    private JComboBox<PoliticaPlanificacion> comboPoliticas;
    private JLabel lblCicloGlobal;
    private JLabel lblProcesoEjecutando;
    private JLabel lblEstadoSO;
    private JSpinner spinnerDuracionCiclo;
    private JButton btnIniciar, btnPausar, btnAgregarProceso, btnMostrarGraficas;
    
    // Modelos de datos
    private ProcesoTableModel tableModel;
    private SimuladorHilo simulador;
    private Configuracion configuracion;
    private GraficasManager graficasManager;
    private PanelEstadisticas panelEstadisticas;

    public VistaPrincipal() {
        this.configuracion = new Configuracion();
        this.graficasManager = new GraficasManager();
        this.panelEstadisticas = new PanelEstadisticas();
        initComponents();
        setupLayout();
        setupListeners();
    }
    
    private void initComponents() {
        setTitle("Simulador de Sistemas Operativos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800); // Aumentado para el panel de estadísticas
        
        // Combo box de políticas
        comboPoliticas = new JComboBox<>(PoliticaPlanificacion.values());
        
        // Spinner para duración del ciclo
        spinnerDuracionCiclo = new JSpinner(new SpinnerNumberModel(1000, 100, 5000, 100));
        
        // Botones
        btnIniciar = new JButton("Iniciar Simulación");
        btnPausar = new JButton("Pausar");
        btnPausar.setEnabled(false);
        btnAgregarProceso = new JButton("Agregar Proceso");
        btnMostrarGraficas = new JButton("Mostrar Estadísticas");
        
        // Labels informativos
        lblCicloGlobal = new JLabel("Ciclo: 0");
        lblProcesoEjecutando = new JLabel("Ejecutando: Ninguno");
        lblEstadoSO = new JLabel("Estado: Sistema Operativo");
        
        // Tabla de procesos
        tableModel = new ProcesoTableModel();
        tablaProcesos = new JTable(tableModel);
        JScrollPane scrollTabla = new JScrollPane(tablaProcesos);
        
        // Área de log de eventos
        logEventos = new JTextArea(10, 50);
        logEventos.setEditable(false);
        JScrollPane scrollLog = new JScrollPane(logEventos);
    }
    
    private void setupLayout() {
        // Panel principal con BorderLayout
        setLayout(new BorderLayout());
        
        // Panel superior - Controles
        JPanel panelSuperior = new JPanel(new FlowLayout());
        panelSuperior.add(new JLabel("Política:"));
        panelSuperior.add(comboPoliticas);
        panelSuperior.add(new JLabel("Duración ciclo (ms):"));
        panelSuperior.add(spinnerDuracionCiclo);
        panelSuperior.add(btnIniciar);
        panelSuperior.add(btnPausar);
        panelSuperior.add(btnAgregarProceso);
        panelSuperior.add(btnMostrarGraficas);
        
        // Panel de información en tiempo real
        JPanel panelInfo = new JPanel(new FlowLayout());
        panelInfo.add(lblCicloGlobal);
        panelInfo.add(lblProcesoEjecutando);
        panelInfo.add(lblEstadoSO);
        
        // Panel central - Tabla y log
        JPanel panelCentral = new JPanel(new GridLayout(2, 1));
        panelCentral.add(new JLabel("Procesos en el Sistema:", JLabel.CENTER));
        panelCentral.add(new JScrollPane(tablaProcesos));
        panelCentral.add(new JLabel("Log de Eventos:", JLabel.CENTER));
        panelCentral.add(new JScrollPane(logEventos));
        
        // Agregar todo al frame
        add(panelSuperior, BorderLayout.NORTH);
        add(panelInfo, BorderLayout.CENTER);
        add(panelCentral, BorderLayout.WEST);
        add(panelEstadisticas, BorderLayout.EAST); // Panel de estadísticas
    }
    
    private void setupListeners() {
        btnIniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarSimulacion();
            }
        });
        
        btnPausar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pausarSimulacion();
            }
        });
        
        btnAgregarProceso.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarDialogoAgregarProceso();
            }
        });
        
        btnMostrarGraficas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                graficasManager.mostrarGraficas();
            }
        });
        
        comboPoliticas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cambiar política en tiempo real
                if (simulador != null && simulador.isEjecutando()) {
                    PoliticaPlanificacion nuevaPolitica = (PoliticaPlanificacion) comboPoliticas.getSelectedItem();
                    simulador.cambiarPolitica(nuevaPolitica);
                    logEventos.append("Política cambiada a: " + nuevaPolitica + "\n");
                }
            }
        });
    }
    
    private void iniciarSimulacion() {
        configuracion.setDuracionCicloMs((Integer) spinnerDuracionCiclo.getValue());
        configuracion.setPoliticaInicial((PoliticaPlanificacion) comboPoliticas.getSelectedItem());
        
        simulador = new SimuladorHilo(configuracion);
        simulador.start();
        
        btnIniciar.setEnabled(false);
        btnPausar.setEnabled(true);
        logEventos.append("Simulación iniciada - Política: " + configuracion.getPoliticaInicial() + "\n");
        
        // Timer para actualizar la interfaz
        Timer timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarInterfaz();
            }
        });
        timer.start();
    }
    
    private void pausarSimulacion() {
        if (simulador != null) {
            simulador.detenerSimulacion();
            btnIniciar.setEnabled(true);
            btnPausar.setEnabled(false);
            logEventos.append("Simulación pausada\n");
        }
    }
    
    private void mostrarDialogoAgregarProceso() {
        // Dialogo para agregar nuevo proceso
        JDialog dialogo = new JDialog(this, "Agregar Proceso", true);
        dialogo.setLayout(new GridLayout(0, 2, 5, 5));
        dialogo.setSize(400, 300);
        
        JTextField txtNombre = new JTextField("Proceso_" + (int)(Math.random() * 1000));
        JSpinner spinnerInstrucciones = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));
        JComboBox<TipoProceso> comboTipo = new JComboBox<>(TipoProceso.values());
        JSpinner spinnerCiclosExcepcion = new JSpinner(new SpinnerNumberModel(5, 1, 100, 1));
        JSpinner spinnerCiclosSatisfacer = new JSpinner(new SpinnerNumberModel(3, 1, 50, 1));
        JSpinner spinnerPrioridad = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
        
        dialogo.add(new JLabel("Nombre:"));
        dialogo.add(txtNombre);
        dialogo.add(new JLabel("Instrucciones:"));
        dialogo.add(spinnerInstrucciones);
        dialogo.add(new JLabel("Tipo:"));
        dialogo.add(comboTipo);
        dialogo.add(new JLabel("Ciclos para Excepción (I/O):"));
        dialogo.add(spinnerCiclosExcepcion);
        dialogo.add(new JLabel("Ciclos para Satisfacer:"));
        dialogo.add(spinnerCiclosSatisfacer);
        dialogo.add(new JLabel("Prioridad (1-10):"));
        dialogo.add(spinnerPrioridad);
        
        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarProcesoDesdeDialogo(
                    txtNombre.getText(),
                    (Integer) spinnerInstrucciones.getValue(),
                    (TipoProceso) comboTipo.getSelectedItem(),
                    (Integer) spinnerCiclosExcepcion.getValue(),
                    (Integer) spinnerCiclosSatisfacer.getValue(),
                    (Integer) spinnerPrioridad.getValue()
                );
                dialogo.dispose();
            }
        });
        
        dialogo.add(new JLabel()); // Espacio vacío
        dialogo.add(btnAgregar);
        dialogo.pack();
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }
    
    private void agregarProcesoDesdeDialogo(String nombre, int instrucciones, 
                                          TipoProceso tipo, int ciclosExcepcion, 
                                          int ciclosSatisfacer, int prioridad) {
        if (simulador != null && !nombre.trim().isEmpty()) {
            int id = (int) (Math.random() * 1000); // ID temporal
            PCB proceso;
            
            if (tipo == TipoProceso.IO_BOUND) {
                proceso = new PCB(id, nombre, instrucciones, tipo, ciclosExcepcion, ciclosSatisfacer);
            } else {
                proceso = new PCB(id, nombre, instrucciones, tipo);
            }
            proceso.setPrioridad(prioridad);
            
            simulador.agregarProceso(proceso);
            logEventos.append("Proceso agregado: " + nombre + " (" + tipo + ", " + instrucciones + " instrucciones)\n");
        } else {
            JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarInterfaz() {
        if (simulador != null) {
            lblCicloGlobal.setText("Ciclo: " + simulador.getCicloGlobal());
            
            PCB procesoEjec = simulador.getProcesoEjecutando();
            if (procesoEjec != null) {
                lblProcesoEjecutando.setText("Ejecutando: " + procesoEjec.getNombre() + 
                                           " (PC: " + procesoEjec.getProgramCounter() + ")");
                lblEstadoSO.setText("Estado: Programa Usuario");
            } else {
                lblProcesoEjecutando.setText("Ejecutando: Ninguno");
                lblEstadoSO.setText("Estado: Sistema Operativo");
            }
            
            // Actualizar tabla
            tableModel.actualizarDatos(simulador.getProcesos());
            
            // Actualizar panel de estadísticas
            panelEstadisticas.actualizarEstadisticas(simulador);
            
            // Actualizar gráficas/estadísticas
            graficasManager.actualizarGraficas(simulador);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new VistaPrincipal().setVisible(true);
            }
        });
    }
}
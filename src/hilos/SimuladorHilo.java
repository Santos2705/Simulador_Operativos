/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// hilos/SimuladorHilo.java
// hilos/SimuladorHilo.java
// hilos/SimuladorHilo.java
package hilos;

import modelo.*;
import planificacion.*;
import java.util.ArrayList;
import java.util.List;

public class SimuladorHilo extends Thread {
    private volatile boolean ejecutando;
    private int cicloGlobal;
    private final List<PCB> procesos;
    private Planificador planificador;
    private final Configuracion configuracion;
    private PCB procesoEjecutando;
    private int procesosCompletados;
    private final List<String> logEventos;
    
    public SimuladorHilo(Configuracion config) {
        this.configuracion = config;
        this.procesos = new ArrayList<>();
        this.ejecutando = false;
        this.cicloGlobal = 0;
        this.procesoEjecutando = null;
        this.procesosCompletados = 0;
        this.logEventos = new ArrayList<>();
        // Inicializar con la política configurada
        this.planificador = PlanificadorFactory.crearPlanificador(config.getPoliticaInicial());
    }
    
    @SuppressWarnings("CallToThreadSleepInLoop")
    @Override
    public void run() {
        ejecutando = true;
        agregarLog("Simulación iniciada - Política: " + configuracion.getPoliticaInicial());
        
        while (ejecutando) {
            ejecutarCiclo();
            try {
                Thread.sleep(configuracion.getDuracionCicloMs()); // Simulación en tiempo real
            } catch (InterruptedException e) {
                System.out.println("Simulación interrumpida");
                Thread.currentThread().interrupt(); // Buena práctica
                break;
            }
        }
        
        agregarLog("Simulación finalizada");
    }
    
    private void ejecutarCiclo() {
        cicloGlobal++;
        String mensajeCiclo = "=== Ciclo " + cicloGlobal + " ===";
        System.out.println(mensajeCiclo);
        agregarLog(mensajeCiclo);
        
        // Lógica específica para cada algoritmo
        if (planificador instanceof RoundRobinPlanificador) {
            manejarRoundRobin();
        } else if (planificador instanceof MultinivelPlanificador) {
            manejarMultinivel();
        }
        
        // Obtener siguiente proceso si no hay uno ejecutándose
        if (procesoEjecutando == null) {
            procesoEjecutando = planificador.siguienteProceso();
            if (procesoEjecutando != null) {
                procesoEjecutando.setEstado(Estado.EJECUCION);
                String mensaje = "Ejecutando: " + procesoEjecutando.getNombre();
                System.out.println(mensaje);
                agregarLog(mensaje);
                
                // Si es Round Robin, reiniciar contador
                if (planificador instanceof RoundRobinPlanificador) {
                    RoundRobinPlanificador rr = (RoundRobinPlanificador) planificador;
                    // El contador se reinicia automáticamente en siguienteProceso()
                }
            }
        }
        
        // Ejecutar instrucción si hay proceso
        if (procesoEjecutando != null) {
            procesoEjecutando.ejecutarInstruccion();
            String mensajeEjecucion = "Proceso " + procesoEjecutando.getNombre() + 
                             " - Instrucciones: " + procesoEjecutando.getInstruccionesEjecutadas() +
                             "/" + procesoEjecutando.getTotalInstrucciones() +
                             " - PC: " + procesoEjecutando.getProgramCounter();
            System.out.println(mensajeEjecucion);
            
            // Verificar si terminó
            if (procesoEjecutando.esTerminado()) {
                procesoEjecutando.setEstado(Estado.TERMINADO);
                procesoEjecutando.setTiempoFinalizacion(cicloGlobal);
                procesosCompletados++;
                String mensajeTerminado = "✓ Proceso " + procesoEjecutando.getNombre() + " TERMINADO";
                System.out.println(mensajeTerminado);
                agregarLog(mensajeTerminado);
                procesoEjecutando = null;
            } 
            // Verificar excepciones I/O para procesos I/O Bound
            else if (procesoEjecutando.debeGenerarExcepcion()) {
                procesoEjecutando.setEstado(Estado.BLOQUEADO);
                String mensajeBloqueado = "⚠ Proceso " + procesoEjecutando.getNombre() + " BLOQUEADO por I/O";
                System.out.println(mensajeBloqueado);
                agregarLog(mensajeBloqueado);
                // En una implementación más avanzada, aquí iría a cola de bloqueados
                procesoEjecutando = null;
            }
        }
        
        actualizarColas();
        mostrarEstadoColas();
    }
    
    private void manejarRoundRobin() {
        RoundRobinPlanificador rr = (RoundRobinPlanificador) planificador;
        
        if (procesoEjecutando != null) {
            rr.incrementarContadorQuantum();
            
            // Verificar si se agotó el quantum (solo si no terminó)
            if (rr.quantumAgotado() && !procesoEjecutando.esTerminado()) {
                procesoEjecutando.setEstado(Estado.LISTO);
                rr.reagregarProceso(procesoEjecutando);
                String mensajeQuantum = "⏰ Quantum agotado - " + procesoEjecutando.getNombre() + " reagregado a cola";
                System.out.println(mensajeQuantum);
                agregarLog(mensajeQuantum);
                procesoEjecutando = null;
            }
        }
    }
    
    private void manejarMultinivel() {
        MultinivelPlanificador multi = (MultinivelPlanificador) planificador;
        
        if (procesoEjecutando != null) {
            multi.incrementarContadorQuantum();
            
            // Verificar si se agotó el quantum específico para este proceso
            if (multi.quantumAgotado(procesoEjecutando) && !procesoEjecutando.esTerminado()) {
                procesoEjecutando.setEstado(Estado.LISTO);
                multi.reagregarProceso(procesoEjecutando);
                String mensajeQuantum = "⏰ Quantum agotado en multinivel - " + 
                                 procesoEjecutando.getNombre() + " reagregado a cola";
                System.out.println(mensajeQuantum);
                agregarLog(mensajeQuantum);
                procesoEjecutando = null;
            }
        }
    }
    
    private void actualizarColas() {
        planificador.actualizarColas();
        
        // Actualizar estados de procesos en cola de listos
        for (PCB proceso : planificador.getColaListos()) {
            if (proceso.getEstado() != Estado.LISTO) {
                proceso.setEstado(Estado.LISTO);
            }
        }
    }
    
    private void mostrarEstadoColas() {
        List<PCB> colaListos = planificador.getColaListos();
        if (!colaListos.isEmpty()) {
            StringBuilder sb = new StringBuilder("Cola de Listos: ");
            for (PCB p : colaListos) {
                sb.append(p.getNombre()).append("(").append(p.getInstruccionesRestantes()).append(") ");
            }
            System.out.println(sb.toString());
        }
        
        if (procesoEjecutando != null) {
            System.out.println("CPU: " + procesoEjecutando.getNombre() + 
                             " - Instrucción: " + procesoEjecutando.getProgramCounter());
        } else {
            System.out.println("CPU: Libre");
        }
    }
    
    private void agregarLog(String mensaje) {
        logEventos.add("Ciclo " + cicloGlobal + ": " + mensaje);
        // Mantener solo los últimos 100 logs para no consumir mucha memoria
        if (logEventos.size() > 100) {
            logEventos.remove(0);
        }
    }
    
    public void agregarProceso(PCB proceso) {
        proceso.setTiempoLlegada(cicloGlobal);
        proceso.setEstado(Estado.LISTO);
        planificador.agregarProceso(proceso);
        procesos.add(proceso);
        String mensaje = "➕ Nuevo proceso agregado: " + proceso.getNombre() + 
                         " (" + proceso.getTipo() + ", " + proceso.getTotalInstrucciones() + " instrucciones)";
        System.out.println(mensaje);
        agregarLog(mensaje);
    }
    
    public void cambiarPolitica(PoliticaPlanificacion nuevaPolitica) {
        // Guardar el proceso actual si existe
        PCB procesoActual = this.procesoEjecutando;
        
        // Reagregar todos los procesos listos al nuevo planificador
        List<PCB> procesosListos = planificador.getColaListos();
        
        // Crear nuevo planificador
        this.planificador = PlanificadorFactory.crearPlanificador(nuevaPolitica);
        
        // Configurar quantum si es Round Robin o Multinivel
        if (planificador instanceof RoundRobinPlanificador) {
            ((RoundRobinPlanificador) planificador).setQuantum(4); // Quantum por defecto
        } else if (planificador instanceof MultinivelPlanificador) {
            ((MultinivelPlanificador) planificador).setQuantum(4); // Quantum base
        }
        
        // Reagregar procesos listos
        for (PCB proceso : procesosListos) {
            if (proceso.getEstado() == Estado.LISTO) {
                planificador.agregarProceso(proceso);
            }
        }
        
        // Reagregar proceso actual si existe y no terminó
        if (procesoActual != null && !procesoActual.esTerminado()) {
            procesoActual.setEstado(Estado.LISTO);
            planificador.agregarProceso(procesoActual);
            this.procesoEjecutando = null;
        }
        
        String mensaje = "🔄 Política cambiada a: " + nuevaPolitica;
        System.out.println(mensaje);
        agregarLog(mensaje);
    }
    
    public void detenerSimulacion() {
        ejecutando = false;
        this.interrupt(); // Interrumpir el sleep si está esperando
    }
    
    // Métodos para métricas y estadísticas
    public double getThroughput() {
        if (cicloGlobal == 0) return 0;
        return (double) procesosCompletados / cicloGlobal;
    }
    
    public double getUtilizacionCPU() {
        if (cicloGlobal == 0) return 0;
        long ciclosOcupados = procesos.stream()
            .mapToInt(p -> p.getInstruccionesEjecutadas())
            .sum();
        return (double) ciclosOcupados / cicloGlobal;
    }
    
    public double getTiempoRespuestaPromedio() {
        if (procesosCompletados == 0) return 0;
        double sumaTiempos = procesos.stream()
            .filter(p -> p.getEstado() == Estado.TERMINADO)
            .mapToDouble(p -> p.getTiempoRetorno())
            .sum();
        return sumaTiempos / procesosCompletados;
    }
    
   public double getEquidad() {
    if (procesosCompletados == 0) return 1.0;
    
    double tiempoRespuestaPromedio = getTiempoRespuestaPromedio();
    if (tiempoRespuestaPromedio == 0) return 1.0;
    
    double sumaDiferencias = 0;
    int count = 0;
    
    for (PCB p : procesos) {
        if (p.getEstado() == Estado.TERMINADO) {
            double tiempoRetorno = p.getTiempoRetorno();
            if (tiempoRetorno > 0) {
                double diferencia = Math.abs(tiempoRetorno - tiempoRespuestaPromedio);
                sumaDiferencias += diferencia;
                count++;
            }
        }
    }
    
    if (count == 0) return 1.0;
    double desviacionPromedio = sumaDiferencias / count;
    
    // Equidad es inversamente proporcional a la desviación relativa
    double desviacionRelativa = desviacionPromedio / tiempoRespuestaPromedio;
    return 1.0 / (1.0 + desviacionRelativa);
}
    
    // Getters para la interfaz
    public int getCicloGlobal() { return cicloGlobal; }
    public PCB getProcesoEjecutando() { return procesoEjecutando; }
    public List<PCB> getProcesos() { return new ArrayList<>(procesos); }
    public List<PCB> getColaListos() { return planificador.getColaListos(); }
    public List<PCB> getProcesosTerminados() {
        List<PCB> terminados = new ArrayList<>();
        for (PCB p : procesos) {
            if (p.getEstado() == Estado.TERMINADO) {
                terminados.add(p);
            }
        }
        return terminados;
    }
    
    public List<String> getLogEventos() { return new ArrayList<>(logEventos); }
    public boolean isEjecutando() { return ejecutando; }
    public Configuracion getConfiguracion() { return configuracion; }
    public Planificador getPlanificador() { return planificador; }
    public int getProcesosCompletados() { return procesosCompletados; }
    
    // Métodos específicos para Multinivel
    public List<PCB> getColaMultinivelAlta() {
        if (planificador instanceof MultinivelPlanificador) {
            return ((MultinivelPlanificador) planificador).getColaAltaPrioridad();
        }
        return new ArrayList<>();
    }
    
    public List<PCB> getColaMultinivelMedia() {
        if (planificador instanceof MultinivelPlanificador) {
            return ((MultinivelPlanificador) planificador).getColaMediaPrioridad();
        }
        return new ArrayList<>();
    }
    
    public List<PCB> getColaMultinivelBaja() {
        if (planificador instanceof MultinivelPlanificador) {
            return ((MultinivelPlanificador) planificador).getColaBajaPrioridad();
        }
        return new ArrayList<>();
    }
    
    // Método para obtener métricas en formato de texto
    public String getMetricasTexto() {
        return String.format(
            "Métricas del Sistema:\n" +
            "Throughput: %.4f procesos/ciclo\n" +
            "Utilización CPU: %.2f%%\n" +
            "Tiempo Respuesta Promedio: %.2f ciclos\n" +
            "Equidad: %.2f\n" +
            "Procesos Completados: %d\n" +
            "Procesos en Sistema: %d\n" +
            "Ciclos Totales: %d",
            getThroughput(),
            getUtilizacionCPU() * 100,
            getTiempoRespuestaPromedio(),
            getEquidad(),
            procesosCompletados,
            procesos.size(),
            cicloGlobal
        );
    }
    
    // Método para reiniciar la simulación
    public void reiniciarSimulacion() {
        detenerSimulacion();
        try {
            this.join(1000); // Esperar a que el hilo termine
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Reiniciar variables
        this.procesos.clear();
        this.logEventos.clear();
        this.cicloGlobal = 0;
        this.procesoEjecutando = null;
        this.procesosCompletados = 0;
        this.planificador = PlanificadorFactory.crearPlanificador(configuracion.getPoliticaInicial());
        this.ejecutando = false;
    }
}
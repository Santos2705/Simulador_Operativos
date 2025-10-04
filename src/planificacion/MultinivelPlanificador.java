/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// planificacion/MultinivelPlanificador.java
package planificacion;

import modelo.PCB;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Queue;

public class MultinivelPlanificador implements Planificador {
    private Queue<PCB> colaAltaPrioridad;   // Round Robin para interactivos
    private Queue<PCB> colaMediaPrioridad;  // Round Robin con quantum mayor
    private Queue<PCB> colaBajaPrioridad;   // FCFS para procesos batch
    
    private int quantumAlta;
    private int quantumMedia;
    private int contadorQuantum;
    
    public MultinivelPlanificador() {
        this.colaAltaPrioridad = new LinkedList<>();
        this.colaMediaPrioridad = new LinkedList<>();
        this.colaBajaPrioridad = new LinkedList<>();
        this.quantumAlta = 2;
        this.quantumMedia = 4;
        this.contadorQuantum = 0;
    }
    
    @Override
    public PCB siguienteProceso() {
        PCB proceso = null;
        
        // Prioridad 1: Cola alta (Round Robin)
        if (!colaAltaPrioridad.isEmpty()) {
            proceso = colaAltaPrioridad.poll();
            contadorQuantum = 0; // Reiniciar contador para nuevo proceso
        }
        // Prioridad 2: Cola media (Round Robin)
        else if (!colaMediaPrioridad.isEmpty()) {
            proceso = colaMediaPrioridad.poll();
            contadorQuantum = 0;
        }
        // Prioridad 3: Cola baja (FCFS)
        else if (!colaBajaPrioridad.isEmpty()) {
            proceso = colaBajaPrioridad.poll();
            contadorQuantum = 0;
        }
        
        return proceso;
    }
    
    @Override
    public void agregarProceso(PCB proceso) {
        // Asignar a cola según prioridad y tipo de proceso
        if (proceso.getPrioridad() <= 3) {
            // Alta prioridad: procesos interactivos
            colaAltaPrioridad.offer(proceso);
        } else if (proceso.getPrioridad() <= 6) {
            // Media prioridad: procesos mixtos
            colaMediaPrioridad.offer(proceso);
        } else {
            // Baja prioridad: procesos batch
            colaBajaPrioridad.offer(proceso);
        }
    }
    
    @Override
    public void actualizarColas() {
        // En multinivel, los procesos pueden cambiar de cola basado en el envejecimiento
        // Implementación básica - podrías expandir esto
        envejecimientoProcesos();
    }
    
    private void envejecimientoProcesos() {
        // Mover procesos de cola baja a media después de cierto tiempo
        if (colaBajaPrioridad.size() > 2) {
            PCB procesoEnvejecido = ((LinkedList<PCB>) colaBajaPrioridad).peekFirst();
            if (procesoEnvejecido != null) {
                // Simular envejecimiento - en implementación real usarías tiempo de espera
                if (Math.random() < 0.1) { // 10% de probabilidad por ciclo
                    procesoEnvejecido = colaBajaPrioridad.poll();
                    procesoEnvejecido.setPrioridad(4); // Subir a prioridad media
                    colaMediaPrioridad.offer(procesoEnvejecido);
                }
            }
        }
    }
    
    @Override
    public List<PCB> getColaListos() {
        List<PCB> todasLasColas = new ArrayList<>();
        todasLasColas.addAll(colaAltaPrioridad);
        todasLasColas.addAll(colaMediaPrioridad);
        todasLasColas.addAll(colaBajaPrioridad);
        return todasLasColas;
    }
    
    @Override
    public void setQuantum(int quantum) {
        this.quantumAlta = Math.max(1, quantum / 2);
        this.quantumMedia = quantum;
    }
    
    // Métodos específicos para Multinivel
    public int getQuantumParaProceso(PCB proceso) {
        if (colaAltaPrioridad.contains(proceso)) {
            return quantumAlta;
        } else if (colaMediaPrioridad.contains(proceso)) {
            return quantumMedia;
        } else {
            return Integer.MAX_VALUE; // FCFS - sin quantum
        }
    }
    
    public void incrementarContadorQuantum() {
        contadorQuantum++;
    }
    
    public boolean quantumAgotado(PCB proceso) {
        if (colaAltaPrioridad.contains(proceso)) {
            return contadorQuantum >= quantumAlta;
        } else if (colaMediaPrioridad.contains(proceso)) {
            return contadorQuantum >= quantumMedia;
        }
        return false; // Cola baja no tiene quantum
    }
    
    public void reagregarProceso(PCB proceso) {
        if (proceso != null && !proceso.esTerminado()) {
            // Reagregar a la misma cola de donde vino
            if (proceso.getPrioridad() <= 3) {
                colaAltaPrioridad.offer(proceso);
            } else if (proceso.getPrioridad() <= 6) {
                colaMediaPrioridad.offer(proceso);
            } else {
                colaBajaPrioridad.offer(proceso);
            }
        }
    }
    
    // Métodos para obtener colas individuales (útiles para la interfaz)
    public List<PCB> getColaAltaPrioridad() {
        return new ArrayList<>(colaAltaPrioridad);
    }
    
    public List<PCB> getColaMediaPrioridad() {
        return new ArrayList<>(colaMediaPrioridad);
    }
    
    public List<PCB> getColaBajaPrioridad() {
        return new ArrayList<>(colaBajaPrioridad);
    }
    
    public String getEstadoColas() {
        return String.format("Multinivel [Alta: %d, Media: %d, Baja: %d]", 
                           colaAltaPrioridad.size(), 
                           colaMediaPrioridad.size(), 
                           colaBajaPrioridad.size());
    }
}

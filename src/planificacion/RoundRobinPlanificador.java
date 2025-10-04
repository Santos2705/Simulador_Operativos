/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// planificacion/RoundRobinPlanificador.java
package planificacion;

import modelo.PCB;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class RoundRobinPlanificador implements Planificador {
    private Queue<PCB> colaListos;
    private int quantum;
    private int contadorQuantum;
    
    public RoundRobinPlanificador() {
        this.colaListos = new LinkedList<>();
        this.quantum = 4; // Quantum por defecto
        this.contadorQuantum = 0;
    }
    
    @Override
    public PCB siguienteProceso() {
        if (colaListos.isEmpty()) {
            contadorQuantum = 0;
            return null;
        }
        
        PCB proceso = colaListos.poll();
        contadorQuantum = 0; // Reiniciar contador para nuevo proceso
        return proceso;
    }
    
    @Override
    public void agregarProceso(PCB proceso) {
        colaListos.offer(proceso);
    }
    
    @Override
    public void actualizarColas() {
        // Round Robin no necesita reordenamiento especial
    }
    
    @Override
    public List<PCB> getColaListos() {
        return new ArrayList<>(colaListos);
    }
    
    @Override
    public void setQuantum(int quantum) {
        this.quantum = quantum;
    }
    
    public int getQuantum() {
        return quantum;
    }
    
    public int getContadorQuantum() {
        return contadorQuantum;
    }
    
    public void incrementarContadorQuantum() {
        contadorQuantum++;
    }
    
    public boolean quantumAgotado() {
        return contadorQuantum >= quantum;
    }
    
    public void reagregarProceso(PCB proceso) {
        if (proceso != null && !proceso.esTerminado()) {
            colaListos.offer(proceso);
        }
    }
}

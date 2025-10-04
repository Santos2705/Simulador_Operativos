/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// planificacion/FCFSPlanificador.java
package planificacion;

import modelo.PCB;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FCFSPlanificador implements Planificador {
    private Queue<PCB> colaListos;
    
    public FCFSPlanificador() {
        this.colaListos = new LinkedList<>();
    }
    
    @Override
    public PCB siguienteProceso() {
        return colaListos.poll(); // Primer proceso en llegar
    }
    
    @Override
    public void agregarProceso(PCB proceso) {
        colaListos.offer(proceso);
    }
    
    @Override
    public void actualizarColas() {
        // Para FCFS no se necesita reordenamiento
    }
    
    @Override
    public List<PCB> getColaListos() {
        return new ArrayList<>(colaListos);
    }
    
    @Override
    public void setQuantum(int quantum) {
        // No aplica para FCFS
    }
}

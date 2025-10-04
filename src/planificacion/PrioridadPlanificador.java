/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// planificacion/PrioridadPlanificador.java
package planificacion;

import modelo.PCB;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class PrioridadPlanificador implements Planificador {
    private PriorityQueue<PCB> colaListos;
    
    public PrioridadPlanificador() {
        // Ordenar por prioridad (menor número = mayor prioridad)
        this.colaListos = new PriorityQueue<>(Comparator.comparingInt(PCB::getPrioridad));
    }
    
    @Override
    public PCB siguienteProceso() {
        return colaListos.poll(); // Proceso con mayor prioridad (menor número)
    }
    
    @Override
    public void agregarProceso(PCB proceso) {
        colaListos.offer(proceso);
    }
    
    @Override
    public void actualizarColas() {
        // La PriorityQueue mantiene el orden por prioridad
    }
    
    @Override
    public List<PCB> getColaListos() {
        return new ArrayList<>(colaListos);
    }
    
    @Override
    public void setQuantum(int quantum) {
        // No aplica para Prioridad
    }
}
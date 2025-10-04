/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// planificacion/SJFPlanificador.java
package planificacion;

import modelo.PCB;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class SJFPlanificador implements Planificador {
    private PriorityQueue<PCB> colaListos;
    
    public SJFPlanificador() {
        // Ordenar por la cantidad total de instrucciones (más corto primero)
        this.colaListos = new PriorityQueue<>(Comparator.comparingInt(PCB::getTotalInstrucciones));
    }
    
    @Override
    public PCB siguienteProceso() {
        return colaListos.poll(); // Proceso con menos instrucciones
    }
    
    @Override
    public void agregarProceso(PCB proceso) {
        colaListos.offer(proceso);
    }
    
    @Override
    public void actualizarColas() {
        // Para SJF no se necesita reordenamiento constante, la PriorityQueue mantiene el orden
    }
    
    @Override
    public List<PCB> getColaListos() {
        return new ArrayList<>(colaListos);
    }
    
    @Override
    public void setQuantum(int quantum) {
        // No aplica para SJF
    }
}

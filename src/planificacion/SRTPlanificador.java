/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// planificacion/SRTPlanificador.java
package planificacion;

import modelo.PCB;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class SRTPlanificador implements Planificador {
    private PriorityQueue<PCB> colaListos;
    
    public SRTPlanificador() {
        // Ordenar por instrucciones restantes (menor primero)
        this.colaListos = new PriorityQueue<>(Comparator.comparingInt(PCB::getInstruccionesRestantes));
    }
    
    @Override
    public PCB siguienteProceso() {
        return colaListos.poll(); // Proceso con menos instrucciones restantes
    }
    
    @Override
    public void agregarProceso(PCB proceso) {
        colaListos.offer(proceso);
    }
    
    @Override
    public void actualizarColas() {
        // La PriorityQueue mantiene automáticamente el orden
    }
    
    @Override
    public List<PCB> getColaListos() {
        return new ArrayList<>(colaListos);
    }
    
    @Override
    public void setQuantum(int quantum) {
        // No aplica para SRT
    }
}

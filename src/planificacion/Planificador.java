/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// planificacion/Planificador.java
package planificacion;

import modelo.PCB;
import java.util.List;

public interface Planificador {
    PCB siguienteProceso();
    void agregarProceso(PCB proceso);
    void actualizarColas();
    List<PCB> getColaListos();
    void setQuantum(int quantum); // Para Round Robin
}



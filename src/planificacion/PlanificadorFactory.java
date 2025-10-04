/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// planificacion/PlanificadorFactory.java
// planificacion/PlanificadorFactory.java
package planificacion;

import modelo.PoliticaPlanificacion;

public class PlanificadorFactory {
    public static Planificador crearPlanificador(PoliticaPlanificacion politica) {
        switch (politica) {
            case FCFS:
                return new FCFSPlanificador();
            case SJF:
                return new SJFPlanificador();
            case ROUND_ROBIN:
                return new RoundRobinPlanificador();
            case PRIORIDAD:
                return new PrioridadPlanificador();
            case SRT:
                return new SRTPlanificador();
            case MULTINIVEL:
                return new MultinivelPlanificador();
            default:
                return new FCFSPlanificador();
        }
    }
}

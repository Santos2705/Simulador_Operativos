/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// modelo/Configuracion.java
package modelo;

public class Configuracion {
    private int duracionCicloMs;
    private PoliticaPlanificacion politicaInicial;
    
    public Configuracion() {
        this.duracionCicloMs = 1000; // 1 segundo por defecto
        this.politicaInicial = PoliticaPlanificacion.FCFS;
    }
    
    // Getters y Setters
    public int getDuracionCicloMs() { return duracionCicloMs; }
    public void setDuracionCicloMs(int duracionCicloMs) { 
        this.duracionCicloMs = duracionCicloMs; 
    }
    
    public PoliticaPlanificacion getPoliticaInicial() { return politicaInicial; }
    public void setPoliticaInicial(PoliticaPlanificacion politicaInicial) { 
        this.politicaInicial = politicaInicial; 
    }
}



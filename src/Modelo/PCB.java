/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// modelo/PCB.java
// modelo/PCB.java
package modelo;

public class PCB {
    private final int id;
    private String nombre;
    private Estado estado;
    private int programCounter;
    private int memoryAddressRegister;
    private int totalInstrucciones;
    private int instruccionesEjecutadas;
    private TipoProceso tipo;
    private int ciclosParaExcepcion;
    private int ciclosParaSatisfacer;
    private int tiempoLlegada;
    private int tiempoFinalizacion;
    private int prioridad;

    // Constructor básico (4 parámetros)
    public PCB(int id, String nombre, int totalInstrucciones, TipoProceso tipo) {
        this.id = id;
        this.nombre = nombre;
        this.totalInstrucciones = totalInstrucciones;
        this.tipo = tipo;
        
        // Valores por defecto
        this.estado = Estado.NUEVO;
        this.programCounter = 0;
        this.memoryAddressRegister = 0;
        this.instruccionesEjecutadas = 0;
        this.ciclosParaExcepcion = 0;
        this.ciclosParaSatisfacer = 0;
        this.tiempoLlegada = 0;
        this.tiempoFinalizacion = 0;
        this.prioridad = 0;
    }
    
    
     public boolean debeGenerarExcepcion() {
        if (tipo == TipoProceso.IO_BOUND && ciclosParaExcepcion > 0) {
            // Generar excepción cada ciertos ciclos
            return (instruccionesEjecutadas % ciclosParaExcepcion) == 0 && 
                   instruccionesEjecutadas > 0 &&
                   !esTerminado();
        }
        return false;
    }
     
     public void atenderExcepcion() {
        if (tipo == TipoProceso.IO_BOUND && ciclosParaSatisfacer > 0) {
            // Simular que se está atendiendo la excepción
            // En una implementación real, esto llevaría varios ciclos
            System.out.println("Atendiendo excepción I/O para proceso: " + nombre);
        }
    }
     
     public int getTiempoRetorno() {
        if (estado == Estado.TERMINADO && tiempoFinalizacion > 0 && tiempoLlegada >= 0) {
            return tiempoFinalizacion - tiempoLlegada;
        }
        return 0;
    }
     
     public int getTiempoEspera(int cicloActual) {
        if (estado == Estado.TERMINADO) {
            return getTiempoRetorno() - totalInstrucciones;
        } else {
            return cicloActual - tiempoLlegada - instruccionesEjecutadas;
        }
    }
     
     

    // Constructor completo para procesos I/O Bound (6 parámetros) - AÑADIDO
    public PCB(int id, String nombre, int totalInstrucciones, TipoProceso tipo, 
               int ciclosParaExcepcion, int ciclosParaSatisfacer) {
        this(id, nombre, totalInstrucciones, tipo); // Llama al constructor básico
        this.ciclosParaExcepcion = ciclosParaExcepcion;
        this.ciclosParaSatisfacer = ciclosParaSatisfacer;
    }

    // Getters y Setters (mantener igual)
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    public int getProgramCounter() { return programCounter; }
    public void setProgramCounter(int programCounter) { this.programCounter = programCounter; }
    public int getMemoryAddressRegister() { return memoryAddressRegister; }
    public void setMemoryAddressRegister(int memoryAddressRegister) { this.memoryAddressRegister = memoryAddressRegister; }
    public int getTotalInstrucciones() { return totalInstrucciones; }
    public void setTotalInstrucciones(int totalInstrucciones) { this.totalInstrucciones = totalInstrucciones; }
    public int getInstruccionesEjecutadas() { return instruccionesEjecutadas; }
    public void setInstruccionesEjecutadas(int instruccionesEjecutadas) { this.instruccionesEjecutadas = instruccionesEjecutadas; }
    public TipoProceso getTipo() { return tipo; }
    public void setTipo(TipoProceso tipo) { this.tipo = tipo; }
    public int getCiclosParaExcepcion() { return ciclosParaExcepcion; }
    public void setCiclosParaExcepcion(int ciclosParaExcepcion) { this.ciclosParaExcepcion = ciclosParaExcepcion; }
    public int getCiclosParaSatisfacer() { return ciclosParaSatisfacer; }
    public void setCiclosParaSatisfacer(int ciclosParaSatisfacer) { this.ciclosParaSatisfacer = ciclosParaSatisfacer; }
    public int getTiempoLlegada() { return tiempoLlegada; }
    public void setTiempoLlegada(int tiempoLlegada) { this.tiempoLlegada = tiempoLlegada; }
    public int getTiempoFinalizacion() { return tiempoFinalizacion; }
    public void setTiempoFinalizacion(int tiempoFinalizacion) { this.tiempoFinalizacion = tiempoFinalizacion; }
    public int getPrioridad() { return prioridad; }
    public void setPrioridad(int prioridad) { this.prioridad = prioridad; }

    // Métodos útiles
    public boolean esTerminado() {
        return instruccionesEjecutadas >= totalInstrucciones;
    }
    
    public int getInstruccionesRestantes() {
        return totalInstrucciones - instruccionesEjecutadas;
    }
    
    public void ejecutarInstruccion() {
        if (instruccionesEjecutadas < totalInstrucciones) {
            instruccionesEjecutadas++;
            programCounter++;
        }
    }
    
    @Override
    public String toString() {
        return "PCB{" + "id=" + id + ", nombre=" + nombre + ", estado=" + estado + 
               ", PC=" + programCounter + ", instrucciones=" + instruccionesEjecutadas + 
               "/" + totalInstrucciones + ", tipo=" + tipo + '}';
    }
}
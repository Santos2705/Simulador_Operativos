/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// vista/ProcesoTableModel.java
package vista;

import modelo.PCB;
import modelo.Estado;
import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ArrayList;

public class ProcesoTableModel extends AbstractTableModel {
    private List<PCB> procesos;
    private final String[] columnNames = {"ID", "Nombre", "Estado", "PC", "Instrucciones", "Tipo", "Prioridad"};

    public ProcesoTableModel() {
        this.procesos = new ArrayList<>();
    }

    public void actualizarDatos(List<PCB> nuevosProcesos) {
        this.procesos = new ArrayList<>(nuevosProcesos);
        fireTableDataChanged(); // Notificar a la tabla que los datos cambiaron
    }

    @Override
    public int getRowCount() {
        return procesos.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex >= procesos.size()) return null;
        
        PCB proceso = procesos.get(rowIndex);
        switch (columnIndex) {
            case 0: return proceso.getId();
            case 1: return proceso.getNombre();
            case 2: return proceso.getEstado();
            case 3: return proceso.getProgramCounter();
            case 4: return proceso.getInstruccionesEjecutadas() + "/" + proceso.getTotalInstrucciones();
            case 5: return proceso.getTipo();
            case 6: return proceso.getPrioridad();
            default: return null;
        }
    }
}

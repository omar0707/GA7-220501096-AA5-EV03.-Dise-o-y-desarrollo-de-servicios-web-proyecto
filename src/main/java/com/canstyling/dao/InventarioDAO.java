package com.canstyling.dao;

import com.canstyling.model.InventarioInsumo;
import java.util.List;
import java.util.Optional;

public interface InventarioDAO {
    List<InventarioInsumo> listarTodos();
    Optional<InventarioInsumo> buscarPorId(int idProducto);
    List<InventarioInsumo> listarAlertasStockBajo();
    int registrar(InventarioInsumo insumo);
    boolean actualizarStock(int idProducto, int cantidadAjuste);
    boolean actualizar(InventarioInsumo insumo);
}

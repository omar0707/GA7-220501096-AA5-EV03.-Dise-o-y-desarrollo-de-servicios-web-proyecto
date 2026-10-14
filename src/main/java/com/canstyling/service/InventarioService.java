package com.canstyling.service;

import com.canstyling.dao.InventarioDAO;
import com.canstyling.dao.impl.InventarioDAOImpl;
import com.canstyling.model.InventarioInsumo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InventarioService {

    private final InventarioDAO inventarioDAO = new InventarioDAOImpl();

    public List<InventarioInsumo> listarInventario() {
        return inventarioDAO.listarTodos();
    }

    public List<InventarioInsumo> listarAlertasStock() {
        return inventarioDAO.listarAlertasStockBajo();
    }

    public Optional<InventarioInsumo> buscarPorId(int idProducto) {
        return inventarioDAO.buscarPorId(idProducto);
    }

    public Map<String, Object> registrarProducto(InventarioInsumo insumo) {
        Map<String, Object> res = new HashMap<>();
        if (insumo.getNombreProducto() == null || insumo.getCategoria() == null || insumo.getPrecioCompra() == null) {
            res.put("success", false);
            res.put("message", "Nombre, categoría y precio de compra son requeridos.");
            return res;
        }

        int id = inventarioDAO.registrar(insumo);
        if (id > 0) {
            insumo.setIdProducto(id);
            res.put("success", true);
            res.put("message", "Producto agregado al inventario exitosamente.");
            res.put("producto", insumo);
        } else {
            res.put("success", false);
            res.put("message", "Error al registrar el producto en la base de datos.");
        }
        return res;
    }

    public Map<String, Object> ajustarStock(int idProducto, int cantidadAjuste, String motivo) {
        Map<String, Object> res = new HashMap<>();
        Optional<InventarioInsumo> opt = inventarioDAO.buscarPorId(idProducto);
        if (opt.isEmpty()) {
            res.put("success", false);
            res.put("message", "Producto no encontrado en inventario.");
            return res;
        }

        boolean ok = inventarioDAO.actualizarStock(idProducto, cantidadAjuste);
        if (ok) {
            InventarioInsumo actualizado = inventarioDAO.buscarPorId(idProducto).orElse(null);
            res.put("success", true);
            res.put("message", "Stock actualizado exitosamente (" + motivo + ").");
            res.put("producto", actualizado);
            if (actualizado != null && actualizado.isStockBajo()) {
                res.put("alerta", "¡ADVERTENCIA: El stock actual (" + actualizado.getStockActual() + ") está por debajo o igual al mínimo (" + actualizado.getStockMinimo() + ")!");
            }
        } else {
            res.put("success", false);
            res.put("message", "No se pudo actualizar el stock (verifique que no resulte en stock negativo).");
        }
        return res;
    }
}

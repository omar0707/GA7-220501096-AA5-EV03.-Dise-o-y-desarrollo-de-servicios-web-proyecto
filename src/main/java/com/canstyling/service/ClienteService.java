package com.canstyling.service;

import com.canstyling.dao.ClienteDAO;
import com.canstyling.dao.MascotaDAO;
import com.canstyling.dao.UsuarioDAO;
import com.canstyling.dao.impl.ClienteDAOImpl;
import com.canstyling.dao.impl.MascotaDAOImpl;
import com.canstyling.dao.impl.UsuarioDAOImpl;
import com.canstyling.dto.RegistroWebClienteRequest;
import com.canstyling.dto.RegistroWhatsAppRequest;
import com.canstyling.model.Cliente;
import com.canstyling.model.ClienteTelefono;
import com.canstyling.model.Mascota;
import com.canstyling.model.Usuario;
import com.canstyling.security.SecurityUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ClienteService {

    private final ClienteDAO clienteDAO = new ClienteDAOImpl();
    private final MascotaDAO mascotaDAO = new MascotaDAOImpl();
    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    public List<Cliente> listarClientesConMascotas() {
        List<Cliente> clientes = clienteDAO.listarTodos();
        for (Cliente c : clientes) {
            c.setMascotas(mascotaDAO.listarPorCliente(c.getIdCliente()));
        }
        return clientes;
    }

    public Optional<Cliente> obtenerClientePorId(int idCliente) {
        Optional<Cliente> opt = clienteDAO.buscarPorId(idCliente);
        opt.ifPresent(c -> c.setMascotas(mascotaDAO.listarPorCliente(c.getIdCliente())));
        return opt;
    }

    /**
     * Canal 1: Registro de cliente y mascota desde la página web (Front-End).
     */
    public Map<String, Object> registrarDesdeWeb(RegistroWebClienteRequest req) {
        Map<String, Object> res = new HashMap<>();

        if (req.getCorreo() == null || !req.getCorreo().contains("@") || req.getNombres() == null || req.getTelefonoPrincipal() == null) {
            res.put("success", false);
            res.put("message", "Correo electrónico válido, nombres y teléfono principal son obligatorios.");
            return res;
        }

        // Verificar si el cliente ya existe por correo
        Optional<Cliente> existente = clienteDAO.buscarPorDocumento(req.getCorreo().trim().toLowerCase());
        if (existente.isPresent()) {
            res.put("success", false);
            res.put("message", "Ya existe un cliente registrado con el correo " + req.getCorreo());
            return res;
        }

        // 1. Crear o asociar en tabla usuarios (Rol 4 = CLIENTE) con password_hash
        String rawPassword = (req.getPassword() != null && !req.getPassword().trim().isEmpty())
                ? req.getPassword().trim()
                : "Password123*";
        String passwordHash = SecurityUtils.hashPassword(rawPassword);

        int idUsuario = 0;
        Optional<Usuario> userOpt = usuarioDAO.buscarPorCorreo(req.getCorreo().trim().toLowerCase());
        if (userOpt.isPresent()) {
            idUsuario = userOpt.get().getIdUsuario();
        } else {
            Usuario u = new Usuario();
            u.setIdRol(4); // CLIENTE
            u.setNombres(req.getNombres());
            u.setApellidos(req.getApellidos());
            u.setCorreo(req.getCorreo().trim().toLowerCase());
            u.setPasswordHash(passwordHash);
            u.setTelefono(req.getTelefonoPrincipal());
            u.setCanalRegistro("WEB");
            idUsuario = usuarioDAO.registrar(u);
        }

        // 2. Crear Cliente vinculado
        Cliente c = new Cliente();
        if (idUsuario > 0) {
            c.setIdUsuario(idUsuario);
        }
        c.setCorreo(req.getCorreo().trim().toLowerCase());
        c.setPasswordHash(passwordHash);
        c.setNombres(req.getNombres());
        c.setApellidos(req.getApellidos());
        c.setDireccion(req.getDireccion());
        c.setBarrioCiudad(req.getBarrioCiudad());
        c.setCanalOrigen("WEB");
        c.setObservaciones("Registrado a través del portal web de CanStyling");

        int idCliente = clienteDAO.registrar(c);
        if (idCliente <= 0) {
            res.put("success", false);
            res.put("message", "No se pudo registrar el cliente en la base de datos.");
            return res;
        }
        c.setIdCliente(idCliente);

        // 3. Registrar Teléfono Principal (WhatsApp)
        ClienteTelefono tel1 = new ClienteTelefono(0, idCliente, req.getTelefonoPrincipal(), "WHATSAPP", true, true);
        clienteDAO.registrarTelefono(tel1);

        // Registrar Teléfono Secundario si existe
        if (req.getTelefonoSecundario() != null && !req.getTelefonoSecundario().trim().isEmpty()) {
            ClienteTelefono tel2 = new ClienteTelefono(0, idCliente, req.getTelefonoSecundario(), "MOVIL", false, false);
            clienteDAO.registrarTelefono(tel2);
        }

        // 4. Registrar Mascota Inicial si se suministró
        if (req.getNombreMascota() != null && !req.getNombreMascota().trim().isEmpty()) {
            Mascota m = new Mascota();
            m.setIdCliente(idCliente);
            m.setIdRaza(req.getIdRaza() > 0 ? req.getIdRaza() : 5);
            m.setNombre(req.getNombreMascota());
            m.setGenero(req.getGeneroMascota() != null ? req.getGeneroMascota() : "MACHO");
            m.setColor(req.getColorMascota());
            m.setEdadAproximada(req.getEdadMeses());
            m.setPesoKg(req.getPesoKg());
            m.setTemperamento(req.getTemperamento());
            m.setAlergiasCondiciones(req.getAlergiasCondiciones());
            m.setVacunasAlDia(true);

            int idMascota = mascotaDAO.registrar(m);
            m.setIdMascota(idMascota);
            c.getMascotas().add(m);
        }

        res.put("success", true);
        res.put("message", "¡Registro web completado exitosamente! Bienvenido a CanStyling.");
        res.put("cliente", c);
        return res;
    }

    /**
     * Canal 2: Registro de cliente y mascota vía WhatsApp (Chatbot / Webhook).
     */
    public Map<String, Object> registrarDesdeWhatsApp(RegistroWhatsAppRequest req) {
        Map<String, Object> res = new HashMap<>();

        if (req.getNumeroWhatsApp() == null || req.getNombres() == null) {
            res.put("success", false);
            res.put("message", "Número de WhatsApp y nombres requeridos.");
            return res;
        }

        // Si ya existe por número de teléfono:
        Optional<Cliente> existente = clienteDAO.buscarPorTelefono(req.getNumeroWhatsApp());
        if (existente.isPresent()) {
            res.put("success", true);
            res.put("message", "Cliente ya existente en la base de datos.");
            res.put("cliente", existente.get());
            return res;
        }

        // 1. Generar credencial y hash seguro para el cliente de WhatsApp
        String rawPassword = (req.getPassword() != null && !req.getPassword().trim().isEmpty())
                ? req.getPassword().trim()
                : ("Wa" + req.getNumeroWhatsApp().trim());
        String passwordHash = SecurityUtils.hashPassword(rawPassword);
        String correoWA = "wa." + req.getNumeroWhatsApp().trim() + "@whatsapp.canstyling.com";

        // 2. Crear o asociar en tabla usuarios (Rol 4 = CLIENTE)
        int idUsuario = 0;
        Optional<Usuario> userOpt = usuarioDAO.buscarPorCorreo(correoWA);
        if (userOpt.isPresent()) {
            idUsuario = userOpt.get().getIdUsuario();
        } else {
            Usuario u = new Usuario();
            u.setIdRol(4); // CLIENTE
            u.setNombres(req.getNombres());
            u.setApellidos(req.getApellidos() != null ? req.getApellidos() : "(WhatsApp)");
            u.setCorreo(correoWA);
            u.setPasswordHash(passwordHash);
            u.setTelefono(req.getNumeroWhatsApp());
            u.setCanalRegistro("WHATSAPP");
            idUsuario = usuarioDAO.registrar(u);
        }

        // 3. Crear Cliente vinculado
        Cliente c = new Cliente();
        if (idUsuario > 0) {
            c.setIdUsuario(idUsuario);
        }
        c.setCorreo(correoWA);
        c.setPasswordHash(passwordHash);
        c.setNombres(req.getNombres());
        c.setApellidos(req.getApellidos() != null ? req.getApellidos() : "(WhatsApp)");
        c.setCanalOrigen("WHATSAPP");
        c.setObservaciones("Auto-registro mediante interacción con bot de WhatsApp (Credencial de acceso: " + rawPassword + ")");

        int idCliente = clienteDAO.registrar(c);
        if (idCliente <= 0) {
            res.put("success", false);
            res.put("message", "Error registrando cliente desde WhatsApp.");
            return res;
        }
        c.setIdCliente(idCliente);

        // Guardar teléfono
        clienteDAO.registrarTelefono(new ClienteTelefono(0, idCliente, req.getNumeroWhatsApp(), "WHATSAPP", true, true));

        // Registrar mascota si fue indicada
        if (req.getNombreMascota() != null && !req.getNombreMascota().trim().isEmpty()) {
            Mascota m = new Mascota();
            m.setIdCliente(idCliente);
            m.setIdRaza(5); // Mestizo por defecto
            m.setNombre(req.getNombreMascota());
            m.setGenero("MACHO");
            m.setColor("Sin especificar");
            m.setVacunasAlDia(true);
            mascotaDAO.registrar(m);
            c.getMascotas().add(m);
        }

        res.put("success", true);
        res.put("message", "Cliente y mascota registrados exitosamente vía WhatsApp con usuario y hash de seguridad asignados.");
        res.put("cliente", c);
        return res;
    }

    public int registrarMascota(Mascota mascota) {
        return mascotaDAO.registrar(mascota);
    }

    public boolean actualizarMascota(Mascota mascota) {
        return mascotaDAO.actualizar(mascota);
    }


    public boolean eliminarCliente(int id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

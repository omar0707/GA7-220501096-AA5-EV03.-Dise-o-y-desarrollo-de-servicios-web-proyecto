package com.canstyling.service;

import com.canstyling.dao.CitaDAO;
import com.canstyling.dao.ConfiguracionDAO;
import com.canstyling.dao.impl.CitaDAOImpl;
import com.canstyling.dao.impl.ConfiguracionDAOImpl;
import com.canstyling.dto.AgendarCitaRequest;
import com.canstyling.model.Cita;
import com.canstyling.security.SecurityUtils;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CitaService {

    private final CitaDAO citaDAO = new CitaDAOImpl();
    private final ConfiguracionDAO configuracionDAO = new ConfiguracionDAOImpl();

    public List<Cita> listarCitas(String estado) {
        return citaDAO.listarTodas(estado);
    }

    public Optional<Cita> buscarPorId(int idCita) {
        return citaDAO.buscarPorId(idCita);
    }

    public Optional<Cita> buscarPorTokenAntiIDOR(String tokenAcceso) {
        return citaDAO.buscarPorToken(tokenAcceso);
    }

    /**
     * Agendar una nueva cita generando token Anti-IDOR y validando límites.
     */
    public Map<String, Object> agendarCita(AgendarCitaRequest req) {
        Map<String, Object> res = new HashMap<>();

        if (req.getIdCliente() <= 0 || req.getIdMascota() <= 0 || req.getIdServicio() <= 0 || req.getFechaCita() == null) {
            res.put("success", false);
            res.put("message", "Cliente, mascota, servicio y fecha son campos obligatorios.");
            return res;
        }

        LocalDate fecha;
        try {
            fecha = LocalDate.parse(req.getFechaCita());
        } catch (Exception e) {
            res.put("success", false);
            res.put("message", "Formato de fecha inválido (use YYYY-MM-DD).");
            return res;
        }

        // Validación 1: No permitir fechas pasadas
        if (fecha.isBefore(LocalDate.now())) {
            res.put("success", false);
            res.put("message", "No es posible agendar citas en fechas anteriores al día de hoy.");
            return res;
        }

        // Validación 2: Días cerrados configurados (0=Domingo, 1=Lunes, etc.)
        String diasCerradosStr = configuracionDAO.obtenerPorClave("dias_cerrados");
        if (diasCerradosStr == null) diasCerradosStr = "0";
        int dayOfWeek = fecha.getDayOfWeek().getValue() % 7;
        List<String> cerrados = Arrays.asList(diasCerradosStr.split(","));
        if (cerrados.contains(String.valueOf(dayOfWeek))) {
            res.put("success", false);
            res.put("message", "El establecimiento se encuentra cerrado en el día seleccionado según la configuración del negocio.");
            return res;
        }

        // Validación 3: Regla de Negocio: Máximo 1 cita activa por mascota (Requisito 19)
        int citasActivasMascota = citaDAO.contarCitasActivasPorMascota(req.getIdMascota());
        if (citasActivasMascota > 0) {
            res.put("success", false);
            res.put("message", "Esta mascota ya cuenta con una cita activa (pendiente o en proceso). Debe completarse antes de solicitar una nueva reserva.");
            return res;
        }

        Cita c = new Cita();
        c.setTokenAcceso(SecurityUtils.generarTokenAntiIDOR()); // Generación Anti-IDOR UUID
        c.setIdCliente(req.getIdCliente());
        c.setIdMascota(req.getIdMascota());
        c.setIdServicio(req.getIdServicio());
        c.setIdEstilista(req.getIdEstilista() != null && req.getIdEstilista() > 0 ? req.getIdEstilista() : 1);
        c.setDuracionMinutos(req.getDuracionMinutos() != null && req.getDuracionMinutos() > 0 ? req.getDuracionMinutos() : 60);

        try {
            c.setFechaCita(Date.valueOf(fecha));
            c.setHoraCita(Time.valueOf(req.getHoraCita() != null ? req.getHoraCita() : "09:00:00"));
        } catch (IllegalArgumentException e) {
            res.put("success", false);
            res.put("message", "Formato de hora (HH:MM:SS) inválido.");
            return res;
        }

        c.setEstadoCita("PENDIENTE");
        c.setOrigenReserva(req.getOrigenReserva() != null ? req.getOrigenReserva() : "WEB");
        c.setObservacionesCliente(req.getObservaciones());

        int idCita = citaDAO.agendar(c);
        if (idCita > 0) {
            c.setIdCita(idCita);
            res.put("success", true);
            res.put("message", "Cita agendada exitosamente en CanStyling.");
            res.put("cita", c);
            res.put("tokenAcceso", c.getTokenAcceso());
        } else {
            res.put("success", false);
            res.put("message", "Error al registrar la cita en la base de datos.");
        }
        return res;
    }

    public boolean completarCita(int idCita) {
        return citaDAO.cambiarEstado(idCita, "COMPLETADA");
    }

    public boolean cambiarEstado(int idCita, String nuevoEstado) {
        return citaDAO.cambiarEstado(idCita, nuevoEstado);
    }

    public boolean actualizarEstadoYNotas(int idCita, String nuevoEstado, String notas) {
        return citaDAO.actualizarEstadoYNotas(idCita, nuevoEstado, notas);
    }

    public boolean eliminarFoto(int idFoto) {
        return citaDAO.eliminarFoto(idFoto);
    }

    public boolean eliminarTodasLasFotos(int idCita) {
        return citaDAO.eliminarFotosPorCita(idCita);
    }

    public boolean cancelarCita(int idCita) {
        return citaDAO.cambiarEstado(idCita, "CANCELADA");
    }

    public Map<String, Object> cancelarCitaConPolitica(int idCita, boolean esAdminORecepcion) {
        Map<String, Object> res = new HashMap<>();
        Optional<Cita> opt = citaDAO.buscarPorId(idCita);
        if (!opt.isPresent()) {
            res.put("success", false);
            res.put("message", "Cita no encontrada con ID: " + idCita);
            return res;
        }

        Cita c = opt.get();
        if ("CANCELADA".equalsIgnoreCase(c.getEstadoCita())) {
            res.put("success", false);
            res.put("message", "La cita ya se encuentra cancelada.");
            return res;
        }

        if ("COMPLETADA".equalsIgnoreCase(c.getEstadoCita())) {
            res.put("success", false);
            res.put("message", "No es posible cancelar una cita que ya fue atendida y completada.");
            return res;
        }

        if ("EN_PROCESO".equalsIgnoreCase(c.getEstadoCita()) || "PENDIENTE_APROBACION".equalsIgnoreCase(c.getEstadoCita())) {
            res.put("success", false);
            res.put("message", "No es posible cancelar el servicio porque ya se encuentra en atención en la mesa de peluquería o en revisión de calidad.");
            return res;
        }

        if (!esAdminORecepcion) {
            String horasPolStr = configuracionDAO.obtenerPorClave("politica_cancelacion_horas");
            long horasMinimas = 24;
            try {
                if (horasPolStr != null) horasMinimas = Long.parseLong(horasPolStr.trim());
            } catch (Exception ignored) {}

            LocalDate fechaCita = c.getFechaCita().toLocalDate();
            LocalTime horaCita = c.getHoraCita().toLocalTime();
            LocalDateTime momentoCita = LocalDateTime.of(fechaCita, horaCita);
            LocalDateTime ahora = LocalDateTime.now();

            long horasRestantes = ChronoUnit.HOURS.between(ahora, momentoCita);
            if (horasRestantes < horasMinimas) {
                res.put("success", false);
                res.put("message", "La política del establecimiento exige un mínimo de " + horasMinimas + " horas de anticipación para cancelar desde el portal del cliente. Por favor contacte a recepción.");
                return res;
            }
        }

        boolean ok = citaDAO.cambiarEstado(idCita, "CANCELADA");
        if (ok) {
            res.put("success", true);
            res.put("message", "Cita #" + idCita + " cancelada exitosamente.");
        } else {
            res.put("success", false);
            res.put("message", "Error al actualizar el estado de la cita.");
        }
        return res;
    }
}

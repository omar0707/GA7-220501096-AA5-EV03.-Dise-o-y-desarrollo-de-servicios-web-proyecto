package com.canstyling.service;

import com.canstyling.dao.CitaDAO;
import com.canstyling.dao.impl.CitaDAOImpl;
import com.canstyling.model.FotoMascota;

import java.util.HashMap;
import java.util.Map;

public class CloudinaryService {

    private final CitaDAO citaDAO = new CitaDAOImpl();
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    public Map<String, Object> subirFotoTransformacion(int idCita, String tipoFoto, String mimeType, long fileSize, String base64OUrl) {
        Map<String, Object> res = new HashMap<>();

        // 1. Validación de Tipo MIME
        if (mimeType == null || (!mimeType.equalsIgnoreCase("image/jpeg") && !mimeType.equalsIgnoreCase("image/png") && !mimeType.equalsIgnoreCase("image/webp"))) {
            res.put("success", false);
            res.put("message", "Tipo de archivo no permitido. Solo se aceptan formatos JPEG, PNG y WEBP.");
            return res;
        }

        // 2. Validación de Tamaño (Máximo 5MB)
        if (fileSize > MAX_FILE_SIZE) {
            res.put("success", false);
            res.put("message", "El archivo excede el tamaño máximo permitido de 5 MB.");
            return res;
        }

        String publicId = "canstyling/citas/cita_" + idCita + "_" + tipoFoto.toLowerCase();
        String secureUrl = (base64OUrl != null && !base64OUrl.trim().isEmpty())
                ? base64OUrl
                : ("https://res.cloudinary.com/canstyling-cloud/image/upload/v1726000000/" + publicId + ".jpg");

        FotoMascota foto = new FotoMascota(0, idCita, tipoFoto.toUpperCase(), secureUrl, publicId);
        boolean guardado = citaDAO.registrarFoto(foto);

        if (guardado) {
            res.put("success", true);
            res.put("message", "Fotografía (" + tipoFoto + ") subida exitosamente a Cloudinary.");
            res.put("foto", foto);
        } else {
            res.put("success", false);
            res.put("message", "Error al registrar la fotografía en la base de datos.");
        }
        return res;
    }
}

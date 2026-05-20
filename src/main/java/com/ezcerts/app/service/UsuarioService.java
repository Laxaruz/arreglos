package com.ezcerts.app.service;

import com.ezcerts.app.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    Usuario crearUsuario(Usuario usuario);
    
    void registrarUsuarioYEmpleado(com.ezcerts.app.dto.UsuarioCrearDto dto) throws Exception;

    Usuario actualizarUsuario(Usuario usuario);

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorUsername(String username);

    Optional<Usuario> buscarPorCorreo(String correo);

    List<Usuario> listar();
    
    List<Usuario> buscarUsuarios(String parametro);

    void inactivarUsuario(Long id);
    
    void actualizarUsuarioYEmpleado(Long id, com.ezcerts.app.dto.UsuarioCrearDto dto) throws Exception;
    
    void eliminarUsuario(Long id);
}

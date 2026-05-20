package com.ezcerts.app.service;

import com.ezcerts.app.model.Usuario;
import com.ezcerts.app.repository.UsuarioRepository;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrCorreo) throws UsernameNotFoundException {
        logger.debug("Attempting to load user by usernameOrCorreo='{}'", usernameOrCorreo);

        var opt = usuarioRepository.findByUsername(usernameOrCorreo)
                .or(() -> usuarioRepository.findByCorreo(usernameOrCorreo));

        if (opt.isEmpty()) {
            logger.warn("Usuario no encontrado por username o correo: {}", usernameOrCorreo);
            throw new UsernameNotFoundException("Usuario no encontrado");
        }

        Usuario usuario = opt.get();
        logger.debug("Usuario encontrado: username='{}', correo='{}', activo={}, hashLen={}",
                usuario.getUsername(), usuario.getCorreo(), usuario.isActivo(),
                usuario.getContrasenaHash() == null ? 0 : usuario.getContrasenaHash().length());

        if (!usuario.isActivo()) {
            logger.warn("Usuario inactivo: {}", usuario.getUsername());
            throw new DisabledException("Usuario inactivo");
        }

        return User.withUsername(usuario.getUsername())
                .password(usuario.getContrasenaHash())
                .authorities(Collections.singleton(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())))
                .build();
    }
}

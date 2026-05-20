package com.ezcerts.app.config;

import com.ezcerts.app.model.Usuario;
import com.ezcerts.app.repository.UsuarioRepository;
import java.util.Collections;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomAuthenticationProvider(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String usernameOrCorreo = (authentication.getName() == null) ? "" : authentication.getName();
        String raw = (authentication.getCredentials() == null) ? "" : authentication.getCredentials().toString();

        var opt = usuarioRepository.findByUsername(usernameOrCorreo)
                .or(() -> usuarioRepository.findByCorreo(usernameOrCorreo));

        if (opt.isEmpty()) {
            throw new BadCredentialsException("Usuario no encontrado");
        }

        Usuario u = opt.get();
        if (!u.isActivo()) {
            throw new DisabledException("Usuario inactivo");
        }

        String stored = u.getContrasenaHash();
        boolean ok = false;

        if (stored != null) {
            // If stored looks like a bcrypt hash, verify using encoder
            String s = stored;
            if (s.startsWith("$2a$") || s.startsWith("$2b$") || s.startsWith("$2y$")) {
                ok = passwordEncoder.matches(raw, s);
            } else {
                // treat as plaintext
                ok = s.equals(raw);
            }
        }

        if (!ok) {
            throw new BadCredentialsException("Credenciales invalidas");
        }

        var authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_" + u.getRol().name()));
        return new UsernamePasswordAuthenticationToken(u.getUsername(), stored, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

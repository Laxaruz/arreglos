package com.ezcerts.app.controller;

import com.ezcerts.app.model.Usuario;
import com.ezcerts.app.repository.UsuarioRepository;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
public class DebugController {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DebugController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/debug/user")
    public ResponseEntity<?> findUser(@RequestParam String q) {
        Optional<Usuario> byUsername = usuarioRepository.findByUsername(q);
        if (byUsername.isPresent()) {
            Usuario u = byUsername.get();
            return ResponseEntity.ok(new UserDto(u));
        }
        Optional<Usuario> byCorreo = usuarioRepository.findByCorreo(q);
        if (byCorreo.isPresent()) {
            Usuario u = byCorreo.get();
            return ResponseEntity.ok(new UserDto(u));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/debug/checkPassword")
    public ResponseEntity<?> checkPassword(@RequestParam String user, @RequestParam String raw) {
        Optional<Usuario> opt = usuarioRepository.findByUsername(user).or(() -> usuarioRepository.findByCorreo(user));
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Usuario u = opt.get();
        String hash = u.getContrasenaHash();
        boolean matches = false;
        if (hash != null) matches = passwordEncoder.matches(raw, hash);
        return ResponseEntity.ok(new CheckDto(u.getUsername(), u.getCorreo(), hash == null ? 0 : hash.length(), matches, hash == null ? null : (hash.length() > 10 ? hash.substring(0,10)+"..." : hash)));
    }

    @GetMapping("/debug/generateHash")
    public ResponseEntity<?> generateHash(@RequestParam String raw) {
        String hash = passwordEncoder.encode(raw);
        return ResponseEntity.ok(java.util.Collections.singletonMap("hash", hash));
    }

    static class CheckDto {
        public String username;
        public String correo;
        public int hashLength;
        public boolean matches;
        public String hashPreview;

        CheckDto(String username, String correo, int hashLength, boolean matches, String hashPreview) {
            this.username = username;
            this.correo = correo;
            this.hashLength = hashLength;
            this.matches = matches;
            this.hashPreview = hashPreview;
        }
    }

    static class UserDto {
        public Long id;
        public String username;
        public String correo;
        public boolean activo;
        public int hashLength;
        public String hashPreview;

        UserDto(Usuario u) {
            this.id = u.getId();
            this.username = u.getUsername();
            this.correo = u.getCorreo();
            this.activo = u.isActivo();
            this.hashLength = u.getContrasenaHash() == null ? 0 : u.getContrasenaHash().length();
            String h = u.getContrasenaHash();
            this.hashPreview = h == null ? null : (h.length() > 8 ? h.substring(0, 8) + "..." : h);
        }
    }
}

package com.sigel.SigelApi.service;

import com.sigel.SigelApi.dto.RegistroRequest;
import com.sigel.SigelApi.enums.UserRole;
import com.sigel.SigelApi.model.Usuario;
import com.sigel.SigelApi.repository.UsuarioRepository;
import com.sigel.SigelApi.service.implementation.UsuarioImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UsuarioImpl {
    private final UsuarioRepository repository;
    private final GrupoService grupoService;

    @Override
    public List<Usuario> listar() {
        return repository.findAll();
    }

    @Override
    public Usuario buscarPorId(Long id, RuntimeException exception) {
        return repository.findById(id).orElseThrow(() -> exception);
    }

    @Override
    public Usuario buscarPorEmail(String email, RuntimeException exception) {
        return repository.findByEmail(email).orElseThrow(() -> exception);
    }

    @Override
    public Usuario buscarPorMatricula(String matricula, RuntimeException exception) {
        return repository.findByMatricula(matricula).orElseThrow(() -> exception);
    }

    @Override
    public Usuario buscarPorClaveDocente(String claveDocente, RuntimeException exception) {
        return repository.findByClaveDocente(claveDocente).orElseThrow(() -> exception);
    }

    @Override
    public boolean existeEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean existeMatricula(String matricula) {
        return repository.existsByMatricula(matricula);
    }

    @Override
    public boolean existeClaveDocente(String claveDocente) {
        return repository.existsByClaveDocente(claveDocente);
    }

    @Override
    public void desactivarUsuario(Usuario usuario) {
        repository.delete(usuario);
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        return repository.save(usuario);
    }

    @Override
    public Usuario construir(RegistroRequest request, String passwordHash) {
        return Usuario.builder()
                .matricula(request.getMatricula())
                .email(request.getEmail())
                .passwordHash(passwordHash)
                .nombre(request.getNombre())
                .apellidoPaterno(request.getApellidoPaterno())
                .apellidoMaterno(request.getApellidoMaterno())
                .telefono(request.getTelefono())
                .rol(request.getRol() != null ? request.getRol() : UserRole.ALUMNO)
                .grupo(request.getGrupoId() != null ? grupoService.buscarPorId(request.getGrupoId()) : null)
                .fechaIngreso(request.getFechaIngreso())
                .claveDocente(request.getClaveDocente())
                .build();
    }

    @Override
    public void aplicarUltimoAcceso(Usuario usuario) {
        usuario.setUltimoAcceso(LocalDateTime.now());
        repository.save(usuario);
    }
}
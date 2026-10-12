package icop.tp8gabrielukf.productos.service;

import icop.tp8gabrielukf.productos.dto.ProductoRequestDTO;
import icop.tp8gabrielukf.productos.dto.ProductoResponseDTO;
import icop.tp8gabrielukf.productos.entity.Producto;
import icop.tp8gabrielukf.productos.exception.ProductoNotFoundException;
import icop.tp8gabrielukf.productos.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository repository;

    // Inyección de dependencias por constructor
    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    public ProductoResponseDTO crear(ProductoRequestDTO dto) {
        validar(dto);
        Producto guardado = repository.save(toEntity(dto));
        return toResponseDTO(guardado);
    }

    public List<ProductoResponseDTO> listar() {
        return repository.findAll().stream().map(this::toResponseDTO).toList();
    }

    public ProductoResponseDTO obtenerPorId(Long id) throws ProductoNotFoundException {
        Producto producto = repository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));
        return toResponseDTO(producto);
    }

    public ProductoResponseDTO actualizar(Long id, ProductoRequestDTO dto)
            throws ProductoNotFoundException {
        Producto existente = repository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));
        validar(dto);
        existente.setNombre(dto.getNombre());
        existente.setPrecio(dto.getPrecio());
        existente.setStock(dto.getStock());
        return toResponseDTO(repository.save(existente));
    }

    public void eliminar(Long id) throws ProductoNotFoundException {
        Producto existente = repository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));
        repository.delete(existente);
    }

    public List<ProductoResponseDTO> buscarPorNombre(String nombre) {
        return repository.findByNombre(nombre).stream().map(this::toResponseDTO).toList();
    }

    // ---------- Reglas de negocio ----------

    private void validar(ProductoRequestDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo ni vacío.");
        }
        if (dto.getPrecio() == null || dto.getPrecio() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que 0.");
        }
        if (dto.getStock() == null || dto.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser nulo ni negativo.");
        }
    }

    // ---------- Conversiones DTO <-> entidad ----------

    private Producto toEntity(ProductoRequestDTO dto) {
        Producto p = new Producto();
        p.setNombre(dto.getNombre());
        p.setPrecio(dto.getPrecio());
        p.setStock(dto.getStock());
        return p;
    }

    private ProductoResponseDTO toResponseDTO(Producto p) {
        return new ProductoResponseDTO(
                p.getIdProducto(),
                p.getNombre(),
                p.getPrecio(),
                p.getStock(),
                p.getStock() > 0);
    }
}

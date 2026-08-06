package ma.lpee.lpeebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProduitRequestDTO {
    @NotNull(message = "L'ID de la famille est obligatoire")
    private Long idFamille;

    @NotBlank(message = "Le code produit est obligatoire")
    private String codeProduit;

    @NotBlank(message = "Le nom du produit est obligatoire")
    private String nomProduit;
}
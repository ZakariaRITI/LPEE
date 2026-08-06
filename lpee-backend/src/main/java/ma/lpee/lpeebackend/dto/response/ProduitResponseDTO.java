package ma.lpee.lpeebackend.dto.response;

import lombok.Data;

@Data
public class ProduitResponseDTO {
    private Long idProduit;
    private Long idFamille;
    private String codeProduit;
    private String nomProduit;
}
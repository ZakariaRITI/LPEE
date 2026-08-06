package ma.lpee.lpeebackend.dto.response;

import lombok.Data;

@Data
public class OrganismeResponseDTO {
    private Long idOrganisme;
    private String codeOrganisme;
    private String nomOrganisme;
    private String imageOrganisme;
}
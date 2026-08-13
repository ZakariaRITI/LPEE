package ma.lpee.lpeebackend.service;

import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.response.HistoriqueActionResponseDTO;
import ma.lpee.lpeebackend.entity.Utilisateur;
import ma.lpee.lpeebackend.repository.ConformiteNormeRepository;
import ma.lpee.lpeebackend.repository.DocumentationEssaiRepository;
import ma.lpee.lpeebackend.repository.EquipementEssaiRepository;
import ma.lpee.lpeebackend.repository.EssaiParametreRepository;
import ma.lpee.lpeebackend.repository.PublicationNormeRepository;
import ma.lpee.lpeebackend.repository.RealisationEssaiRepository;
import ma.lpee.lpeebackend.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoriqueService {

    private final ConformiteNormeRepository conformiteNormeRepository;
    private final DocumentationEssaiRepository documentationEssaiRepository;
    private final EquipementEssaiRepository equipementEssaiRepository;
    private final EssaiParametreRepository essaiParametreRepository;
    private final PublicationNormeRepository publicationNormeRepository;
    private final RealisationEssaiRepository realisationEssaiRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Transactional(readOnly = true)
    public List<HistoriqueActionResponseDTO> findAll() {
        Map<Long, Utilisateur> users = utilisateurRepository.findAll().stream()
                .collect(Collectors.toMap(Utilisateur::getIdUser, Function.identity()));
        List<HistoriqueActionResponseDTO> actions = new ArrayList<>();

        conformiteNormeRepository.findAll().forEach(entity -> addActions(actions, users,
                entity.getEssai().getNumeroEssai(), "Norme", entity.getNorme().getNumeroNorme(),
                entity.getCreePar(), entity.getCreeLe(), entity.getModifiePar(), entity.getModifieLe(),
                entity.getAnnulePar(), entity.getAnnuleLe()));
        documentationEssaiRepository.findAll().forEach(entity -> addActions(actions, users,
                entity.getEssai().getNumeroEssai(), "Produit", entity.getEssai().getProduit().getNomProduit(),
                entity.getCreePar(), entity.getCreeLe(), entity.getModifiePar(), entity.getModifieLe(),
                entity.getAnnulePar(), entity.getAnnuleLe()));
        equipementEssaiRepository.findAll().forEach(entity -> addActions(actions, users,
                entity.getEssai().getNumeroEssai(), "Équipement",
                entity.getEquipement().getDesignation() + " " + entity.getEquipement().getNumeroSerie(),
                entity.getCreePar(), entity.getCreeLe(), entity.getModifiePar(), entity.getModifieLe(),
                entity.getAnnulePar(), entity.getAnnuleLe()));
        essaiParametreRepository.findAll().forEach(entity -> addActions(actions, users,
                entity.getEssai().getNumeroEssai(), "Produit", entity.getEssai().getProduit().getNomProduit(),
                entity.getCreePar(), entity.getCreeLe(), entity.getModifiePar(), entity.getModifieLe(),
                entity.getAnnulePar(), entity.getAnnuleLe()));
        publicationNormeRepository.findAll().forEach(entity -> addActions(actions, users,
                "—", "Norme", entity.getNorme().getNumeroNorme(),
                entity.getCreePar(), entity.getCreeLe(), entity.getModifiePar(), entity.getModifieLe(),
                entity.getAnnulePar(), entity.getAnnuleLe()));
        realisationEssaiRepository.findAll().forEach(entity -> addActions(actions, users,
                entity.getEssai().getNumeroEssai(), "Unité", entity.getUnite().getNomUnite(),
                entity.getCreePar(), entity.getCreeLe(), entity.getModifiePar(), entity.getModifieLe(),
                entity.getAnnulePar(), entity.getAnnuleLe()));

        actions.sort(Comparator.comparing(this::timestamp).reversed());
        return actions;
    }

    private void addActions(List<HistoriqueActionResponseDTO> actions, Map<Long, Utilisateur> users,
                            String essaiConcerne, String actionSur, String detail,
                            Long creePar, LocalDateTime creeLe,
                            Long modifiePar, LocalDateTime modifieLe,
                            Long annulePar, LocalDateTime annuleLe) {
        addAction(actions, users, "Creation", essaiConcerne, actionSur, detail, creePar, creeLe);
        addAction(actions, users, "Modification", essaiConcerne, actionSur, detail, modifiePar, modifieLe);
        addAction(actions, users, "Suppression", essaiConcerne, actionSur, detail, annulePar, annuleLe);
    }

    private void addAction(List<HistoriqueActionResponseDTO> actions, Map<Long, Utilisateur> users,
                           String action, String essaiConcerne, String actionSur,
                           String detail, Long idUser, LocalDateTime timestamp) {
        if (idUser == null || timestamp == null) {
            return;
        }
        Utilisateur user = users.get(idUser);
        actions.add(new HistoriqueActionResponseDTO(
                action,
                essaiConcerne,
                actionSur,
                detail,
                idUser,
                user != null ? user.getMatricule() : "Utilisateur #" + idUser,
                user != null ? user.getNomUser() : "Utilisateur supprimé",
                timestamp.toLocalDate(),
                timestamp.toLocalTime()));
    }

    private LocalDateTime timestamp(HistoriqueActionResponseDTO action) {
        return LocalDateTime.of(action.date(), action.heure());
    }
}

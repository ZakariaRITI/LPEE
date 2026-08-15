package ma.lpee.lpeebackend.service;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.response.ChampModifieResponseDTO;
import ma.lpee.lpeebackend.dto.response.HistoriqueActionResponseDTO;
import ma.lpee.lpeebackend.entity.HistoriqueAction;
import ma.lpee.lpeebackend.entity.Utilisateur;
import ma.lpee.lpeebackend.repository.ConformiteNormeRepository;
import ma.lpee.lpeebackend.repository.DocumentationEssaiRepository;
import ma.lpee.lpeebackend.repository.EquipementEssaiRepository;
import ma.lpee.lpeebackend.repository.EssaiParametreRepository;
import ma.lpee.lpeebackend.repository.PublicationNormeRepository;
import ma.lpee.lpeebackend.repository.RealisationEssaiRepository;
import ma.lpee.lpeebackend.repository.HistoriqueActionRepository;
import ma.lpee.lpeebackend.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private final HistoriqueActionRepository historiqueActionRepository;
    private final ObjectMapper objectMapper;
    private final AuditTrailService auditTrailService;

    @Transactional(readOnly = true)
    public List<HistoriqueActionResponseDTO> findAll() {
        Map<Long, Utilisateur> users = utilisateurRepository.findAll().stream()
                .collect(Collectors.toMap(Utilisateur::getIdUser, Function.identity()));
        List<HistoriqueActionResponseDTO> actions = new ArrayList<>();
        List<HistoriqueActionResponseDTO> legacyActions = new ArrayList<>();
        List<HistoriqueAction> persistedActions = historiqueActionRepository.findAllByOrderByDateHeureDesc();

        persistedActions.stream()
                .map(this::toResponse)
                .filter(this::isUseful)
                .forEach(actions::add);

        conformiteNormeRepository.findAll().forEach(entity -> addActions(legacyActions, users,
                entity.getEssai().getNumeroEssai(), "Norme", entity.getNorme().getNumeroNorme(),
                entity.getCreePar(), entity.getCreeLe(), entity.getModifiePar(), entity.getModifieLe(),
                entity.getAnnulePar(), entity.getAnnuleLe()));
        documentationEssaiRepository.findAll().forEach(entity -> addActions(legacyActions, users,
                entity.getEssai().getNumeroEssai(), "Document", entity.getDocument().getNomDocument(),
                entity.getCreePar(), entity.getCreeLe(), entity.getModifiePar(), entity.getModifieLe(),
                entity.getAnnulePar(), entity.getAnnuleLe()));
        equipementEssaiRepository.findAll().forEach(entity -> addActions(legacyActions, users,
                entity.getEssai().getNumeroEssai(), "Équipement",
                entity.getEquipement().getDesignation() + " " + entity.getEquipement().getNumeroSerie(),
                entity.getCreePar(), entity.getCreeLe(), entity.getModifiePar(), entity.getModifieLe(),
                entity.getAnnulePar(), entity.getAnnuleLe()));
        essaiParametreRepository.findAll().forEach(entity -> addActions(legacyActions, users,
                entity.getEssai().getNumeroEssai(), "Paramètre", entity.getParametre().getNomParametre(),
                entity.getCreePar(), entity.getCreeLe(), entity.getModifiePar(), entity.getModifieLe(),
                entity.getAnnulePar(), entity.getAnnuleLe()));
        publicationNormeRepository.findAll().forEach(entity -> addActions(legacyActions, users,
                "—", "Norme", entity.getNorme().getNumeroNorme(),
                entity.getCreePar(), entity.getCreeLe(), entity.getModifiePar(), entity.getModifieLe(),
                entity.getAnnulePar(), entity.getAnnuleLe()));
        realisationEssaiRepository.findAll().forEach(entity -> addActions(legacyActions, users,
                entity.getEssai().getNumeroEssai(), "Unité", entity.getUnite().getNomUnite(),
                entity.getCreePar(), entity.getCreeLe(), entity.getModifiePar(), entity.getModifieLe(),
                entity.getAnnulePar(), entity.getAnnuleLe()));

        LocalDateTime firstPersistedAudit = persistedActions.stream()
                .map(HistoriqueAction::getDateHeure)
                .min(LocalDateTime::compareTo)
                .orElse(null);
        legacyActions.stream()
                .filter(action -> firstPersistedAudit == null
                        || timestamp(action).isBefore(firstPersistedAudit.minusSeconds(5)))
                .forEach(actions::add);

        actions.sort(Comparator.comparing(this::timestamp).reversed());
        return retirerDoublonsLegacy(actions);
    }

    private boolean isUseful(HistoriqueActionResponseDTO action) {
        if (action.detail() == null || action.detail().isBlank()) return false;
        return !"Modification".equals(action.action()) || !action.changements().isEmpty();
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
                timestamp.toLocalTime(),
                List.of()));
    }

    private HistoriqueActionResponseDTO toResponse(HistoriqueAction entity) {
        List<ChampModifieResponseDTO> changements;
        try {
            changements = objectMapper.<List<ChampModifieResponseDTO>>readValue(entity.getChangements(), new TypeReference<>() {})
                    .stream()
                    .filter(change -> !"Statut".equalsIgnoreCase(change.champ()))
                    .map(auditTrailService::rendreLisible)
                    .toList();
        } catch (Exception ignored) {
            changements = List.of();
        }
        return new HistoriqueActionResponseDTO(
                normaliserAction(entity.getAction()), entity.getNumeroEssai(), entity.getElementType(), detail(entity, changements),
                entity.getIdUser(), entity.getMatricule(), entity.getNomUser(),
                entity.getDateHeure().toLocalDate(), entity.getDateHeure().toLocalTime(), changements);
    }

    private String detail(HistoriqueAction entity, List<ChampModifieResponseDTO> changements) {
        if (changements.isEmpty()) return entity.getElementLibelle();
        return changements.stream()
                .map(change -> change.champ() + " : " + valeur(change.ancienneValeur()) + " → " + valeur(change.nouvelleValeur()))
                .collect(Collectors.joining(" ; "));
    }

    private String valeur(String value) {
        return value == null ? "∅" : "\"" + value + "\"";
    }

    private String normaliserAction(String action) {
        return "Association".equals(action) ? "Creation" : action;
    }

    private List<HistoriqueActionResponseDTO> retirerDoublonsLegacy(List<HistoriqueActionResponseDTO> actions) {
        return actions.stream().filter(candidate -> {
            if (!candidate.changements().isEmpty()) return true;
            return actions.stream().noneMatch(detailed -> !detailed.changements().isEmpty()
                    && Objects.equals(candidate.essaiConcerne(), detailed.essaiConcerne())
                    && Objects.equals(candidate.actionSur(), detailed.actionSur())
                    && actionsEquivalentes(candidate.action(), detailed.action())
                    && Math.abs(java.time.Duration.between(timestamp(candidate), timestamp(detailed)).toSeconds()) <= 5);
        }).toList();
    }

    private boolean actionsEquivalentes(String first, String second) {
        return Objects.equals(first, second)
                || ("Creation".equals(first) && "Association".equals(second))
                || ("Association".equals(first) && "Creation".equals(second));
    }

    private LocalDateTime timestamp(HistoriqueActionResponseDTO action) {
        return LocalDateTime.of(action.date(), action.heure());
    }
}

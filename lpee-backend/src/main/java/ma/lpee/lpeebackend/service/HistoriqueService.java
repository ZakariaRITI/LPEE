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
import ma.lpee.lpeebackend.repository.EquipementRepository;
import ma.lpee.lpeebackend.repository.EquipementEssaiRepository;
import ma.lpee.lpeebackend.repository.EssaiParametreRepository;
import ma.lpee.lpeebackend.repository.PublicationNormeRepository;
import ma.lpee.lpeebackend.repository.ProduitRepository;
import ma.lpee.lpeebackend.repository.RealisationEssaiRepository;
import ma.lpee.lpeebackend.repository.HistoriqueActionRepository;
import ma.lpee.lpeebackend.repository.NormeRepository;
import ma.lpee.lpeebackend.repository.UniteRepository;
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
    private final EquipementRepository equipementRepository;
    private final NormeRepository normeRepository;
    private final ProduitRepository produitRepository;
    private final UniteRepository uniteRepository;
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
            changements = adapterDetailUnite(entity, changements);
            changements = adapterDetailProduit(entity, changements);
            changements = adapterDetailEquipement(entity, changements);
            changements = adapterDetailNorme(entity, changements);
            changements = adapterDetailParametre(entity, changements);
            changements = adapterRealisationEssai(entity, changements);
            changements = adapterEquipementRealisation(entity, changements);
        } catch (Exception ignored) {
            changements = List.of();
        }
        return new HistoriqueActionResponseDTO(
                normaliserAction(entity.getAction()), entity.getNumeroEssai(), entity.getElementType(), detail(entity, changements),
                entity.getIdUser(), entity.getMatricule(), entity.getNomUser(),
                entity.getDateHeure().toLocalDate(), entity.getDateHeure().toLocalTime(), changements);
    }

    private List<ChampModifieResponseDTO> adapterDetailUnite(
            HistoriqueAction entity, List<ChampModifieResponseDTO> changements) {
        String action = normaliserAction(entity.getAction());
        boolean champsUnite = changements.stream().anyMatch(change -> "code Unite".equals(change.champ()));
        if (!("Creation".equals(action) || "Suppression".equals(action))
                || !"Unité".equals(entity.getElementType()) || !champsUnite) {
            return changements;
        }

        List<ChampModifieResponseDTO> detail = new ArrayList<>(changements.stream()
                .filter(change -> !"Unité".equals(change.champ()))
                .toList());
        boolean regionPresente = detail.stream().anyMatch(change -> "Région".equals(change.champ()));
        if (!regionPresente && entity.getElementId() != null) {
            uniteRepository.findById(entity.getElementId())
                    .map(unite -> unite.getRegion().getNomRegion())
                    .ifPresent(nomRegion -> detail.add(new ChampModifieResponseDTO("Région", null, nomRegion)));
        }
        return detail;
    }

    private List<ChampModifieResponseDTO> adapterDetailProduit(
            HistoriqueAction entity, List<ChampModifieResponseDTO> changements) {
        String action = normaliserAction(entity.getAction());
        if (!("Creation".equals(action) || "Suppression".equals(action)) || !"Produit".equals(entity.getElementType())) {
            return changements;
        }

        List<ChampModifieResponseDTO> detail = new ArrayList<>(changements.stream()
                .filter(change -> !"Produit".equals(change.champ()))
                .toList());
        boolean famillePresente = detail.stream()
                .anyMatch(change -> "Famille de produit".equals(change.champ()));
        if (!famillePresente && entity.getElementId() != null) {
            produitRepository.findById(entity.getElementId())
                    .map(produit -> produit.getFamilleProduit().getNomFamille())
                    .ifPresent(nomFamille -> detail.add(
                            new ChampModifieResponseDTO("Famille de produit", null, nomFamille)));
        }
        return detail;
    }

    private List<ChampModifieResponseDTO> adapterDetailEquipement(
            HistoriqueAction entity, List<ChampModifieResponseDTO> changements) {
        String action = normaliserAction(entity.getAction());
        boolean champsEquipement = changements.stream().anyMatch(change -> "numero Serie".equals(change.champ()));
        if (!("Creation".equals(action) || "Suppression".equals(action))
                || !"Équipement".equals(entity.getElementType()) || !champsEquipement) {
            return changements;
        }

        List<ChampModifieResponseDTO> detail = new ArrayList<>(changements.stream()
                .filter(change -> !"Équipement".equals(change.champ()))
                .toList());
        boolean marquePresente = detail.stream().anyMatch(change -> "Marque".equals(change.champ()));
        if (!marquePresente && entity.getElementId() != null) {
            equipementRepository.findById(entity.getElementId())
                    .map(equipement -> equipement.getMarque().getNomMarque())
                    .ifPresent(nomMarque -> detail.add(
                            new ChampModifieResponseDTO("Marque", null, nomMarque)));
        }
        return detail;
    }

    private List<ChampModifieResponseDTO> adapterDetailNorme(
            HistoriqueAction entity, List<ChampModifieResponseDTO> changements) {
        String action = normaliserAction(entity.getAction());
        boolean champsDeNorme = changements.stream().anyMatch(change ->
                "code Norme".equals(change.champ()) || "Code norme".equals(change.champ()));
        if (!("Creation".equals(action) || "Suppression".equals(action))
                || !"Norme".equals(entity.getElementType()) || !champsDeNorme) {
            return changements;
        }

        List<ChampModifieResponseDTO> detail = new ArrayList<>(changements.stream()
                .filter(change -> !"Norme".equals(change.champ()))
                .toList());
        boolean organismePresent = detail.stream().anyMatch(change -> "Organisme".equals(change.champ()));
        if (!organismePresent && entity.getElementId() != null) {
            normeRepository.findById(entity.getElementId())
                    .map(norme -> norme.getOrganisme().getNomOrganisme())
                    .ifPresent(nomOrganisme -> detail.add(
                            new ChampModifieResponseDTO("Organisme", null, nomOrganisme)));
        }
        return detail;
    }

    private List<ChampModifieResponseDTO> adapterDetailParametre(
            HistoriqueAction entity, List<ChampModifieResponseDTO> changements) {
        String action = normaliserAction(entity.getAction());
        boolean nomParametrePresent = changements.stream().anyMatch(change ->
                "nom Parametre".equals(change.champ()) || "Nom paramètre".equals(change.champ()));
        if (!("Creation".equals(action) || "Suppression".equals(action))
                || !"Paramètre".equals(entity.getElementType()) || !nomParametrePresent) {
            return changements;
        }
        return changements.stream()
                .filter(change -> !"Paramètre".equals(change.champ()))
                .toList();
    }

    private List<ChampModifieResponseDTO> adapterRealisationEssai(
            HistoriqueAction entity, List<ChampModifieResponseDTO> changements) {
        boolean realisation = "Unité".equals(entity.getElementType()) && changements.stream()
                .anyMatch(change -> "Date de réalisation".equals(change.champ()));
        if (!realisation) return changements;

        List<ChampModifieResponseDTO> detail = new ArrayList<>(changements.stream()
                .filter(change -> !"Région".equals(change.champ()) && !"Unité".equals(change.champ()))
                .toList());
        if (entity.getElementId() != null) {
            uniteRepository.findById(entity.getElementId())
                    .map(unite -> unite.getNomUnite())
                    .ifPresent(nomUnite -> detail.add(new ChampModifieResponseDTO(
                            "Unité", null, nomUnite)));
        }
        return detail;
    }

    private List<ChampModifieResponseDTO> adapterEquipementRealisation(
            HistoriqueAction entity, List<ChampModifieResponseDTO> changements) {
        boolean utilisation = "Équipement".equals(entity.getElementType()) && changements.stream()
                .anyMatch(change -> "Début d’utilisation".equals(change.champ())
                        || "Fin d’utilisation".equals(change.champ()));
        if (!utilisation || entity.getElementId() == null) return changements;

        return equipementRepository.findById(entity.getElementId()).map(equipement -> {
            List<ChampModifieResponseDTO> detail = new ArrayList<>(changements);
            detail.add(new ChampModifieResponseDTO("Numéro de série", null, equipement.getNumeroSerie()));
            detail.add(new ChampModifieResponseDTO("Désignation", null, equipement.getDesignation()));
            detail.add(new ChampModifieResponseDTO("Modèle", null, equipement.getModele()));
            return detail;
        }).orElse(changements);
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

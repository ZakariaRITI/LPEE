package ma.lpee.lpeebackend.service;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.response.ChampModifieResponseDTO;
import ma.lpee.lpeebackend.entity.Essai;
import ma.lpee.lpeebackend.entity.HistoriqueAction;
import ma.lpee.lpeebackend.entity.Utilisateur;
import ma.lpee.lpeebackend.repository.EssaiRepository;
import ma.lpee.lpeebackend.repository.DocumentRepository;
import ma.lpee.lpeebackend.repository.EquipementRepository;
import ma.lpee.lpeebackend.repository.FamilleProduitRepository;
import ma.lpee.lpeebackend.repository.HistoriqueActionRepository;
import ma.lpee.lpeebackend.repository.MarqueRepository;
import ma.lpee.lpeebackend.repository.NormeRepository;
import ma.lpee.lpeebackend.repository.OrganismeRepository;
import ma.lpee.lpeebackend.repository.ParametreRepository;
import ma.lpee.lpeebackend.repository.ProduitRepository;
import ma.lpee.lpeebackend.repository.RegionRepository;
import ma.lpee.lpeebackend.repository.UniteRepository;
import ma.lpee.lpeebackend.repository.UtilisateurRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuditTrailService {
    private static final Set<String> IGNORED = Set.of(
            "id", "motDePasse", "password", "statut", "creeLe", "creePar", "modifieLe", "modifiePar", "annuleLe", "annulePar");
    private static final Map<String, String> LABELS = Map.ofEntries(
            Map.entry("numeroEssai", "Numéro d’essai"), Map.entry("description", "Description"),
            Map.entry("dateEssai", "Date d’essai"), Map.entry("etalonnage", "Étalonnage"),
            Map.entry("statut", "Statut"), Map.entry("idProduit", "Produit"),
            Map.entry("idEssai", "Essai"), Map.entry("idEquipement", "Équipement"),
            Map.entry("idNorme", "Norme"), Map.entry("idParametre", "Paramètre"),
            Map.entry("idUnite", "Unité"), Map.entry("idDocument", "Document"),
            Map.entry("idRegion", "Région"),
            Map.entry("idFamille", "Famille de produit"),
            Map.entry("idMarque", "Marque"),
            Map.entry("idOrganisme", "Organisme"),
            Map.entry("valeurCible", "Valeur cible"), Map.entry("dateUtilisationDebut", "Début d’utilisation"),
            Map.entry("dateUtilisationFin", "Fin d’utilisation"), Map.entry("statutConformite", "Conformité"),
            Map.entry("dateEvaluation", "Date d’évaluation"), Map.entry("dateRealisation", "Date de réalisation"));
    private static final Map<String, String> ID_FIELDS = Map.ofEntries(
            Map.entry("Essai", "idEssai"), Map.entry("Unité", "idUnite"), Map.entry("Produit", "idProduit"),
            Map.entry("Équipement", "idEquipement"), Map.entry("Norme", "idNorme"), Map.entry("Paramètre", "idParametre"),
            Map.entry("Document", "idDocument"), Map.entry("Utilisateur", "idUser"), Map.entry("Région", "idRegion"),
            Map.entry("Marque", "idMarque"), Map.entry("Organisme", "idOrganisme"));

    private final HistoriqueActionRepository historiqueActionRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final EssaiRepository essaiRepository;
    private final ProduitRepository produitRepository;
    private final FamilleProduitRepository familleProduitRepository;
    private final MarqueRepository marqueRepository;
    private final OrganismeRepository organismeRepository;
    private final RegionRepository regionRepository;
    private final EquipementRepository equipementRepository;
    private final NormeRepository normeRepository;
    private final ParametreRepository parametreRepository;
    private final UniteRepository uniteRepository;
    private final DocumentRepository documentRepository;
    private final ObjectMapper objectMapper;

    public void enregistrer(String action, String elementType, Object avant, Object apres) {
        Map<String, Object> oldValues = values(avant);
        Map<String, Object> newValues = values(apres);

        if ("Modification".equals(action)) {
            boolean wasInactive = isInactive(oldValues.get("statut"));
            boolean isInactive = isInactive(newValues.get("statut"));
            if (!wasInactive && isInactive) action = "Suppression";
            else if (wasInactive && !isInactive) action = "Creation";
        }

        List<ChampModifieResponseDTO> changements = switch (action) {
            case "Creation" -> changements(action, elementType, Map.of(), newValues);
            case "Suppression" -> changements(action, elementType, oldValues, Map.of());
            default -> changements(action, elementType, oldValues, newValues);
        };

        // Les mises à jour techniques et les appels répétés sans changement métier sont ignorés.
        if ("Modification".equals(action) && changements.isEmpty()) return;

        // Une mise à jour sans différence métier ne doit pas polluer le journal.
        Utilisateur utilisateur = utilisateurCourant();
        HistoriqueAction historique = new HistoriqueAction();
        historique.setAction(action);
        historique.setElementType(elementType);
        historique.setElementId(longValue(first(newValues, oldValues, ID_FIELDS.getOrDefault(elementType, "id"), "id")));
        historique.setElementLibelle(libelle(elementType, newValues.isEmpty() ? oldValues : newValues));
        definirEssai(historique, newValues.isEmpty() ? oldValues : newValues, elementType);
        historique.setIdUser(utilisateur.getIdUser());
        historique.setMatricule(utilisateur.getMatricule());
        historique.setNomUser(utilisateur.getNomUser());
        historique.setDateHeure(LocalDateTime.now());
        try {
            historique.setChangements(objectMapper.writeValueAsString(changements));
        } catch (JacksonException exception) {
            throw new IllegalStateException("Impossible de sérialiser les changements d’audit.", exception);
        }
        historiqueActionRepository.save(historique);
    }

    private boolean isInactive(Object statut) {
        return statut != null && "INACTIF".equalsIgnoreCase(String.valueOf(statut).trim());
    }

    private List<ChampModifieResponseDTO> changements(String action, String elementType,
                                                       Map<String, Object> avant, Map<String, Object> apres) {
        Set<String> champs = new TreeSet<>();
        champs.addAll(avant.keySet());
        champs.addAll(apres.keySet());
        List<ChampModifieResponseDTO> result = new ArrayList<>();
        boolean detailUnite = isDetailUnite(action, elementType, avant, apres);
        boolean detailEquipement = isDetailEquipement(action, elementType, avant, apres);
        boolean detailNorme = isDetailNorme(action, elementType, avant, apres);
        boolean detailParametre = isDetailParametre(action, elementType, avant, apres);
        for (String champ : champs) {
            if (detailUnite && "idUnite".equals(champ)) continue;
            if ("idRegion".equals(champ) && !detailUnite) continue;
            if (isDetailProduit(action, elementType) && "idProduit".equals(champ)) continue;
            if ("idFamille".equals(champ) && !isDetailProduit(action, elementType)) continue;
            if (detailEquipement && "idEquipement".equals(champ)) continue;
            if ("idMarque".equals(champ) && !detailEquipement) continue;
            if (detailNorme && "idNorme".equals(champ)) continue;
            if ("idOrganisme".equals(champ) && !detailNorme) continue;
            if (detailParametre && "idParametre".equals(champ)) continue;
            if (isIgnored(champ)) continue;
            String oldValue = displayField(champ, avant.get(champ));
            String newValue = displayField(champ, apres.get(champ));
            if (Objects.equals(oldValue, newValue)) continue;
            if ("Creation".equals(action)) oldValue = null;
            if ("Suppression".equals(action)) newValue = null;
            result.add(new ChampModifieResponseDTO(LABELS.getOrDefault(champ, humanize(champ)), oldValue, newValue));
        }
        return result;
    }

    private boolean isDetailUnite(String action, String elementType,
                                  Map<String, Object> avant, Map<String, Object> apres) {
        return ("Creation".equals(action) || "Suppression".equals(action))
                && "Unité".equals(elementType)
                && (avant.containsKey("codeUnite") || apres.containsKey("codeUnite"));
    }

    private boolean isDetailProduit(String action, String elementType) {
        return ("Creation".equals(action) || "Suppression".equals(action)) && "Produit".equals(elementType);
    }

    private boolean isDetailEquipement(String action, String elementType,
                                       Map<String, Object> avant, Map<String, Object> apres) {
        return ("Creation".equals(action) || "Suppression".equals(action))
                && "Équipement".equals(elementType)
                && (avant.containsKey("numeroSerie") || apres.containsKey("numeroSerie"));
    }

    private boolean isDetailNorme(String action, String elementType,
                                  Map<String, Object> avant, Map<String, Object> apres) {
        return ("Creation".equals(action) || "Suppression".equals(action))
                && "Norme".equals(elementType)
                && (avant.containsKey("codeNorme") || apres.containsKey("codeNorme"));
    }

    private boolean isDetailParametre(String action, String elementType,
                                      Map<String, Object> avant, Map<String, Object> apres) {
        return ("Creation".equals(action) || "Suppression".equals(action))
                && "Paramètre".equals(elementType)
                && (avant.containsKey("nomParametre") || apres.containsKey("nomParametre"));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> values(Object source) {
        if (source == null) return Map.of();
        Map<String, Object> raw = objectMapper.convertValue(source, Map.class);
        Map<String, Object> flat = new LinkedHashMap<>();
        raw.forEach((key, value) -> {
            if (value instanceof Map<?, ?> nested && nested.containsKey("id")) flat.put(key + "Id", nested.get("id"));
            else if (!(value instanceof Collection<?>) && !(value instanceof Map<?, ?>)) flat.put(key, value);
        });
        return flat;
    }

    private void definirEssai(HistoriqueAction historique, Map<String, Object> values, String type) {
        Long idEssai = longValue(values.get("idEssai"));
        String numero = string(values.get("numeroEssai"));
        if ("Essai".equals(type)) idEssai = longValue(first(values, Map.of(), "idEssai", "id"));
        if (idEssai != null) {
            Optional<Essai> essai = essaiRepository.findById(idEssai);
            historique.setIdEssai(idEssai);
            historique.setNumeroEssai(essai.map(Essai::getNumeroEssai).orElse(numero));
        } else {
            historique.setNumeroEssai(numero);
        }
    }

    private Utilisateur utilisateurCourant() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Utilisateur authentifié introuvable pour l’audit.");
        }
        return utilisateurRepository.findByMatricule(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Utilisateur authentifié introuvable pour l’audit."));
    }

    private String libelle(String type, Map<String, Object> values) {
        List<String> candidates = switch (type) {
            case "Essai" -> List.of("numeroEssai", "description");
            case "Unité" -> List.of("nomUnite", "codeUnite");
            case "Produit" -> List.of("nomProduit", "codeProduit");
            case "Équipement" -> List.of("designation", "numeroSerie");
            case "Norme" -> List.of("numeroNorme", "nomNorme");
            case "Paramètre" -> List.of("nomParametre");
            case "Document" -> List.of("nomDocument", "numeroDocument");
            case "Utilisateur" -> List.of("nomUser", "matricule");
            default -> List.of("nom" + type.replace(" ", ""), "code" + type.replace(" ", ""));
        };
        for (String candidate : candidates) if (values.get(candidate) != null) return display(values.get(candidate));
        return type;
    }

    private boolean isIgnored(String field) {
        return IGNORED.contains(field) || field.startsWith("id") && !LABELS.containsKey(field);
    }
    private String humanize(String value) { return value.replaceAll("([a-z])([A-Z])", "$1 $2"); }
    private String displayField(String field, Object value) {
        Long id = longValue(value);
        if (id == null) return display(value);
        return switch (field) {
            case "idProduit" -> produitRepository.findById(id).map(item -> item.getCodeProduit()).orElse(display(value));
            case "idEssai" -> essaiRepository.findById(id).map(Essai::getNumeroEssai).orElse(display(value));
            case "idEquipement" -> equipementRepository.findById(id)
                    .map(item -> item.getDesignation() + " (" + item.getNumeroSerie() + ")").orElse(display(value));
            case "idNorme" -> normeRepository.findById(id).map(item -> item.getNumeroNorme()).orElse(display(value));
            case "idParametre" -> parametreRepository.findById(id).map(item -> item.getNomParametre()).orElse(display(value));
            case "idUnite" -> uniteRepository.findById(id).map(item -> item.getCodeUnite()).orElse(display(value));
            case "idDocument" -> documentRepository.findById(id).map(item -> item.getNumeroDocument()).orElse(display(value));
            case "idRegion" -> regionRepository.findById(id).map(item -> item.getNomRegion()).orElse(display(value));
            case "idFamille" -> familleProduitRepository.findById(id).map(item -> item.getNomFamille()).orElse(display(value));
            case "idMarque" -> marqueRepository.findById(id).map(item -> item.getNomMarque()).orElse(display(value));
            case "idOrganisme" -> organismeRepository.findById(id).map(item -> item.getNomOrganisme()).orElse(display(value));
            default -> display(value);
        };
    }
    public ChampModifieResponseDTO rendreLisible(ChampModifieResponseDTO change) {
        String field = switch (change.champ()) {
            case "Produit" -> "idProduit";
            case "Essai" -> "idEssai";
            case "Équipement" -> "idEquipement";
            case "Norme" -> "idNorme";
            case "Paramètre" -> "idParametre";
            case "Unité" -> "idUnite";
            case "Document" -> "idDocument";
            default -> null;
        };
        if (field == null) return change;
        return new ChampModifieResponseDTO(change.champ(),
                displayField(field, change.ancienneValeur()), displayField(field, change.nouvelleValeur()));
    }
    private String display(Object value) { return value == null || String.valueOf(value).isBlank() ? null : String.valueOf(value); }
    private String string(Object value) { return value == null ? null : String.valueOf(value); }
    private Long longValue(Object value) {
        if (value instanceof Number n) return n.longValue();
        try { return value == null ? null : Long.valueOf(String.valueOf(value)); }
        catch (NumberFormatException ignored) { return null; }
    }
    private Object first(Map<String, Object> primary, Map<String, Object> fallback, String... keys) {
        for (String key : keys) if (primary.get(key) != null) return primary.get(key);
        for (String key : keys) if (fallback.get(key) != null) return fallback.get(key);
        return null;
    }
}

package ma.lpee.lpeebackend.service;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditTrailAspect {
    private static final Map<String, String> TYPES = Map.ofEntries(
            Map.entry("Essai", "Essai"), Map.entry("Unite", "Unité"), Map.entry("Produit", "Produit"),
            Map.entry("Equipement", "Équipement"), Map.entry("Norme", "Norme"), Map.entry("Parametre", "Paramètre"),
            Map.entry("Document", "Document"), Map.entry("Utilisateur", "Utilisateur"), Map.entry("Region", "Région"),
            Map.entry("Marque", "Marque"), Map.entry("Organisme", "Organisme"), Map.entry("FamilleProduit", "Famille de produit"),
            Map.entry("TypeDocument", "Type de document"), Map.entry("Role", "Rôle"),
            Map.entry("EquipementEssai", "Équipement"), Map.entry("EssaiParametre", "Paramètre"),
            Map.entry("ConformiteNorme", "Norme"), Map.entry("DocumentationEssai", "Document"),
            Map.entry("RealisationEssai", "Unité"), Map.entry("PublicationNorme", "Norme"));

    private final AuditTrailService auditTrailService;

    @Around("execution(public * ma.lpee.lpeebackend.service.impl.*ServiceImpl.create(..)) || " +
            "execution(public * ma.lpee.lpeebackend.service.impl.*ServiceImpl.update(..)) || " +
            "execution(public * ma.lpee.lpeebackend.service.impl.*ServiceImpl.delete(..))")
    public Object auditer(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().getName();
        String type = type(joinPoint.getTarget().getClass().getSimpleName());
        if (type == null) return joinPoint.proceed();

        Object avant = null;
        if (("update".equals(method) || "delete".equals(method)) && joinPoint.getArgs().length > 0
                && joinPoint.getArgs()[0] instanceof Long id) {
            avant = lire(joinPoint.getTarget(), id);
        }

        Object result = joinPoint.proceed();
        String action = "create".equals(method)
                ? "Creation"
                : "delete".equals(method) ? "Suppression" : "Modification";
        auditTrailService.enregistrer(action, type, avant, "delete".equals(method) ? null : result);
        return result;
    }

    private Object lire(Object target, Long id) {
        for (String name : new String[]{"getById", "findById"}) {
            try {
                Method method = target.getClass().getMethod(name, Long.class);
                return method.invoke(target, id);
            } catch (NoSuchMethodException ignored) {
                // Certains services utilisent getById, d'autres findById.
            } catch (ReflectiveOperationException exception) {
                throw new IllegalStateException("Impossible de capturer l’état avant modification.", exception);
            }
        }
        return null;
    }

    private String type(String serviceName) {
        String key = serviceName.replace("ServiceImpl", "");
        return TYPES.get(key);
    }

}

import { useEffect, useMemo, useRef, useState } from "react";
import { Boxes, Building2, ChevronDown, FileDown, FlaskConical, MapPinned, Search, Settings2, ShieldCheck, Tags, Wrench } from "lucide-react";
import api from "../services/api";
import "./Vue360.css";
import "./Vue360Print.css";

const normalize = (value) => (value || "").normalize("NFD").replace(/[\u0300-\u036f]/g, "").trim().toLowerCase();
const isActive = (item) => normalize(item?.statut) !== "inactif";
const findBy = (items, key, value) => items.find((item) => item[key] === value);
const resolveImageSource = (source) => source?.startsWith("/") ? `${api.defaults.baseURL}${source}` : source;

function DetailCard({ icon: Icon, title, children }) {
  return <article className="view360-card"><div className="view360-card-title"><span><Icon size={19} /></span><h2>{title}</h2></div>{children}</article>;
}

function EmptyValue({ children }) {
  return children ? children : <span className="view360-empty-value">Non renseigné</span>;
}

function OrganismeImage({ organisme }) {
  const [hasError, setHasError] = useState(false);
  if (!organisme?.imageOrganisme || hasError) return <span className="view360-organisme-image-empty">Image non renseignée</span>;
  return <img className="view360-organisme-image" src={resolveImageSource(organisme.imageOrganisme)} alt={`Image de ${organisme.nomOrganisme || "l’organisme"}`} onError={() => setHasError(true)} />;
}

function EssaiSearchSelect({ value, options, loading, onChange }) {
  const [open, setOpen] = useState(false);
  const [query, setQuery] = useState("");
  const rootRef = useRef(null);
  useEffect(() => { const close = (event) => { if (!rootRef.current?.contains(event.target)) setOpen(false); }; document.addEventListener("pointerdown", close); return () => document.removeEventListener("pointerdown", close); }, []);
  const selected = options.find((item) => item.numeroEssai === value);
  const normalizedQuery = normalize(query);
  const filtered = options.filter((item) => !normalizedQuery || normalize(`${item.numeroEssai} ${item.libelle || ""}`).includes(normalizedQuery));
  return <div className={`view360-essai-select ${open ? "open" : ""}`} ref={rootRef}><button type="button" className="view360-essai-trigger" onClick={() => setOpen((current) => !current)} aria-expanded={open} disabled={loading}><span>{loading ? "Chargement des essais…" : selected ? `${selected.numeroEssai}${selected.libelle ? ` — ${selected.libelle}` : ""}` : "Sélectionnez un essai"}</span><ChevronDown size={17} /></button>{open && <div className="view360-essai-menu"><label className="view360-essai-filter"><Search size={16} /><input autoFocus type="search" value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Rechercher par numéro ou libellé" /></label><div className="view360-essai-options" role="listbox">{filtered.length ? filtered.map((item) => <button type="button" role="option" aria-selected={item.numeroEssai === value} key={item.idEssai} onClick={() => { onChange(item.numeroEssai); setOpen(false); setQuery(""); }}><strong>{item.numeroEssai}</strong>{item.libelle && <small>{item.libelle}</small>}</button>) : <p>Aucun essai disponible</p>}</div></div>}</div>;
}

function Vue360() {
  const printRef = useRef(null);
  const [form, setForm] = useState({ typeUnite: "", numeroEssai: "" });
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");
  const [searching, setSearching] = useState(false);
  const [selectionData, setSelectionData] = useState({ essais: [], realisations: [], unites: [] });
  const [loadingEssais, setLoadingEssais] = useState(false);

  useEffect(() => {
    if (!form.typeUnite) return;
    let active = true;
    Promise.all([api.get("/api/essais"), api.get("/api/realisations-essais"), api.get("/api/unites")])
      .then(([essais, realisations, unites]) => { if (active) setSelectionData({ essais: essais.data, realisations: realisations.data, unites: unites.data }); })
      .catch(() => active && setError("Impossible de charger la liste des essais disponibles."))
      .finally(() => active && setLoadingEssais(false));
    return () => { active = false; };
  }, [form.typeUnite]);

  const availableEssais = useMemo(() => {
    if (!form.typeUnite) return [];
    const unitIds = new Set(selectionData.unites.filter((item) => isActive(item) && normalize(item.typeUnite) === normalize(form.typeUnite)).map((item) => item.idUnite));
    const essaiIds = new Set(selectionData.realisations.filter((item) => isActive(item) && unitIds.has(item.idUnite)).map((item) => item.idEssai));
    return selectionData.essais.filter((item) => isActive(item) && essaiIds.has(item.idEssai));
  }, [form.typeUnite, selectionData]);

  useEffect(() => {
    const fitToSinglePage = () => {
      const sheet = printRef.current;
      if (!sheet) return;
      sheet.style.setProperty("--view360-print-scale", "1");
      sheet.style.setProperty("--view360-print-width", "100%");
      void sheet.offsetHeight;
      const a4PrintableHeight = (297 - 16) * (96 / 25.4);
      const scale = Math.min(1, (a4PrintableHeight * 0.98) / sheet.scrollHeight);
      sheet.style.setProperty("--view360-print-scale", scale.toFixed(4));
      sheet.style.setProperty("--view360-print-width", `${100 / scale}%`);
    };
    const resetPrintSize = () => {
      printRef.current?.style.removeProperty("--view360-print-scale");
      printRef.current?.style.removeProperty("--view360-print-width");
    };
    window.addEventListener("beforeprint", fitToSinglePage);
    window.addEventListener("afterprint", resetPrintSize);
    return () => { window.removeEventListener("beforeprint", fitToSinglePage); window.removeEventListener("afterprint", resetPrintSize); };
  }, []);

  const search = async (event) => {
    event.preventDefault();
    setError(""); setResult(null);
    if (!form.typeUnite || !form.numeroEssai.trim()) {
      setError("Le type d’unité et le numéro de l’essai sont obligatoires.");
      return;
    }

    setSearching(true);
    try {
      const endpoints = [
        ["essais", "/api/essais"], ["realisations", "/api/realisations-essais"], ["unites", "/api/unites"], ["regions", "/api/regions"],
        ["produits", "/api/produits"], ["familles", "/api/familles-produits"], ["equipementLinks", "/api/equipements-essais"],
        ["equipements", "/api/equipements"], ["marques", "/api/marques"], ["normeLinks", "/api/conformites-normes"],
        ["normes", "/api/normes"], ["organismes", "/api/organismes"], ["parametreLinks", "/api/essais-parametres"], ["parametres", "/api/parametres"],
      ];
      const responses = await Promise.all(endpoints.map(async ([key, endpoint]) => [key, (await api.get(endpoint)).data]));
      const data = Object.fromEntries(responses);
      const essai = data.essais.find((item) => normalize(item.numeroEssai) === normalize(form.numeroEssai) && isActive(item));
      const realisations = essai ? data.realisations.filter((item) => item.idEssai === essai.idEssai && isActive(item) && normalize(findBy(data.unites, "idUnite", item.idUnite)?.typeUnite) === normalize(form.typeUnite)) : [];
      if (!essai || !realisations.length) {
        setError(`Aucun essai « ${form.numeroEssai.trim()} » n’est réalisé dans une unité ${form.typeUnite.toLowerCase()}.`);
        return;
      }

      const produit = findBy(data.produits, "idProduit", essai.idProduit);
      const famille = findBy(data.familles, "idFamille", produit?.idFamille);
      const equipementLinks = data.equipementLinks.filter((item) => item.idEssai === essai.idEssai && isActive(item));
      const normeLinks = data.normeLinks.filter((item) => item.idEssai === essai.idEssai && isActive(item));
      const parametreLinks = data.parametreLinks.filter((item) => item.idEssai === essai.idEssai && isActive(item));
      setResult({
        essai, produit, famille,
        unites: realisations.map((realisation) => ({ realisation, unite: findBy(data.unites, "idUnite", realisation.idUnite), region: findBy(data.regions, "idRegion", findBy(data.unites, "idUnite", realisation.idUnite)?.idRegion) })),
        equipements: equipementLinks.map((link) => ({ link, equipement: findBy(data.equipements, "idEquipement", link.idEquipement), marque: findBy(data.marques, "idMarque", findBy(data.equipements, "idEquipement", link.idEquipement)?.idMarque) })),
        normes: normeLinks.map((link) => ({ link, norme: findBy(data.normes, "idNorme", link.idNorme), organisme: findBy(data.organismes, "idOrganisme", findBy(data.normes, "idNorme", link.idNorme)?.idOrganisme) })),
        parametres: parametreLinks.map((link) => ({ link, parametre: findBy(data.parametres, "idParametre", link.idParametre) })),
      });
    } catch (requestError) {
      setError(requestError.response?.data?.message || "Impossible de charger la vue complète de l’essai.");
    } finally {
      setSearching(false);
    }
  };

  return <section className="unite-page view360-page">
    <div className="page-heading"><div><p className="dashboard-eyebrow">Vue 360</p><h1>Vue complète d’un essai</h1><p>Retrouvez toutes les informations liées à un essai et à son unité de réalisation.</p></div><span className="page-heading-icon"><Search /></span></div>
    <article className="unite-form-card view360-search"><div className="form-card-heading"><div><h2>Rechercher un essai</h2><p>Renseignez le numéro exact et le type d’unité concerné.</p></div></div>
      <form className="unite-form" onSubmit={search}><div className="form-grid">
        <div className="form-field"><label htmlFor="view360-type">Type d’unité <b>*</b></label><select id="view360-type" value={form.typeUnite} onChange={(event) => { setForm({ typeUnite: event.target.value, numeroEssai: "" }); setLoadingEssais(Boolean(event.target.value)); setResult(null); setError(""); }}><option value="">Sélectionnez</option><option value="Régionale">Régionale</option><option value="Spécialisée">Spécialisée</option></select></div>
        <div className="form-field"><label>Essai <b>*</b></label>{form.typeUnite ? <EssaiSearchSelect key={form.typeUnite} value={form.numeroEssai} options={availableEssais} loading={loadingEssais} onChange={(numeroEssai) => setForm((current) => ({ ...current, numeroEssai }))} /> : <button type="button" className="view360-essai-trigger" disabled>Sélectionnez d’abord un type d’unité</button>}</div>
      </div>{error && <p className="form-global-error" role="alert">{error}</p>}<div className="form-actions"><button className="save-button" disabled={searching}>{searching ? "Recherche…" : <><Search size={17} />Rechercher</>}</button></div></form>
    </article>

    {result && <><div className="view360-export-actions"><button type="button" className="view360-export-button" onClick={() => window.print()}><span><FileDown size={19} /></span><span><strong>Exporter en PDF</strong><small>Télécharger la fiche complète</small></span></button></div><div className="view360-results" ref={printRef}>
      <header className="view360-print-heading"><img src="/images/logo_LPEE.png" alt="LPEE" /><div><span>Rapport Vue 360</span><h1>Fiche complète de l’essai</h1><p><strong>{result.essai.numeroEssai}</strong>{result.essai.libelle ? ` · ${result.essai.libelle}` : ""}</p></div></header>
      <DetailCard icon={FlaskConical} title="Essai"><dl className="view360-details"><div><dt>Numéro</dt><dd>{result.essai.numeroEssai}</dd></div><div><dt>Libellé</dt><dd><EmptyValue>{result.essai.libelle}</EmptyValue></dd></div><div className="wide"><dt>Description</dt><dd><EmptyValue>{result.essai.description}</EmptyValue></dd></div><div><dt>Étalonnage</dt><dd>{result.essai.etalonnage == null ? <EmptyValue /> : result.essai.etalonnage ? "Oui" : "Non"}</dd></div></dl></DetailCard>
      <DetailCard icon={Boxes} title="Produit et famille"><dl className="view360-details"><div><dt>Produit</dt><dd><EmptyValue>{result.produit?.nomProduit}</EmptyValue></dd></div><div><dt>Code produit</dt><dd><EmptyValue>{result.produit?.codeProduit}</EmptyValue></dd></div><div><dt>Famille produit</dt><dd><EmptyValue>{result.famille?.nomFamille}</EmptyValue></dd></div><div><dt>Code famille</dt><dd><EmptyValue>{result.famille?.codeFamille}</EmptyValue></dd></div></dl></DetailCard>
      <DetailCard icon={Building2} title="Unités et régions"><div className="view360-list">{result.unites.map(({ realisation, unite, region }) => <div className="view360-list-item" key={realisation.idRealisation}><div><strong>{unite?.nomUnite || "Unité non renseignée"}</strong><span>{unite?.codeUnite} · {unite?.ville || "Ville non renseignée"}</span><span>Type unité : {unite?.typeUnite || "Non renseigné"}</span><span>Téléphone : {unite?.telephone || "Non renseigné"}</span><span>Adresse : {unite?.adresse || "Non renseignée"}</span><span>Effectif : opérateurs de saisie {unite?.nbrOperateurSaisie ?? "—"} · responsables dossier {unite?.nbrResponsableDossier ?? "—"} · laboratoire {unite?.nbrResponsableLaboratoire ?? "—"} · chantier {unite?.nbrResponsableChantier ?? "—"}</span></div><div><span className="view360-label"><MapPinned size={15} />{region?.nomRegion || "Région non renseignée"}</span><small>Code région : {region?.codeRegion || "Non renseigné"}</small></div></div>)}</div></DetailCard>
      <DetailCard icon={Wrench} title="Équipements et marques"><div className="view360-list">{result.equipements.length ? result.equipements.map(({ link, equipement, marque }) => <div className="view360-list-item" key={link.idUtilisationEquipement}><div><strong>{equipement?.designation || "Équipement non renseigné"}</strong><span>{equipement?.numeroSerie || "N° série non renseigné"}{equipement?.modele ? ` · ${equipement.modele}` : ""}</span><span>Périodicité d’étalonnage : {equipement?.periodiciteEtalonnage || "Non renseignée"}</span><span>Étalonnage requis : {equipement?.etalonnageRequis == null ? "Non renseigné" : equipement.etalonnageRequis ? "Oui" : "Non"}</span></div><div><span className="view360-label"><Tags size={15} />{marque?.nomMarque || "Marque non renseignée"}</span><small>Fabricant : {marque?.nomFabricant || "Non renseigné"}</small></div></div>) : <p className="view360-empty">Aucun équipement associé.</p>}</div></DetailCard>
      <DetailCard icon={ShieldCheck} title="Normes et organismes"><div className="view360-list">{result.normes.length ? result.normes.map(({ link, norme, organisme }) => <div className="view360-list-item" key={link.idConformite}><div><strong>{norme?.numeroNorme || norme?.codeNorme || "Norme non renseignée"}</strong><span>{norme?.nomNorme}</span><span>Code norme : {norme?.codeNorme || "Non renseigné"}</span></div><div className="view360-organisme"><OrganismeImage organisme={organisme} /><span className="view360-label">{organisme?.nomOrganisme || "Organisme non renseigné"}</span><small>Code organisme : {organisme?.codeOrganisme || "Non renseigné"}</small><small>{norme?.annee || "Année non renseignée"}</small></div></div>) : <p className="view360-empty">Aucune norme associée.</p>}</div></DetailCard>
      <DetailCard icon={Settings2} title="Paramètres"><div className="view360-tags">{result.parametres.length ? result.parametres.map(({ link, parametre }) => <span key={link.idMesure}><strong>{parametre?.nomParametre || "Paramètre"}</strong>{link.valeurCible != null ? ` : ${link.valeurCible}` : ""}{parametre?.uniteParametre ? ` ${parametre.uniteParametre}` : ""}</span>) : <p className="view360-empty">Aucun paramètre associé.</p>}</div></DetailCard>
      <footer className="view360-print-footer"><span>LPEE · Vue 360</span><span>{result.essai.numeroEssai}</span></footer>
    </div></>}
  </section>;
}

export default Vue360;

import { useState } from "react";
import { Boxes, Building2, FlaskConical, MapPinned, Search, Settings2, ShieldCheck, Tags, Wrench } from "lucide-react";
import api from "../services/api";
import "./Vue360.css";

const normalize = (value) => (value || "").normalize("NFD").replace(/[\u0300-\u036f]/g, "").trim().toLowerCase();
const isActive = (item) => normalize(item?.statut) !== "inactif";
const findBy = (items, key, value) => items.find((item) => item[key] === value);

function DetailCard({ icon: Icon, title, children }) {
  return <article className="view360-card"><div className="view360-card-title"><span><Icon size={19} /></span><h2>{title}</h2></div>{children}</article>;
}

function EmptyValue({ children }) {
  return children ? children : <span className="view360-empty-value">Non renseigné</span>;
}

function Vue360() {
  const [form, setForm] = useState({ typeUnite: "", numeroEssai: "" });
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");
  const [searching, setSearching] = useState(false);

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
        <div className="form-field"><label htmlFor="view360-type">Type d’unité <b>*</b></label><select id="view360-type" value={form.typeUnite} onChange={(event) => setForm((current) => ({ ...current, typeUnite: event.target.value }))}><option value="">Sélectionnez</option><option value="Régionale">Régionale</option><option value="Spécialisée">Spécialisée</option></select></div>
        <div className="form-field"><label htmlFor="view360-number">Numéro de l’essai <b>*</b></label><input id="view360-number" value={form.numeroEssai} onChange={(event) => setForm((current) => ({ ...current, numeroEssai: event.target.value }))} placeholder="Saisissez le numéro exact" /></div>
      </div>{error && <p className="form-global-error" role="alert">{error}</p>}<div className="form-actions"><button className="save-button" disabled={searching}>{searching ? "Recherche…" : <><Search size={17} />Rechercher</>}</button></div></form>
    </article>

    {result && <div className="view360-results">
      <DetailCard icon={FlaskConical} title="Essai"><dl className="view360-details"><div><dt>Numéro</dt><dd>{result.essai.numeroEssai}</dd></div><div><dt>Date</dt><dd><EmptyValue>{result.essai.dateEssai}</EmptyValue></dd></div><div className="wide"><dt>Description</dt><dd><EmptyValue>{result.essai.description}</EmptyValue></dd></div><div><dt>Étalonnage</dt><dd>{result.essai.etalonnage == null ? <EmptyValue /> : result.essai.etalonnage ? "Oui" : "Non"}</dd></div></dl></DetailCard>
      <DetailCard icon={Boxes} title="Produit et famille"><dl className="view360-details"><div><dt>Produit</dt><dd><EmptyValue>{result.produit?.nomProduit}</EmptyValue></dd></div><div><dt>Code produit</dt><dd><EmptyValue>{result.produit?.codeProduit}</EmptyValue></dd></div><div><dt>Famille produit</dt><dd><EmptyValue>{result.famille?.nomFamille}</EmptyValue></dd></div><div><dt>Code famille</dt><dd><EmptyValue>{result.famille?.codeFamille}</EmptyValue></dd></div></dl></DetailCard>
      <DetailCard icon={Building2} title="Unités et régions"><div className="view360-list">{result.unites.map(({ realisation, unite, region }) => <div className="view360-list-item" key={realisation.idRealisation}><div><strong>{unite?.nomUnite || "Unité non renseignée"}</strong><span>{unite?.codeUnite} · {unite?.ville || "Ville non renseignée"}</span><span>Type unité : {unite?.typeUnite || "Non renseigné"}</span><span>Téléphone : {unite?.telephone || "Non renseigné"}</span><span>Adresse : {unite?.adresse || "Non renseignée"}</span><span>Effectif : opérateurs de saisie {unite?.nbrOperateurSaisie ?? "—"} · responsables dossier {unite?.nbrResponsableDossier ?? "—"} · laboratoire {unite?.nbrResponsableLaboratoire ?? "—"} · chantier {unite?.nbrResponsableChantier ?? "—"}</span></div><div><span className="view360-label"><MapPinned size={15} />{region?.nomRegion || "Région non renseignée"}</span><small>Code région : {region?.codeRegion || "Non renseigné"}</small><small>Réalisation : {realisation.dateRealisation || "Non renseignée"}</small></div></div>)}</div></DetailCard>
      <DetailCard icon={Wrench} title="Équipements et marques"><div className="view360-list">{result.equipements.length ? result.equipements.map(({ link, equipement, marque }) => <div className="view360-list-item" key={link.idUtilisationEquipement}><div><strong>{equipement?.designation || "Équipement non renseigné"}</strong><span>{equipement?.numeroSerie || "N° série non renseigné"}{equipement?.modele ? ` · ${equipement.modele}` : ""}</span></div><div><span className="view360-label"><Tags size={15} />{marque?.nomMarque || "Marque non renseignée"}</span><small>Fabricant : {marque?.nomFabricant || "Non renseigné"}</small><small>{link.dateUtilisationDebut || "—"} au {link.dateUtilisationFin || "—"}</small></div></div>) : <p className="view360-empty">Aucun équipement associé.</p>}</div></DetailCard>
      <DetailCard icon={ShieldCheck} title="Normes et organismes"><div className="view360-list">{result.normes.length ? result.normes.map(({ link, norme, organisme }) => <div className="view360-list-item" key={link.idConformite}><div><strong>{norme?.numeroNorme || norme?.codeNorme || "Norme non renseignée"}</strong><span>{norme?.nomNorme}</span><span>Code norme : {norme?.codeNorme || "Non renseigné"}</span></div><div><span className="view360-label">{organisme?.nomOrganisme || "Organisme non renseigné"}</span><small>Code organisme : {organisme?.codeOrganisme || "Non renseigné"}</small><small>{norme?.annee || "Année non renseignée"}</small></div></div>) : <p className="view360-empty">Aucune norme associée.</p>}</div></DetailCard>
      <DetailCard icon={Settings2} title="Paramètres"><div className="view360-tags">{result.parametres.length ? result.parametres.map(({ link, parametre }) => <span key={link.idMesure}><strong>{parametre?.nomParametre || "Paramètre"}</strong>{link.valeurCible != null ? ` : ${link.valeurCible}` : ""}{parametre?.uniteParametre ? ` ${parametre.uniteParametre}` : ""}</span>) : <p className="view360-empty">Aucun paramètre associé.</p>}</div></DetailCard>
    </div>}
  </section>;
}

export default Vue360;

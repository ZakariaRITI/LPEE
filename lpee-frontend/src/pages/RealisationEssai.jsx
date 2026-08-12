import { useCallback, useEffect, useMemo, useState } from "react";
import { ClipboardCheck, Pencil, Plus, Save, Trash2 } from "lucide-react";
import api from "../services/api";
import "./Unite.css";

const initialForm = { idEssai: "", idUnite: "", dateRealisation: "", equipementIds: [], equipementDates: {}, normeIds: [] };

function RealisationEssai() {
  const [form, setForm] = useState(initialForm);
  const [data, setData] = useState({ essais: [], unites: [], produits: [], equipements: [], normes: [], realisations: [], equipementLinks: [], normeLinks: [] });
  const [showForm, setShowForm] = useState(false);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [errors, setErrors] = useState({});
  const [message, setMessage] = useState("");
  const [editingId, setEditingId] = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const endpoints = [
        ["essais", "/api/essais"], ["unites", "/api/unites"], ["produits", "/api/produits"],
        ["equipements", "/api/equipements"], ["normes", "/api/normes"],
        ["realisations", "/api/realisations-essais"], ["equipementLinks", "/api/equipements-essais"],
        ["normeLinks", "/api/conformites-normes"],
      ];
      const responses = await Promise.all(endpoints.map(async ([key, endpoint]) => [key, (await api.get(endpoint)).data]));
      setData(Object.fromEntries(responses));
    } catch {
      setErrors({ form: "Impossible de charger les données des réalisations d’essais." });
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    const timer = window.setTimeout(load, 0);
    return () => window.clearTimeout(timer);
  }, [load]);

  const active = (items) => items.filter((item) => item.statut?.trim().toLowerCase() !== "inactif");
  const setField = (name, value) => {
    setForm((current) => ({ ...current, [name]: value }));
    setErrors((current) => ({ ...current, [name]: undefined, form: undefined }));
    setMessage("");
  };
  const selectedIds = (event) => Array.from(event.target.selectedOptions, (option) => Number(option.value));
  const selectEquipements = (event) => {
    const equipementIds = selectedIds(event);
    setForm((current) => ({
      ...current,
      equipementIds,
      equipementDates: Object.fromEntries(equipementIds.map((id) => [id, current.equipementDates[id] || { dateUtilisationDebut: "", dateUtilisationFin: "" }])),
    }));
    setErrors((current) => ({ ...current, equipementIds: undefined, equipementDates: undefined, form: undefined }));
    setMessage("");
  };
  const setEquipementDate = (idEquipement, name, value) => {
    setForm((current) => ({ ...current, equipementDates: { ...current.equipementDates, [idEquipement]: { ...current.equipementDates[idEquipement], [name]: value } } }));
    setErrors((current) => ({ ...current, equipementDates: undefined, form: undefined }));
  };
  const resetForm = () => { setForm(initialForm); setEditingId(null); setShowForm(false); setErrors({}); };

  const submit = async (event) => {
    event.preventDefault();
    const nextErrors = {};
    if (!form.idEssai) nextErrors.idEssai = "L’essai est obligatoire.";
    if (!form.idUnite) nextErrors.idUnite = "L’unité est obligatoire.";
    if (!form.dateRealisation) nextErrors.dateRealisation = "La date de réalisation est obligatoire.";
    if (!form.equipementIds.length) nextErrors.equipementIds = "Sélectionnez au moins un équipement.";
    else if (form.equipementIds.some((id) => !form.equipementDates[id]?.dateUtilisationDebut || !form.equipementDates[id]?.dateUtilisationFin)) nextErrors.equipementDates = "Renseignez les dates de début et de fin pour chaque équipement.";
    else if (form.equipementIds.some((id) => form.equipementDates[id].dateUtilisationFin < form.equipementDates[id].dateUtilisationDebut)) nextErrors.equipementDates = "La date de fin doit être postérieure ou égale à la date de début.";
    if (!form.normeIds.length) nextErrors.normeIds = "Sélectionnez au moins une norme.";
    if (Object.keys(nextErrors).length) { setErrors(nextErrors); return; }

    const idEssai = Number(form.idEssai);
    const essai = data.essais.find((item) => item.idEssai === idEssai);
    if (!essai) { setErrors({ form: "L’essai sélectionné est introuvable." }); return; }

    setSaving(true);
    setErrors({});
    try {
      const existingEquipements = data.equipementLinks.filter((link) => link.idEssai === idEssai);
      const existingNormes = data.normeLinks.filter((link) => link.idEssai === idEssai);
      const equipmentRequests = editingId
        ? existingEquipements.map((link) => api.put(`/api/equipements-essais/${link.idUtilisationEquipement}`, {
          idEssai, idEquipement: link.idEquipement,
          dateUtilisationDebut: form.equipementDates[link.idEquipement]?.dateUtilisationDebut || link.dateUtilisationDebut,
          dateUtilisationFin: form.equipementDates[link.idEquipement]?.dateUtilisationFin || link.dateUtilisationFin,
          statut: form.equipementIds.includes(link.idEquipement) ? "ACTIF" : "INACTIF",
        })).concat(form.equipementIds.filter((id) => !existingEquipements.some((link) => link.idEquipement === id)).map((idEquipement) => api.post("/api/equipements-essais", { idEssai, idEquipement, ...form.equipementDates[idEquipement], statut: "ACTIF" })))
        : form.equipementIds.filter((id) => !existingEquipements.some((link) => link.idEquipement === id)).map((idEquipement) => api.post("/api/equipements-essais", { idEssai, idEquipement, ...form.equipementDates[idEquipement], statut: "ACTIF" }));
      const normeRequests = editingId
        ? existingNormes.map((link) => api.put(`/api/conformites-normes/${link.idConformite}`, { idEssai, idNorme: link.idNorme, statutConformite: link.statutConformite, dateEvaluation: link.dateEvaluation, statut: form.normeIds.includes(link.idNorme) ? "ACTIF" : "INACTIF" })).concat(form.normeIds.filter((id) => !existingNormes.some((link) => link.idNorme === id)).map((idNorme) => api.post("/api/conformites-normes", { idEssai, idNorme, statut: "ACTIF" })))
        : form.normeIds.filter((id) => !existingNormes.some((link) => link.idNorme === id)).map((idNorme) => api.post("/api/conformites-normes", { idEssai, idNorme, statut: "ACTIF" }));
      await Promise.all([...equipmentRequests, ...normeRequests]);
      const realisationPayload = { idEssai, idUnite: Number(form.idUnite), dateRealisation: form.dateRealisation, statut: "ACTIF" };
      if (editingId) await api.put(`/api/realisations-essais/${editingId}`, realisationPayload);
      else await api.post("/api/realisations-essais", realisationPayload);
      const edited = Boolean(editingId);
      resetForm();
      setMessage(edited ? "La réalisation d’essai a été modifiée avec succès." : "La réalisation d’essai a été enregistrée avec succès.");
      await load();
    } catch (error) {
      const response = error.response?.data;
      setErrors(response?.messages || { form: response?.message || "L’enregistrement de la réalisation d’essai a échoué." });
    } finally {
      setSaving(false);
    }
  };

  const edit = (item) => {
    const equipmentLinks = data.equipementLinks.filter((link) => link.idEssai === item.idEssai && link.statut?.trim().toLowerCase() !== "inactif");
    const normeIds = data.normeLinks.filter((link) => link.idEssai === item.idEssai && link.statut?.trim().toLowerCase() !== "inactif").map((link) => link.idNorme);
    setForm({
      idEssai: String(item.idEssai), idUnite: String(item.idUnite), dateRealisation: item.dateRealisation || "",
      equipementIds: equipmentLinks.map((link) => link.idEquipement),
      equipementDates: Object.fromEntries(equipmentLinks.map((link) => [link.idEquipement, { dateUtilisationDebut: link.dateUtilisationDebut || "", dateUtilisationFin: link.dateUtilisationFin || "" }])),
      normeIds,
    });
    setEditingId(item.idRealisation); setShowForm(true); setErrors({}); setMessage("");
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const remove = async (item) => {
    if (!window.confirm("Supprimer cette réalisation d’essai ?")) return;
    try {
      await api.put(`/api/realisations-essais/${item.idRealisation}`, { idEssai: item.idEssai, idUnite: item.idUnite, dateRealisation: item.dateRealisation, statut: "INACTIF" });
      if (editingId === item.idRealisation) resetForm();
      setMessage("La réalisation d’essai a été supprimée.");
      await load();
    } catch (error) {
      setErrors({ form: error.response?.data?.message || "La suppression de la réalisation a échoué." });
    }
  };

  const byId = (items, key, id) => items.find((item) => item[key] === id);
  const namesFor = (links, idEssai, source, sourceId, label) => links
    .filter((link) => link.idEssai === idEssai && link.statut?.trim().toLowerCase() !== "inactif")
    .map((link) => byId(source, sourceId, link[sourceId])?.[label]).filter(Boolean);
  const rows = useMemo(() => data.realisations.filter((item) => item.statut?.trim().toLowerCase() !== "inactif"), [data.realisations]);

  return <section className="unite-page">
    <div className="page-heading"><div><p className="dashboard-eyebrow">Référentiel</p><h1>Réalisation Essai</h1><p>Associez les essais aux unités, équipements et normes.</p></div><span className="page-heading-icon"><ClipboardCheck /></span></div>
    <div className="unit-toolbar"><button className="add-region-button" onClick={() => { const open = !showForm || Boolean(editingId); resetForm(); setShowForm(open); setMessage(""); }}><Plus size={17} />Ajouter une réalisation</button></div>
    {message && <p className="form-success" role="status">{message}</p>}
    {showForm && <article className="unite-form-card"><div className="form-card-heading"><div><h2>{editingId ? "Modifier la réalisation d’essai" : "Nouvelle réalisation d’essai"}</h2><p>Le produit est automatiquement déterminé par l’essai sélectionné.</p></div></div>
      <form className="unite-form" onSubmit={submit}><div className="form-grid">
        <div className="form-field"><label htmlFor="realisation-essai">Essai <b>*</b></label><select id="realisation-essai" value={form.idEssai} onChange={(event) => setField("idEssai", event.target.value)}><option value="">Sélectionnez</option>{active(data.essais).map((item) => <option key={item.idEssai} value={item.idEssai}>{item.numeroEssai}</option>)}</select>{errors.idEssai && <span className="field-error">{errors.idEssai}</span>}</div>
        <div className="form-field"><label htmlFor="realisation-unite">Unité <b>*</b></label><select id="realisation-unite" value={form.idUnite} onChange={(event) => setField("idUnite", event.target.value)}><option value="">Sélectionnez</option>{data.unites.map((item) => <option key={item.idUnite} value={item.idUnite}>{item.nomUnite}</option>)}</select>{errors.idUnite && <span className="field-error">{errors.idUnite}</span>}</div>
        <div className="form-field"><label htmlFor="date-realisation">Date de réalisation <b>*</b></label><input id="date-realisation" type="date" value={form.dateRealisation} onChange={(event) => setField("dateRealisation", event.target.value)} />{errors.dateRealisation && <span className="field-error">{errors.dateRealisation}</span>}</div>
        <div className="form-field form-field-wide"><label htmlFor="realisation-equipements">Équipements <b>*</b></label><select id="realisation-equipements" className="parameters-select" multiple value={form.equipementIds.map(String)} onChange={selectEquipements}>{active(data.equipements).map((item) => <option key={item.idEquipement} value={item.idEquipement}>{item.designation}{item.numeroSerie ? ` — ${item.numeroSerie}` : ""}</option>)}</select><span className="field-help">Maintenez Ctrl pour sélectionner plusieurs éléments.</span>{errors.equipementIds && <span className="field-error">{errors.equipementIds}</span>}</div>
        {form.equipementIds.length > 0 && <div className="form-field form-field-wide equipment-dates"><label>Dates d’utilisation des équipements <b>*</b></label>{form.equipementIds.map((idEquipement) => { const equipement = byId(data.equipements, "idEquipement", idEquipement); const dates = form.equipementDates[idEquipement] || {}; return <div className="equipment-date-row" key={idEquipement}><strong>{equipement?.designation || `Équipement #${idEquipement}`}</strong><label>Début<input type="date" value={dates.dateUtilisationDebut || ""} onChange={(event) => setEquipementDate(idEquipement, "dateUtilisationDebut", event.target.value)} /></label><label>Fin<input type="date" value={dates.dateUtilisationFin || ""} onChange={(event) => setEquipementDate(idEquipement, "dateUtilisationFin", event.target.value)} /></label></div>; })}{errors.equipementDates && <span className="field-error">{errors.equipementDates}</span>}</div>}
        <div className="form-field"><label htmlFor="realisation-normes">Normes <b>*</b></label><select id="realisation-normes" className="parameters-select" multiple value={form.normeIds.map(String)} onChange={(event) => setField("normeIds", selectedIds(event))}>{active(data.normes).map((item) => <option key={item.idNorme} value={item.idNorme}>{item.numeroNorme || item.codeNorme} — {item.nomNorme}</option>)}</select><span className="field-help">Maintenez Ctrl pour sélectionner plusieurs éléments.</span>{errors.normeIds && <span className="field-error">{errors.normeIds}</span>}</div>
      </div>{errors.form && <p className="form-global-error">{errors.form}</p>}<div className="form-actions"><button type="button" className="reset-button" onClick={resetForm}>Annuler</button><button className="save-button" disabled={saving}>{saving ? "Enregistrement…" : <><Save size={17} />Enregistrer</>}</button></div></form>
    </article>}
    <article className="units-list-card"><div className="list-card-heading"><div><h2>Liste des réalisations d’essais</h2><p>{rows.length} réalisation(s) active(s)</p></div></div>
      <div className="units-table-wrap"><table><thead><tr><th>Essai</th><th>Produit</th><th>Unité</th><th>Équipements</th><th>Normes</th><th>Actions</th></tr></thead><tbody>
        {loading ? <tr><td className="table-state" colSpan="6">Chargement des réalisations…</td></tr> : rows.length === 0 ? <tr><td className="table-state" colSpan="6">Aucune réalisation d’essai enregistrée.</td></tr> : rows.map((item) => {
          const essai = byId(data.essais, "idEssai", item.idEssai);
          const equipements = namesFor(data.equipementLinks, item.idEssai, data.equipements, "idEquipement", "designation");
          const normes = namesFor(data.normeLinks, item.idEssai, data.normes, "idNorme", "numeroNorme");
          return <tr key={item.idRealisation}><td><strong>{essai?.numeroEssai || "—"}</strong></td><td>{byId(data.produits, "idProduit", essai?.idProduit)?.nomProduit || "—"}</td><td>{byId(data.unites, "idUnite", item.idUnite)?.nomUnite || "—"}</td><td>{equipements.join(", ") || "—"}</td><td>{normes.join(", ") || "—"}</td><td><div className="table-actions"><button className="edit-action" onClick={() => edit(item)}><Pencil size={16} />Modifier</button><button className="delete-action" onClick={() => remove(item)}><Trash2 size={16} />Supprimer</button></div></td></tr>;
        })}
      </tbody></table></div>
    </article>
  </section>;
}

export default RealisationEssai;

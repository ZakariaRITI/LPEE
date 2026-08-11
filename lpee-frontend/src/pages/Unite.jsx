import { useCallback, useEffect, useRef, useState } from "react";
import { Building2, Eye, MapPinned, Pencil, Plus, RotateCcw, Save, Trash2, X } from "lucide-react";
import api from "../services/api";
import "./Unite.css";

const initialForm = { idRegion: "", codeUnite: "", nomUnite: "", typeUnite: "", ville: "", adresse: "", telephone: "", nbrOperateurSaisie: "", nbrResponsableDossier: "", nbrResponsableLaboratoire: "", nbrResponsableChantier: "" };
const optionalNumbers = ["nbrOperateurSaisie", "nbrResponsableDossier", "nbrResponsableLaboratoire", "nbrResponsableChantier"];
const initialRegionForm = { codeRegion: "", nomRegion: "" };

function Unite() {
  const formRef = useRef(null);
  const [form, setForm] = useState(initialForm);
  const [regions, setRegions] = useState([]);
  const [units, setUnits] = useState([]);
  const [errors, setErrors] = useState({});
  const [message, setMessage] = useState("");
  const [isSaving, setSaving] = useState(false);
  const [isListVisible, setListVisible] = useState(false);
  const [isLoadingUnits, setLoadingUnits] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [isRegionsVisible, setRegionsVisible] = useState(false);
  const [isRegionFormVisible, setRegionFormVisible] = useState(false);
  const [regionForm, setRegionForm] = useState(initialRegionForm);
  const [regionErrors, setRegionErrors] = useState({});
  const [editingRegionId, setEditingRegionId] = useState(null);
  const [isSavingRegion, setSavingRegion] = useState(false);

  const loadUnits = useCallback(async () => {
    try {
      setLoadingUnits(true);
      const { data } = await api.get("/api/unites");
      setUnits(data);
    } catch {
      setErrors((current) => ({ ...current, form: "Impossible de charger les unités." }));
    } finally {
      setLoadingUnits(false);
    }
  }, []);

  const loadRegions = useCallback(async () => {
    try {
      const { data } = await api.get("/api/regions");
      setRegions(data);
    } catch {
      setRegionErrors({ form: "Impossible de charger les régions." });
    }
  }, []);

  useEffect(() => {
    api.get("/api/regions")
      .then(({ data }) => setRegions(data))
      .catch(() => setRegionErrors({ form: "Impossible de charger les régions." }));
  }, []);

  const handleChange = ({ target: { name, value } }) => {
    setForm((current) => ({ ...current, [name]: value }));
    setErrors((current) => ({ ...current, [name]: undefined, form: undefined }));
    setMessage("");
  };

  const validate = () => {
    const nextErrors = {};
    if (!form.idRegion) nextErrors.idRegion = "La région est obligatoire.";
    if (!form.codeUnite.trim()) nextErrors.codeUnite = "Le code unité est obligatoire.";
    if (!form.nomUnite.trim()) nextErrors.nomUnite = "Le nom de l’unité est obligatoire.";
    optionalNumbers.forEach((field) => {
      if (form[field] !== "" && (!Number.isInteger(Number(form[field])) || Number(form[field]) < 0)) nextErrors[field] = "Saisissez un nombre entier positif ou nul.";
    });
    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setMessage("");
    if (!validate()) return;
    const payload = { ...form, idRegion: Number(form.idRegion), ...Object.fromEntries(optionalNumbers.map((field) => [field, form[field] === "" ? null : Number(form[field])])) };

    try {
      setSaving(true);
      if (editingId) await api.put(`/api/unites/${editingId}`, payload);
      else await api.post("/api/unites", payload);
      setForm(initialForm);
      setEditingId(null);
      setMessage(editingId ? "L’unité a été modifiée avec succès." : "L’unité a été enregistrée avec succès.");
      if (isListVisible) loadUnits();
    } catch (requestError) {
      const response = requestError.response?.data;
      setErrors(response?.messages || { form: response?.message || "L’enregistrement a échoué. Veuillez réessayer." });
    } finally {
      setSaving(false);
    }
  };

  const resetForm = () => { setForm(initialForm); setEditingId(null); setErrors({}); setMessage(""); };
  const toggleList = () => { const next = !isListVisible; setListVisible(next); if (next) loadUnits(); };
  const editUnit = (unit) => {
    setForm({ ...initialForm, ...unit, idRegion: String(unit.idRegion ?? ""), ...Object.fromEntries(optionalNumbers.map((field) => [field, unit[field] ?? ""])) });
    setEditingId(unit.idUnite);
    setErrors({}); setMessage("");
    formRef.current?.scrollIntoView({ behavior: "smooth", block: "start" });
  };
  const deleteUnit = async (unit) => {
    if (!window.confirm(`Supprimer l’unité « ${unit.nomUnite} » ? Cette action est irréversible.`)) return;
    try {
      await api.delete(`/api/unites/${unit.idUnite}`);
      setMessage("L’unité a été supprimée.");
      if (editingId === unit.idUnite) resetForm();
      loadUnits();
    } catch (requestError) {
      setErrors({ form: requestError.response?.data?.message || "La suppression a échoué. Veuillez réessayer." });
    }
  };
  const toggleRegions = () => { const next = !isRegionsVisible; setRegionsVisible(next); if (next) loadRegions(); };
  const resetRegionForm = () => { setRegionForm(initialRegionForm); setEditingRegionId(null); setRegionErrors({}); setRegionFormVisible(false); };
  const handleRegionChange = ({ target: { name, value } }) => { setRegionForm((current) => ({ ...current, [name]: value })); setRegionErrors((current) => ({ ...current, [name]: undefined, form: undefined })); };
  const saveRegion = async (event) => {
    event.preventDefault();
    const nextErrors = {};
    if (!regionForm.codeRegion.trim()) nextErrors.codeRegion = "Le code région est obligatoire.";
    if (!regionForm.nomRegion.trim()) nextErrors.nomRegion = "Le nom de la région est obligatoire.";
    if (Object.keys(nextErrors).length) { setRegionErrors(nextErrors); return; }
    try {
      setSavingRegion(true);
      if (editingRegionId) await api.put(`/api/regions/${editingRegionId}`, regionForm);
      else await api.post("/api/regions", regionForm);
      resetRegionForm();
      loadRegions();
    } catch (requestError) {
      const response = requestError.response?.data;
      setRegionErrors(response?.messages || { form: response?.message || "L’enregistrement de la région a échoué." });
    } finally { setSavingRegion(false); }
  };
  const editRegion = (region) => { setRegionForm({ codeRegion: region.codeRegion, nomRegion: region.nomRegion }); setEditingRegionId(region.idRegion); setRegionFormVisible(true); setRegionErrors({}); };
  const deleteRegion = async (region) => {
    if (!window.confirm(`Supprimer la région « ${region.nomRegion} » ? Cette action est irréversible.`)) return;
    try { await api.delete(`/api/regions/${region.idRegion}`); if (editingRegionId === region.idRegion) resetRegionForm(); loadRegions(); }
    catch (requestError) { setRegionErrors({ form: requestError.response?.data?.message || "La suppression de la région a échoué." }); }
  };
  const fieldError = (name) => errors[name] && <span className="field-error">{errors[name]}</span>;

  return <section className="unite-page">
    <div className="page-heading"><div><p className="dashboard-eyebrow">Référentiel</p><h1>Gestion des unités</h1><p>Créez et organisez les unités du réseau LPEE.</p></div><span className="page-heading-icon"><Building2 /></span></div>
    <div className="unit-toolbar"><button className="region-button" onClick={toggleRegions}><MapPinned size={18} />Région</button><button className="view-units-button" onClick={toggleList}><Eye size={18} />{isListVisible ? "Masquer les unités" : "Voir les unités"}</button></div>
    <article className="unite-form-card" ref={formRef}>
      <div className="form-card-heading"><div><h2>{editingId ? "Modifier l’unité" : "Nouvelle unité"}</h2><p>Les champs marqués d’un astérisque sont obligatoires.</p></div>{editingId && <button className="cancel-edit-button" onClick={resetForm}><X size={17} />Annuler la modification</button>}</div>
      <form className="unite-form" onSubmit={handleSubmit} noValidate>
        <div className="form-grid">
          <div className="form-field"><label htmlFor="idRegion">Région <b>*</b></label><select id="idRegion" name="idRegion" value={form.idRegion} onChange={handleChange} aria-invalid={Boolean(errors.idRegion)}><option value="">Sélectionnez une région</option>{regions.map((region) => <option key={region.idRegion} value={region.idRegion}>{region.nomRegion} ({region.codeRegion})</option>)}</select>{fieldError("idRegion")}</div>
          <div className="form-field"><label htmlFor="typeUnite">Type d’unité</label><select id="typeUnite" name="typeUnite" value={form.typeUnite} onChange={handleChange}><option value="">Sélectionnez un type</option><option value="Régionale">Régionale</option><option value="Spécialisée">Spécialisée</option></select></div>
          <div className="form-field"><label htmlFor="codeUnite">Code unité <b>*</b></label><input id="codeUnite" name="codeUnite" value={form.codeUnite} onChange={handleChange} placeholder="Ex. UNI-CAS-01" aria-invalid={Boolean(errors.codeUnite)} />{fieldError("codeUnite")}</div>
          <div className="form-field"><label htmlFor="nomUnite">Nom de l’unité <b>*</b></label><input id="nomUnite" name="nomUnite" value={form.nomUnite} onChange={handleChange} placeholder="Nom de l’unité" aria-invalid={Boolean(errors.nomUnite)} />{fieldError("nomUnite")}</div>
          <div className="form-field"><label htmlFor="ville">Ville</label><input id="ville" name="ville" value={form.ville} onChange={handleChange} placeholder="Ville" /></div>
          <div className="form-field"><label htmlFor="telephone">Téléphone</label><input id="telephone" name="telephone" type="tel" value={form.telephone} onChange={handleChange} placeholder="Ex. 05 XX XX XX XX" /></div>
          <div className="form-field form-field-wide"><label htmlFor="adresse">Adresse</label><input id="adresse" name="adresse" value={form.adresse} onChange={handleChange} placeholder="Adresse complète" /></div>
        </div>
        <fieldset className="capacity-fieldset"><legend>Effectif de l’unité</legend><div className="form-grid capacity-grid">{[['nbrOperateurSaisie', 'Opérateurs de saisie'], ['nbrResponsableDossier', 'Responsables dossier'], ['nbrResponsableLaboratoire', 'Responsables laboratoire'], ['nbrResponsableChantier', 'Responsables chantier']].map(([name, label]) => <div className="form-field" key={name}><label htmlFor={name}>{label}</label><input id={name} name={name} type="number" min="0" step="1" value={form[name]} onChange={handleChange} aria-invalid={Boolean(errors[name])} />{fieldError(name)}</div>)}</div></fieldset>
        {errors.form && <p className="form-global-error" role="alert">{errors.form}</p>}{message && <p className="form-success" role="status">{message}</p>}
        <div className="form-actions"><button type="button" className="reset-button" onClick={resetForm}><RotateCcw size={18} />Réinitialiser</button><button className="save-button" type="submit" disabled={isSaving}><Save size={18} />{isSaving ? "Enregistrement…" : editingId ? "Modifier" : "Enregistrer"}</button></div>
      </form>
    </article>
    {isListVisible && <article className="units-list-card"><div className="list-card-heading"><div><h2>Liste des unités</h2><p>{isLoadingUnits ? "Chargement…" : `${units.length} unité(s) disponible(s)`}</p></div></div><div className="units-table-wrap"><table><thead><tr><th>Code</th><th>Unité</th><th>Type</th><th>Ville</th><th>Téléphone</th><th>Actions</th></tr></thead><tbody>{isLoadingUnits ? <tr><td colSpan="6" className="table-state">Chargement des unités…</td></tr> : units.length ? units.map((unit) => <tr key={unit.idUnite}><td>{unit.codeUnite}</td><td><strong>{unit.nomUnite}</strong></td><td>{unit.typeUnite || "—"}</td><td>{unit.ville || "—"}</td><td>{unit.telephone || "—"}</td><td><div className="table-actions"><button className="edit-action" onClick={() => editUnit(unit)}><Pencil size={16} />Modifier</button><button className="delete-action" onClick={() => deleteUnit(unit)}><Trash2 size={16} />Supprimer</button></div></td></tr>) : <tr><td colSpan="6" className="table-state">Aucune unité disponible.</td></tr>}</tbody></table></div></article>}
    {isRegionsVisible && <article className="regions-card"><div className="list-card-heading"><div><h2>Gestion des régions</h2><p>{regions.length} région(s) disponible(s)</p></div><button className="add-region-button" onClick={() => { resetRegionForm(); setRegionFormVisible(true); }}><Plus size={17} />Ajouter</button></div>
      {isRegionFormVisible && <form className="region-form" onSubmit={saveRegion} noValidate><div className="form-field"><label htmlFor="codeRegion">Code région <b>*</b></label><input id="codeRegion" name="codeRegion" value={regionForm.codeRegion} onChange={handleRegionChange} aria-invalid={Boolean(regionErrors.codeRegion)} />{regionErrors.codeRegion && <span className="field-error">{regionErrors.codeRegion}</span>}</div><div className="form-field"><label htmlFor="nomRegion">Nom de la région <b>*</b></label><input id="nomRegion" name="nomRegion" value={regionForm.nomRegion} onChange={handleRegionChange} aria-invalid={Boolean(regionErrors.nomRegion)} />{regionErrors.nomRegion && <span className="field-error">{regionErrors.nomRegion}</span>}</div><div className="region-form-actions"><button type="button" className="reset-button" onClick={resetRegionForm}>Annuler</button><button type="submit" className="save-button" disabled={isSavingRegion}>{isSavingRegion ? "Enregistrement…" : editingRegionId ? "Modifier" : "Ajouter"}</button></div></form>}
      {regionErrors.form && <p className="region-error" role="alert">{regionErrors.form}</p>}<div className="units-table-wrap"><table><thead><tr><th>Code</th><th>Région</th><th>Actions</th></tr></thead><tbody>{regions.length ? regions.map((region) => <tr key={region.idRegion}><td>{region.codeRegion}</td><td><strong>{region.nomRegion}</strong></td><td><div className="table-actions"><button className="edit-action" onClick={() => editRegion(region)}><Pencil size={16} />Modifier</button><button className="delete-action" onClick={() => deleteRegion(region)}><Trash2 size={16} />Supprimer</button></div></td></tr>) : <tr><td colSpan="3" className="table-state">Aucune région disponible.</td></tr>}</tbody></table></div>
    </article>}
  </section>;
}

export default Unite;

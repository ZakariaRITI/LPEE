import { useCallback, useEffect, useRef, useState } from "react";
import { Building2, ChevronDown, ChevronLeft, ChevronRight, ChevronUp, Eye, MapPinned, Pencil, Plus, RotateCcw, Save, Search, Trash2, X } from "lucide-react";
import Swal from "sweetalert2";
import api from "../services/api";
import "./Unite.css";

const initialForm = { idRegion: "", codeUnite: "", nomUnite: "", typeUnite: "", ville: "", adresse: "", telephone: "", nbrOperateurSaisie: "", nbrResponsableDossier: "", nbrResponsableLaboratoire: "", nbrResponsableChantier: "" };
const optionalNumbers = ["nbrOperateurSaisie", "nbrResponsableDossier", "nbrResponsableLaboratoire", "nbrResponsableChantier"];
const initialRegionForm = { codeRegion: "", nomRegion: "" };
const unitsPerPage = 5;

function Unite() {
  const formRef = useRef(null);
  const listRef = useRef(null);
  const shouldScrollToListRef = useRef(false);
  const highlightTimeoutRef = useRef(null);
  const searchTimeoutRef = useRef(null);
  const unitsRequestRef = useRef(0);
  const regionHighlightTimeoutRef = useRef(null);
  const regionSearchTimeoutRef = useRef(null);
  const regionsRequestRef = useRef(0);
  const [form, setForm] = useState(initialForm);
  const [regions, setRegions] = useState([]);
  const [regionRows, setRegionRows] = useState([]);
  const [units, setUnits] = useState([]);
  const [errors, setErrors] = useState({});
  const [message, setMessage] = useState("");
  const [pageSuccess, setPageSuccess] = useState("");
  const [highlightedUnitId, setHighlightedUnitId] = useState(null);
  const [editSuccessSequence, setEditSuccessSequence] = useState(0);
  const [isSaving, setSaving] = useState(false);
  const [isFormVisible, setFormVisible] = useState(true);
  const [isListVisible, setListVisible] = useState(false);
  const [isListExpanded, setListExpanded] = useState(true);
  const [isLoadingUnits, setLoadingUnits] = useState(false);
  const [unitPage, setUnitPage] = useState(0);
  const [unitTotalPages, setUnitTotalPages] = useState(0);
  const [unitTotalElements, setUnitTotalElements] = useState(0);
  const [unitCodeSearch, setUnitCodeSearch] = useState("");
  const [editingId, setEditingId] = useState(null);
  const [isRegionsVisible, setRegionsVisible] = useState(false);
  const [isRegionListExpanded, setRegionListExpanded] = useState(true);
  const [isLoadingRegions, setLoadingRegions] = useState(false);
  const [regionPage, setRegionPage] = useState(0);
  const [regionTotalPages, setRegionTotalPages] = useState(0);
  const [regionTotalElements, setRegionTotalElements] = useState(0);
  const [regionCodeSearch, setRegionCodeSearch] = useState("");
  const [regionPageSuccess, setRegionPageSuccess] = useState("");
  const [highlightedRegionId, setHighlightedRegionId] = useState(null);
  const [isRegionFormVisible, setRegionFormVisible] = useState(false);
  const [regionForm, setRegionForm] = useState(initialRegionForm);
  const [regionErrors, setRegionErrors] = useState({});
  const [editingRegionId, setEditingRegionId] = useState(null);
  const [isSavingRegion, setSavingRegion] = useState(false);

  const loadUnits = useCallback(async (page = 0, codeUnite = "") => {
    const requestId = ++unitsRequestRef.current;
    try {
      setLoadingUnits(true);
      const { data } = await api.get("/api/unites", { params: { page, size: unitsPerPage, sort: "idUnite,asc", codeUnite: codeUnite.trim() || undefined } });
      if (requestId !== unitsRequestRef.current) return;
      setUnits(data.content);
      setUnitPage(data.number);
      setUnitTotalPages(data.totalPages);
      setUnitTotalElements(data.totalElements);
    } catch {
      if (requestId !== unitsRequestRef.current) return;
      setErrors((current) => ({ ...current, form: "Impossible de charger les unités." }));
    } finally {
      if (requestId === unitsRequestRef.current) setLoadingUnits(false);
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

  const loadRegionPage = useCallback(async (page = 0, codeRegion = "") => {
    const requestId = ++regionsRequestRef.current;
    try {
      setLoadingRegions(true);
      const { data } = await api.get("/api/regions", { params: { page, size: unitsPerPage, sort: "idRegion,asc", codeRegion: codeRegion.trim() || undefined } });
      if (requestId !== regionsRequestRef.current) return;
      setRegionRows(data.content);
      setRegionPage(data.number);
      setRegionTotalPages(data.totalPages);
      setRegionTotalElements(data.totalElements);
    } catch {
      if (requestId !== regionsRequestRef.current) return;
      setRegionErrors((current) => ({ ...current, form: "Impossible de charger les régions." }));
    } finally {
      if (requestId === regionsRequestRef.current) setLoadingRegions(false);
    }
  }, []);

  useEffect(() => {
    api.get("/api/regions")
      .then(({ data }) => setRegions(data))
      .catch(() => setRegionErrors({ form: "Impossible de charger les régions." }));
  }, []);

  useEffect(() => () => {
    clearTimeout(highlightTimeoutRef.current);
    clearTimeout(searchTimeoutRef.current);
    clearTimeout(regionHighlightTimeoutRef.current);
    clearTimeout(regionSearchTimeoutRef.current);
  }, []);

  useEffect(() => {
    if (!isListVisible || !isListExpanded || isLoadingUnits || !shouldScrollToListRef.current || !listRef.current) return;
    shouldScrollToListRef.current = false;
    const listTop = listRef.current.getBoundingClientRect().top + window.scrollY;
    window.scrollTo({ top: Math.max(0, listTop - 105), behavior: "smooth" });
  }, [isListVisible, isListExpanded, isLoadingUnits]);

  useEffect(() => {
    if (!editSuccessSequence) return undefined;
    const frame = requestAnimationFrame(() => {
      const scrollContainer = document.scrollingElement || document.documentElement;
      scrollContainer.scrollTo({ top: 0, left: 0, behavior: "smooth" });
    });
    return () => cancelAnimationFrame(frame);
  }, [editSuccessSequence]);

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

  const showTimedSuccess = (unitId, successMessage, scrollAfterRender = false) => {
    setMessage("");
    setPageSuccess(successMessage);
    setHighlightedUnitId(unitId);
    clearTimeout(highlightTimeoutRef.current);
    highlightTimeoutRef.current = setTimeout(() => {
      setPageSuccess("");
      setHighlightedUnitId(null);
    }, 10000);
    if (scrollAfterRender) {
      shouldScrollToListRef.current = false;
      setEditSuccessSequence((sequence) => sequence + 1);
    } else {
      window.scrollTo({ top: 0, left: 0, behavior: "smooth" });
    }
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setMessage("");
    if (!validate()) return;
    const payload = { ...form, idRegion: Number(form.idRegion), ...Object.fromEntries(optionalNumbers.map((field) => [field, form[field] === "" ? null : Number(form[field])])) };

    if (editingId) {
      const confirmation = await Swal.fire({
        title: "Confirmer la modification ?",
        text: `Les informations de l’unité « ${form.nomUnite} » seront mises à jour.`,
        icon: "question",
        showCancelButton: true,
        confirmButtonText: "Confirmer",
        cancelButtonText: "Annuler",
        confirmButtonColor: "#0877b6",
        cancelButtonColor: "#6e8195",
        focusCancel: true,
        returnFocus: false,
        reverseButtons: true,
      });
      if (!confirmation.isConfirmed) return;
    }

    try {
      setSaving(true);
      const wasEditing = Boolean(editingId);
      const { data: savedUnit } = wasEditing
        ? await api.put(`/api/unites/${editingId}`, payload)
        : await api.post("/api/unites", payload);
      setForm(initialForm);
      setEditingId(null);
      showTimedSuccess(savedUnit?.idUnite ?? editingId, wasEditing ? "L’unité a été modifiée avec succès." : "L’unité a été enregistrée avec succès.", wasEditing);
      setListVisible(true);
      setListExpanded(true);
      const targetPage = wasEditing ? unitPage : unitCodeSearch.trim() ? 0 : Math.floor(unitTotalElements / unitsPerPage);
      loadUnits(targetPage, unitCodeSearch);
    } catch (requestError) {
      const response = requestError.response?.data;
      setErrors(response?.messages || { form: response?.message || "L’enregistrement a échoué. Veuillez réessayer." });
    } finally {
      setSaving(false);
    }
  };

  const resetForm = () => { setForm(initialForm); setEditingId(null); setErrors({}); setMessage(""); };
  const toggleList = () => { const next = !isListVisible; setListVisible(next); if (next) { shouldScrollToListRef.current = true; setListExpanded(true); loadUnits(unitPage, unitCodeSearch); } };
  const changeUnitPage = (page) => { if (page < 0 || page >= unitTotalPages || page === unitPage) return; loadUnits(page, unitCodeSearch); };
  const handleUnitSearch = ({ target: { value } }) => {
    setUnitCodeSearch(value);
    unitsRequestRef.current += 1;
    clearTimeout(searchTimeoutRef.current);
    setUnitPage(0);
    setUnitTotalPages(0);
    setUnitTotalElements(0);
    setUnits([]);
    setLoadingUnits(true);
    searchTimeoutRef.current = setTimeout(() => loadUnits(0, value), 250);
  };
  const editUnit = (unit) => {
    setForm({ ...initialForm, ...unit, idRegion: String(unit.idRegion ?? ""), ...Object.fromEntries(optionalNumbers.map((field) => [field, unit[field] ?? ""])) });
    setEditingId(unit.idUnite);
    setFormVisible(true);
    setErrors({}); setMessage("");
    requestAnimationFrame(() => formRef.current?.scrollIntoView({ behavior: "smooth", block: "start" }));
  };
  const deleteUnit = async (unit) => {
    const confirmation = await Swal.fire({
      title: "Supprimer cette unité ?",
      text: `L’unité « ${unit.nomUnite} » sera définitivement supprimée.`,
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Supprimer",
      cancelButtonText: "Annuler",
      confirmButtonColor: "#b8444d",
      cancelButtonColor: "#6e8195",
      focusCancel: true,
      reverseButtons: true,
    });
    if (!confirmation.isConfirmed) return;
    try {
      await api.delete(`/api/unites/${unit.idUnite}`);
      if (editingId === unit.idUnite) resetForm();
      showTimedSuccess(null, "L’unité a été supprimée avec succès.");
      const targetPage = units.length === 1 && unitPage > 0 ? unitPage - 1 : unitPage;
      loadUnits(targetPage, unitCodeSearch);
    } catch (requestError) {
      setErrors({ form: requestError.response?.data?.message || "La suppression a échoué. Veuillez réessayer." });
    }
  };
  const showRegions = () => { setRegionsVisible(true); setRegionListExpanded(true); loadRegions(); loadRegionPage(regionPage, regionCodeSearch); };
  const showUnits = () => setRegionsVisible(false);
  const resetRegionForm = () => { setRegionForm(initialRegionForm); setEditingRegionId(null); setRegionErrors({}); setRegionFormVisible(false); };
  const handleRegionChange = ({ target: { name, value } }) => { setRegionForm((current) => ({ ...current, [name]: value })); setRegionErrors((current) => ({ ...current, [name]: undefined, form: undefined })); };
  const showRegionSuccess = (regionId, successMessage) => {
    setRegionPageSuccess(successMessage);
    setHighlightedRegionId(regionId);
    clearTimeout(regionHighlightTimeoutRef.current);
    regionHighlightTimeoutRef.current = setTimeout(() => {
      setRegionPageSuccess("");
      setHighlightedRegionId(null);
    }, 10000);
    requestAnimationFrame(() => {
      const scrollContainer = document.scrollingElement || document.documentElement;
      scrollContainer.scrollTo({ top: 0, left: 0, behavior: "smooth" });
    });
  };
  const changeRegionPage = (page) => { if (page < 0 || page >= regionTotalPages || page === regionPage) return; loadRegionPage(page, regionCodeSearch); };
  const handleRegionSearch = ({ target: { value } }) => {
    setRegionCodeSearch(value);
    regionsRequestRef.current += 1;
    clearTimeout(regionSearchTimeoutRef.current);
    setRegionPage(0);
    setRegionTotalPages(0);
    setRegionTotalElements(0);
    setRegionRows([]);
    setLoadingRegions(true);
    regionSearchTimeoutRef.current = setTimeout(() => loadRegionPage(0, value), 250);
  };
  const saveRegion = async (event) => {
    event.preventDefault();
    const nextErrors = {};
    if (!regionForm.codeRegion.trim()) nextErrors.codeRegion = "Le code région est obligatoire.";
    if (!regionForm.nomRegion.trim()) nextErrors.nomRegion = "Le nom de la région est obligatoire.";
    if (Object.keys(nextErrors).length) { setRegionErrors(nextErrors); return; }
    if (editingRegionId) {
      const confirmation = await Swal.fire({
        title: "Confirmer la modification ?",
        text: `Les informations de la région « ${regionForm.nomRegion} » seront mises à jour.`,
        icon: "question",
        showCancelButton: true,
        confirmButtonText: "Confirmer",
        cancelButtonText: "Annuler",
        confirmButtonColor: "#0877b6",
        cancelButtonColor: "#6e8195",
        focusCancel: true,
        returnFocus: false,
        reverseButtons: true,
      });
      if (!confirmation.isConfirmed) return;
    }
    try {
      setSavingRegion(true);
      const wasEditing = Boolean(editingRegionId);
      const { data: savedRegion } = wasEditing
        ? await api.put(`/api/regions/${editingRegionId}`, regionForm)
        : await api.post("/api/regions", regionForm);
      const targetPage = wasEditing ? regionPage : regionCodeSearch.trim() ? 0 : Math.floor(regionTotalElements / unitsPerPage);
      resetRegionForm();
      loadRegions();
      setRegionListExpanded(true);
      showRegionSuccess(savedRegion?.idRegion ?? editingRegionId, wasEditing ? "La région a été modifiée avec succès." : "La région a été enregistrée avec succès.");
      loadRegionPage(targetPage, regionCodeSearch);
    } catch (requestError) {
      const response = requestError.response?.data;
      setRegionErrors(response?.messages || { form: response?.message || "L’enregistrement de la région a échoué." });
    } finally { setSavingRegion(false); }
  };
  const editRegion = (region) => { setRegionForm({ codeRegion: region.codeRegion, nomRegion: region.nomRegion }); setEditingRegionId(region.idRegion); setRegionFormVisible(true); setRegionErrors({}); };
  const deleteRegion = async (region) => {
    const confirmation = await Swal.fire({
      title: "Supprimer cette région ?",
      text: `La région « ${region.nomRegion} » sera définitivement supprimée.`,
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Supprimer",
      cancelButtonText: "Annuler",
      confirmButtonColor: "#b8444d",
      cancelButtonColor: "#6e8195",
      focusCancel: true,
      returnFocus: false,
      reverseButtons: true,
    });
    if (!confirmation.isConfirmed) return;
    try {
      await api.delete(`/api/regions/${region.idRegion}`);
      if (editingRegionId === region.idRegion) resetRegionForm();
      const targetPage = regionRows.length === 1 && regionPage > 0 ? regionPage - 1 : regionPage;
      showRegionSuccess(null, "La région a été supprimée avec succès.");
      loadRegions();
      loadRegionPage(targetPage, regionCodeSearch);
    }
    catch (requestError) { setRegionErrors({ form: requestError.response?.data?.message || "La suppression de la région a échoué." }); }
  };
  const fieldError = (name) => errors[name] && <span className="field-error">{errors[name]}</span>;

  return <section className="unite-page unit-management-page">
    <div className="page-heading"><div><p className="dashboard-eyebrow">Référentiel</p><h1>{isRegionsVisible ? "Gestion des régions" : "Gestion des unités"}</h1><p>{isRegionsVisible ? "Créez et organisez les régions du réseau LPEE." : "Créez et organisez les unités du réseau LPEE."}</p></div><span className="page-heading-icon">{isRegionsVisible ? <MapPinned /> : <Building2 />}</span></div>
    <div className="unit-toolbar"><button className={`view-switch-button ${!isRegionsVisible ? "active" : ""}`} onClick={showUnits}><Building2 size={18} />Unité</button><button className={`view-switch-button ${isRegionsVisible ? "active" : ""}`} onClick={showRegions}><MapPinned size={18} />Région</button>{!isRegionsVisible && <button className="view-units-button" onClick={toggleList}><Eye size={18} />{isListVisible ? "Masquer les unités" : "Voir les unités"}</button>}</div>
    {!isRegionsVisible ? <>
    {pageSuccess && <p className="unit-page-success" role="status">{pageSuccess}</p>}
    <article className="unite-form-card" ref={formRef}>
      <div className="form-card-heading"><div><h2>{editingId ? "Modifier l’unité" : "Nouvelle unité"}</h2><p>Les champs marqués d’un astérisque sont obligatoires.</p></div><div className="form-heading-actions">{editingId && <button className="cancel-edit-button" onClick={resetForm}><X size={17} />Annuler la modification</button>}<button className="form-collapse-button" type="button" onClick={() => setFormVisible((visible) => !visible)} aria-expanded={isFormVisible} aria-label={isFormVisible ? "Réduire le formulaire" : "Afficher le formulaire"}>{isFormVisible ? <ChevronUp size={18} /> : <ChevronDown size={18} />}</button></div></div>
      {isFormVisible && <form className="unite-form" onSubmit={handleSubmit} noValidate>
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
      </form>}
    </article>
    {isListVisible && <article className="units-list-card" ref={listRef}>
      <div className="list-card-heading"><div><h2>Liste des unités</h2><p>{isLoadingUnits ? "Chargement…" : `${unitTotalElements} unité(s) disponible(s)`}</p></div><button className="form-collapse-button" type="button" onClick={() => setListExpanded((expanded) => !expanded)} aria-expanded={isListExpanded} aria-label={isListExpanded ? "Réduire la liste des unités" : "Afficher la liste des unités"}>{isListExpanded ? <ChevronUp size={18} /> : <ChevronDown size={18} />}</button></div>
      {isListExpanded && <>
        <div className="unit-list-search"><label htmlFor="unit-code-search">Rechercher par Code Unité</label><div><Search size={18} aria-hidden="true" /><input id="unit-code-search" type="search" value={unitCodeSearch} onChange={handleUnitSearch} placeholder="Saisissez un code unité" /></div></div>
        <div className="units-table-wrap"><table><thead><tr><th>Code</th><th>Unité</th><th>Type</th><th>Ville</th><th>Téléphone</th><th>Actions</th></tr></thead><tbody>{isLoadingUnits ? <tr><td colSpan="6" className="table-state">Chargement des unités…</td></tr> : units.length ? units.map((unit) => <tr key={unit.idUnite} className={unit.idUnite === highlightedUnitId ? "unit-row-highlighted" : undefined}><td>{unit.codeUnite}</td><td><strong>{unit.nomUnite}</strong></td><td>{unit.typeUnite || "—"}</td><td>{unit.ville || "—"}</td><td>{unit.telephone || "—"}</td><td><div className="table-actions"><button className="edit-action" onClick={() => editUnit(unit)}><Pencil size={16} />Modifier</button><button className="delete-action" onClick={() => deleteUnit(unit)}><Trash2 size={16} />Supprimer</button></div></td></tr>) : <tr><td colSpan="6" className="table-state">Aucune unité disponible.</td></tr>}</tbody></table></div>
        {unitTotalPages > 1 && <nav className="unit-pagination" aria-label="Pagination des unités"><button type="button" onClick={() => changeUnitPage(unitPage - 1)} disabled={unitPage === 0 || isLoadingUnits} aria-label="Page précédente"><ChevronLeft size={17} />Précédent</button><div>{Array.from({ length: unitTotalPages }, (_, page) => <button type="button" key={page} className={page === unitPage ? "active" : undefined} onClick={() => changeUnitPage(page)} disabled={isLoadingUnits} aria-label={`Page ${page + 1}`} aria-current={page === unitPage ? "page" : undefined}>{page + 1}</button>)}</div><button type="button" onClick={() => changeUnitPage(unitPage + 1)} disabled={unitPage >= unitTotalPages - 1 || isLoadingUnits}>Suivant<ChevronRight size={17} /></button></nav>}
      </>}
    </article>}
    </> : <>
      {regionPageSuccess && <p className="unit-page-success" role="status">{regionPageSuccess}</p>}
      <article className="regions-card region-management-view">
        <div className="list-card-heading"><div><h2>Liste des régions</h2><p>{isLoadingRegions ? "Chargement…" : `${regionTotalElements} région(s) disponible(s)`}</p></div><div className="form-heading-actions"><button className="add-region-button" onClick={() => { resetRegionForm(); setRegionFormVisible(true); }}><Plus size={17} />Ajouter</button><button className="form-collapse-button" type="button" onClick={() => setRegionListExpanded((expanded) => !expanded)} aria-expanded={isRegionListExpanded} aria-label={isRegionListExpanded ? "Réduire la liste des régions" : "Afficher la liste des régions"}>{isRegionListExpanded ? <ChevronUp size={18} /> : <ChevronDown size={18} />}</button></div></div>
        {isRegionFormVisible && <form className="region-form" onSubmit={saveRegion} noValidate><div className="form-field"><label htmlFor="codeRegion">Code région <b>*</b></label><input id="codeRegion" name="codeRegion" value={regionForm.codeRegion} onChange={handleRegionChange} aria-invalid={Boolean(regionErrors.codeRegion)} />{regionErrors.codeRegion && <span className="field-error">{regionErrors.codeRegion}</span>}</div><div className="form-field"><label htmlFor="nomRegion">Nom de la région <b>*</b></label><input id="nomRegion" name="nomRegion" value={regionForm.nomRegion} onChange={handleRegionChange} aria-invalid={Boolean(regionErrors.nomRegion)} />{regionErrors.nomRegion && <span className="field-error">{regionErrors.nomRegion}</span>}</div><div className="region-form-actions"><button type="button" className="reset-button" onClick={resetRegionForm}>Annuler</button><button type="submit" className="save-button" disabled={isSavingRegion}>{isSavingRegion ? "Enregistrement…" : editingRegionId ? "Modifier" : "Ajouter"}</button></div></form>}
        {regionErrors.form && <p className="region-error" role="alert">{regionErrors.form}</p>}
        {isRegionListExpanded && <>
          <div className="unit-list-search"><label htmlFor="region-code-search">Rechercher par Code Région</label><div><Search size={18} aria-hidden="true" /><input id="region-code-search" type="search" value={regionCodeSearch} onChange={handleRegionSearch} placeholder="Saisissez un code région" /></div></div>
          <div className="units-table-wrap"><table><thead><tr><th>Code</th><th>Région</th><th>Actions</th></tr></thead><tbody>{isLoadingRegions ? <tr><td colSpan="3" className="table-state">Chargement des régions…</td></tr> : regionRows.length ? regionRows.map((region) => <tr key={region.idRegion} className={region.idRegion === highlightedRegionId ? "unit-row-highlighted" : undefined}><td>{region.codeRegion}</td><td><strong>{region.nomRegion}</strong></td><td><div className="table-actions"><button className="edit-action" onClick={() => editRegion(region)}><Pencil size={16} />Modifier</button><button className="delete-action" onClick={() => deleteRegion(region)}><Trash2 size={16} />Supprimer</button></div></td></tr>) : <tr><td colSpan="3" className="table-state">Aucune région disponible.</td></tr>}</tbody></table></div>
          {regionTotalPages > 1 && <nav className="unit-pagination" aria-label="Pagination des régions"><button type="button" onClick={() => changeRegionPage(regionPage - 1)} disabled={regionPage === 0 || isLoadingRegions} aria-label="Page précédente"><ChevronLeft size={17} />Précédent</button><div>{Array.from({ length: regionTotalPages }, (_, page) => <button type="button" key={page} className={page === regionPage ? "active" : undefined} onClick={() => changeRegionPage(page)} disabled={isLoadingRegions} aria-label={`Page ${page + 1}`} aria-current={page === regionPage ? "page" : undefined}>{page + 1}</button>)}</div><button type="button" onClick={() => changeRegionPage(regionPage + 1)} disabled={regionPage >= regionTotalPages - 1 || isLoadingRegions}>Suivant<ChevronRight size={17} /></button></nav>}
        </>}
      </article>
    </>}
  </section>;
}

export default Unite;

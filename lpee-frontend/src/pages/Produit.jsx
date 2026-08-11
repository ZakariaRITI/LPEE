import { useCallback, useEffect, useRef, useState } from "react";
import { Boxes, Eye, FolderTree, Pencil, Plus, RotateCcw, Save, Trash2, X } from "lucide-react";
import api from "../services/api";
import "./Unite.css";

const initialForm = { idFamille: "", codeProduit: "", nomProduit: "" };
const initialFamilyForm = { codeFamille: "", nomFamille: "" };

function Produit() {
  const formRef = useRef(null);
  const [form, setForm] = useState(initialForm);
  const [families, setFamilies] = useState([]);
  const [products, setProducts] = useState([]);
  const [errors, setErrors] = useState({});
  const [message, setMessage] = useState("");
  const [isSaving, setSaving] = useState(false);
  const [isListVisible, setListVisible] = useState(false);
  const [isLoadingProducts, setLoadingProducts] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [isFamiliesVisible, setFamiliesVisible] = useState(false);
  const [isFamilyFormVisible, setFamilyFormVisible] = useState(false);
  const [familyForm, setFamilyForm] = useState(initialFamilyForm);
  const [familyErrors, setFamilyErrors] = useState({});
  const [editingFamilyId, setEditingFamilyId] = useState(null);
  const [isSavingFamily, setSavingFamily] = useState(false);

  const loadFamilies = useCallback(async () => {
    try { const { data } = await api.get("/api/familles-produits"); setFamilies(data); }
    catch { setFamilyErrors({ form: "Impossible de charger les familles de produits." }); }
  }, []);
  const loadProducts = useCallback(async () => {
    try { setLoadingProducts(true); const { data } = await api.get("/api/produits"); setProducts(data); }
    catch { setErrors((current) => ({ ...current, form: "Impossible de charger les produits." })); }
    finally { setLoadingProducts(false); }
  }, []);
  useEffect(() => {
    api.get("/api/familles-produits")
      .then(({ data }) => setFamilies(data))
      .catch(() => setFamilyErrors({ form: "Impossible de charger les familles de produits." }));
  }, []);

  const handleChange = ({ target: { name, value } }) => { setForm((current) => ({ ...current, [name]: value })); setErrors((current) => ({ ...current, [name]: undefined, form: undefined })); setMessage(""); };
  const validate = () => {
    const next = {};
    if (!form.idFamille) next.idFamille = "La famille de produit est obligatoire.";
    if (!form.codeProduit.trim()) next.codeProduit = "Le code produit est obligatoire.";
    if (!form.nomProduit.trim()) next.nomProduit = "Le nom du produit est obligatoire.";
    setErrors(next); return !Object.keys(next).length;
  };
  const handleSubmit = async (event) => {
    event.preventDefault(); setMessage(""); if (!validate()) return;
    try {
      setSaving(true); const payload = { ...form, idFamille: Number(form.idFamille) };
      if (editingId) await api.put(`/api/produits/${editingId}`, payload); else await api.post("/api/produits", payload);
      const wasEditing = Boolean(editingId); setForm(initialForm); setEditingId(null); setMessage(wasEditing ? "Le produit a été modifié avec succès." : "Le produit a été enregistré avec succès."); if (isListVisible) loadProducts();
    } catch (requestError) { const response = requestError.response?.data; setErrors(response?.messages || { form: response?.message || "L’enregistrement a échoué. Veuillez réessayer." }); }
    finally { setSaving(false); }
  };
  const resetForm = () => { setForm(initialForm); setEditingId(null); setErrors({}); setMessage(""); };
  const toggleList = () => { const next = !isListVisible; setListVisible(next); if (next) loadProducts(); };
  const editProduct = (product) => { setForm({ idFamille: String(product.idFamille), codeProduit: product.codeProduit, nomProduit: product.nomProduit }); setEditingId(product.idProduit); setErrors({}); setMessage(""); formRef.current?.scrollIntoView({ behavior: "smooth", block: "start" }); };
  const deleteProduct = async (product) => { if (!window.confirm(`Supprimer le produit « ${product.nomProduit} » ? Cette action est irréversible.`)) return; try { await api.delete(`/api/produits/${product.idProduit}`); if (editingId === product.idProduit) resetForm(); setMessage("Le produit a été supprimé."); loadProducts(); } catch (error) { setErrors({ form: error.response?.data?.message || "La suppression a échoué." }); } };

  const toggleFamilies = () => { const next = !isFamiliesVisible; setFamiliesVisible(next); if (next) loadFamilies(); };
  const resetFamilyForm = () => { setFamilyForm(initialFamilyForm); setEditingFamilyId(null); setFamilyErrors({}); setFamilyFormVisible(false); };
  const handleFamilyChange = ({ target: { name, value } }) => { setFamilyForm((current) => ({ ...current, [name]: value })); setFamilyErrors((current) => ({ ...current, [name]: undefined, form: undefined })); };
  const saveFamily = async (event) => {
    event.preventDefault(); const next = {};
    if (!familyForm.codeFamille.trim()) next.codeFamille = "Le code famille est obligatoire.";
    if (!familyForm.nomFamille.trim()) next.nomFamille = "Le nom de la famille est obligatoire.";
    if (Object.keys(next).length) { setFamilyErrors(next); return; }
    try { setSavingFamily(true); if (editingFamilyId) await api.put(`/api/familles-produits/${editingFamilyId}`, familyForm); else await api.post("/api/familles-produits", familyForm); resetFamilyForm(); loadFamilies(); }
    catch (error) { const response = error.response?.data; setFamilyErrors(response?.messages || { form: response?.message || "L’enregistrement de la famille a échoué." }); }
    finally { setSavingFamily(false); }
  };
  const editFamily = (family) => { setFamilyForm({ codeFamille: family.codeFamille, nomFamille: family.nomFamille }); setEditingFamilyId(family.idFamille); setFamilyFormVisible(true); setFamilyErrors({}); };
  const deleteFamily = async (family) => { if (!window.confirm(`Supprimer la famille « ${family.nomFamille} » ? Cette action est irréversible.`)) return; try { await api.delete(`/api/familles-produits/${family.idFamille}`); if (editingFamilyId === family.idFamille) resetFamilyForm(); loadFamilies(); } catch (error) { setFamilyErrors({ form: error.response?.data?.message || "La suppression de la famille a échoué." }); } };
  const fieldError = (name) => errors[name] && <span className="field-error">{errors[name]}</span>;
  const familyName = (id) => families.find((family) => family.idFamille === id)?.nomFamille || "—";

  return <section className="unite-page">
    <div className="page-heading"><div><p className="dashboard-eyebrow">Référentiel</p><h1>Gestion des produits</h1><p>Créez et organisez les produits du référentiel LPEE.</p></div><span className="page-heading-icon"><Boxes /></span></div>
    <div className="unit-toolbar"><button className="region-button" onClick={toggleFamilies}><FolderTree size={18} />Famille de produit</button><button className="view-units-button" onClick={toggleList}><Eye size={18} />{isListVisible ? "Masquer les produits" : "Voir les produits"}</button></div>
    <article className="unite-form-card" ref={formRef}><div className="form-card-heading"><div><h2>{editingId ? "Modifier le produit" : "Nouveau produit"}</h2><p>Les champs marqués d’un astérisque sont obligatoires.</p></div>{editingId && <button className="cancel-edit-button" onClick={resetForm}><X size={17} />Annuler la modification</button>}</div>
      <form className="unite-form" onSubmit={handleSubmit} noValidate><div className="form-grid">
        <div className="form-field"><label htmlFor="idFamille">Famille de produit <b>*</b></label><select id="idFamille" name="idFamille" value={form.idFamille} onChange={handleChange} aria-invalid={Boolean(errors.idFamille)}><option value="">Sélectionnez une famille</option>{families.map((family) => <option key={family.idFamille} value={family.idFamille}>{family.nomFamille} ({family.codeFamille})</option>)}</select>{fieldError("idFamille")}</div>
        <div className="form-field"><label htmlFor="codeProduit">Code produit <b>*</b></label><input id="codeProduit" name="codeProduit" value={form.codeProduit} onChange={handleChange} placeholder="Ex. PRD-001" aria-invalid={Boolean(errors.codeProduit)} />{fieldError("codeProduit")}</div>
        <div className="form-field form-field-wide"><label htmlFor="nomProduit">Nom du produit <b>*</b></label><input id="nomProduit" name="nomProduit" value={form.nomProduit} onChange={handleChange} placeholder="Nom du produit" aria-invalid={Boolean(errors.nomProduit)} />{fieldError("nomProduit")}</div>
      </div>{errors.form && <p className="form-global-error" role="alert">{errors.form}</p>}{message && <p className="form-success" role="status">{message}</p>}<div className="form-actions"><button type="button" className="reset-button" onClick={resetForm}><RotateCcw size={18} />Réinitialiser</button><button className="save-button" type="submit" disabled={isSaving}><Save size={18} />{isSaving ? "Enregistrement…" : editingId ? "Modifier" : "Enregistrer"}</button></div></form>
    </article>
    {isListVisible && <article className="units-list-card"><div className="list-card-heading"><div><h2>Liste des produits</h2><p>{isLoadingProducts ? "Chargement…" : `${products.length} produit(s) disponible(s)`}</p></div></div><div className="units-table-wrap"><table><thead><tr><th>Code</th><th>Produit</th><th>Famille de produit</th><th>Actions</th></tr></thead><tbody>{isLoadingProducts ? <tr><td colSpan="4" className="table-state">Chargement des produits…</td></tr> : products.length ? products.map((product) => <tr key={product.idProduit}><td>{product.codeProduit}</td><td><strong>{product.nomProduit}</strong></td><td>{familyName(product.idFamille)}</td><td><div className="table-actions"><button className="edit-action" onClick={() => editProduct(product)}><Pencil size={16} />Modifier</button><button className="delete-action" onClick={() => deleteProduct(product)}><Trash2 size={16} />Supprimer</button></div></td></tr>) : <tr><td colSpan="4" className="table-state">Aucun produit disponible.</td></tr>}</tbody></table></div></article>}
    {isFamiliesVisible && <article className="regions-card"><div className="list-card-heading"><div><h2>Gestion des familles de produits</h2><p>{families.length} famille(s) disponible(s)</p></div><button className="add-region-button" onClick={() => { resetFamilyForm(); setFamilyFormVisible(true); }}><Plus size={17} />Ajouter</button></div>
      {isFamilyFormVisible && <form className="region-form" onSubmit={saveFamily} noValidate><div className="form-field"><label htmlFor="codeFamille">Code famille <b>*</b></label><input id="codeFamille" name="codeFamille" value={familyForm.codeFamille} onChange={handleFamilyChange} aria-invalid={Boolean(familyErrors.codeFamille)} />{familyErrors.codeFamille && <span className="field-error">{familyErrors.codeFamille}</span>}</div><div className="form-field"><label htmlFor="nomFamille">Nom de la famille <b>*</b></label><input id="nomFamille" name="nomFamille" value={familyForm.nomFamille} onChange={handleFamilyChange} aria-invalid={Boolean(familyErrors.nomFamille)} />{familyErrors.nomFamille && <span className="field-error">{familyErrors.nomFamille}</span>}</div><div className="region-form-actions"><button type="button" className="reset-button" onClick={resetFamilyForm}>Annuler</button><button type="submit" className="save-button" disabled={isSavingFamily}>{isSavingFamily ? "Enregistrement…" : editingFamilyId ? "Modifier" : "Ajouter"}</button></div></form>}
      {familyErrors.form && <p className="region-error" role="alert">{familyErrors.form}</p>}<div className="units-table-wrap"><table><thead><tr><th>Code</th><th>Famille de produit</th><th>Actions</th></tr></thead><tbody>{families.length ? families.map((family) => <tr key={family.idFamille}><td>{family.codeFamille}</td><td><strong>{family.nomFamille}</strong></td><td><div className="table-actions"><button className="edit-action" onClick={() => editFamily(family)}><Pencil size={16} />Modifier</button><button className="delete-action" onClick={() => deleteFamily(family)}><Trash2 size={16} />Supprimer</button></div></td></tr>) : <tr><td colSpan="3" className="table-state">Aucune famille disponible.</td></tr>}</tbody></table></div>
    </article>}
  </section>;
}

export default Produit;

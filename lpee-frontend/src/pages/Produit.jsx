import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { Boxes, ChevronDown, ChevronLeft, ChevronRight, ChevronUp, Eye, FolderTree, Pencil, Plus, RotateCcw, Save, Search, Trash2, X } from "lucide-react";
import Swal from "sweetalert2";
import api from "../services/api";
import "./Unite.css";

const initialForm = { idFamille: "", codeProduit: "", nomProduit: "" };
const initialFamilyForm = { codeFamille: "", nomFamille: "" };
const itemsPerPage = 5;

function Pagination({ page, totalPages, loading = false, label, onChange }) {
  if (totalPages <= 1) return null;
  return <nav className="unit-pagination" aria-label={label}><button type="button" onClick={() => onChange(page - 1)} disabled={page === 0 || loading} aria-label="Page précédente"><ChevronLeft size={17} />Précédent</button><div>{Array.from({ length: totalPages }, (_, index) => <button type="button" key={index} className={index === page ? "active" : undefined} onClick={() => onChange(index)} disabled={loading} aria-label={`Page ${index + 1}`} aria-current={index === page ? "page" : undefined}>{index + 1}</button>)}</div><button type="button" onClick={() => onChange(page + 1)} disabled={page >= totalPages - 1 || loading}>Suivant<ChevronRight size={17} /></button></nav>;
}

function Produit() {
  const formRef = useRef(null);
  const listRef = useRef(null);
  const productTableRef = useRef(null);
  const familyTableRef = useRef(null);
  const shouldScrollToListRef = useRef(false);
  const shouldScrollToProductTableRef = useRef(false);
  const shouldScrollToFamilyTableRef = useRef(false);
  const productHighlightTimeoutRef = useRef(null);
  const familyHighlightTimeoutRef = useRef(null);
  const [form, setForm] = useState(initialForm);
  const [families, setFamilies] = useState([]);
  const [products, setProducts] = useState([]);
  const [errors, setErrors] = useState({});
  const [message, setMessage] = useState("");
  const [pageSuccess, setPageSuccess] = useState("");
  const [highlightedProductId, setHighlightedProductId] = useState(null);
  const [isSaving, setSaving] = useState(false);
  const [isFormVisible, setFormVisible] = useState(true);
  const [isListVisible, setListVisible] = useState(false);
  const [isListExpanded, setListExpanded] = useState(true);
  const [isLoadingProducts, setLoadingProducts] = useState(false);
  const [productPage, setProductPage] = useState(0);
  const [productSearch, setProductSearch] = useState("");
  const [editingId, setEditingId] = useState(null);
  const [isFamiliesVisible, setFamiliesVisible] = useState(false);
  const [isFamilyListExpanded, setFamilyListExpanded] = useState(true);
  const [isFamilyFormVisible, setFamilyFormVisible] = useState(false);
  const [familyForm, setFamilyForm] = useState(initialFamilyForm);
  const [familyErrors, setFamilyErrors] = useState({});
  const [familyPageSuccess, setFamilyPageSuccess] = useState("");
  const [highlightedFamilyId, setHighlightedFamilyId] = useState(null);
  const [familyPage, setFamilyPage] = useState(0);
  const [familySearch, setFamilySearch] = useState("");
  const [editingFamilyId, setEditingFamilyId] = useState(null);
  const [isSavingFamily, setSavingFamily] = useState(false);

  const loadFamilies = useCallback(async () => { try { const { data } = await api.get("/api/familles-produits"); setFamilies(data); } catch { setFamilyErrors({ form: "Impossible de charger les familles de produits." }); } }, []);
  const loadProducts = useCallback(async () => { try { setLoadingProducts(true); const { data } = await api.get("/api/produits"); setProducts(data); } catch { setErrors((current) => ({ ...current, form: "Impossible de charger les produits." })); } finally { setLoadingProducts(false); } }, []);
  useEffect(() => {
    api.get("/api/familles-produits")
      .then(({ data }) => setFamilies(data))
      .catch(() => setFamilyErrors({ form: "Impossible de charger les familles de produits." }));
  }, []);
  useEffect(() => () => { clearTimeout(productHighlightTimeoutRef.current); clearTimeout(familyHighlightTimeoutRef.current); }, []);

  const scrollTo = (ref) => { const top = ref.current.getBoundingClientRect().top + window.scrollY; window.scrollTo({ top: Math.max(0, top - 105), behavior: "smooth" }); };
  useEffect(() => { if (!isLoadingProducts && shouldScrollToListRef.current && listRef.current) { shouldScrollToListRef.current = false; scrollTo(listRef); } }, [isLoadingProducts]);
  useEffect(() => { if (!isLoadingProducts && shouldScrollToProductTableRef.current && productTableRef.current) { shouldScrollToProductTableRef.current = false; scrollTo(productTableRef); } }, [isLoadingProducts, productPage, productSearch]);
  useEffect(() => { if (shouldScrollToFamilyTableRef.current && familyTableRef.current) { shouldScrollToFamilyTableRef.current = false; scrollTo(familyTableRef); } }, [familyPage, familySearch]);

  const filteredProducts = useMemo(() => { const search = productSearch.trim().toLocaleLowerCase("fr"); return search ? products.filter((item) => item.codeProduit?.toLocaleLowerCase("fr").includes(search)) : products; }, [products, productSearch]);
  const productTotalPages = Math.ceil(filteredProducts.length / itemsPerPage);
  const productRows = filteredProducts.slice(productPage * itemsPerPage, (productPage + 1) * itemsPerPage);
  const filteredFamilies = useMemo(() => { const search = familySearch.trim().toLocaleLowerCase("fr"); return search ? families.filter((item) => item.codeFamille?.toLocaleLowerCase("fr").includes(search)) : families; }, [families, familySearch]);
  const familyTotalPages = Math.ceil(filteredFamilies.length / itemsPerPage);
  const familyRows = filteredFamilies.slice(familyPage * itemsPerPage, (familyPage + 1) * itemsPerPage);

  const showProductSuccess = (productId, successMessage) => {
    setMessage(""); setPageSuccess(successMessage); setHighlightedProductId(productId);
    clearTimeout(productHighlightTimeoutRef.current);
    productHighlightTimeoutRef.current = setTimeout(() => { setPageSuccess(""); setHighlightedProductId(null); }, 10000);
    window.scrollTo({ top: 0, left: 0, behavior: "smooth" });
  };
  const showFamilySuccess = (familyId, successMessage) => {
    setFamilyPageSuccess(successMessage); setHighlightedFamilyId(familyId);
    clearTimeout(familyHighlightTimeoutRef.current);
    familyHighlightTimeoutRef.current = setTimeout(() => { setFamilyPageSuccess(""); setHighlightedFamilyId(null); }, 10000);
    requestAnimationFrame(() => { const container = document.scrollingElement || document.documentElement; container.scrollTo({ top: 0, left: 0, behavior: "smooth" }); });
  };

  const handleChange = ({ target: { name, value } }) => { setForm((current) => ({ ...current, [name]: value })); setErrors((current) => ({ ...current, [name]: undefined, form: undefined })); setMessage(""); };
  const validate = () => { const next = {}; if (!form.idFamille) next.idFamille = "La famille de produit est obligatoire."; if (!form.codeProduit.trim()) next.codeProduit = "Le code produit est obligatoire."; if (!form.nomProduit.trim()) next.nomProduit = "Le nom du produit est obligatoire."; setErrors(next); return !Object.keys(next).length; };
  const handleSubmit = async (event) => {
    event.preventDefault(); setMessage(""); if (!validate()) return;
    if (editingId) {
      const confirmation = await Swal.fire({ title: "Confirmer la modification ?", text: `Les informations du produit « ${form.nomProduit} » seront mises à jour.`, icon: "question", showCancelButton: true, confirmButtonText: "Confirmer", cancelButtonText: "Annuler", confirmButtonColor: "#0877b6", cancelButtonColor: "#6e8195", focusCancel: true, returnFocus: false, reverseButtons: true });
      if (!confirmation.isConfirmed) return;
    }
    try { setSaving(true); const payload = { ...form, idFamille: Number(form.idFamille) }; const wasEditing = Boolean(editingId); const { data: savedProduct } = wasEditing ? await api.put(`/api/produits/${editingId}`, payload) : await api.post("/api/produits", payload); setForm(initialForm); setEditingId(null); showProductSuccess(savedProduct?.idProduit ?? editingId, wasEditing ? "Le produit a été modifié avec succès." : "Le produit a été enregistré avec succès."); if (isListVisible) loadProducts(); } catch (requestError) { const response = requestError.response?.data; setErrors(response?.messages || { form: response?.message || "L’enregistrement a échoué. Veuillez réessayer." }); } finally { setSaving(false); }
  };
  const resetForm = () => { setForm(initialForm); setEditingId(null); setErrors({}); setMessage(""); };
  const toggleList = () => { const next = !isListVisible; setListVisible(next); if (next) { shouldScrollToListRef.current = true; setListExpanded(true); loadProducts(); } };
  const changeProductPage = (page) => { if (page < 0 || page >= productTotalPages || page === productPage) return; shouldScrollToProductTableRef.current = true; setProductPage(page); };
  const handleProductSearch = ({ target: { value } }) => { shouldScrollToProductTableRef.current = true; setProductSearch(value); setProductPage(0); };
  const editProduct = (item) => { setForm({ idFamille: String(item.idFamille), codeProduit: item.codeProduit, nomProduit: item.nomProduit }); setEditingId(item.idProduit); setFormVisible(true); setErrors({}); setMessage(""); formRef.current?.scrollIntoView({ behavior: "smooth", block: "start" }); };
  const deleteProduct = async (item) => { const confirmation = await Swal.fire({ title: "Supprimer ce produit ?", text: `Le produit « ${item.nomProduit} » sera définitivement supprimé.`, icon: "warning", showCancelButton: true, confirmButtonText: "Supprimer", cancelButtonText: "Annuler", confirmButtonColor: "#b8444d", cancelButtonColor: "#6e8195", focusCancel: true, reverseButtons: true }); if (!confirmation.isConfirmed) return; try { await api.delete(`/api/produits/${item.idProduit}`); if (editingId === item.idProduit) resetForm(); showProductSuccess(null, "Le produit a été supprimé avec succès."); if (productRows.length === 1 && productPage > 0) setProductPage((page) => page - 1); loadProducts(); } catch (error) { setErrors({ form: error.response?.data?.message || "La suppression a échoué." }); } };

  const resetFamilyForm = () => { setFamilyForm(initialFamilyForm); setEditingFamilyId(null); setFamilyErrors({}); setFamilyFormVisible(false); };
  const handleFamilyChange = ({ target: { name, value } }) => { setFamilyForm((current) => ({ ...current, [name]: value })); setFamilyErrors((current) => ({ ...current, [name]: undefined, form: undefined })); };
  const saveFamily = async (event) => {
    event.preventDefault(); const next = {}; if (!familyForm.codeFamille.trim()) next.codeFamille = "Le code famille est obligatoire."; if (!familyForm.nomFamille.trim()) next.nomFamille = "Le nom de la famille est obligatoire."; if (Object.keys(next).length) { setFamilyErrors(next); return; }
    if (editingFamilyId) {
      const confirmation = await Swal.fire({ title: "Confirmer la modification ?", text: `Les informations de la famille « ${familyForm.nomFamille} » seront mises à jour.`, icon: "question", showCancelButton: true, confirmButtonText: "Confirmer", cancelButtonText: "Annuler", confirmButtonColor: "#0877b6", cancelButtonColor: "#6e8195", focusCancel: true, returnFocus: false, reverseButtons: true });
      if (!confirmation.isConfirmed) return;
    }
    try { setSavingFamily(true); const wasEditing = Boolean(editingFamilyId); const { data: savedFamily } = wasEditing ? await api.put(`/api/familles-produits/${editingFamilyId}`, familyForm) : await api.post("/api/familles-produits", familyForm); resetFamilyForm(); showFamilySuccess(savedFamily?.idFamille ?? editingFamilyId, wasEditing ? "La famille de produit a été modifiée avec succès." : "La famille de produit a été enregistrée avec succès."); loadFamilies(); } catch (error) { const response = error.response?.data; setFamilyErrors(response?.messages || { form: response?.message || "L’enregistrement de la famille a échoué." }); } finally { setSavingFamily(false); }
  };
  const changeFamilyPage = (page) => { if (page < 0 || page >= familyTotalPages || page === familyPage) return; shouldScrollToFamilyTableRef.current = true; setFamilyPage(page); };
  const handleFamilySearch = ({ target: { value } }) => { shouldScrollToFamilyTableRef.current = true; setFamilySearch(value); setFamilyPage(0); };
  const editFamily = (item) => { setFamilyForm({ codeFamille: item.codeFamille, nomFamille: item.nomFamille }); setEditingFamilyId(item.idFamille); setFamilyFormVisible(true); setFamilyErrors({}); };
  const deleteFamily = async (item) => { const confirmation = await Swal.fire({ title: "Supprimer cette famille de produit ?", text: `La famille « ${item.nomFamille} » sera définitivement supprimée.`, icon: "warning", showCancelButton: true, confirmButtonText: "Supprimer", cancelButtonText: "Annuler", confirmButtonColor: "#b8444d", cancelButtonColor: "#6e8195", focusCancel: true, returnFocus: false, reverseButtons: true }); if (!confirmation.isConfirmed) return; try { await api.delete(`/api/familles-produits/${item.idFamille}`); if (editingFamilyId === item.idFamille) resetFamilyForm(); showFamilySuccess(null, "La famille de produit a été supprimée avec succès."); if (familyRows.length === 1 && familyPage > 0) setFamilyPage((page) => page - 1); loadFamilies(); } catch (error) { setFamilyErrors({ form: error.response?.data?.message || "La suppression de la famille a échoué." }); } };
  const fieldError = (name) => errors[name] && <span className="field-error">{errors[name]}</span>;
  const familyName = (id) => families.find((item) => item.idFamille === id)?.nomFamille || "—";

  return <section className="unite-page unit-management-page">
    <div className="page-heading"><div><p className="dashboard-eyebrow">Référentiel</p><h1>{isFamiliesVisible ? "Gestion des familles de produits" : "Gestion des produits"}</h1><p>{isFamiliesVisible ? "Créez et organisez les familles de produits du référentiel LPEE." : "Créez et organisez les produits du référentiel LPEE."}</p></div><span className="page-heading-icon">{isFamiliesVisible ? <FolderTree /> : <Boxes />}</span></div>
    <div className="unit-toolbar"><button className={`view-switch-button ${!isFamiliesVisible ? "active" : ""}`} onClick={() => setFamiliesVisible(false)}><Boxes size={18} />Produit</button><button className={`view-switch-button ${isFamiliesVisible ? "active" : ""}`} onClick={() => { setFamiliesVisible(true); setFamilyListExpanded(true); loadFamilies(); }}><FolderTree size={18} />Famille Produit</button>{!isFamiliesVisible && <button className="view-units-button" onClick={toggleList}><Eye size={18} />{isListVisible ? "Masquer les produits" : "Voir les produits"}</button>}</div>
    {!isFamiliesVisible ? <>
      {pageSuccess && <p className="unit-page-success" role="status">{pageSuccess}</p>}
      <article className="unite-form-card" ref={formRef}><div className="form-card-heading"><div><h2>{editingId ? "Modifier le produit" : "Nouveau produit"}</h2><p>Les champs marqués d’un astérisque sont obligatoires.</p></div><div className="form-heading-actions">{editingId && <button className="cancel-edit-button" onClick={resetForm}><X size={17} />Annuler la modification</button>}<button className="form-collapse-button" type="button" onClick={() => setFormVisible((visible) => !visible)} aria-expanded={isFormVisible} aria-label={isFormVisible ? "Réduire le formulaire" : "Afficher le formulaire"}>{isFormVisible ? <ChevronUp size={18} /> : <ChevronDown size={18} />}</button></div></div>{isFormVisible && <form className="unite-form" onSubmit={handleSubmit} noValidate><div className="form-grid"><div className="form-field"><label htmlFor="idFamille">Famille de produit <b>*</b></label><select id="idFamille" name="idFamille" value={form.idFamille} onChange={handleChange} aria-invalid={Boolean(errors.idFamille)}><option value="">Sélectionnez une famille</option>{families.map((item) => <option key={item.idFamille} value={item.idFamille}>{item.nomFamille} ({item.codeFamille})</option>)}</select>{fieldError("idFamille")}</div><div className="form-field"><label htmlFor="codeProduit">Code produit <b>*</b></label><input id="codeProduit" name="codeProduit" value={form.codeProduit} onChange={handleChange} placeholder="Ex. PRD-001" aria-invalid={Boolean(errors.codeProduit)} />{fieldError("codeProduit")}</div><div className="form-field form-field-wide"><label htmlFor="nomProduit">Nom du produit <b>*</b></label><input id="nomProduit" name="nomProduit" value={form.nomProduit} onChange={handleChange} placeholder="Nom du produit" aria-invalid={Boolean(errors.nomProduit)} />{fieldError("nomProduit")}</div></div>{errors.form && <p className="form-global-error" role="alert">{errors.form}</p>}{message && <p className="form-success" role="status">{message}</p>}<div className="form-actions"><button type="button" className="reset-button" onClick={resetForm}><RotateCcw size={18} />Réinitialiser</button><button className="save-button" type="submit" disabled={isSaving}><Save size={18} />{isSaving ? "Enregistrement…" : editingId ? "Modifier" : "Enregistrer"}</button></div></form>}</article>
      {isListVisible && <article className="units-list-card" ref={listRef}><div className="list-card-heading"><div><h2>Liste des produits</h2><p>{isLoadingProducts ? "Chargement…" : `${filteredProducts.length} produit(s) disponible(s)`}</p></div><button className="form-collapse-button" type="button" onClick={() => setListExpanded((value) => !value)} aria-expanded={isListExpanded} aria-label={isListExpanded ? "Réduire la liste des produits" : "Afficher la liste des produits"}>{isListExpanded ? <ChevronUp size={18} /> : <ChevronDown size={18} />}</button></div>{isListExpanded && <><div className="unit-list-search"><label htmlFor="product-code-search">Rechercher par Code Produit</label><div><Search size={18} aria-hidden="true" /><input id="product-code-search" type="search" value={productSearch} onChange={handleProductSearch} placeholder="Saisissez un code produit" /></div></div><div className="units-table-wrap" ref={productTableRef}><table><thead><tr><th>Code</th><th>Produit</th><th>Famille de produit</th><th>Actions</th></tr></thead><tbody>{isLoadingProducts ? <tr><td colSpan="4" className="table-state">Chargement des produits…</td></tr> : productRows.length ? productRows.map((item) => <tr key={item.idProduit} className={item.idProduit === highlightedProductId ? "unit-row-highlighted" : undefined}><td>{item.codeProduit}</td><td><strong>{item.nomProduit}</strong></td><td>{familyName(item.idFamille)}</td><td><div className="table-actions"><button className="edit-action" onClick={() => editProduct(item)}><Pencil size={16} />Modifier</button><button className="delete-action" onClick={() => deleteProduct(item)}><Trash2 size={16} />Supprimer</button></div></td></tr>) : <tr><td colSpan="4" className="table-state">Aucun produit disponible.</td></tr>}</tbody></table></div><Pagination page={productPage} totalPages={productTotalPages} loading={isLoadingProducts} label="Pagination des produits" onChange={changeProductPage} /></>}</article>}
    </> : <>{familyPageSuccess && <p className="unit-page-success" role="status">{familyPageSuccess}</p>}<article className="regions-card region-management-view"><div className="list-card-heading"><div><h2>Liste des familles de produits</h2><p>{filteredFamilies.length} famille(s) disponible(s)</p></div><div className="form-heading-actions"><button className="add-region-button" onClick={() => { resetFamilyForm(); setFamilyFormVisible(true); }}><Plus size={17} />Ajouter</button><button className="form-collapse-button" type="button" onClick={() => setFamilyListExpanded((value) => !value)} aria-expanded={isFamilyListExpanded} aria-label={isFamilyListExpanded ? "Réduire la liste des familles" : "Afficher la liste des familles"}>{isFamilyListExpanded ? <ChevronUp size={18} /> : <ChevronDown size={18} />}</button></div></div>{isFamilyFormVisible && <form className="region-form" onSubmit={saveFamily} noValidate><div className="form-field"><label htmlFor="codeFamille">Code famille <b>*</b></label><input id="codeFamille" name="codeFamille" value={familyForm.codeFamille} onChange={handleFamilyChange} aria-invalid={Boolean(familyErrors.codeFamille)} />{familyErrors.codeFamille && <span className="field-error">{familyErrors.codeFamille}</span>}</div><div className="form-field"><label htmlFor="nomFamille">Nom de la famille <b>*</b></label><input id="nomFamille" name="nomFamille" value={familyForm.nomFamille} onChange={handleFamilyChange} aria-invalid={Boolean(familyErrors.nomFamille)} />{familyErrors.nomFamille && <span className="field-error">{familyErrors.nomFamille}</span>}</div><div className="region-form-actions"><button type="button" className="reset-button" onClick={resetFamilyForm}>Annuler</button><button type="submit" className="save-button" disabled={isSavingFamily}>{isSavingFamily ? "Enregistrement…" : editingFamilyId ? "Modifier" : "Ajouter"}</button></div></form>}{familyErrors.form && <p className="region-error" role="alert">{familyErrors.form}</p>}{isFamilyListExpanded && <><div className="unit-list-search"><label htmlFor="family-code-search">Rechercher par Code Famille</label><div><Search size={18} aria-hidden="true" /><input id="family-code-search" type="search" value={familySearch} onChange={handleFamilySearch} placeholder="Saisissez un code famille" /></div></div><div className="units-table-wrap" ref={familyTableRef}><table><thead><tr><th>Code</th><th>Famille de produit</th><th>Actions</th></tr></thead><tbody>{familyRows.length ? familyRows.map((item) => <tr key={item.idFamille} className={item.idFamille === highlightedFamilyId ? "unit-row-highlighted" : undefined}><td>{item.codeFamille}</td><td><strong>{item.nomFamille}</strong></td><td><div className="table-actions"><button className="edit-action" onClick={() => editFamily(item)}><Pencil size={16} />Modifier</button><button className="delete-action" onClick={() => deleteFamily(item)}><Trash2 size={16} />Supprimer</button></div></td></tr>) : <tr><td colSpan="3" className="table-state">Aucune famille disponible.</td></tr>}</tbody></table></div><Pagination page={familyPage} totalPages={familyTotalPages} label="Pagination des familles de produits" onChange={changeFamilyPage} /></>}</article></>}
  </section>;
}

export default Produit;

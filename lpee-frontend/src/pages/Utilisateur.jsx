import { useCallback, useEffect, useState } from "react";
import { KeyRound, Pencil, Plus, Search, Trash2, Users } from "lucide-react";
import api from "../services/api";
import "./Unite.css";

const DEFAULT_PASSWORD = "Password.123";
const initial = { idRole: "", idUnite: "", nomUser: "", email: "", motDePasse: "" };

function Utilisateur() {
  const [users, setUsers] = useState([]);
  const [units, setUnits] = useState([]);
  const [roles, setRoles] = useState([]);
  const [form, setForm] = useState(initial);
  const [editing, setEditing] = useState(null);
  const [search, setSearch] = useState("");
  const [errors, setErrors] = useState({});
  const [message, setMessage] = useState("");
  const [showForm, setShowForm] = useState(false);

  const load = useCallback(async () => {
    try {
      const [userResponse, unitResponse, roleResponse] = await Promise.all([
        api.get("/api/utilisateurs"), api.get("/api/unites"), api.get("/api/roles"),
      ]);
      setUsers(userResponse.data);
      setUnits(unitResponse.data);
      setRoles(roleResponse.data);
    } catch {
      setErrors({ form: "Impossible de charger les utilisateurs." });
    }
  }, [setErrors]);

  useEffect(() => {
    Promise.all([api.get("/api/utilisateurs"), api.get("/api/unites"), api.get("/api/roles")])
      .then(([userResponse, unitResponse, roleResponse]) => {
        setUsers(userResponse.data);
        setUnits(unitResponse.data);
        setRoles(roleResponse.data);
      })
      .catch(() => setErrors({ form: "Impossible de charger les utilisateurs." }));
  }, []);

  const submit = async (event) => {
    event.preventDefault();
    const nextErrors = {};
    ["idRole", "idUnite", "nomUser", "email", "motDePasse"].forEach((key) => {
      if (!form[key]) nextErrors[key] = "Champ obligatoire.";
    });
    if (Object.keys(nextErrors).length) { setErrors(nextErrors); return; }

    const payload = { ...form, idRole: Number(form.idRole), idUnite: Number(form.idUnite), statut: "ACTIF" };
    try {
      if (editing) await api.put(`/api/utilisateurs/${editing}`, payload);
      else await api.post("/api/utilisateurs", payload);
      setForm(initial); setEditing(null); setShowForm(false); setErrors({});
      setMessage("Utilisateur enregistré avec succès.");
      load();
    } catch (error) {
      const response = error.response?.data;
      setErrors(response?.messages || { form: response?.message || "L’enregistrement a échoué." });
    }
  };

  const edit = (user) => {
    setForm({ idRole: String(user.idRole), idUnite: String(user.idUnite), nomUser: user.nomUser, email: user.email, motDePasse: DEFAULT_PASSWORD });
    setEditing(user.idUser); setErrors({}); setShowForm(true);
  };

  const updateStatus = async (user, statut, label) => {
    if (!window.confirm(`${label} l’utilisateur « ${user.nomUser} » ?`)) return;
    try {
      await api.put(`/api/utilisateurs/${user.idUser}`, {
        idRole: user.idRole, idUnite: user.idUnite, nomUser: user.nomUser,
        email: user.email, motDePasse: DEFAULT_PASSWORD, statut,
      });
      setMessage(label === "Supprimer" ? "Utilisateur désactivé." : "Mot de passe réinitialisé.");
      load();
    } catch (error) {
      setErrors({ form: error.response?.data?.message || "L’opération a échoué." });
    }
  };

  const getUnit = (user) => units.find((unit) => unit.idUnite === user.idUnite);
  const typeClass = (user) => {
    const unitType = getUnit(user)?.typeUnite?.normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase();
    return unitType === "regionale" ? "regional" : "specialized";
  };
  const visibleUsers = users.filter((user) => user.statut?.toLowerCase() !== "inactif" && (!search || String(user.idUnite) === search));
  const setField = (key, value) => setForm((current) => ({ ...current, [key]: value }));

  return <section className="unite-page">
    <div className="page-heading"><div><p className="dashboard-eyebrow">Administration</p><h1>Gestion des utilisateurs</h1><p>Administrez les accès au référentiel LPEE.</p></div><span className="page-heading-icon"><Users /></span></div>
    <div className="unit-toolbar"><button className="add-region-button" onClick={() => { setForm(initial); setEditing(null); setErrors({}); setShowForm(!showForm); }}><Plus size={17} />Ajouter un utilisateur</button></div>
    {message && <p className="form-success" role="status">{message}</p>}
    {showForm && <article className="unite-form-card"><div className="form-card-heading"><div><h2>{editing ? "Modifier l’utilisateur" : "Nouvel utilisateur"}</h2></div></div><form className="unite-form" onSubmit={submit}><div className="form-grid">
      {[['nomUser', 'Nom'], ['email', 'Email']].map(([key, label]) => <div className="form-field" key={key}><label>{label} <b>*</b></label><input type={key === "email" ? "email" : "text"} value={form[key]} onChange={(event) => setField(key, event.target.value)} />{errors[key] && <span className="field-error">{errors[key]}</span>}</div>)}
      <div className="form-field"><label>Rôle <b>*</b></label><select value={form.idRole} onChange={(event) => setField("idRole", event.target.value)}><option value="">Sélectionnez</option>{roles.map((role) => <option key={role.idRole} value={role.idRole}>{role.nomRole}</option>)}</select>{errors.idRole && <span className="field-error">{errors.idRole}</span>}</div>
      <div className="form-field"><label>Unité <b>*</b></label><select value={form.idUnite} onChange={(event) => setField("idUnite", event.target.value)}><option value="">Sélectionnez</option>{units.map((unit) => <option key={unit.idUnite} value={unit.idUnite}>{unit.nomUnite}</option>)}</select>{errors.idUnite && <span className="field-error">{errors.idUnite}</span>}</div>
      <div className="form-field form-field-wide"><label>Mot de passe <b>*</b></label><input type="password" value={form.motDePasse} onChange={(event) => setField("motDePasse", event.target.value)} />{errors.motDePasse && <span className="field-error">{errors.motDePasse}</span>}</div>
    </div>{errors.form && <p className="form-global-error">{errors.form}</p>}<div className="form-actions"><button type="button" className="reset-button" onClick={() => setShowForm(false)}>Annuler</button><button className="save-button">Enregistrer</button></div></form></article>}
    <article className="units-list-card"><div className="list-card-heading"><div><h2>Liste des utilisateurs</h2><p>{visibleUsers.length} utilisateur(s) actif(s)</p></div><div className="user-filter"><Search size={17} /><select aria-label="Filtrer par unité" value={search} onChange={(event) => setSearch(event.target.value)}><option value="">Toutes les unités</option>{units.map((unit) => <option key={unit.idUnite} value={unit.idUnite}>{unit.nomUnite}</option>)}</select></div></div>
      <div className="units-table-wrap"><table><thead><tr><th>Utilisateur</th><th>Email</th><th>Unité</th><th>Rôle</th><th>Actions</th></tr></thead><tbody>{visibleUsers.map((user) => <tr key={user.idUser}><td><strong>{user.nomUser}</strong></td><td>{user.email}</td><td><span className={`unit-type ${typeClass(user)}`}>{getUnit(user)?.nomUnite || "—"}</span></td><td>{roles.find((role) => role.idRole === user.idRole)?.nomRole || "—"}</td><td><div className="table-actions"><button className="edit-action" onClick={() => edit(user)}><Pencil size={16} />Modifier</button><button className="edit-action" onClick={() => updateStatus(user, "ACTIF", "Réinitialiser le mot de passe de")}><KeyRound size={16} />Réinitialiser</button><button className="delete-action" onClick={() => updateStatus(user, "INACTIF", "Supprimer")}><Trash2 size={16} />Supprimer</button></div></td></tr>)}</tbody></table></div>
    </article>
  </section>;
}

export default Utilisateur;

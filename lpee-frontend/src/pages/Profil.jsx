import { useEffect, useState } from "react";
import { Save, UserRound } from "lucide-react";
import api from "../services/api";
import { getValidSession } from "../services/auth";
import "./Profil.css";

const emptyForm = { nomUser: "", email: "", motDePasse: "" };

function Profil() {
  const [session] = useState(() => getValidSession());
  const [user, setUser] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [errors, setErrors] = useState({});
  const [message, setMessage] = useState("");
  const [isLoading, setLoading] = useState(true);
  const [isSaving, setSaving] = useState(false);

  useEffect(() => {
    const request = session?.idUser
      ? api.get(`/api/utilisateurs/${session.idUser}`)
      : Promise.reject(new Error("Votre session utilisateur est introuvable."));

    request
      .then(({ data }) => { setUser(data); setForm({ nomUser: data.nomUser || "", email: data.email || "", motDePasse: "" }); })
      .catch((error) => setErrors({ form: error.response?.data?.message || error.message || "Impossible de charger votre profil." }))
      .finally(() => setLoading(false));
  }, [session?.idUser]);

  const change = (key, value) => setForm((current) => ({ ...current, [key]: value }));
  const submit = async (event) => {
    event.preventDefault();
    const nextErrors = {};
    if (!form.nomUser.trim()) nextErrors.nomUser = "Le nom est obligatoire.";
    if (!form.email.trim()) nextErrors.email = "L’email est obligatoire.";
    if (!form.motDePasse) nextErrors.motDePasse = "Le mot de passe est obligatoire pour enregistrer les modifications.";
    if (Object.keys(nextErrors).length) { setErrors(nextErrors); return; }

    setSaving(true); setErrors({}); setMessage("");
    try {
      const { data } = await api.put(`/api/utilisateurs/${user.idUser}`, {
        idRole: user.idRole,
        idUnite: user.idUnite,
        nomUser: form.nomUser.trim(),
        email: form.email.trim(),
        motDePasse: form.motDePasse,
        statut: user.statut || "ACTIF",
      });
      setUser(data);
      setForm((current) => ({ ...current, nomUser: data.nomUser, email: data.email, motDePasse: "" }));
      setMessage("Vos informations ont été mises à jour avec succès.");
    } catch (error) {
      const response = error.response?.data;
      setErrors(response?.messages || { form: response?.message || "La mise à jour a échoué." });
    } finally {
      setSaving(false);
    }
  };

  return <section className="unite-page profile-page">
    <div className="page-heading"><div><p className="dashboard-eyebrow">Compte utilisateur</p><h1>Mon profil</h1><p>Consultez et mettez à jour vos informations personnelles.</p></div><span className="page-heading-icon"><UserRound /></span></div>
    <article className="unite-form-card profile-card">
      <div className="form-card-heading"><div><h2>Informations personnelles</h2><p>Les rôles et l’unité sont gérés par l’administration.</p></div></div>
      {isLoading ? <p className="table-state">Chargement de votre profil…</p> : !user ? <p className="form-global-error">{errors.form}</p> : <form className="unite-form" onSubmit={submit}>
        <div className="form-grid">
          <div className="form-field"><label htmlFor="profile-name">Nom <b>*</b></label><input id="profile-name" value={form.nomUser} onChange={(event) => change("nomUser", event.target.value)} />{errors.nomUser && <span className="field-error">{errors.nomUser}</span>}</div>
          <div className="form-field"><label htmlFor="profile-email">Email <b>*</b></label><input id="profile-email" type="email" value={form.email} onChange={(event) => change("email", event.target.value)} />{errors.email && <span className="field-error">{errors.email}</span>}</div>
          <div className="form-field form-field-wide"><label htmlFor="profile-password">Nouveau mot de passe <b>*</b></label><input id="profile-password" type="password" value={form.motDePasse} onChange={(event) => change("motDePasse", event.target.value)} placeholder="8 caractères minimum, avec majuscule, chiffre et caractère spécial" />{errors.motDePasse && <span className="field-error">{errors.motDePasse}</span>}</div>
        </div>
        {errors.form && <p className="form-global-error">{errors.form}</p>}
        {message && <p className="form-success" role="status">{message}</p>}
        <div className="form-actions"><button className="save-button" disabled={isSaving}>{isSaving ? "Enregistrement…" : <><Save size={17} />Enregistrer les modifications</>}</button></div>
      </form>}
    </article>
  </section>;
}

export default Profil;

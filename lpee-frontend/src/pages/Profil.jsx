import { useEffect, useState } from "react";
import { Save, UserRound } from "lucide-react";
import api from "../services/api";
import { getValidSession } from "../services/auth";
import "./Profil.css";

const emptyForm = { nomUser: "", matricule: "", email: "", motDePasseActuel: "", nouveauMotDePasse: "", confirmationMotDePasse: "" };

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
      .then(({ data }) => {
        setUser(data);
        setForm({ ...emptyForm, nomUser: data.nomUser || "", matricule: data.matricule || "", email: data.email || "" });
      })
      .catch((error) => setErrors({ form: error.response?.data?.message || error.message || "Impossible de charger votre profil." }))
      .finally(() => setLoading(false));
  }, [session?.idUser]);

  const change = (key, value) => setForm((current) => ({ ...current, [key]: value }));
  const submit = async (event) => {
    event.preventDefault();
    const nextErrors = {};
    if (!form.nomUser.trim()) nextErrors.nomUser = "Le nom est obligatoire.";
    if (!form.matricule.trim()) nextErrors.matricule = "Le matricule est obligatoire.";
    if (!form.email.trim()) nextErrors.email = "L’email est obligatoire.";
    if (!form.motDePasseActuel) nextErrors.motDePasseActuel = "Le mot de passe actuel est obligatoire pour enregistrer les modifications.";
    const wantsPasswordChange = Boolean(form.nouveauMotDePasse || form.confirmationMotDePasse);
    if (wantsPasswordChange) {
      if (!form.nouveauMotDePasse) nextErrors.nouveauMotDePasse = "Le nouveau mot de passe est obligatoire.";
      if (!form.confirmationMotDePasse) nextErrors.confirmationMotDePasse = "La confirmation du nouveau mot de passe est obligatoire.";
      else if (form.nouveauMotDePasse !== form.confirmationMotDePasse) nextErrors.confirmationMotDePasse = "Les deux nouveaux mots de passe doivent être identiques.";
    }
    if (Object.keys(nextErrors).length) { setErrors(nextErrors); return; }

    setSaving(true); setErrors({}); setMessage("");
    try {
      try {
        await api.post("/api/auth/login", { matricule: form.matricule, motDePasse: form.motDePasseActuel });
      } catch (error) {
        setErrors({ motDePasseActuel: error.response?.status === 401 ? "Le mot de passe actuel est incorrect." : "Impossible de vérifier le mot de passe actuel." });
        return;
      }

      const { data } = await api.put(`/api/utilisateurs/${user.idUser}`, {
        idRole: user.idRole,
        idUnite: user.idUnite,
        nomUser: form.nomUser.trim(),
        matricule: form.matricule.trim(),
        email: form.email.trim(),
        motDePasse: wantsPasswordChange ? form.nouveauMotDePasse : form.motDePasseActuel,
        statut: user.statut || "ACTIF",
      });
      setUser(data);
      setForm((current) => ({ ...current, nomUser: data.nomUser, matricule: data.matricule, email: data.email, motDePasseActuel: "", nouveauMotDePasse: "", confirmationMotDePasse: "" }));
      setMessage("Vos informations ont été mises à jour avec succès.");
    } catch (error) {
      const response = error.response?.data;
      if (response?.messages?.motDePasse) {
        setErrors({ [wantsPasswordChange ? "nouveauMotDePasse" : "motDePasseActuel"]: response.messages.motDePasse });
      } else {
        setErrors(response?.messages || { form: response?.message || "La mise à jour a échoué." });
      }
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
          <div className="form-field"><label htmlFor="profile-matricule">Matricule <b>*</b></label><input id="profile-matricule" value={form.matricule} readOnly />{errors.matricule && <span className="field-error">{errors.matricule}</span>}</div>
          <div className="form-field"><label htmlFor="profile-email">Email <b>*</b></label><input id="profile-email" type="email" value={form.email} onChange={(event) => change("email", event.target.value)} />{errors.email && <span className="field-error">{errors.email}</span>}</div>
          <div className="form-field form-field-wide"><p className="profile-password-info">Si vous ne souhaitez pas changer votre mot de passe, saisissez uniquement votre mot de passe actuel pour enregistrer les modifications du profil.</p></div>
          <div className="form-field form-field-wide"><label htmlFor="profile-current-password">Mot de passe actuel <b>*</b></label><input id="profile-current-password" type="password" autoComplete="current-password" value={form.motDePasseActuel} onChange={(event) => change("motDePasseActuel", event.target.value)} />{errors.motDePasseActuel && <span className="field-error">{errors.motDePasseActuel}</span>}</div>
          <div className="form-field form-field-wide"><label htmlFor="profile-new-password">Nouveau mot de passe</label><input id="profile-new-password" type="password" autoComplete="new-password" value={form.nouveauMotDePasse} onChange={(event) => change("nouveauMotDePasse", event.target.value)} placeholder="8 caractères minimum, avec majuscule, chiffre et caractère spécial" />{errors.nouveauMotDePasse && <span className="field-error">{errors.nouveauMotDePasse}</span>}</div>
          <div className="form-field form-field-wide"><label htmlFor="profile-confirm-password">Confirmer le nouveau mot de passe</label><input id="profile-confirm-password" type="password" autoComplete="new-password" value={form.confirmationMotDePasse} onChange={(event) => change("confirmationMotDePasse", event.target.value)} />{errors.confirmationMotDePasse && <span className="field-error">{errors.confirmationMotDePasse}</span>}</div>
        </div>
        {errors.form && <p className="form-global-error">{errors.form}</p>}
        {message && <p className="form-success" role="status">{message}</p>}
        <div className="form-actions"><button className="save-button" disabled={isSaving}>{isSaving ? "Enregistrement…" : <><Save size={17} />Enregistrer les modifications</>}</button></div>
      </form>}
    </article>
  </section>;
}

export default Profil;

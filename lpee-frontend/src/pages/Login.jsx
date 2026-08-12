import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Eye, EyeOff, Fingerprint, LockKeyhole, ArrowRight } from "lucide-react";
import api from "../services/api";
import { getValidSession, saveSession } from "../services/auth";
import "./Login.css";

function Login({ onLogin }) {
  const navigate = useNavigate();
  const [form, setForm] = useState({ matricule: "", motDePasse: "" });
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [pendingSession, setPendingSession] = useState(null);
  const [passwordForm, setPasswordForm] = useState({ nouveauMotDePasse: "", confirmation: "" });
  const [passwordErrors, setPasswordErrors] = useState({});
  const [isChangingPassword, setIsChangingPassword] = useState(false);

  const completeLogin = (session) => {
    saveSession(session);

    if (!getValidSession()) {
      throw new Error("Invalid authentication response.");
    }

    onLogin();
    navigate("/dashboard", { replace: true });
  };

  const handleChange = ({ target: { name, value } }) => {
    setForm((current) => ({ ...current, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setIsSubmitting(true);

    try {
      const { data } = await api.post("/api/auth/login", form);

      if (!data?.token || data.type !== "Bearer") {
        throw new Error("Invalid authentication response.");
      }

      if (form.motDePasse === "Password.123") {
        setPendingSession(data);
        setPasswordForm({ nouveauMotDePasse: "", confirmation: "" });
        setPasswordErrors({});
        return;
      }

      completeLogin(data);
    } catch (requestError) {
      const messages = requestError.response?.data?.messages;
      setError(
        messages?.matricule ||
          requestError.response?.data?.message ||
          (requestError.response?.status === 401
            ? "Matricule ou mot de passe incorrect."
            : "La connexion au serveur est impossible. Réessayez.")
      );
    } finally {
      setIsSubmitting(false);
    }
  };

  const handlePasswordChange = async (event) => {
    event.preventDefault();
    const nextErrors = {};
    const passwordRule = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z\d]).{8,}$/;

    if (!passwordForm.nouveauMotDePasse) nextErrors.nouveauMotDePasse = "Le nouveau mot de passe est obligatoire.";
    else if (passwordForm.nouveauMotDePasse === "Password.123") nextErrors.nouveauMotDePasse = "Le nouveau mot de passe doit être différent du mot de passe temporaire.";
    else if (!passwordRule.test(passwordForm.nouveauMotDePasse)) nextErrors.nouveauMotDePasse = "Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial.";
    if (!passwordForm.confirmation) nextErrors.confirmation = "La confirmation est obligatoire.";
    else if (passwordForm.nouveauMotDePasse !== passwordForm.confirmation) nextErrors.confirmation = "Les deux mots de passe doivent être identiques.";
    if (Object.keys(nextErrors).length) { setPasswordErrors(nextErrors); return; }

    setIsChangingPassword(true);
    setPasswordErrors({});
    const authorization = { headers: { Authorization: `Bearer ${pendingSession.token}` } };

    try {
      const { data: user } = await api.get(`/api/utilisateurs/${pendingSession.idUser}`, authorization);
      await api.put(`/api/utilisateurs/${pendingSession.idUser}`, {
        idRole: user.idRole,
        idUnite: user.idUnite,
        nomUser: user.nomUser,
        matricule: user.matricule,
        email: user.email,
        motDePasse: passwordForm.nouveauMotDePasse,
        statut: user.statut || "ACTIF",
      }, authorization);

      const { data: newSession } = await api.post("/api/auth/login", {
        matricule: form.matricule,
        motDePasse: passwordForm.nouveauMotDePasse,
      });
      if (!newSession?.token || newSession.type !== "Bearer") throw new Error("Invalid authentication response.");

      setPendingSession(null);
      completeLogin(newSession);
    } catch (requestError) {
      const response = requestError.response?.data;
      setPasswordErrors(response?.messages || { form: response?.message || "La modification du mot de passe a échoué. Veuillez réessayer." });
    } finally {
      setIsChangingPassword(false);
    }
  };

  return (
    <main className="login-page">
      <section className="login-visual" aria-label="Laboratoire Public d'Essais et d'Études">
        <div className="visual-content">
          <img className="lpee-logo" src="/images/Lpee-logo.png" alt="LPEE" />
        </div>
        <img className="lpee-worksite" src="/images/LPEE-image.jpg" alt="Intervention LPEE sur un chantier" />
        <div className="visual-copy">
          <p className="visual-kicker">Référentiel des essais</p>
          <h1>Maîtrisez vos essais de laboratoire.</h1>
          <p>Centralisez, organisez et suivez l’ensemble du référentiel des essais, normes, équipements et documents de votre laboratoire.</p>
        </div>
      </section>

      <section className="login-panel">
        <div className="login-card">
          <div className="mobile-brand"><img src="/images/Lpee-logo.png" alt="LPEE" /></div>
          <p className="eyebrow">Espace sécurisé</p>
          <h2>Bienvenue</h2>
          <p className="intro">Connectez-vous pour accéder à votre tableau de bord.</p>

          <form onSubmit={handleSubmit} noValidate>
            <label htmlFor="matricule">Matricule</label>
            <div className="input-wrap">
              <Fingerprint aria-hidden="true" size={19} />
              <input id="matricule" name="matricule" type="text" autoComplete="username" value={form.matricule} onChange={handleChange} placeholder="MAT-001" required />
            </div>

            <label htmlFor="motDePasse">Mot de passe</label>
            <div className="input-wrap">
              <LockKeyhole aria-hidden="true" size={19} />
              <input id="motDePasse" name="motDePasse" type={showPassword ? "text" : "password"} autoComplete="current-password" value={form.motDePasse} onChange={handleChange} placeholder="Votre mot de passe" required />
              <button className="password-toggle" type="button" onClick={() => setShowPassword((current) => !current)} aria-label={showPassword ? "Masquer le mot de passe" : "Afficher le mot de passe"}>
                {showPassword ? <EyeOff size={19} /> : <Eye size={19} />}
              </button>
            </div>

            {error && <p className="form-error" role="alert">{error}</p>}
            <button className="submit-button" type="submit" disabled={isSubmitting}>
              {isSubmitting ? "Connexion en cours…" : <>Se connecter <ArrowRight size={19} /></>}
            </button>
          </form>
          <p className="support-text">Besoin d’aide ? Contactez votre administrateur.</p>
        </div>
      </section>

      {pendingSession && <div className="password-modal-backdrop">
        <section className="password-modal" role="dialog" aria-modal="true" aria-labelledby="password-modal-title">
          <p className="eyebrow">Sécurité du compte</p>
          <h2 id="password-modal-title">Modifiez votre mot de passe</h2>
          <p className="password-modal-intro">Votre mot de passe temporaire doit être remplacé avant d’accéder à l’application.</p>
          <form onSubmit={handlePasswordChange} noValidate>
            <label htmlFor="nouveauMotDePasse">Nouveau mot de passe</label>
            <div className="input-wrap"><LockKeyhole aria-hidden="true" size={19} /><input id="nouveauMotDePasse" type="password" autoComplete="new-password" value={passwordForm.nouveauMotDePasse} onChange={(event) => setPasswordForm((current) => ({ ...current, nouveauMotDePasse: event.target.value }))} required /></div>
            {passwordErrors.nouveauMotDePasse && <p className="form-error" role="alert">{passwordErrors.nouveauMotDePasse}</p>}
            <label htmlFor="confirmationMotDePasse">Confirmer le nouveau mot de passe</label>
            <div className="input-wrap"><LockKeyhole aria-hidden="true" size={19} /><input id="confirmationMotDePasse" type="password" autoComplete="new-password" value={passwordForm.confirmation} onChange={(event) => setPasswordForm((current) => ({ ...current, confirmation: event.target.value }))} required /></div>
            {passwordErrors.confirmation && <p className="form-error" role="alert">{passwordErrors.confirmation}</p>}
            {passwordErrors.form && <p className="form-error" role="alert">{passwordErrors.form}</p>}
            <button className="submit-button" type="submit" disabled={isChangingPassword}>{isChangingPassword ? "Modification en cours…" : "Modifier et continuer"}</button>
          </form>
        </section>
      </div>}
    </main>
  );
}

export default Login;

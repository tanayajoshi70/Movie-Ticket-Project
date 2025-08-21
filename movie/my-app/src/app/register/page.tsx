"use client";

import { useMemo, useState } from "react";
import { useRouter } from "next/navigation";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

export default function RegisterPage() {
  const router = useRouter();

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [phone, setPhone] = useState(""); // 10 digits only
  const [password, setPassword] = useState("");
  const [showPwd, setShowPwd] = useState(false);

  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // simple strength meter 0-4
  const strength = useMemo(() => {
    if (!password) return 0;
    let s = 0;
    if (password.length >= 6) s++;
    if (/[a-z]/.test(password)) s++;
    if (/[A-Z]/.test(password)) s++;
    if (/\d/.test(password) || /[^A-Za-z]/.test(password)) s++;
    return Math.min(s, 4);
  }, [password]);

  const handleRegister = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError(null);

    // Client-side checks mirroring your DTO
    const phoneDigits = phone.replace(/\D/g, "");
    if (!name.trim()) return setError("Name is required");
    if (!/^\S+@\S+\.\S+$/.test(email)) return setError("Invalid email format");
    if (!/^\d{10}$/.test(phoneDigits)) return setError("Phone number must be 10 digits");
    if (password.length < 6) return setError("Password must be at least 6 characters");

    setSubmitting(true);
    try {
      const res = await fetch(`${API_BASE}/api/auth/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          name: name.trim(),
          email: email.trim(),
          phone: phoneDigits,
          password,
        }),
      });

      if (!res.ok) {
        let msg = "";
        try { msg = (await res.text()) || ""; } catch {}
        throw new Error(msg || `Registration failed (${res.status})`);
      }

      alert("Registration successful. Please log in.");
      router.push("/login");
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "Something went wrong";
      setError(msg);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <main className="screen">
      {/* subtle cinema backdrop */}
      <div aria-hidden className="backdrop">
        <div className="spot spot-a" />
        <div className="spot spot-b" />
        <div className="film film-left" />
        <div className="film film-right" />
      </div>

      <section className="card" role="dialog" aria-labelledby="title">
        <header className="card-head">
          <div className="badge">🎟️</div>
          <h1 id="title">Create your account</h1>
          <p className="sub">Join CineBook to reserve seats in seconds.</p>
        </header>

        {error && (
          <div className="alert" role="alert">
            <span className="alert-dot" />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleRegister} className="form" noValidate>
          <label className="field">
            <span className="label">Full name</span>
            <div className="input-wrap">
              <span className="icon" aria-hidden>🧑</span>
              <input
                type="text"
                placeholder="Aniket Joshi"
                value={name}
                onChange={(e) => setName(e.currentTarget.value)}
                required
                maxLength={80}
              />
            </div>
          </label>

          <label className="field">
            <span className="label">Email</span>
            <div className="input-wrap">
              <span className="icon" aria-hidden>✉️</span>
              <input
                type="email"
                inputMode="email"
                autoComplete="email"
                placeholder="you@example.com"
                value={email}
                onChange={(e) => setEmail(e.currentTarget.value)}
                required
              />
            </div>
          </label>

          <label className="field">
            <span className="label">Phone (10 digits)</span>
            <div className="input-wrap">
              <span className="icon" aria-hidden>📱</span>
              <input
                type="tel"
                inputMode="numeric"
                placeholder="98XXXXXXXX"
                value={phone}
                onChange={(e) => {
                  // keep only digits, max 10
                  const digits = e.currentTarget.value.replace(/\D/g, "").slice(0, 10);
                  setPhone(digits);
                }}
                required
                pattern="\d{10}"
              />
            </div>
          </label>

          <label className="field">
            <span className="label">
              Password <span className="hint">— min 6 characters</span>
            </span>
            <div className="input-wrap">
              <span className="icon" aria-hidden>🔒</span>
              <input
                type={showPwd ? "text" : "password"}
                autoComplete="new-password"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.currentTarget.value)}
                required
                minLength={6}
              />
              <button
                type="button"
                className="ghost"
                aria-label={showPwd ? "Hide password" : "Show password"}
                onClick={() => setShowPwd((s) => !s)}
                title={showPwd ? "Hide" : "Show"}
              >
                {showPwd ? "🙈" : "👁️"}
              </button>
            </div>

            {/* strength meter */}
            <div className="meter" aria-hidden>
              <div className={`bar ${strength >= 1 ? "on" : ""}`} />
              <div className={`bar ${strength >= 2 ? "on" : ""}`} />
              <div className={`bar ${strength >= 3 ? "on" : ""}`} />
              <div className={`bar ${strength >= 4 ? "on" : ""}`} />
            </div>
          </label>

          <button className="cta" type="submit" disabled={submitting}>
            {submitting ? (
              <>
                <span className="spinner" aria-hidden />
                Creating…
              </>
            ) : (
              "Create account"
            )}
          </button>

          <p className="meta">
            Already have an account?{" "}
            <a className="link" href="/login">Sign in</a>
          </p>
        </form>
      </section>

      <style jsx>{`
        :root{
          --bg:#0f1115;
          --card-bg:rgba(255,255,255,0.06);
          --card-stroke:rgba(255,255,255,0.12);
          --fg:#eaeef5;
          --muted:#a8b0bf;
          --accent:#d5b069;     /* ticket gold */
          --focus:#8ea0ff;      /* slate blue */
          --error:#ff6b6b;
          --shadow:0 12px 40px rgba(0,0,0,0.35);
        }
        .screen{
          min-height:100dvh;
          background:
            radial-gradient(1200px 600px at 80% -10%, rgba(142,160,255,0.18), transparent 60%),
            radial-gradient(1200px 600px at 0% 100%, rgba(213,176,105,0.12), transparent 60%),
            var(--bg);
          display:grid; place-items:center; padding:28px; color:var(--fg);
          position:relative; overflow:hidden;
        }
        .backdrop{ position:absolute; inset:0; pointer-events:none; }
        .spot{ position:absolute; filter:blur(50px); opacity:0.22; }
        .spot-a{ width:360px; height:360px; left:10%; top:15%; background:#8ea0ff; }
        .spot-b{ width:420px; height:420px; right:5%; bottom:10%; background:#d5b069; opacity:0.18; }
        .film{
          position:absolute; top:0; bottom:0; width:84px;
          background:
            linear-gradient(to bottom, transparent 0 24px, rgba(255,255,255,0.06) 24px 40px, transparent 40px 64px) repeat-y;
          mask-image:linear-gradient(to bottom, transparent, black 20%, black 80%, transparent);
          opacity:0.35;
        }
        .film-left{ left:0; }
        .film-right{ right:0; transform:scaleX(-1); }

        .card{
          width:100%;
          max-width:520px;
          border-radius:26px;
          border:1px solid var(--card-stroke);
          background:linear-gradient(180deg, rgba(255,255,255,0.10), rgba(255,255,255,0.05)), var(--card-bg);
          box-shadow:var(--shadow);
          backdrop-filter: blur(10px) saturate(120%);
          padding:24px 22px 16px;
          position:relative;
        }
        .card::before{
          content:"";
          position:absolute; left:-11px; top:120px; width:22px; height:22px; border-radius:50%;
          background:var(--bg); box-shadow: 0 200px 0 0 var(--bg);
        }
        .card::after{
          content:"";
          position:absolute; right:-11px; top:200px; width:22px; height:22px; border-radius:50%;
          background:var(--bg); box-shadow: 0 200px 0 0 var(--bg);
        }
        .card-head{ text-align:center; margin-bottom:8px; }
        .badge{
          width:52px; height:52px; border-radius:14px;
          display:grid; place-items:center; margin:0 auto 8px;
          background: radial-gradient(120% 120% at 30% 30%, rgba(213,176,105,0.35), rgba(142,160,255,0.20));
          border:1px solid var(--card-stroke);
        }
        h1{ margin:0; font-size:26px; font-weight:800; letter-spacing:0.3px; }
        .sub{ margin:4px 0 0; color:var(--muted); font-size:14px; }

        .alert{
          display:flex; gap:10px; align-items:center;
          border:1px solid rgba(255,107,107,0.35);
          background:rgba(255,107,107,0.08);
          color: var(--fg);
          padding:10px 12px; border-radius:12px; margin:14px 0 6px;
        }
        .alert-dot{
          width:9px; height:9px; border-radius:50%; background:var(--error); flex:0 0 auto;
          box-shadow:0 0 10px rgba(255,107,107,0.6);
        }

        .form{ margin-top:8px; display:grid; gap:14px; }
        .field{ display:grid; gap:8px; }
        .label{ font-size:12px; color:var(--muted); letter-spacing:0.3px; }
        .hint{ color:var(--muted); font-weight:400; }
        .input-wrap{
          display:flex; align-items:center; gap:8px;
          padding:12px 12px;
          border-radius:14px;
          border:1px solid var(--card-stroke);
          background:rgba(10,12,18,0.35);
          transition:border 0.2s ease, box-shadow 0.2s ease, transform 0.02s ease;
        }
        .input-wrap:focus-within{
          border-color:rgba(142,160,255,0.65);
          box-shadow:0 0 0 4px rgba(142,160,255,0.12);
        }
        .icon{ opacity:0.9; }
        input{
          flex:1; background:transparent; color:var(--fg); border:0; outline:0;
          font-size:15px; letter-spacing:0.2px;
        }
        .ghost{
          border:0; background:transparent; color:var(--muted);
          padding:4px 6px; border-radius:8px; cursor:pointer;
        }
        .ghost:hover{ color:var(--fg); }

        .meter{
          display:grid; grid-template-columns: repeat(4, 1fr); gap:6px; margin-top:8px;
        }
        .bar{
          height:6px; border-radius:999px; background:rgba(255,255,255,0.12);
          border:1px solid var(--card-stroke);
        }
        .bar.on:nth-child(1){ background:#ff9f68; border-color:#ff9f68; }
        .bar.on:nth-child(2){ background:#ffd168; border-color:#ffd168; }
        .bar.on:nth-child(3){ background:#c6e377; border-color:#c6e377; }
        .bar.on:nth-child(4){ background:#8ea0ff; border-color:#8ea0ff; }

        .cta{
          margin-top:6px; width:100%;
          border:1px solid #f5b942;
          background: linear-gradient(180deg, #ffd97a, #f5b942);
          color:#0f1115;
          font-weight:800; letter-spacing:0.3px;
          padding:12px 16px; border-radius:14px; cursor:pointer;
          transition: transform 0.02s ease, box-shadow 0.2s ease, filter 0.2s ease;
          box-shadow: 0 10px 22px rgba(245,185,66,0.35);
        }
        .cta:hover{ box-shadow: 0 12px 28px rgba(245,185,66,0.48); filter: brightness(1.03); }
        .cta:active{ transform: translateY(1px); }
        .cta:disabled{ opacity:0.75; cursor:not-allowed; transform:none; box-shadow:none; }

        .spinner{
          width:16px; height:16px; border-radius:50%;
          border:2px solid rgba(0,0,0,0.25); border-top-color: rgba(0,0,0,0.6);
          display:inline-block; margin-right:8px;
          animation: spin 0.8s linear infinite;
        }
        @keyframes spin{ to{ transform: rotate(360deg); } }

        .meta{ text-align:center; margin:12px 0 4px; color:var(--muted); }
        .link{
          color:var(--fg); text-decoration:none; border-bottom:1px dashed rgba(255,255,255,0.3);
        }
        .link:hover{ border-bottom-color: rgba(255,255,255,0.6); }

        @media (max-width:520px){
          .card{ padding:20px 16px 12px; }
        }
      `}</style>
    </main>
  );
}

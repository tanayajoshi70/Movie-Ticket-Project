"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

/* ================= API helpers ================= */
const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";
const getToken = () => (typeof window === "undefined" ? null : localStorage.getItem("token"));

async function apiFetch<T = any>(path: string, options: RequestInit = {}): Promise<T> {
  const token = getToken();
  const res = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {}),
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    cache: "no-store",
  });
  if (!res.ok) {
    let msg = "";
    try { msg = await res.text(); } catch {}
    throw new Error(msg || `Request failed (${res.status})`);
  }
  const text = await res.text();
  try { return text ? JSON.parse(text) : ({} as T); } catch { return text as unknown as T; }
}

/* ================= tiny UI utils ================= */
const cn = (...c: (string | false | undefined)[]) => c.filter(Boolean).join(" ");

function Toast({ text, tone }: { text: string; tone: "info" | "ok" | "err" }) {
  const toneCls =
    tone === "ok"
      ? "border-emerald-500/30 bg-emerald-500/10 text-emerald-200"
      : tone === "err"
      ? "border-rose-500/30 bg-rose-500/10 text-rose-200"
      : "border-indigo-500/30 bg-indigo-500/10 text-indigo-200";
  return <div className={cn("rounded-xl px-3 py-2 text-sm border", toneCls)}>{text}</div>;
}

function Shell({ children }: { children: React.ReactNode }) {
  return (
    <div className="min-h-screen text-slate-100 bg-[#0b0e16] relative">
      {/* decorations (do NOT capture clicks) */}
      <div aria-hidden className="pointer-events-none absolute inset-0">
        <div className="absolute -top-24 -left-24 w-[520px] h-[520px] rounded-full bg-indigo-600/15 blur-3xl" />
        <div className="absolute bottom-0 right-0 w-[600px] h-[400px] rounded-full bg-amber-500/10 blur-3xl" />
      </div>
      {children}
    </div>
  );
}
function TopBar({ busy }: { busy: string }) {
  const router = useRouter();
  return (
    <header className="sticky top-0 z-20 border-b border-white/10 bg-[#0b0e16]/80 backdrop-blur">
      <div className="mx-auto max-w-[1200px] px-4 py-3 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-xl border border-white/10 bg-gradient-to-br from-indigo-500/30 to-amber-400/20 grid place-items-center text-xl">🎬</div>
          <div>
            <h1 className="text-lg font-semibold tracking-wide">CineBook — User</h1>
            <p className="text-xs text-slate-400">{busy || "Ready"}</p>
          </div>
        </div>
        <div className="flex items-center gap-2">
          <Btn type="button" label="Home" onClick={() => router.push("/")} />
          <Btn
            type="button"
            label="Logout"
            variant="danger"
            onClick={() => { localStorage.removeItem("token"); router.replace("/login"); }}
          />
        </div>
      </div>
    </header>
  );
}
function Panel({ children, title, subtitle }: { children: React.ReactNode; title?: string; subtitle?: string }) {
  return (
    <section className="rounded-2xl border border-white/10 bg-white/[0.04] shadow-[0_20px_60px_-20px_rgba(0,0,0,.6)]">
      {(title || subtitle) && (
        <div className="px-5 py-3 border-b border-white/10">
          <div className="text-slate-100 font-semibold">{title}</div>
          {subtitle && <div className="text-xs text-slate-400 mt-0.5">{subtitle}</div>}
        </div>
      )}
      <div className="p-5">{children}</div>
    </section>
  );
}
function SectionTitle({ icon, label }: { icon?: string; label: string }) {
  return (
    <div className="flex items-center gap-2 text-slate-200 mb-3">
      <span className="text-xl">{icon}</span>
      <h2 className="text-base font-semibold tracking-wide">{label}</h2>
    </div>
  );
}
function Field({ label, children, className }: { label: string; children: React.ReactNode; className?: string }) {
  return (
    <label className={cn("w-full", className)}>
      <div className="text-[12px] text-slate-400 mb-1">{label}</div>
      {children}
    </label>
  );
}
function Input(props: React.InputHTMLAttributes<HTMLInputElement>) {
  return (
    <input
      {...props}
      className={cn(
        "w-full rounded-xl border border-white/10 bg-[#0f1424] px-3 py-2 text-sm text-slate-100 placeholder:text-slate-500",
        "focus:outline-none focus:ring-2 focus:ring-indigo-500/60",
        props.className
      )}
    />
  );
}
function Btn(
  props: React.ButtonHTMLAttributes<HTMLButtonElement> & { label: string; variant?: "primary" | "ghost" | "danger" }
) {
  const base =
    "rounded-xl px-3 py-2 text-sm font-medium border transition-all disabled:opacity-50 disabled:cursor-not-allowed";
  const style =
    props.variant === "primary"
      ? "bg-gradient-to-b from-indigo-500 to-indigo-600 text-white border-indigo-400 hover:brightness-110 shadow-lg shadow-indigo-800/30"
      : props.variant === "danger"
      ? "bg-gradient-to-b from-rose-500 to-rose-600 text-white border-rose-400 hover:brightness-110 shadow-lg shadow-rose-900/30"
      : "bg-[#141a2b] text-slate-100 border-white/10 hover:bg-[#1a2136]";
  const { label, className, ...rest } = props;
  return (
    <button {...rest} className={cn(base, style, className)}>{label}</button>
  );
}

/* ============ Human‑readable result viewer (table / key-value) ============ */
const Result = ({ data, title }: { data: any; title?: string }) => {
  if (!data) return null;

  const isObj = (v: any) => v && typeof v === "object" && !Array.isArray(v);
  const labelize = (s: string) =>
    s.replace(/[_-]/g, " ").replace(/([a-z])([A-Z])/g, "$1 $2").replace(/\b\w/g, (c) => c.toUpperCase());
  const looksDate = (k: string, v: any) => typeof v === "string" && /(date|time|at|created|updated|start|end)/i.test(k) && !isNaN(Date.parse(v));
  const fmtDate = (v: string) => new Date(v).toLocaleString();
  const isMoney = (k: string) => /(amount|price|total|fare)/i.test(k);
  const fmtMoney = (v: any) =>
    isFinite(Number(v)) ? new Intl.NumberFormat(undefined, { style: "currency", currency: "INR" }).format(Number(v)) : String(v);

  const Badge = ({ tone, children }: { tone: "ok" | "warn" | "bad" | "info"; children: React.ReactNode }) => {
    const map = {
      ok: "bg-emerald-500/15 text-emerald-300 border-emerald-500/30",
      warn: "bg-amber-500/15 text-amber-300 border-amber-500/30",
      bad: "bg-rose-500/15 text-rose-300 border-rose-500/30",
      info: "bg-indigo-500/15 text-indigo-300 border-indigo-500/30",
    } as const;
    return <span className={cn("inline-block px-2 py-0.5 rounded-full text-xs border", map[tone])}>{children}</span>;
  };

  const renderVal = (k: string, v: any) => {
    if (v === null || v === undefined) return <span className="text-slate-500">—</span>;
    if (looksDate(k, v)) return <span>{fmtDate(v)}</span>;
    if (isMoney(k)) return <span>{fmtMoney(v)}</span>;
    if (typeof v === "boolean") return <Badge tone={v ? "ok" : "bad"}>{v ? "Yes" : "No"}</Badge>;
    if (typeof v === "string") {
      if (/success|paid|confirmed|active|ok/i.test(v)) return <Badge tone="ok">{v}</Badge>;
      if (/pending|processing/i.test(v)) return <Badge tone="warn">{v}</Badge>;
      if (/cancel|fail|error|blocked|inactive/i.test(v)) return <Badge tone="bad">{v}</Badge>;
      return <span className="whitespace-pre-wrap">{v}</span>;
    }
    if (Array.isArray(v)) return <span>{v.join(", ")}</span>;
    if (isObj(v)) return <code className="text-xs bg-white/5 border border-white/10 rounded px-1">{JSON.stringify(v)}</code>;
    return <span>{String(v)}</span>;
  };

  if (Array.isArray(data)) {
    const sample = data.slice(0, 12);
    const cols = Array.from(new Set(sample.flatMap((r) => (isObj(r) ? Object.keys(r) : ["Value"]))));

    return (
      <div className="mt-4 rounded-2xl border border-white/10 overflow-hidden">
        {title && <div className="px-4 py-2 bg-white/5 border-b border-white/10 font-semibold">{title}</div>}
        <div className="overflow-x-auto">
          <table className="min-w-full text-sm">
            <thead className="bg-white/[0.04] border-b border-white/10">
              <tr>{cols.map((c) => <th key={c} className="px-3 py-2 text-left font-semibold">{labelize(c)}</th>)}</tr>
            </thead>
            <tbody className="[&>tr:nth-child(odd)]:bg-white/[0.02]">
              {data.map((row: any, i: number) => (
                <tr key={i} className="border-b border-white/5 last:border-b-0">
                  {isObj(row)
                    ? cols.map((c) => <td key={c} className="px-3 py-2 align-top">{renderVal(c, row?.[c])}</td>)
                    : <td className="px-3 py-2">{renderVal("value", row)}</td>}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        <div className="px-4 py-2 text-xs text-slate-400 bg-white/5 border-t border-white/10">{data.length} item{data.length === 1 ? "" : "s"}</div>
      </div>
    );
  }

  if (typeof data === "object" && data) {
    return (
      <div className="mt-4 rounded-2xl border border-white/10 overflow-hidden">
        {title && <div className="px-4 py-2 bg-white/5 border-b border-white/10 font-semibold">{title}</div>}
        <dl className="divide-y divide-white/10 bg-white/[0.02]">
          {Object.entries(data).map(([k, v]) => (
            <div key={k} className="grid grid-cols-1 sm:grid-cols-3 gap-2 px-4 py-2">
              <dt className="text-slate-400">{labelize(k)}</dt>
              <dd className="sm:col-span-2">{renderVal(k, v)}</dd>
            </div>
          ))}
        </dl>
      </div>
    );
  }
  return <div className="mt-3">{String(data)}</div>;
};

/* ================= Page ================= */
export default function UserDashboardPage() {
  const router = useRouter();

  // auth guard
  useEffect(() => {
    if (!getToken()) router.replace("/login");
  }, [router]);

  const [busy, setBusy] = useState("");
  const [toast, setToast] = useState<{ text: string; tone: "info" | "ok" | "err" } | null>(null);
  const flash = (text: string, tone: "info" | "ok" | "err" = "info") => {
    setToast({ text, tone });
    setTimeout(() => setToast(null), 1800);
  };

  const run = async (label: string, fn: () => Promise<void>) => {
    setBusy(label);
    flash(label, "info");
    try {
      await fn();
      flash(label.replace(/\.\.\.$/, "") + " done", "ok");
    } catch (e: any) {
      flash(e?.message || "Request failed", "err");
    } finally {
      setBusy("");
    }
  };

  /* ---------- state & actions ---------- */
  // Profile
  const [profile, setProfile] = useState<any>(null);
  const [updProfile, setUpdProfile] = useState({ name: "", phone: "", city: "" });
  const [pwd, setPwd] = useState({ oldPassword: "", newPassword: "" });
  const [deact, setDeact] = useState({ email: "" });
  const [profileMsg, setProfileMsg] = useState<any>(null);
  const [pwdMsg, setPwdMsg] = useState<any>(null);
  const [deactMsg, setDeactMsg] = useState<any>(null);

  const getProfile = async () => run("Loading profile...", async () => setProfile(await apiFetch("/user/profile")));
  const updateProfile = async () =>
    run("Updating profile...", async () => setProfileMsg(await apiFetch("/user/update-profile", { method: "PUT", body: JSON.stringify(updProfile) })));
  const updatePassword = async () =>
    run("Updating password...", async () => setPwdMsg(await apiFetch("/user/update-password", { method: "PUT", body: JSON.stringify(pwd) })));
  const deactivateOwn = async () =>
    run("Deactivating account...", async () => setDeactMsg(await apiFetch("/user/deactivate-own", { method: "POST", body: JSON.stringify(deact) })));

  // Seats
  const [seatQuery, setSeatQuery] = useState({ showId: "" });
  const [availableSeats, setAvailableSeats] = useState<any>(null);
  const getAvailableSeats = async () =>
    run("Loading seats...", async () => setAvailableSeats(await apiFetch(`/api/user/shows/${seatQuery.showId}/seats/available`)));

  // Shows
  const [showQuery, setShowQuery] = useState({ showId: "", title: "", theater: "", date: "", startDT: "" });
  const [showDetails, setShowDetails] = useState<any>(null);
  const [showsByTitle, setShowsByTitle] = useState<any>(null);
  const [showsByTheater, setShowsByTheater] = useState<any>(null);
  const [showsByDate, setShowsByDate] = useState<any>(null);
  const [showsByStartDT, setShowsByStartDT] = useState<any>(null);

  const getShowDetails = async () => run("Fetching show...", async () => setShowDetails(await apiFetch(`/api/user/shows/${showQuery.showId}`)));
  const byMovieTitle = async () => run("By movie title...", async () => setShowsByTitle(await apiFetch(`/api/user/shows/by-movie?title=${encodeURIComponent(showQuery.title)}`)));
  const byTheaterName = async () => run("By theater...", async () => setShowsByTheater(await apiFetch(`/api/user/shows/by-theater?name=${encodeURIComponent(showQuery.theater)}`)));
  const byDate = async () => run("By date...", async () => setShowsByDate(await apiFetch(`/api/user/shows/by-date?date=${encodeURIComponent(showQuery.date)}`)));
  const byStartDateTime = async () => run("By start datetime...", async () => setShowsByStartDT(await apiFetch(`/api/user/shows/search/by-start-datetime?datetime=${encodeURIComponent(showQuery.startDT)}`)));

  // Theaters
  const [theaterQuery, setTheaterQuery] = useState({ id: "", location: "", name: "" });
  const [theatersAll, setTheatersAll] = useState<any>(null);
  const [theaterById, setTheaterById] = useState<any>(null);
  const [theatersByLoc, setTheatersByLoc] = useState<any>(null);
  const [theatersByName, setTheatersByName] = useState<any>(null);

  const getAllTheaters = async () => run("Loading theaters...", async () => setTheatersAll(await apiFetch("/api/theaters")));
  const getTheaterById = async () => run("Theater by id...", async () => setTheaterById(await apiFetch(`/api/theaters/${theaterQuery.id}`)));
  const searchTheatersByLocation = async () =>
    run("Theaters by location...", async () => setTheatersByLoc(await apiFetch(`/api/theaters/search?location=${encodeURIComponent(theaterQuery.location)}`)));
  const searchTheatersByName = async () =>
    run("Theaters by name...", async () => setTheatersByName(await apiFetch(`/api/theaters/search-by-name?name=${encodeURIComponent(theaterQuery.name)}`)));

  // Payments
  const [paymentForm, setPaymentForm] = useState({ bookingId: "", amount: "", method: "" } as any);
  const [retryForm, setRetryForm] = useState({ paymentId: "", reason: "" } as any);
  const [payResp, setPayResp] = useState<any>(null);
  const [myPays, setMyPays] = useState<any>(null);
  const [payByBooking, setPayByBooking] = useState<any>(null);
  const [retryResp, setRetryResp] = useState<any>(null);

  const makePayment = async () => run("Making payment...", async () => setPayResp(await apiFetch("/api/user/payments", { method: "POST", body: JSON.stringify(paymentForm) })));
  const viewMyPayments = async () => run("Loading my payments...", async () => setMyPays(await apiFetch("/api/user/payments")));
  const getPaymentByBookingId = async () => run("Payment by booking...", async () => setPayByBooking(await apiFetch(`/api/user/payments/booking/${paymentForm.bookingId}`)));
  const retryPayment = async () => run("Retrying payment...", async () => setRetryResp(await apiFetch("/api/user/payments/retry", { method: "POST", body: JSON.stringify(retryForm) })));

  // Bookings
  const [bookingForm, setBookingForm] = useState({
    bookingIdForCancel: "",
    showBooking: { showId: "", seatIds: [] as number[] },
    bookSeats: { showId: "", seatIds: [] as number[] },
    bookingIdForSeats: "",
  });
  const [myBookings, setMyBookings] = useState<any>(null);
  const [bookedSeatsForMyBooking, setBookedSeatsForMyBooking] = useState<any>(null);
  const [bookedSeatsByBooking, setBookedSeatsByBooking] = useState<any>(null);
  const [bookResp, setBookResp] = useState<any>(null);
  const [bookResp2, setBookResp2] = useState<any>(null);
  const [cancelResp, setCancelResp] = useState<any>(null);

  const getMyBookings = async () => run("Loading my bookings...", async () => setMyBookings(await apiFetch("/api/user/bookings")));
  const bookShow = async () =>
    run("Booking show...", async () => {
      const body = { ...bookingForm.showBooking, showId: Number(bookingForm.showBooking.showId || 0) };
      setBookResp(await apiFetch("/api/user/bookings/shows", { method: "POST", body: JSON.stringify(body) }));
    });
  const bookSeats = async () =>
    run("Booking seats...", async () => {
      const body = { ...bookingForm.bookSeats, showId: Number(bookingForm.bookSeats.showId || 0) };
      setBookResp2(await apiFetch("/api/user/bookings", { method: "POST", body: JSON.stringify(body) }));
    });
  const getBookedSeatsForMyBooking = async () =>
    run("Booked seats (mine)...", async () => setBookedSeatsForMyBooking(await apiFetch(`/api/user/bookings/booked/${bookingForm.bookingIdForSeats}/seats`)));
  const getBookedSeatsByBooking = async () =>
    run("Booked seats (by booking)...", async () => setBookedSeatsByBooking(await apiFetch(`/api/user/bookings/${bookingForm.bookingIdForSeats}/seats`)));
  const cancelBooking = async () =>
    run("Cancelling booking...", async () => setCancelResp(await apiFetch(`/api/user/bookings/${bookingForm.bookingIdForCancel}`, { method: "DELETE" })));

  // Receipt
  const [receiptBookingId, setReceiptBookingId] = useState("");
  const downloadReceipt = async () => {
    await run("Downloading receipt...", async () => {
      const token = getToken();
      const res = await fetch(`${API_BASE}/api/user/receipt/${receiptBookingId}`, {
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      if (!res.ok) throw new Error(`Failed (${res.status})`);
      const blob = await res.blob();
      const url = URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url; a.download = `receipt_${receiptBookingId}.pdf`;
      document.body.appendChild(a); a.click(); a.remove();
      URL.revokeObjectURL(url);
    });
  };

  // Movies
  const [movieQuery, setMovieQuery] = useState({ id: "", name: "", genre: "", language: "" });
  const [moviesAll, setMoviesAll] = useState<any>(null);
  const [movieById, setMovieById] = useState<any>(null);
  const [moviesSearch, setMoviesSearch] = useState<any>(null);
  const [nowShowing, setNowShowing] = useState<any>(null);
  const [upcoming, setUpcoming] = useState<any>(null);

  const getAllMoviesForUsers = async () => run("Loading movies...", async () => setMoviesAll(await apiFetch("/api/movies")));
  const getMovieByIdForUsers = async () => run("Movie by id...", async () => setMovieById(await apiFetch(`/api/movies/${movieQuery.id}`)));
  const searchMovies = async () => run("Searching movies...", async () => {
    const p = new URLSearchParams();
    if (movieQuery.name) p.set("name", movieQuery.name);
    if (movieQuery.genre) p.set("genre", movieQuery.genre);
    if (movieQuery.language) p.set("language", movieQuery.language);
    setMoviesSearch(await apiFetch(`/api/movies/search?${p.toString()}`));
  });
  const getNowShowingMovies = async () => run("Now showing...", async () => setNowShowing(await apiFetch("/api/movies/now-showing")));
  const getUpcomingMovies = async () => run("Upcoming...", async () => setUpcoming(await apiFetch("/api/movies/upcoming")));

  /* ================= Render ================= */
  return (
    <Shell>
      <TopBar busy={busy} />

      {/* toast */}
      <div className="fixed z-30 top-3 left-1/2 -translate-x-1/2">{toast && <Toast text={toast.text} tone={toast.tone} />}</div>

      <main className="max-w-[1200px] mx-auto px-4 py-6 grid grid-cols-1 gap-6">
        {/* PROFILE */}
        <Panel title="My Profile" subtitle="Manage your account info & security">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-5">
            <div>
              <SectionTitle icon="👤" label="Profile" />
              <div className="flex flex-wrap gap-2 mb-3">
                <Btn type="button" label="Get Profile" variant="primary" onClick={getProfile} />
              </div>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <Field label="Name"><Input value={updProfile.name} onChange={(e) => setUpdProfile({ ...updProfile, name: e.target.value })} /></Field>
                <Field label="Phone"><Input value={updProfile.phone} onChange={(e) => setUpdProfile({ ...updProfile, phone: e.target.value })} /></Field>
              </div>
              <Field label="City" className="mt-3"><Input value={updProfile.city} onChange={(e) => setUpdProfile({ ...updProfile, city: e.target.value })} /></Field>
              <div className="mt-3"><Btn type="button" label="Update Profile" onClick={updateProfile} /></div>
              <Result data={profile || profileMsg} title="Profile" />
            </div>

            <div>
              <SectionTitle icon="🔒" label="Security" />
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <Field label="Old Password"><Input type="password" value={pwd.oldPassword} onChange={(e) => setPwd({ ...pwd, oldPassword: e.target.value })} /></Field>
                <Field label="New Password"><Input type="password" value={pwd.newPassword} onChange={(e) => setPwd({ ...pwd, newPassword: e.target.value })} /></Field>
              </div>
              <div className="mt-3"><Btn type="button" label="Update Password" onClick={updatePassword} /></div>

              <div className="mt-6">
                <Field label="Email (confirm deactivation)"><Input value={deact.email} onChange={(e) => setDeact({ email: e.target.value })} /></Field>
                <div className="mt-3"><Btn type="button" label="Deactivate My Account" variant="danger" onClick={deactivateOwn} /></div>
              </div>
              <Result data={pwdMsg || deactMsg} title="Security Response" />
            </div>
          </div>
        </Panel>

        {/* SEATS */}
        <Panel title="Seats (Available)" subtitle="Find free seats for a show">
          <div className="flex flex-wrap items-end gap-3">
            <Field label="Show ID"><Input value={seatQuery.showId} onChange={(e) => setSeatQuery({ showId: e.target.value })} /></Field>
            <Btn type="button" label="Get Available Seats" variant="primary" onClick={getAvailableSeats} />
          </div>
          <Result data={availableSeats} title="Available Seats" />
        </Panel>

        {/* SHOWS */}
        <Panel title="Shows" subtitle="Search showtimes by movie, theater, date or time">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <div>
              <SectionTitle icon="🎟️" label="Look Up" />
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <Field label="Show ID"><Input value={showQuery.showId} onChange={(e) => setShowQuery({ ...showQuery, showId: e.target.value })} /></Field>
                <div className="flex items-end"><Btn type="button" label="Show Details" onClick={getShowDetails} /></div>

                <Field label="Movie Title"><Input value={showQuery.title} onChange={(e) => setShowQuery({ ...showQuery, title: e.target.value })} /></Field>
                <div className="flex items-end"><Btn type="button" label="By Movie Title" onClick={byMovieTitle} /></div>

                <Field label="Theater Name"><Input value={showQuery.theater} onChange={(e) => setShowQuery({ ...showQuery, theater: e.target.value })} /></Field>
                <div className="flex items-end"><Btn type="button" label="By Theater Name" onClick={byTheaterName} /></div>
              </div>
            </div>
            <div>
              <SectionTitle icon="🗓️" label="Plan" />
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <Field label="Date (YYYY-MM-DD)"><Input value={showQuery.date} onChange={(e) => setShowQuery({ ...showQuery, date: e.target.value })} /></Field>
                <div className="flex items-end"><Btn type="button" label="By Date" onClick={byDate} /></div>

                <Field label="Start Datetime (YYYY-MM-DDTHH:mm:ss)"><Input value={showQuery.startDT} onChange={(e) => setShowQuery({ ...showQuery, startDT: e.target.value })} /></Field>
                <div className="flex items-end"><Btn type="button" label="By Start Datetime" onClick={byStartDateTime} /></div>
              </div>
            </div>
          </div>
          <Result data={showDetails || showsByTitle || showsByTheater || showsByDate || showsByStartDT} title="Shows" />
        </Panel>

        {/* THEATERS */}
        <Panel title="Theaters" subtitle="Browse by id, name or location">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <div>
              <div className="flex flex-wrap gap-2 mb-3">
                <Btn type="button" label="All Theaters" onClick={getAllTheaters} />
              </div>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <Field label="Theater ID"><Input value={theaterQuery.id} onChange={(e) => setTheaterQuery({ ...theaterQuery, id: e.target.value })} /></Field>
                <div className="flex items-end"><Btn type="button" label="Get By ID" onClick={getTheaterById} /></div>
              </div>
            </div>
            <div>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <Field label="Location"><Input value={theaterQuery.location} onChange={(e) => setTheaterQuery({ ...theaterQuery, location: e.target.value })} /></Field>
                <div className="flex items-end"><Btn type="button" label="By Location" onClick={searchTheatersByLocation} /></div>

                <Field label="Name"><Input value={theaterQuery.name} onChange={(e) => setTheaterQuery({ ...theaterQuery, name: e.target.value })} /></Field>
                <div className="flex items-end"><Btn type="button" label="By Name" onClick={searchTheatersByName} /></div>
              </div>
            </div>
          </div>
          <Result data={theatersAll || theaterById || theatersByLoc || theatersByName} title="Theaters" />
        </Panel>

        {/* PAYMENTS */}
        <Panel title="Payments" subtitle="Pay for a booking, view or retry">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <div>
              <SectionTitle icon="💳" label="Pay / View" />
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                <Field label="Booking ID"><Input value={paymentForm.bookingId} onChange={(e) => setPaymentForm({ ...paymentForm, bookingId: e.target.value })} /></Field>
                <Field label="Amount"><Input type="number" value={paymentForm.amount || ""} onChange={(e) => setPaymentForm({ ...paymentForm, amount: e.target.value })} /></Field>
                <Field label="Method"><Input value={paymentForm.method || ""} onChange={(e) => setPaymentForm({ ...paymentForm, method: e.target.value })} /></Field>
              </div>
              <div className="flex flex-wrap gap-2 mt-3">
                <Btn type="button" label="Make Payment" variant="primary" onClick={makePayment} />
                <Btn type="button" label="My Payments" onClick={viewMyPayments} />
                <Btn type="button" label="Payment by Booking" onClick={getPaymentByBookingId} />
              </div>
              <Result data={payResp || myPays || payByBooking} title="Payment Response" />
            </div>
            <div>
              <SectionTitle icon="🔁" label="Retry" />
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <Field label="Payment ID"><Input value={retryForm.paymentId || ""} onChange={(e) => setRetryForm({ ...retryForm, paymentId: e.target.value })} /></Field>
                <Field label="Reason"><Input value={retryForm.reason || ""} onChange={(e) => setRetryForm({ ...retryForm, reason: e.target.value })} /></Field>
              </div>
              <div className="mt-3"><Btn type="button" label="Retry Payment" onClick={retryPayment} /></div>
              <Result data={retryResp} title="Retry Response" />
            </div>
          </div>
        </Panel>

        {/* BOOKINGS */}
        <Panel title="Bookings" subtitle="Book seats, manage and view">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <div>
              <SectionTitle icon="🪑" label="Book" />
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <Field label="Show ID (book show)"><Input value={bookingForm.showBooking.showId} onChange={(e) => setBookingForm({ ...bookingForm, showBooking: { ...bookingForm.showBooking, showId: e.target.value } })} /></Field>
                <Field label="Seat IDs (comma)"><Input onChange={(e) => setBookingForm({ ...bookingForm, showBooking: { ...bookingForm.showBooking, seatIds: e.target.value.split(",").map(s => Number(s.trim())).filter(Boolean) } })} /></Field>
              </div>
              <div className="mt-3"><Btn type="button" label="Book Show" variant="primary" onClick={bookShow} /></div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 mt-5">
                <Field label="Show ID (book seats)"><Input value={bookingForm.bookSeats.showId} onChange={(e) => setBookingForm({ ...bookingForm, bookSeats: { ...bookingForm.bookSeats, showId: e.target.value } })} /></Field>
                <Field label="Seat IDs (comma)"><Input onChange={(e) => setBookingForm({ ...bookingForm, bookSeats: { ...bookingForm.bookSeats, seatIds: e.target.value.split(",").map(s => Number(s.trim())).filter(Boolean) } })} /></Field>
              </div>
              <div className="mt-3"><Btn type="button" label="Book Seats" onClick={bookSeats} /></div>

              <Result data={bookResp || bookResp2} title="Booking Response" />
            </div>

            <div>
              <SectionTitle icon="🧾" label="Manage" />
              <div className="flex flex-wrap gap-2 mb-3"><Btn type="button" label="My Bookings" onClick={getMyBookings} /></div>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <Field label="Booking ID (seats)"><Input value={bookingForm.bookingIdForSeats} onChange={(e) => setBookingForm({ ...bookingForm, bookingIdForSeats: e.target.value })} /></Field>
                <Field label="Booking ID (cancel)"><Input value={bookingForm.bookingIdForCancel} onChange={(e) => setBookingForm({ ...bookingForm, bookingIdForCancel: e.target.value })} /></Field>
              </div>
              <div className="flex flex-wrap gap-2 mt-3">
                <Btn type="button" label="Booked Seats (My Booking)" onClick={getBookedSeatsForMyBooking} />
                <Btn type="button" label="Booked Seats (By Booking)" onClick={getBookedSeatsByBooking} />
                <Btn type="button" label="Cancel Booking" variant="danger" onClick={cancelBooking} />
              </div>
              <Result data={myBookings || bookedSeatsForMyBooking || bookedSeatsByBooking || cancelResp} title="Manage Response" />
            </div>
          </div>
        </Panel>

        {/* RECEIPT */}
        <Panel title="Receipt" subtitle="Download a PDF receipt for a booking">
          <div className="flex flex-wrap items-end gap-3">
            <Field label="Booking ID"><Input value={receiptBookingId} onChange={(e) => setReceiptBookingId(e.target.value)} /></Field>
            <Btn type="button" label="Download" onClick={downloadReceipt} />
          </div>
        </Panel>

        {/* MOVIES */}
        <Panel title="Movies (User)" subtitle="Explore movies: all, search, now showing, upcoming">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <div>
              <div className="flex flex-wrap gap-2 mb-3">
                <Btn type="button" label="All Movies" onClick={getAllMoviesForUsers} />
              </div>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <Field label="Movie ID"><Input value={movieQuery.id} onChange={(e) => setMovieQuery({ ...movieQuery, id: e.target.value })} /></Field>
                <div className="flex items-end"><Btn type="button" label="Get By ID" onClick={getMovieByIdForUsers} /></div>
              </div>
              <Result data={moviesAll || movieById} title="Movies" />
            </div>

            <div>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                <Field label="Name"><Input value={movieQuery.name} onChange={(e) => setMovieQuery({ ...movieQuery, name: e.target.value })} /></Field>
                <Field label="Genre"><Input value={movieQuery.genre} onChange={(e) => setMovieQuery({ ...movieQuery, genre: e.target.value })} /></Field>
                <Field label="Language"><Input value={movieQuery.language} onChange={(e) => setMovieQuery({ ...movieQuery, language: e.target.value })} /></Field>
              </div>
              <div className="flex flex-wrap gap-2 mt-3">
                <Btn type="button" label="Search" onClick={searchMovies} />
                <Btn type="button" label="Now Showing" variant="primary" onClick={getNowShowingMovies} />
                <Btn type="button" label="Upcoming" onClick={getUpcomingMovies} />
              </div>
              <Result data={moviesSearch || nowShowing || upcoming} title="Movie Results" />
            </div>
          </div>
        </Panel>

        <p className="text-xs text-slate-400 text-center">
          All requests include <code className="px-1 rounded bg-white/5 border border-white/10">Authorization: Bearer &lt;token&gt;</code>.
        </p>
      </main>
    </Shell>
  );
}

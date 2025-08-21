"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

/* ---------------- Helpers ---------------- */
function getToken() {
  if (typeof window === "undefined") return null;
  return localStorage.getItem("token");
}
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

/* ---------- Pretty Result Viewer (tables/cards) ---------- */
const Result = ({ data, title }: { data: any; title?: string }) => {
  if (!data) return null;

  const isObj = (v: any) => v && typeof v === "object" && !Array.isArray(v);
  const formatKey = (k: string) =>
    k.replace(/[_-]/g, " ")
     .replace(/([a-z])([A-Z])/g, "$1 $2")
     .replace(/\b\w/g, (c) => c.toUpperCase());
  const looksDate = (k: string, v: any) =>
    typeof v === "string" && /(date|time|at|on|start|end)/i.test(k) && !isNaN(Date.parse(v));
  const fmtDate = (v: string) =>
    new Date(v).toLocaleString(undefined, { dateStyle: "medium", timeStyle: "short" });
  const looksMoney = (k: string) => /(amount|price|total|cost|fare)/i.test(k);
  const fmtMoney = (v: any) => {
    const n = Number(v);
    return isFinite(n)
      ? new Intl.NumberFormat(undefined, { style: "currency", currency: "INR", maximumFractionDigits: 2 }).format(n)
      : String(v);
  };
  const renderVal = (k: string, v: any) => {
    if (v === null || v === undefined) return <span className="muted">—</span>;
    if (looksDate(k, v)) return <span>{fmtDate(v)}</span>;
    if (looksMoney(k)) return <span>{fmtMoney(v)}</span>;
    if (typeof v === "boolean") return <span className={`badge ${v ? "ok" : "bad"}`}>{v ? "Yes" : "No"}</span>;
    if (typeof v === "string" && /(success|failed|pending|cancel)/i.test(v))
      return <span className={`badge ${/success|ok/i.test(v) ? "ok" : /pending/i.test(v) ? "warn" : "bad"}`}>{v}</span>;
    if (Array.isArray(v)) return <span className="mono">{v.join(", ") || "—"}</span>;
    if (isObj(v)) return <code className="mono tiny">{JSON.stringify(v)}</code>;
    return <span>{String(v)}</span>;
  };

  if (isObj(data)) {
    const entries = Object.entries(data);
    return (
      <div className="result-card">
        {title && <div className="result-head">{title}</div>}
        <dl className="kv">
          {entries.map(([k, v]) => (
            <div className="kv-row" key={k}>
              <dt>{formatKey(k)}</dt>
              <dd>{renderVal(k, v)}</dd>
            </div>
          ))}
        </dl>
      </div>
    );
  }

  if (Array.isArray(data)) {
    if (data.length === 0) return <div className="result-empty">No items found.</div>;
    const sample = data.slice(0, 12);
    const cols = Array.from(new Set(sample.flatMap((row) => (isObj(row) ? Object.keys(row) : ["Value"]))));
    return (
      <div className="result-table-wrap">
        {title && <div className="result-head">{title}</div>}
        <table className="result-table">
          <thead>
            <tr>{cols.map((c) => <th key={c}>{formatKey(c)}</th>)}</tr>
          </thead>
          <tbody>
            {data.map((row: any, i: number) => (
              <tr key={i}>
                {isObj(row)
                  ? cols.map((c) => <td key={c}>{renderVal(c, row?.[c])}</td>)
                  : <td>{renderVal("value", row)}</td>}
              </tr>
            ))}
          </tbody>
        </table>
        <div className="result-meta">{data.length} item{data.length === 1 ? "" : "s"}</div>
      </div>
    );
  }

  return <div className="result-plain">{String(data)}</div>;
};

/* ---------------- Small UI Kit (dark) ---------------- */
function Btn({
  label, variant = "primary", ...rest
}: React.ButtonHTMLAttributes<HTMLButtonElement> & { label: string; variant?: "primary" | "secondary" | "danger" }) {
  return (
    <button {...rest} className={`btn ${variant} ${rest.disabled ? "disabled" : ""}`}>
      {label}
    </button>
  );
}
const Input = (props: React.InputHTMLAttributes<HTMLInputElement>) => <input {...props} className="inp" />;
const Row = ({ children }: { children: React.ReactNode }) => <div className="row">{children}</div>;
const Section = ({ title, children }: { title: string; children: React.ReactNode }) => (
  <section className="card">
    <header className="card-head"><h2>{title}</h2></header>
    <div className="card-body">{children}</div>
  </section>
);

/* ---------------- Page ---------------- */
export default function UserDashboardPage() {
  const router = useRouter();

  const [busy, setBusy] = useState<string>("");
  const [errorMsg, setErrorMsg] = useState("");
  const [okMsg, setOkMsg] = useState("");

  useEffect(() => {
    const token = getToken();
    if (!token) router.replace("/login");
  }, [router]);

  async function run(label: string, fn: () => Promise<void>) {
    setBusy(label);
    setErrorMsg("");
    setOkMsg("");
    try {
      await fn();
      setOkMsg(label.replace(/\.\.\.$/, "") + " done.");
    } catch (e: unknown) {
      const msg = e instanceof Error ? e.message : "Request failed";
      setErrorMsg(msg);
    } finally {
      setBusy("");
    }
  }

  /* ---------- User Profile ---------- */
  const [profile, setProfile] = useState<any>(null);
  const [updProfile, setUpdProfile] = useState({ name: "", phone: "", city: "" });
  const [updPwd, setUpdPwd] = useState({ oldPassword: "", newPassword: "" });
  const [deact, setDeact] = useState({ email: "" });
  const [profileMsg, setProfileMsg] = useState<any>(null);
  const [pwdMsg, setPwdMsg] = useState<any>(null);
  const [deactMsg, setDeactMsg] = useState<any>(null);

  const getProfile = async () => run("Loading profile...", async () => {
    const data = await apiFetch("/user/profile");
    setProfile(data);
  });
  const updateProfile = async () => run("Updating profile...", async () => {
    const data = await apiFetch("/user/update-profile", { method: "PUT", body: JSON.stringify(updProfile) });
    setProfileMsg(data);
  });
  const updatePassword = async () => run("Updating password...", async () => {
    const data = await apiFetch("/user/update-password", { method: "PUT", body: JSON.stringify(updPwd) });
    setPwdMsg(data);
  });
  const deactivateOwn = async () => run("Deactivating account...", async () => {
    const data = await apiFetch("/user/deactivate-own", { method: "POST", body: JSON.stringify(deact) });
    setDeactMsg(data);
  });

  /* ---------- Seats ---------- */
  const [seatQuery, setSeatQuery] = useState({ showId: "" });
  const [availableSeats, setAvailableSeats] = useState<any>(null);
  const getAvailableSeats = async () => run("Loading seats...", async () => {
    const data = await apiFetch(`/api/user/shows/${seatQuery.showId}/seats/available`);
    setAvailableSeats(data);
  });

  /* ---------- Shows ---------- */
  const [showQuery, setShowQuery] = useState({ showId: "", title: "", theater: "", date: "", startDT: "" });
  const [showDetails, setShowDetails] = useState<any>(null);
  const [showsByTitle, setShowsByTitle] = useState<any>(null);
  const [showsByTheater, setShowsByTheater] = useState<any>(null);
  const [showsByDate, setShowsByDate] = useState<any>(null);
  const [showsByStartDT, setShowsByStartDT] = useState<any>(null);

  const getShowDetails = async () => run("Fetching show...", async () => {
    const data = await apiFetch(`/api/user/shows/${showQuery.showId}`);
    setShowDetails(data);
  });
  const byMovieTitle = async () => run("Fetching by movie title...", async () => {
    const data = await apiFetch(`/api/user/shows/by-movie?title=${encodeURIComponent(showQuery.title)}`);
    setShowsByTitle(data);
  });
  const byTheaterName = async () => run("Fetching by theater name...", async () => {
    const data = await apiFetch(`/api/user/shows/by-theater?name=${encodeURIComponent(showQuery.theater)}`);
    setShowsByTheater(data);
  });
  const byDate = async () => run("Fetching by date...", async () => {
    const data = await apiFetch(`/api/user/shows/by-date?date=${encodeURIComponent(showQuery.date)}`);
    setShowsByDate(data);
  });
  const byStartDateTime = async () => run("Fetching by start datetime...", async () => {
    const data = await apiFetch(`/api/user/shows/search/by-start-datetime?datetime=${encodeURIComponent(showQuery.startDT)}`);
    setShowsByStartDT(data);
  });

  /* ---------- Theaters ---------- */
  const [theaterQuery, setTheaterQuery] = useState({ id: "", location: "", name: "" });
  const [theatersAll, setTheatersAll] = useState<any>(null);
  const [theaterById, setTheaterById] = useState<any>(null);
  const [theatersByLoc, setTheatersByLoc] = useState<any>(null);
  const [theatersByName, setTheatersByName] = useState<any>(null);

  const getAllTheaters = async () => run("Loading theaters...", async () => {
    const data = await apiFetch("/api/theaters");
    setTheatersAll(data);
  });
  const getTheaterById = async () => run("Fetching theater...", async () => {
    const data = await apiFetch(`/api/theaters/${theaterQuery.id}`);
    setTheaterById(data);
  });
  const searchTheatersByLocation = async () => run("Searching theaters by location...", async () => {
    const data = await apiFetch(`/api/theaters/search?location=${encodeURIComponent(theaterQuery.location)}`);
    setTheatersByLoc(data);
  });
  const searchTheatersByName = async () => run("Searching theaters by name...", async () => {
    const data = await apiFetch(`/api/theaters/search-by-name?name=${encodeURIComponent(theaterQuery.name)}`);
    setTheatersByName(data);
  });

  /* ---------- Payments ---------- */
  const [paymentForm, setPaymentForm] = useState({ bookingId: "", amount: "", method: "" } as any);
  const [retryForm, setRetryForm] = useState({ paymentId: "", reason: "" } as any);
  const [payResp, setPayResp] = useState<any>(null);
  const [myPays, setMyPays] = useState<any>(null);
  const [payByBooking, setPayByBooking] = useState<any>(null);
  const [retryResp, setRetryResp] = useState<any>(null);

  const makePayment = async () => run("Making payment...", async () => {
    const data = await apiFetch("/api/user/payments", { method: "POST", body: JSON.stringify(paymentForm) });
    setPayResp(data);
  });
  const viewMyPayments = async () => run("Loading my payments...", async () => {
    const data = await apiFetch("/api/user/payments");
    setMyPays(data);
  });
  const getPaymentByBookingId = async () => run("Fetching payment by booking...", async () => {
    const data = await apiFetch(`/api/user/payments/booking/${paymentForm.bookingId}`);
    setPayByBooking(data);
  });
  const retryPayment = async () => run("Retrying payment...", async () => {
    const data = await apiFetch("/api/user/payments/retry", { method: "POST", body: JSON.stringify(retryForm) });
    setRetryResp(data);
  });

  /* ---------- Bookings ---------- */
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

  const getMyBookings = async () => run("Loading my bookings...", async () => {
    const data = await apiFetch("/api/user/bookings");
    setMyBookings(data);
  });
  const bookShow = async () => run("Booking show...", async () => {
    const body = { ...bookingForm.showBooking, showId: Number(bookingForm.showBooking.showId || 0) };
    const data = await apiFetch("/api/user/bookings/shows", { method: "POST", body: JSON.stringify(body) });
    setBookResp(data);
  });
  const bookSeats = async () => run("Booking seats...", async () => {
    const body = { ...bookingForm.bookSeats, showId: Number(bookingForm.bookSeats.showId || 0) };
    const data = await apiFetch("/api/user/bookings", { method: "POST", body: JSON.stringify(body) });
    setBookResp2(data);
  });
  const getBookedSeatsForMyBooking = async () => run("Fetching booked seats (mine)...", async () => {
    const data = await apiFetch(`/api/user/bookings/booked/${bookingForm.bookingIdForSeats}/seats`);
    setBookedSeatsForMyBooking(data);
  });
  const getBookedSeatsByBooking = async () => run("Fetching booked seats (by booking)...", async () => {
    const data = await apiFetch(`/api/user/bookings/${bookingForm.bookingIdForSeats}/seats`);
    setBookedSeatsByBooking(data);
  });
  const cancelBooking = async () => run("Cancelling booking...", async () => {
    const data = await apiFetch(`/api/user/bookings/${bookingForm.bookingIdForCancel}`, { method: "DELETE" });
    setCancelResp(data);
  });

  /* ---------- Receipt ---------- */
  const [receiptBookingId, setReceiptBookingId] = useState("");
  const downloadReceipt = async () => {
    try {
      setBusy("Downloading receipt...");
      setErrorMsg("");
      setOkMsg("");
      const token = getToken();
      const res = await fetch(`${API_BASE}/api/user/receipt/${receiptBookingId}`, {
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      if (!res.ok) throw new Error(`Failed (${res.status})`);
      const blob = await res.blob();
      const url = URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = `receipt_${receiptBookingId}.pdf`;
      document.body.appendChild(a);
      a.click();
      a.remove();
      URL.revokeObjectURL(url);
      setOkMsg("Receipt downloaded.");
    } catch (e: any) {
      setErrorMsg(e?.message || "Download failed");
    } finally {
      setBusy("");
    }
  };

  /* ---------- Movies ---------- */
  const [movieQuery, setMovieQuery] = useState({ id: "", name: "", genre: "", language: "" });
  const [moviesAll, setMoviesAll] = useState<any>(null);
  const [movieById, setMovieById] = useState<any>(null);
  const [moviesSearch, setMoviesSearch] = useState<any>(null);
  const [nowShowing, setNowShowing] = useState<any>(null);
  const [upcoming, setUpcoming] = useState<any>(null);

  const getAllMoviesForUsers = async () => run("Loading movies...", async () => {
    const data = await apiFetch("/api/movies");
    setMoviesAll(data);
  });
  const getMovieByIdForUsers = async () => run("Fetching movie...", async () => {
    const data = await apiFetch(`/api/movies/${movieQuery.id}`);
    setMovieById(data);
  });
  const searchMovies = async () => run("Searching movies...", async () => {
    const params = new URLSearchParams();
    if (movieQuery.name) params.set("name", movieQuery.name);
    if (movieQuery.genre) params.set("genre", movieQuery.genre);
    if (movieQuery.language) params.set("language", movieQuery.language);
    const data = await apiFetch(`/api/movies/search?${params.toString()}`);
    setMoviesSearch(data);
  });
  const getNowShowingMovies = async () => run("Loading now showing...", async () => {
    const data = await apiFetch("/api/movies/now-showing");
    setNowShowing(data);
  });
  const getUpcomingMovies = async () => run("Loading upcoming...", async () => {
    const data = await apiFetch("/api/movies/upcoming");
    setUpcoming(data);
  });

  return (
    <div className="page">
      {/* Top Bar */}
      <header className="topbar">
        <div className="brand">
          <div className="badge">🎟️</div>
          <div>
            <h1>User Dashboard</h1>
            <p className="muted">{busy ? `(${busy})` : "Ready"}</p>
          </div>
        </div>
        <div className="actions">
          <Btn label="Home" variant="secondary" onClick={() => router.push("/")} disabled={!!busy} />
          <Btn
            label="Logout"
            variant="danger"
            onClick={() => { localStorage.removeItem("token"); router.replace("/login"); }}
            disabled={!!busy}
          />
        </div>
      </header>

      <main className="container">
        {errorMsg && <div className="banner error">{errorMsg}</div>}
        {okMsg && <div className="banner ok">{okMsg}</div>}

        {/* one clean vertical stack */}
        <div className="stack">
          {/* PROFILE */}
          <Section title="My Profile">
            <div className="vstack">
              <Row>
                <Btn label="Get Profile (GET)" onClick={getProfile} disabled={!!busy} />
              </Row>
              <Row>
                <Input placeholder="Name" value={updProfile.name} onChange={(e) => setUpdProfile({ ...updProfile, name: e.target.value })} />
                <Input placeholder="Phone" value={updProfile.phone} onChange={(e) => setUpdProfile({ ...updProfile, phone: e.target.value })} />
              </Row>
              <Row>
                <Input placeholder="City" value={updProfile.city} onChange={(e) => setUpdProfile({ ...updProfile, city: e.target.value })} />
                <Btn label="Update Profile (PUT)" onClick={updateProfile} disabled={!!busy} />
              </Row>
              <Row>
                <Input placeholder="Old Password" type="password" value={updPwd.oldPassword} onChange={(e) => setUpdPwd({ ...updPwd, oldPassword: e.target.value })} />
                <Input placeholder="New Password" type="password" value={updPwd.newPassword} onChange={(e) => setUpdPwd({ ...updPwd, newPassword: e.target.value })} />
                <Btn label="Update Password (PUT)" onClick={updatePassword} disabled={!!busy} />
              </Row>
              <Row>
                <Input placeholder="Email (confirm to deactivate)" value={deact.email} onChange={(e) => setDeact({ email: e.target.value })} />
                <Btn label="Deactivate Own Account (POST)" variant="danger" onClick={deactivateOwn} disabled={!!busy} />
              </Row>
              <Result data={profile || profileMsg || pwdMsg || deactMsg} title="Profile Response" />
            </div>
          </Section>

          {/* SEATS */}
          <Section title="Seats (Available)">
            <Row>
              <Input placeholder="Show ID" value={seatQuery.showId} onChange={(e) => setSeatQuery({ showId: e.target.value })} />
              <Btn label="Get Available Seats (GET)" onClick={getAvailableSeats} disabled={!!busy} />
            </Row>
            <Result data={availableSeats} title="Available Seats" />
          </Section>

          {/* SHOWS */}
          <Section title="Shows">
            <div className="vstack">
              <Row>
                <Input placeholder="Show ID (details)" value={showQuery.showId} onChange={(e) => setShowQuery({ ...showQuery, showId: e.target.value })} />
                <Btn label="Show Details (GET)" onClick={getShowDetails} disabled={!!busy} />
              </Row>
              <Row>
                <Input placeholder="Movie Title" value={showQuery.title} onChange={(e) => setShowQuery({ ...showQuery, title: e.target.value })} />
                <Btn label="By Movie Title (GET)" onClick={byMovieTitle} disabled={!!busy} />
              </Row>
              <Row>
                <Input placeholder="Theater Name" value={showQuery.theater} onChange={(e) => setShowQuery({ ...showQuery, theater: e.target.value })} />
                <Btn label="By Theater Name (GET)" onClick={byTheaterName} disabled={!!busy} />
              </Row>
              <Row>
                <Input placeholder="Date (YYYY-MM-DD)" value={showQuery.date} onChange={(e) => setShowQuery({ ...showQuery, date: e.target.value })} />
                <Btn label="By Date (GET)" onClick={byDate} disabled={!!busy} />
              </Row>
              <Row>
                <Input placeholder="Start Datetime (YYYY-MM-DDTHH:mm:ss)" value={showQuery.startDT} onChange={(e) => setShowQuery({ ...showQuery, startDT: e.target.value })} />
                <Btn label="By Start Datetime (GET)" onClick={byStartDateTime} disabled={!!busy} />
              </Row>
              <Result data={showDetails || showsByTitle || showsByTheater || showsByDate || showsByStartDT} title="Shows Response" />
            </div>
          </Section>

          {/* THEATERS */}
          <Section title="Theaters">
            <div className="vstack">
              <Row>
                <Btn label="All Theaters (GET)" onClick={getAllTheaters} disabled={!!busy} />
              </Row>
              <Row>
                <Input placeholder="Theater ID" value={theaterQuery.id} onChange={(e) => setTheaterQuery({ ...theaterQuery, id: e.target.value })} />
                <Btn label="Get By ID (GET)" onClick={getTheaterById} disabled={!!busy} />
              </Row>
              <Row>
                <Input placeholder="Location" value={theaterQuery.location} onChange={(e) => setTheaterQuery({ ...theaterQuery, location: e.target.value })} />
                <Btn label="By Location (GET)" onClick={searchTheatersByLocation} disabled={!!busy} />
              </Row>
              <Row>
                <Input placeholder="Name" value={theaterQuery.name} onChange={(e) => setTheaterQuery({ ...theaterQuery, name: e.target.value })} />
                <Btn label="By Name (GET)" onClick={searchTheatersByName} disabled={!!busy} />
              </Row>
              <Result data={theatersAll || theaterById || theatersByLoc || theatersByName} title="Theaters Response" />
            </div>
          </Section>

          {/* PAYMENTS */}
          <Section title="Payments">
            <div className="vstack">
              <Row>
                <Input placeholder="Booking ID" value={paymentForm.bookingId} onChange={(e) => setPaymentForm({ ...paymentForm, bookingId: e.target.value })} />
                <Input placeholder="Amount" type="number" value={paymentForm.amount || ""} onChange={(e) => setPaymentForm({ ...paymentForm, amount: e.target.value })} />
                <Input placeholder="Method" value={paymentForm.method || ""} onChange={(e) => setPaymentForm({ ...paymentForm, method: e.target.value })} />
              </Row>
              <Row>
                <Btn label="Make Payment (POST)" onClick={makePayment} disabled={!!busy} />
                <Btn label="My Payments (GET)" onClick={viewMyPayments} disabled={!!busy} />
                <Btn label="Payment by Booking (GET)" onClick={getPaymentByBookingId} disabled={!!busy} />
              </Row>
              <Row>
                <Input placeholder="Payment ID (retry)" value={retryForm.paymentId || ""} onChange={(e) => setRetryForm({ ...retryForm, paymentId: e.target.value })} />
                <Input placeholder="Reason" value={retryForm.reason || ""} onChange={(e) => setRetryForm({ ...retryForm, reason: e.target.value })} />
                <Btn label="Retry (POST)" onClick={retryPayment} disabled={!!busy} />
              </Row>
              <Result data={payResp || myPays || payByBooking || retryResp} title="Payments Response" />
            </div>
          </Section>

          {/* BOOKINGS */}
          <Section title="Bookings">
            <div className="vstack">
              <h3 className="h3">Book</h3>
              <Row>
                <Input placeholder="Show ID (book show)" value={bookingForm.showBooking.showId} onChange={(e) => setBookingForm({ ...bookingForm, showBooking: { ...bookingForm.showBooking, showId: e.target.value } })} />
                <Input
                  placeholder="Seat IDs (comma separated)"
                  onChange={(e) =>
                    setBookingForm({
                      ...bookingForm,
                      showBooking: { ...bookingForm.showBooking, seatIds: e.target.value.split(",").map(s => Number(s.trim())).filter(Boolean) },
                    })
                  }
                />
                <Btn label="Book Show (POST)" onClick={bookShow} disabled={!!busy} />
              </Row>
              <Row>
                <Input placeholder="Show ID (book seats)" value={bookingForm.bookSeats.showId} onChange={(e) => setBookingForm({ ...bookingForm, bookSeats: { ...bookingForm.bookSeats, showId: e.target.value } })} />
                <Input
                  placeholder="Seat IDs (comma separated)"
                  onChange={(e) =>
                    setBookingForm({
                      ...bookingForm,
                      bookSeats: { ...bookingForm.bookSeats, seatIds: e.target.value.split(",").map(s => Number(s.trim())).filter(Boolean) },
                    })
                  }
                />
                <Btn label="Book Seats (POST)" onClick={bookSeats} disabled={!!busy} />
              </Row>

              <h3 className="h3">Manage</h3>
              <Row>
                <Btn label="My Bookings (GET)" onClick={getMyBookings} disabled={!!busy} />
              </Row>
              <Row>
                <Input placeholder="Booking ID (for seats)" value={bookingForm.bookingIdForSeats} onChange={(e) => setBookingForm({ ...bookingForm, bookingIdForSeats: e.target.value })} />
                <Btn label="Booked Seats (My Booking) (GET)" onClick={getBookedSeatsForMyBooking} disabled={!!busy} />
              </Row>
              <Row>
                <Input placeholder="Booking ID (by booking / cancel)" value={bookingForm.bookingIdForCancel} onChange={(e) => setBookingForm({ ...bookingForm, bookingIdForCancel: e.target.value })} />
                <Btn label="Booked Seats (By Booking) (GET)" onClick={getBookedSeatsByBooking} disabled={!!busy} />
                <Btn label="Cancel Booking (DELETE)" variant="danger" onClick={cancelBooking} disabled={!!busy} />
              </Row>
              <Result
                data={myBookings || bookedSeatsForMyBooking || bookedSeatsByBooking || bookResp || bookResp2 || cancelResp}
                title="Bookings Response"
              />
            </div>
          </Section>

          {/* RECEIPT */}
          <Section title="Receipt">
            <Row>
              <Input placeholder="Booking ID" value={receiptBookingId} onChange={(e) => setReceiptBookingId(e.target.value)} />
              <Btn label="Download (GET)" onClick={downloadReceipt} disabled={!!busy} />
            </Row>
          </Section>
        </div>

        <footer className="foot">
          All requests include <code>Authorization: Bearer &lt;token&gt;</code>.
        </footer>
      </main>

      {/* ---------------- Styles ---------------- */}
      <style jsx global>{`
        :root{
          --bg:#0b0f17; --ink:#eaeef5; --muted:#9aa3b2;
          --panel:rgba(255,255,255,.06); --stroke:rgba(255,255,255,.12);
          --gold:#f5b942; --gold-2:#ffd97a; --focus:#8ea0ff;
          --danger:#ef4444; --danger-600:#dc2626;
          --code-bg:#0a0e1a; --code-border:#1f2740;
          --shadow:0 14px 40px rgba(0,0,0,.45);
        }
        html,body{ background:var(--bg); color:var(--ink); font-family: Inter, ui-sans-serif, system-ui,-apple-system, Segoe UI, Roboto, Helvetica, Arial; }
        *{ box-sizing:border-box; }

        .page{ min-height:100dvh; }
        .topbar{
          position:sticky; top:0; z-index:10;
          display:flex; align-items:center; justify-content:space-between;
          padding:12px 16px; border-bottom:1px solid var(--stroke);
          background:linear-gradient(180deg, rgba(10,14,22,.88), rgba(10,14,22,.70));
          backdrop-filter: blur(10px) saturate(120%);
        }
        .brand{ display:flex; gap:12px; align-items:center; }
        .badge{ width:36px; height:36px; border-radius:12px; display:grid; place-items:center;
                background: radial-gradient(120% 120% at 30% 30%, rgba(245,185,66,.35), rgba(142,160,255,.20));
                border:1px solid var(--stroke); }
        .topbar h1{ margin:0; font-size:18px; font-weight:800; letter-spacing:.3px; }
        .muted{ color:var(--muted); font-size:12px; margin:2px 0 0; }
        .actions{ display:flex; gap:10px; }

        .container{ max-width:900px; margin:20px auto 60px; padding:0 14px; }

        .banner{ padding:10px 12px; border-radius:14px; margin:12px 0 16px; border:1px solid; box-shadow: var(--shadow); }
        .banner.error{ background: rgba(239,68,68,0.12); color:#fecaca; border-color: rgba(239,68,68,.35); }
        .banner.ok{ background: rgba(34,197,94,.12); color:#bbf7d0; border-color: rgba(34,197,94,.35); }

        .stack{ display:grid; gap:18px; }

        .card{
          border:1px solid var(--stroke);
          background: linear-gradient(180deg, rgba(255,255,255,.08), rgba(255,255,255,.04)), var(--panel);
          border-radius:20px; box-shadow: var(--shadow);
          overflow:hidden; backdrop-filter: blur(10px) saturate(120%);
        }
        .card-head{ padding:12px 16px; border-bottom:1px solid var(--stroke); background:linear-gradient(180deg, rgba(255,255,255,.06), rgba(255,255,255,.03)); }
        .card-head h2{ margin:0; font-size:16px; font-weight:800; letter-spacing:.3px; }
        .card-body{ padding:14px 16px; display:grid; gap:12px; }
        .vstack{ display:grid; gap:12px; }
        .row{ display:grid; grid-template-columns: 1fr auto; gap:10px; align-items:end; }

        .inp{
          padding:12px; width:100%;
          border:1px solid var(--stroke); border-radius:14px;
          background: rgba(10,12,18,.55); color:var(--ink);
          outline:0; transition:border .15s, box-shadow .15s, background .15s;
        }
        .inp::placeholder{ color:#93a4bf; }
        .inp:focus{ border-color: rgba(142,160,255,.75); box-shadow: 0 0 0 4px rgba(142,160,255,.18); }

        .btn{
          padding:12px 16px; border-radius:14px; font-weight:800; cursor:pointer;
          border:1px solid transparent; letter-spacing:.2px;
          transition: background .15s, transform .05s, box-shadow .15s, border-color .15s;
          box-shadow: 0 8px 20px rgba(0,0,0,.35);
        }
        .btn:active{ transform: translateY(1px); }
        .btn.primary{
          background: linear-gradient(180deg, var(--gold-2), var(--gold));
          color:#0b0f17; border-color: var(--gold);
          box-shadow: 0 10px 24px rgba(245,185,66,.35);
        }
        .btn.primary:hover{ filter: brightness(1.06); box-shadow: 0 12px 28px rgba(245,185,66,.50); }
        .btn.secondary{
          background: rgba(255,255,255,.10); color:var(--ink); border-color: var(--stroke);
        }
        .btn.secondary:hover{ border-color: rgba(142,160,255,.65); box-shadow: 0 0 0 4px rgba(142,160,255,.14); }
        .btn.danger{
          background: linear-gradient(180deg, #ff9b9b, var(--danger)); color:#fff; border-color: var(--danger);
          box-shadow: 0 10px 24px rgba(239,68,68,.30);
        }
        .btn.danger:hover{ background: linear-gradient(180deg, #ffb4b4, var(--danger-600)); }
        .btn.disabled{ opacity:.6; cursor:not-allowed; box-shadow:none; }

        .h3{ margin: 4px 0; font-size: 13px; color: var(--muted); font-weight: 900; letter-spacing:.4px; text-transform: uppercase; }

        /* Result viewer styles */
        .result-table-wrap {
          border: 1px solid var(--stroke);
          border-radius: 14px;
          overflow: hidden;
          background: rgba(255,255,255,0.04);
        }
        .result-head {
          padding: 10px 12px;
          border-bottom: 1px solid var(--stroke);
          background: linear-gradient(180deg, rgba(255,255,255,.06), rgba(255,255,255,.03));
          font-weight: 800; letter-spacing: .3px;
        }
        .result-table { width: 100%; border-collapse: collapse; font-size: 14px; }
        .result-table thead th {
          text-align: left; padding: 10px 12px; color: var(--ink);
          background: rgba(255,255,255,0.04); border-bottom: 1px solid var(--stroke);
          position: sticky; top: 0; z-index: 1;
        }
        .result-table tbody td { padding: 10px 12px; border-top: 1px solid rgba(255,255,255,0.06); vertical-align: top; }
        .result-table tbody tr:nth-child(odd) td { background: rgba(255,255,255,0.03); }
        .result-meta { padding: 8px 12px; color: var(--muted); font-size: 12px; border-top: 1px solid var(--stroke); }

        .result-card { border: 1px solid var(--stroke); border-radius: 14px; background: rgba(255,255,255,0.04); overflow: hidden; }
        .kv { display: grid; gap: 0; }
        .kv-row { display: grid; grid-template-columns: 220px 1fr; border-top: 1px solid rgba(255,255,255,0.06); }
        .kv-row:first-child { border-top: 0; }
        .kv-row dt { padding: 10px 12px; color: var(--muted); background: rgba(255,255,255,0.03); }
        .kv-row dd { padding: 10px 12px; margin: 0; }
        @media (max-width: 560px){
          .kv-row { grid-template-columns: 1fr; }
          .kv-row dt { border-bottom: 1px solid rgba(255,255,255,0.06); }
        }

        .result-empty, .result-plain {
          border: 1px dashed var(--stroke);
          border-radius: 14px;
          padding: 12px;
          color: var(--muted);
          background: rgba(255,255,255,0.03);
        }
        .badge { display: inline-block; padding: 2px 8px; font-size: 12px; border-radius: 999px; border: 1px solid rgba(255,255,255,0.18); background: rgba(255,255,255,0.08); }
        .badge.ok { border-color: rgba(34,197,94,.45); background: rgba(34,197,94,.18); color: #bbf7d0; }
        .badge.bad { border-color: rgba(239,68,68,.45); background: rgba(239,68,68,.18); color: #fecaca; }
        .badge.warn { border-color: rgba(234,179,8,.45); background: rgba(234,179,8,.18); color: #fde68a; }

        .mono { font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace; }
        .tiny { font-size: 12px; }
        .foot{ margin: 18px 0 60px; color: var(--muted); font-size: 13px; text-align: center; }
        code{ background: rgba(142,160,255,.15); padding: 2px 6px; border-radius: 6px; border: 1px solid rgba(142,160,255,.25); }
      `}</style>
    </div>
  );
}

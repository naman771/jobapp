import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Upload, FileText, Briefcase, Plus, Trash2, Eye, CheckCircle, XCircle, Clock } from 'lucide-react';
import { useAuth } from '../AuthContext';
import {
  getMyResumes, uploadResume,
  getMyApplications,
  getMyJobs, createJob, deleteJob,
  getJobApplications, acknowledgeApplication, rejectApplication,
} from '../api';

export default function Dashboard() {
  const { user } = useAuth();
  const navigate = useNavigate();

  if (!user) {
    navigate('/login');
    return null;
  }

  return user.role === 'RECRUITER' ? <RecruiterDashboard user={user} /> : <CandidateDashboard user={user} />;
}

/* Helper: extract skills array from a resume object */
function getResumeSkills(resume) {
  if (!resume || !resume.parsedJson) return [];
  try {
    const data = typeof resume.parsedJson === 'string' ? JSON.parse(resume.parsedJson) : resume.parsedJson;
    return Array.isArray(data.skills) ? data.skills : [];
  } catch {
    return [];
  }
}

/* Helper: extract a display name from a resume */
function getResumeDisplayName(resume) {
  if (!resume) return 'Unknown Resume';
  // fileUrl is like "uploads/1234567_myresume.pdf"
  if (resume.fileUrl) {
    const parts = resume.fileUrl.split('/');
    const filename = parts[parts.length - 1];
    // Strip the timestamp prefix
    const idx = filename.indexOf('_');
    return idx > 0 ? filename.substring(idx + 1) : filename;
  }
  return `Resume #${resume.id}`;
}

/* ===== CANDIDATE ===== */
function CandidateDashboard({ user }) {
  const [tab, setTab] = useState('resumes');
  const [resumes, setResumes] = useState([]);
  const [applications, setApplications] = useState([]);
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  useEffect(() => {
    getMyResumes().then(setResumes).catch(() => {});
    getMyApplications().then(setApplications).catch(() => {});
  }, []);

  async function handleUpload(e) {
    const file = e.target.files[0];
    if (!file) return;
    setUploading(true);
    setError('');
    setMessage('');
    try {
      const r = await uploadResume(file);
      setResumes(prev => [...prev, r]);
      setMessage('Resume uploaded and parsed successfully!');
    } catch (err) {
      setError(err.message);
    } finally {
      setUploading(false);
      e.target.value = '';
    }
  }

  function statusBadge(status) {
    const s = (status || 'PENDING').toUpperCase();
    const cls = s === 'ACKNOWLEDGED' ? 'badge-acknowledged' : s === 'REJECTED' ? 'badge-rejected' : 'badge-pending';
    const icon = s === 'ACKNOWLEDGED' ? <CheckCircle size={12} /> : s === 'REJECTED' ? <XCircle size={12} /> : <Clock size={12} />;
    return <span className={`badge ${cls}`}>{icon} {s}</span>;
  }

  return (
    <div className="page">
      <div className="container">
        <h1 className="mb-1">Dashboard</h1>
        <p className="text-muted text-sm mb-3">Welcome, {user.name}</p>

        <div className="stats-row">
          <div className="stat-card">
            <div className="stat-value">{resumes.length}</div>
            <div className="stat-label">Resumes</div>
          </div>
          <div className="stat-card">
            <div className="stat-value">{applications.length}</div>
            <div className="stat-label">Applications</div>
          </div>
          <div className="stat-card">
            <div className="stat-value">
              {applications.filter(a => {
                const st = a.application?.status || a.status || '';
                return st.toUpperCase() === 'ACKNOWLEDGED';
              }).length}
            </div>
            <div className="stat-label">Acknowledged</div>
          </div>
        </div>

        <div className="tabs">
          <button className={`tab ${tab === 'resumes' ? 'active' : ''}`} onClick={() => setTab('resumes')}>
            Resumes
          </button>
          <button className={`tab ${tab === 'applications' ? 'active' : ''}`} onClick={() => setTab('applications')}>
            Applications
          </button>
        </div>

        {error && <p className="form-error mb-2">{error}</p>}
        {message && <p style={{ color: 'var(--success)', marginBottom: '1rem', fontWeight: 600, fontSize: '0.875rem' }}>{message}</p>}

        {tab === 'resumes' && (
          <>
            <label className="btn btn-primary mb-3" style={{ cursor: 'pointer' }}>
              <Upload size={16} /> {uploading ? 'Uploading...' : 'Upload Resume (PDF)'}
              <input type="file" accept=".pdf" onChange={handleUpload} hidden disabled={uploading} />
            </label>

            {resumes.length === 0 ? (
              <div className="empty-state">
                <FileText size={48} />
                <h3>No resumes yet</h3>
                <p className="text-sm text-muted mt-1">Upload your resume to get started</p>
              </div>
            ) : (
              <div className="grid" style={{ gap: '0.75rem' }}>
                {resumes.map(r => {
                  const skills = getResumeSkills(r);
                  return (
                    <div className="card" key={r.id}>
                      <div className="flex items-center gap-1">
                        <FileText size={18} />
                        <h4>{getResumeDisplayName(r)}</h4>
                      </div>
                      {skills.length > 0 && (
                        <div className="tags mt-2">
                          {skills.slice(0, 10).map((s, i) => <span className="tag" key={i}>{s}</span>)}
                        </div>
                      )}
                    </div>
                  );
                })}
              </div>
            )}
          </>
        )}

        {tab === 'applications' && (
          applications.length === 0 ? (
            <div className="empty-state">
              <Briefcase size={48} />
              <h3>No applications yet</h3>
              <p className="text-sm text-muted mt-1">Apply to jobs to see your applications here</p>
            </div>
          ) : (
            <div className="table-wrap">
              <table className="table">
                <thead>
                  <tr>
                    <th>Job</th>
                    <th>Company</th>
                    <th>Match</th>
                    <th>Status</th>
                    <th>Applied</th>
                  </tr>
                </thead>
                <tbody>
                  {applications.map(dto => {
                    // Backend returns ApplicationDTO: { application, resume, user, job }
                    const app = dto.application || dto;
                    const job = dto.job || {};
                    const appId = app.id || app.applicationId;
                    const appliedAt = app.appliedAt;
                    return (
                      <tr key={appId}>
                        <td style={{ fontWeight: 600 }}>{job.title || 'N/A'}</td>
                        <td>{job.company || 'N/A'}</td>
                        <td>
                          {app.matchScore != null ? (
                            <span className="match-score">{Math.round(app.matchScore)}%</span>
                          ) : '—'}
                        </td>
                        <td>{statusBadge(app.status)}</td>
                        <td className="text-muted text-sm">
                          {appliedAt ? new Date(appliedAt).toLocaleDateString() : '—'}
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )
        )}
      </div>
    </div>
  );
}

/* ===== RECRUITER ===== */
function RecruiterDashboard({ user }) {
  const [jobs, setJobs] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({ title: '', company: '', location: '', skills: '', description: '', salaryRange: '', jobType: 'Full-time' });
  const [creating, setCreating] = useState(false);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  // Applicant viewer
  const [viewingJob, setViewingJob] = useState(null);
  const [applicants, setApplicants] = useState([]);
  const [loadingApps, setLoadingApps] = useState(false);

  useEffect(() => {
    getMyJobs().then(setJobs).catch(() => {});
  }, []);

  async function handleCreate(e) {
    e.preventDefault();
    setCreating(true);
    setError('');
    try {
      const job = await createJob(form);
      setJobs(prev => [job, ...prev]);
      setShowForm(false);
      setForm({ title: '', company: '', location: '', skills: '', description: '', salaryRange: '', jobType: 'Full-time' });
      setMessage('Job posted successfully!');
    } catch (err) {
      setError(err.message);
    } finally {
      setCreating(false);
    }
  }

  async function handleDelete(id) {
    if (!confirm('Delete this job?')) return;
    try {
      await deleteJob(id);
      setJobs(prev => prev.filter(j => j.id !== id));
    } catch (err) {
      setError(err.message);
    }
  }

  async function viewApplicants(job) {
    setViewingJob(job);
    setLoadingApps(true);
    try {
      const apps = await getJobApplications(job.id);
      setApplicants(apps || []);
    } catch {
      setApplicants([]);
    } finally {
      setLoadingApps(false);
    }
  }

  async function handleAcknowledge(appId) {
    try {
      await acknowledgeApplication(appId);
      setApplicants(prev => prev.map(dto => {
        const app = dto.application || dto;
        if ((app.id || app.applicationId) === appId) {
          return { ...dto, application: { ...app, status: 'ACKNOWLEDGED' } };
        }
        return dto;
      }));
    } catch (err) {
      setError(err.message);
    }
  }

  async function handleReject(appId) {
    try {
      await rejectApplication(appId);
      setApplicants(prev => prev.map(dto => {
        const app = dto.application || dto;
        if ((app.id || app.applicationId) === appId) {
          return { ...dto, application: { ...app, status: 'REJECTED' } };
        }
        return dto;
      }));
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <div className="page">
      <div className="container">
        <h1 className="mb-1">Recruiter Dashboard</h1>
        <p className="text-muted text-sm mb-3">Welcome, {user.name}</p>

        <div className="stats-row">
          <div className="stat-card">
            <div className="stat-value">{jobs.length}</div>
            <div className="stat-label">Jobs Posted</div>
          </div>
        </div>

        {error && <p className="form-error mb-2">{error}</p>}
        {message && <p style={{ color: 'var(--success)', marginBottom: '1rem', fontWeight: 600, fontSize: '0.875rem' }}>{message}</p>}

        {/* Applicant viewer modal */}
        {viewingJob && (
          <div className="modal-overlay" onClick={() => setViewingJob(null)}>
            <div className="modal" style={{ maxWidth: 640 }} onClick={e => e.stopPropagation()}>
              <h2 style={{ marginBottom: '0.25rem' }}>Applicants — {viewingJob.title}</h2>
              <p className="text-muted text-sm mb-3">{viewingJob.company}</p>

              {loadingApps ? (
                <div className="flex items-center" style={{ justifyContent: 'center', padding: '2rem' }}><div className="spinner" /></div>
              ) : applicants.length === 0 ? (
                <p className="text-muted text-sm">No applicants yet.</p>
              ) : (
                <div className="table-wrap">
                  <table className="table">
                    <thead>
                      <tr>
                        <th>Candidate</th>
                        <th>Match</th>
                        <th>Status</th>
                        <th>Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {applicants.map(dto => {
                        // Backend returns ApplicationDTO: { application, resume, user }
                        const app = dto.application || dto;
                        const appUser = dto.user || {};
                        const appId = app.id || app.applicationId;
                        const st = (app.status || 'PENDING').toUpperCase();
                        return (
                          <tr key={appId}>
                            <td style={{ fontWeight: 600 }}>{appUser.name || appUser.email || 'Unknown'}</td>
                            <td>
                              {app.matchScore != null ? (
                                <span className="match-score">{Math.round(app.matchScore)}%</span>
                              ) : '—'}
                            </td>
                            <td>
                              <span className={`badge ${st === 'ACKNOWLEDGED' ? 'badge-acknowledged' : st === 'REJECTED' ? 'badge-rejected' : 'badge-pending'}`}>
                                {st}
                              </span>
                            </td>
                            <td>
                              {(st === 'PENDING' || st === 'APPLIED') && (
                                <div className="table-actions">
                                  <button className="btn btn-primary btn-sm" onClick={() => handleAcknowledge(appId)}>
                                    <CheckCircle size={13} /> Accept
                                  </button>
                                  <button className="btn btn-danger btn-sm" onClick={() => handleReject(appId)}>
                                    <XCircle size={13} /> Reject
                                  </button>
                                </div>
                              )}
                            </td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </table>
                </div>
              )}

              <button className="btn btn-secondary mt-3" onClick={() => setViewingJob(null)}>Close</button>
            </div>
          </div>
        )}

        {/* Post job form */}
        {showForm && (
          <div className="card mb-3">
            <h3 className="mb-2">Post a New Job</h3>
            <form onSubmit={handleCreate}>
              <div className="grid grid-2">
                <div className="form-group">
                  <label className="form-label">Title</label>
                  <input className="form-input" value={form.title} onChange={e => setForm({ ...form, title: e.target.value })} required />
                </div>
                <div className="form-group">
                  <label className="form-label">Company</label>
                  <input className="form-input" value={form.company} onChange={e => setForm({ ...form, company: e.target.value })} required />
                </div>
              </div>
              <div className="grid grid-2">
                <div className="form-group">
                  <label className="form-label">Location</label>
                  <input className="form-input" value={form.location} onChange={e => setForm({ ...form, location: e.target.value })} required />
                </div>
                <div className="form-group">
                  <label className="form-label">Salary Range</label>
                  <input className="form-input" value={form.salaryRange} onChange={e => setForm({ ...form, salaryRange: e.target.value })} placeholder="e.g. $80k - $120k" />
                </div>
              </div>
              <div className="grid grid-2">
                <div className="form-group">
                  <label className="form-label">Skills (comma-separated)</label>
                  <input className="form-input" value={form.skills} onChange={e => setForm({ ...form, skills: e.target.value })} required placeholder="Java, React, Python" />
                </div>
                <div className="form-group">
                  <label className="form-label">Job Type</label>
                  <select className="form-select" value={form.jobType} onChange={e => setForm({ ...form, jobType: e.target.value })}>
                    <option>Full-time</option>
                    <option>Part-time</option>
                    <option>Contract</option>
                    <option>Internship</option>
                    <option>Remote</option>
                  </select>
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">Description</label>
                <textarea className="form-textarea" value={form.description} onChange={e => setForm({ ...form, description: e.target.value })} required minLength={20} />
              </div>
              <div className="flex gap-1">
                <button type="submit" className="btn btn-primary" disabled={creating}>
                  {creating ? 'Posting...' : 'Post Job'}
                </button>
                <button type="button" className="btn btn-secondary" onClick={() => setShowForm(false)}>Cancel</button>
              </div>
            </form>
          </div>
        )}

        {!showForm && (
          <button className="btn btn-primary mb-3" onClick={() => setShowForm(true)}>
            <Plus size={16} /> Post New Job
          </button>
        )}

        {jobs.length === 0 ? (
          <div className="empty-state">
            <Briefcase size={48} />
            <h3>No jobs posted</h3>
            <p className="text-sm text-muted mt-1">Create your first job listing</p>
          </div>
        ) : (
          <div className="grid" style={{ gap: '0.75rem' }}>
            {jobs.map(job => (
              <div className="card" key={job.id}>
                <div className="card-header">
                  <div>
                    <h3 style={{ marginBottom: '0.25rem' }}>{job.title}</h3>
                    <p className="text-sm text-muted">{job.company} • {job.location}</p>
                  </div>
                  <div className="flex gap-1">
                    <button className="btn btn-secondary btn-sm" onClick={() => viewApplicants(job)}>
                      <Eye size={14} /> Applicants
                    </button>
                    <button className="btn btn-danger btn-sm" onClick={() => handleDelete(job.id)}>
                      <Trash2 size={14} />
                    </button>
                  </div>
                </div>
                {job.skills && (
                  <div className="tags">
                    {job.skills.split(',').slice(0, 5).map((s, i) => <span className="tag" key={i}>{s.trim()}</span>)}
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

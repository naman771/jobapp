import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { MapPin, Briefcase, Clock, DollarSign, ArrowLeft } from 'lucide-react';
import { getJob, applyToJob, getMyResumes } from '../api';
import { useAuth } from '../AuthContext';

export default function JobDetail() {
  const { id } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [job, setJob] = useState(null);
  const [loading, setLoading] = useState(true);
  const [applying, setApplying] = useState(false);
  const [resumes, setResumes] = useState([]);
  const [showApply, setShowApply] = useState(false);
  const [selectedResume, setSelectedResume] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    getJob(id).then(setJob).catch(() => navigate('/jobs')).finally(() => setLoading(false));
  }, [id]);

  async function handleApplyClick() {
    if (!user) {
      navigate('/login');
      return;
    }
    try {
      const r = await getMyResumes();
      setResumes(r || []);
      if (r && r.length > 0) setSelectedResume(r[0].id);
      setShowApply(true);
    } catch {
      setError('Failed to load resumes');
    }
  }

  async function handleApply() {
    if (!selectedResume) {
      setError('Please select a resume');
      return;
    }
    setApplying(true);
    setError('');
    try {
      await applyToJob(job.id, selectedResume);
      setMessage('Application submitted successfully!');
      setShowApply(false);
    } catch (err) {
      setError(err.message);
    } finally {
      setApplying(false);
    }
  }

  if (loading) return <div className="loading-page"><div className="spinner" /></div>;
  if (!job) return null;

  return (
    <div className="page">
      <div className="container" style={{ maxWidth: 800 }}>
        <button className="btn btn-secondary btn-sm mb-3" onClick={() => navigate(-1)}>
          <ArrowLeft size={14} /> Back
        </button>

        <div className="card">
          <div className="card-header">
            <div>
              <h1 style={{ marginBottom: '0.25rem' }}>{job.title}</h1>
              <p className="text-muted">{job.company}</p>
            </div>
            {job.jobType && <span className="tag">{job.jobType}</span>}
          </div>

          <div className="job-meta">
            {job.location && (
              <span className="job-meta-item"><MapPin size={15} /> {job.location}</span>
            )}
            {job.salaryRange && (
              <span className="job-meta-item"><DollarSign size={15} /> {job.salaryRange}</span>
            )}
            {job.createdAt && (
              <span className="job-meta-item"><Clock size={15} /> Posted {new Date(job.createdAt).toLocaleDateString()}</span>
            )}
          </div>

          {job.skills && (
            <div style={{ marginBottom: '1.5rem' }}>
              <h4 className="mb-1">Required Skills</h4>
              <div className="tags">
                {job.skills.split(',').map((s, i) => (
                  <span className="tag" key={i}>{s.trim()}</span>
                ))}
              </div>
            </div>
          )}

          <div style={{ marginBottom: '1.5rem' }}>
            <h4 className="mb-1">Description</h4>
            <p className="text-sm" style={{ whiteSpace: 'pre-wrap', color: 'var(--text-secondary)' }}>
              {job.description}
            </p>
          </div>

          {message && <p style={{ color: 'var(--success)', marginBottom: '1rem', fontWeight: 600 }}>{message}</p>}
          {error && <p className="form-error mb-2">{error}</p>}

          {!message && user?.role === 'CANDIDATE' && !showApply && (
            <button className="btn btn-primary btn-lg" onClick={handleApplyClick}>
              Apply Now
            </button>
          )}

          {showApply && (
            <div style={{ borderTop: '1px solid var(--border-color)', paddingTop: '1.5rem', marginTop: '0.5rem' }}>
              <h4 className="mb-2">Select Resume</h4>
              {resumes.length === 0 ? (
                <p className="text-sm text-muted">
                  No resumes uploaded yet.{' '}
                  <a href="/dashboard" style={{ textDecoration: 'underline' }}>Upload one from your dashboard</a>.
                </p>
              ) : (
                <>
                  <select
                    className="form-select mb-2"
                    value={selectedResume}
                    onChange={e => setSelectedResume(e.target.value)}
                  >
                    {resumes.map(r => (
                      <option key={r.id} value={r.id}>
                        {r.fileName || r.originalFilename || `Resume #${r.id}`}
                      </option>
                    ))}
                  </select>
                  <div className="flex gap-1">
                    <button className="btn btn-primary" onClick={handleApply} disabled={applying}>
                      {applying ? 'Submitting...' : 'Submit Application'}
                    </button>
                    <button className="btn btn-secondary" onClick={() => setShowApply(false)}>
                      Cancel
                    </button>
                  </div>
                </>
              )}
            </div>
          )}

          {!user && (
            <button className="btn btn-primary btn-lg" onClick={() => navigate('/login')}>
              Sign in to Apply
            </button>
          )}
        </div>
      </div>
    </div>
  );
}

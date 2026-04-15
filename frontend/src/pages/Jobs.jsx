import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Search, MapPin, Briefcase, Clock } from 'lucide-react';
import { searchJobs } from '../api';

export default function Jobs() {
  const [jobs, setJobs] = useState([]);
  const [query, setQuery] = useState('');
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    loadJobs();
  }, [page]);

  async function loadJobs(searchQuery) {
    setLoading(true);
    try {
      const data = await searchJobs(searchQuery || query, page);
      setJobs(data.content || []);
      setTotalPages(data.totalPages || 0);
    } catch {
      setJobs([]);
    } finally {
      setLoading(false);
    }
  }

  function handleSearch(e) {
    e.preventDefault();
    setPage(0);
    loadJobs(query);
  }

  return (
    <div className="page">
      <div className="container">
        <h1 className="mb-3">Job Listings</h1>

        <form onSubmit={handleSearch} className="search-bar">
          <input
            type="text"
            className="form-input"
            placeholder="Search by title, company, or skill..."
            value={query}
            onChange={e => setQuery(e.target.value)}
          />
          <button type="submit" className="btn btn-primary">
            <Search size={16} /> Search
          </button>
        </form>

        {loading ? (
          <div className="loading-page"><div className="spinner" /></div>
        ) : jobs.length === 0 ? (
          <div className="empty-state">
            <Briefcase size={48} />
            <h3>No jobs found</h3>
            <p className="text-sm text-muted mt-1">Try adjusting your search terms</p>
          </div>
        ) : (
          <>
            <div className="grid" style={{ gap: '0.75rem' }}>
              {jobs.map(job => (
                <Link to={`/jobs/${job.id}`} key={job.id} className="card" style={{ textDecoration: 'none' }}>
                  <div className="card-header">
                    <div>
                      <h3 style={{ marginBottom: '0.25rem' }}>{job.title}</h3>
                      <p className="text-sm text-muted">{job.company}</p>
                    </div>
                    {job.jobType && <span className="tag">{job.jobType}</span>}
                  </div>
                  <div className="flex gap-2" style={{ flexWrap: 'wrap' }}>
                    {job.location && (
                      <span className="text-sm text-muted flex items-center gap-1">
                        <MapPin size={13} /> {job.location}
                      </span>
                    )}
                    {job.salaryRange && (
                      <span className="text-sm text-muted">{job.salaryRange}</span>
                    )}
                    {job.createdAt && (
                      <span className="text-sm text-muted flex items-center gap-1">
                        <Clock size={13} /> {new Date(job.createdAt).toLocaleDateString()}
                      </span>
                    )}
                  </div>
                  {job.skills && (
                    <div className="tags mt-2">
                      {job.skills.split(',').slice(0, 5).map((s, i) => (
                        <span className="tag" key={i}>{s.trim()}</span>
                      ))}
                    </div>
                  )}
                </Link>
              ))}
            </div>

            {totalPages > 1 && (
              <div className="flex items-center gap-1 mt-3" style={{ justifyContent: 'center' }}>
                <button
                  className="btn btn-secondary btn-sm"
                  disabled={page === 0}
                  onClick={() => setPage(p => p - 1)}
                >
                  Previous
                </button>
                <span className="text-sm text-muted" style={{ padding: '0 0.75rem' }}>
                  Page {page + 1} of {totalPages}
                </span>
                <button
                  className="btn btn-secondary btn-sm"
                  disabled={page >= totalPages - 1}
                  onClick={() => setPage(p => p + 1)}
                >
                  Next
                </button>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}

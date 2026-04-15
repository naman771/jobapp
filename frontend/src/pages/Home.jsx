import { Link } from 'react-router-dom';
import { Search, Upload, Briefcase, ArrowRight } from 'lucide-react';
import { useAuth } from '../AuthContext';

export default function Home() {
  const { user } = useAuth();

  return (
    <div className="page">
      <div className="container">
        {/* Hero */}
        <section style={{ padding: '4rem 0 3rem', maxWidth: 640 }}>
          <h1 style={{ fontSize: '2.75rem', lineHeight: 1.1, letterSpacing: '-0.04em', marginBottom: '1rem' }}>
            Find your next opportunity.
          </h1>
          <p className="text-muted" style={{ fontSize: '1.125rem', lineHeight: 1.6, marginBottom: '2rem' }}>
            A job portal built with Spring Boot, React, and AI-powered resume parsing.
            Search openings, upload your resume, and get matched with the right roles.
          </p>
          <div className="flex gap-2">
            <Link to="/jobs" className="btn btn-primary btn-lg">
              Browse Jobs <ArrowRight size={16} />
            </Link>
            {!user && (
              <Link to="/register" className="btn btn-secondary btn-lg">
                Create Account
              </Link>
            )}
          </div>
        </section>

        {/* Features */}
        <section style={{ padding: '2rem 0 4rem' }}>
          <div className="grid grid-3">
            <div className="card">
              <Search size={24} style={{ marginBottom: '0.75rem' }} />
              <h3>Search & Filter</h3>
              <p className="text-muted text-sm mt-1">
                Browse job listings with keyword search. Filter by location, type, and required skills.
              </p>
            </div>
            <div className="card">
              <Upload size={24} style={{ marginBottom: '0.75rem' }} />
              <h3>AI Resume Parsing</h3>
              <p className="text-muted text-sm mt-1">
                Upload your resume and our AI extracts skills, experience, and contact info automatically.
              </p>
            </div>
            <div className="card">
              <Briefcase size={24} style={{ marginBottom: '0.75rem' }} />
              <h3>Smart Matching</h3>
              <p className="text-muted text-sm mt-1">
                Get a match score for each job based on your parsed resume skills versus job requirements.
              </p>
            </div>
          </div>
        </section>
      </div>
    </div>
  );
}

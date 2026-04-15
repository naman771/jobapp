const API_BASE = 'http://localhost:8080';

function getHeaders(auth = true) {
  const headers = { 'Content-Type': 'application/json' };
  if (auth) {
    const token = localStorage.getItem('token');
    if (token) headers['Authorization'] = `Bearer ${token}`;
  }
  return headers;
}

async function handleResponse(res) {
  const text = await res.text();
  let data = null;
  try {
    data = JSON.parse(text);
  } catch {
    // Response is not JSON (e.g. plain text error)
    if (!res.ok) throw new Error(text || `Request failed (${res.status})`);
    return text;
  }
  if (!res.ok) {
    const msg = data?.error || data?.message || `Request failed (${res.status})`;
    throw new Error(msg);
  }
  return data;
}

// Auth
export async function register(name, email, password, role) {
  const res = await fetch(`${API_BASE}/auth/register`, {
    method: 'POST',
    headers: getHeaders(false),
    body: JSON.stringify({ name, email, password, role }),
  });
  return handleResponse(res);
}

export async function login(email, password) {
  const res = await fetch(`${API_BASE}/auth/login`, {
    method: 'POST',
    headers: getHeaders(false),
    body: JSON.stringify({ email, password }),
  });
  return handleResponse(res);
}

export async function getMe() {
  const res = await fetch(`${API_BASE}/auth/me`, { headers: getHeaders() });
  return handleResponse(res);
}

// Jobs
export async function searchJobs(q = '', page = 0, size = 20) {
  const params = new URLSearchParams({ page, size });
  if (q) params.set('q', q);
  const res = await fetch(`${API_BASE}/jobs?${params}`, { headers: getHeaders(false) });
  return handleResponse(res);
}

export async function getJob(id) {
  const res = await fetch(`${API_BASE}/jobs/${id}`, { headers: getHeaders(false) });
  return handleResponse(res);
}

export async function getMyJobs() {
  const res = await fetch(`${API_BASE}/jobs/my`, { headers: getHeaders() });
  return handleResponse(res);
}

export async function createJob(job) {
  const res = await fetch(`${API_BASE}/jobs`, {
    method: 'POST',
    headers: getHeaders(),
    body: JSON.stringify(job),
  });
  return handleResponse(res);
}

export async function deleteJob(id) {
  const res = await fetch(`${API_BASE}/jobs/${id}`, {
    method: 'DELETE',
    headers: getHeaders(),
  });
  return handleResponse(res);
}

export async function getRecommendedJobs() {
  const res = await fetch(`${API_BASE}/jobs/recommended`, { headers: getHeaders() });
  return handleResponse(res);
}

// Resume
export async function uploadResume(file) {
  const formData = new FormData();
  formData.append('file', file);
  const token = localStorage.getItem('token');
  const res = await fetch(`${API_BASE}/api/resume/upload`, {
    method: 'POST',
    headers: token ? { Authorization: `Bearer ${token}` } : {},
    body: formData,
  });
  return handleResponse(res);
}

export async function getMyResumes() {
  const res = await fetch(`${API_BASE}/api/resume/my`, { headers: getHeaders() });
  return handleResponse(res);
}

// Applications
export async function applyToJob(jobId, resumeId) {
  const res = await fetch(`${API_BASE}/applications/apply`, {
    method: 'POST',
    headers: getHeaders(),
    body: JSON.stringify({ jobId, resumeId }),
  });
  return handleResponse(res);
}

export async function getMyApplications() {
  const res = await fetch(`${API_BASE}/applications/my`, { headers: getHeaders() });
  return handleResponse(res);
}

export async function getJobApplications(jobId) {
  const res = await fetch(`${API_BASE}/applications/job/${jobId}`, { headers: getHeaders() });
  return handleResponse(res);
}

export async function acknowledgeApplication(applicationId) {
  const res = await fetch(`${API_BASE}/applications/${applicationId}/acknowledge`, {
    method: 'POST',
    headers: getHeaders(),
  });
  return handleResponse(res);
}

export async function rejectApplication(applicationId) {
  const res = await fetch(`${API_BASE}/applications/${applicationId}/reject`, {
    method: 'POST',
    headers: getHeaders(),
  });
  return handleResponse(res);
}

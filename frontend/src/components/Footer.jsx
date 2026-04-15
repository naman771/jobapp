import { Globe, BookOpen, Camera } from 'lucide-react';

export default function Footer() {
  return (
    <footer className="footer">
      <div className="container footer-inner">
        <span className="footer-copy">&copy; {new Date().getFullYear()} JobPortal</span>
        <div className="footer-links">
          <a href="https://linkedin.com/in/namanmishra771" target="_blank" rel="noopener noreferrer">
            <BookOpen size={14} /> LinkedIn
          </a>
          <a href="https://github.com/naman771" target="_blank" rel="noopener noreferrer">
            <Globe size={14} /> GitHub
          </a>
          <a href="https://instagram.com/naman.mishra_" target="_blank" rel="noopener noreferrer">
            <Camera size={14} /> Instagram
          </a>
        </div>
      </div>
    </footer>
  );
}

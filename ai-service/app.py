from flask import Flask, request, jsonify
import pdfplumber
import re
import os

app = Flask(__name__)

def extract_text_from_pdf(file):
    text = ""
    with pdfplumber.open(file) as pdf:
        for page in pdf.pages:
            text += page.extract_text() or ""
    return text

def extract_email(text):
    email_pattern = r'[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}'
    match = re.search(email_pattern, text)
    return match.group(0) if match else "Not Found"

def extract_phone(text):
    # Matches various phone formats like (123) 456-7890, 123-456-7890, 123 456 7890, +1 123 456 7890
    phone_pattern = r'(\+?\d{1,3}[-.\s]?)?(\(?\d{3}\)?[-.\s]?)?\d{3}[-.\s]?\d{4}'
    match = re.search(phone_pattern, text)
    return match.group(0) if match else "Not Found"

def extract_skills(text):
    # Expanded skill list
    common_skills = [
        "Java", "Python", "C++", "C#", "JavaScript", "TypeScript", "Angular", "React", "Vue", 
        "Spring Boot", "Node.js", "Django", "Flask", "SQL", "NoSQL", "MongoDB", "PostgreSQL", 
        "HTML", "CSS", "AWS", "Azure", "GCP", "Docker", "Kubernetes", "Git", "CI/CD",
        "Machine Learning", "AI", "Deep Learning", "NLP", "Rust", "Go", "Swift", "Kotlin",
        "Ruby", "PHP", "Terraform", "Ansible", "Jenkins", "Linux", "Unix"
    ]
    found_skills = []
    for skill in common_skills:
        # Escape the skill for regex
        escaped_skill = re.escape(skill)
        # Use a pattern that handles boundaries correctly for special chars
        # If skill starts with word char, use \b, else allow any non-word char before
        # If skill ends with word char, use \b, else allow any non-word char after
        
        start_bound = r'\b' if re.match(r'\w', skill[0]) else r'(?:^|[\s\(\[\{,])'
        # Allow punctuation after the skill if it ends with a symbol
        end_bound = r'\b' if re.match(r'\w', skill[-1]) else r'(?:$|[\s\.,;:\?!])'
        
        pattern = start_bound + escaped_skill + end_bound
        
        if re.search(pattern, text, re.IGNORECASE):
            found_skills.append(skill)
    return found_skills

@app.route('/parse', methods=['POST'])
def parse_resume():
    if 'file' not in request.files:
        return jsonify({"error": "No file part"}), 400
    
    file = request.files['file']
    if file.filename == '':
        return jsonify({"error": "No selected file"}), 400

    if file:
        try:
            text = extract_text_from_pdf(file)
            
            email = extract_email(text)
            phone = extract_phone(text)
            skills = extract_skills(text)
            
            # Simple name extraction heuristic (first line or first few words)
            lines = [line.strip() for line in text.split('\n') if line.strip()]
            name = lines[0] if lines else "Unknown"

            return jsonify({
                "name": name,
                "email": email,
                "phone": phone,
                "skills": skills,
                "experience": [], # Placeholder for now
                "education": [], # Placeholder for now
                "certifications": [] # Placeholder for now
            })
        except Exception as e:
            return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(port=5001, debug=True)

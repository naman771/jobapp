from fpdf import FPDF

# Create a professional sample resume
pdf = FPDF()
pdf.add_page()
pdf.set_font("Arial", 'B', 16)

# Name
pdf.cell(0, 10, "John Michael Smith", ln=True, align="C")
pdf.set_font("Arial", size=10)
pdf.cell(0, 5, "john.smith@email.com | +1-555-0123 | LinkedIn: linkedin.com/in/johnsmith", ln=True, align="C")
pdf.ln(5)

# Professional Summary
pdf.set_font("Arial", 'B', 12)
pdf.cell(0, 8, "PROFESSIONAL SUMMARY", ln=True)
pdf.set_font("Arial", size=10)
pdf.multi_cell(0, 5, "Senior Software Engineer with 5+ years of experience in full-stack development. Expertise in building scalable web applications using modern technologies. Proven track record of delivering high-quality solutions and leading development teams.")
pdf.ln(3)

# Skills
pdf.set_font("Arial", 'B', 12)
pdf.cell(0, 8, "TECHNICAL SKILLS", ln=True)
pdf.set_font("Arial", size=10)
pdf.multi_cell(0, 5, "Programming Languages: Java, Python, JavaScript, TypeScript\nFrameworks: Spring Boot, React, Angular, Node.js\nDatabases: MySQL, PostgreSQL, MongoDB\nTools: Git, Docker, Kubernetes, Jenkins, AWS")
pdf.ln(3)

# Experience
pdf.set_font("Arial", 'B', 12)
pdf.cell(0, 8, "PROFESSIONAL EXPERIENCE", ln=True)
pdf.set_font("Arial", 'B', 10)
pdf.cell(0, 6, "Senior Software Engineer - Tech Solutions Inc.", ln=True)
pdf.set_font("Arial", 'I', 9)
pdf.cell(0, 5, "January 2021 - Present", ln=True)
pdf.set_font("Arial", size=9)
pdf.multi_cell(0, 4, "- Led development of microservices architecture serving 1M+ users\n- Implemented CI/CD pipelines reducing deployment time by 60%\n- Mentored junior developers and conducted code reviews")
pdf.ln(2)

pdf.set_font("Arial", 'B', 10)
pdf.cell(0, 6, "Software Engineer - Digital Innovations Ltd.", ln=True)
pdf.set_font("Arial", 'I', 9)
pdf.cell(0, 5, "June 2019 - December 2020", ln=True)
pdf.set_font("Arial", size=9)
pdf.multi_cell(0, 4, "- Developed RESTful APIs using Spring Boot and Node.js\n- Built responsive web applications with React and Angular\n- Collaborated with cross-functional teams in Agile environment")
pdf.ln(3)

# Education
pdf.set_font("Arial", 'B', 12)
pdf.cell(0, 8, "EDUCATION", ln=True)
pdf.set_font("Arial", 'B', 10)
pdf.cell(0, 6, "Bachelor of Science in Computer Science", ln=True)
pdf.set_font("Arial", size=9)
pdf.cell(0, 5, "University of Technology, 2019", ln=True)
pdf.ln(3)

# Certifications
pdf.set_font("Arial", 'B', 12)
pdf.cell(0, 8, "CERTIFICATIONS", ln=True)
pdf.set_font("Arial", size=9)
pdf.cell(0, 5, "- AWS Certified Solutions Architect - Associate", ln=True)
pdf.cell(0, 5, "- Oracle Certified Professional, Java SE 11 Developer", ln=True)
pdf.cell(0, 5, "- Certified Kubernetes Administrator (CKA)", ln=True)

# Save the PDF
pdf.output("sample_resume.pdf")
print("Sample resume created: sample_resume.pdf")

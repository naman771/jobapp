from fpdf import FPDF

pdf = FPDF()
pdf.add_page()
pdf.set_font("Arial", size=12)
pdf.cell(200, 10, txt="John Doe", ln=1, align="C")
pdf.cell(200, 10, txt="john.doe@example.com", ln=1, align="C")
pdf.cell(200, 10, txt="555-0199", ln=1, align="C")
pdf.cell(200, 10, txt="Skills: Java, Python, Spring Boot", ln=1, align="L")
pdf.output("test_resume.pdf")

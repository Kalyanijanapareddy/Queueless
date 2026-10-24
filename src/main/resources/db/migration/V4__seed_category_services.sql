-- Add sample organizations and services for every homepage category.
-- This migration preserves existing records and uses stable registration/branch codes.

INSERT INTO organizations (category_id, name, description, registration_number, active)
SELECT c.id, 'Sunrise Medical Centre', 'Multi-speciality clinic and diagnostic services', 'QL-MED-2026', TRUE
FROM service_categories c
WHERE c.slug = 'medical'
  AND NOT EXISTS (SELECT 1 FROM organizations o WHERE o.registration_number = 'QL-MED-2026');

INSERT INTO organizations (category_id, name, description, registration_number, active)
SELECT c.id, 'SBI Hyderabad Service Centre', 'Banking and customer account services', 'QL-BANK-2026', TRUE
FROM service_categories c
WHERE c.slug = 'banking'
  AND NOT EXISTS (SELECT 1 FROM organizations o WHERE o.registration_number = 'QL-BANK-2026');

INSERT INTO organizations (category_id, name, description, registration_number, active)
SELECT c.id, 'Citizen Service Centre', 'Public documentation and citizen services', 'QL-GOV-2026', TRUE
FROM service_categories c
WHERE c.slug = 'government'
  AND NOT EXISTS (SELECT 1 FROM organizations o WHERE o.registration_number = 'QL-GOV-2026');

INSERT INTO organizations (category_id, name, description, registration_number, active)
SELECT c.id, 'Knowledge Learning Centre', 'Student admissions and campus services', 'QL-EDU-2026', TRUE
FROM service_categories c
WHERE c.slug = 'education'
  AND NOT EXISTS (SELECT 1 FROM organizations o WHERE o.registration_number = 'QL-EDU-2026');

INSERT INTO organizations (category_id, name, description, registration_number, active)
SELECT c.id, 'Community Service Hub', 'General public and community services', 'QL-OTHER-2026', TRUE
FROM service_categories c
WHERE c.slug = 'others'
  AND NOT EXISTS (SELECT 1 FROM organizations o WHERE o.registration_number = 'QL-OTHER-2026');

INSERT INTO branches (organization_id, name, branch_code, address_line, city, state, active)
SELECT o.id, 'Sunrise Main Branch', 'QL-MED-001', 'Main Road', 'Hyderabad', 'Telangana', TRUE
FROM organizations o WHERE o.registration_number = 'QL-MED-2026'
AND NOT EXISTS (SELECT 1 FROM branches b WHERE b.organization_id = o.id AND b.branch_code = 'QL-MED-001');

INSERT INTO branches (organization_id, name, branch_code, address_line, city, state, active)
SELECT o.id, 'SBI Customer Service Branch', 'QL-BANK-001', 'Market Road', 'Hyderabad', 'Telangana', TRUE
FROM organizations o WHERE o.registration_number = 'QL-BANK-2026'
AND NOT EXISTS (SELECT 1 FROM branches b WHERE b.organization_id = o.id AND b.branch_code = 'QL-BANK-001');

INSERT INTO branches (organization_id, name, branch_code, address_line, city, state, active)
SELECT o.id, 'Citizen Help Desk', 'QL-GOV-001', 'Collectorate Road', 'Hyderabad', 'Telangana', TRUE
FROM organizations o WHERE o.registration_number = 'QL-GOV-2026'
AND NOT EXISTS (SELECT 1 FROM branches b WHERE b.organization_id = o.id AND b.branch_code = 'QL-GOV-001');

INSERT INTO branches (organization_id, name, branch_code, address_line, city, state, active)
SELECT o.id, 'Knowledge Centre Campus', 'QL-EDU-001', 'College Road', 'Hyderabad', 'Telangana', TRUE
FROM organizations o WHERE o.registration_number = 'QL-EDU-2026'
AND NOT EXISTS (SELECT 1 FROM branches b WHERE b.organization_id = o.id AND b.branch_code = 'QL-EDU-001');

INSERT INTO branches (organization_id, name, branch_code, address_line, city, state, active)
SELECT o.id, 'Community Help Desk', 'QL-OTHER-001', 'Central Road', 'Hyderabad', 'Telangana', TRUE
FROM organizations o WHERE o.registration_number = 'QL-OTHER-2026'
AND NOT EXISTS (SELECT 1 FROM branches b WHERE b.organization_id = o.id AND b.branch_code = 'QL-OTHER-001');

INSERT INTO professionals (branch_id, full_name, designation, qualification, specialization, active)
SELECT b.id, 'Dr. Ananya Rao', 'Doctor', 'MBBS, MD', 'Pediatrics', TRUE
FROM branches b WHERE b.branch_code = 'QL-MED-001'
AND NOT EXISTS (SELECT 1 FROM professionals p WHERE p.branch_id = b.id AND p.full_name = 'Dr. Ananya Rao');

INSERT INTO professionals (branch_id, full_name, designation, qualification, specialization, active)
SELECT b.id, 'Dr. Sandeep Reddy', 'Doctor', 'MBBS, MD', 'Cardiology', TRUE
FROM branches b WHERE b.branch_code = 'QL-MED-001'
AND NOT EXISTS (SELECT 1 FROM professionals p WHERE p.branch_id = b.id AND p.full_name = 'Dr. Sandeep Reddy');

INSERT INTO professionals (branch_id, full_name, designation, qualification, specialization, active)
SELECT b.id, 'Dr. Meera Shah', 'Doctor', 'MBBS, DGO', 'Women’s Health', TRUE
FROM branches b WHERE b.branch_code = 'QL-MED-001'
AND NOT EXISTS (SELECT 1 FROM professionals p WHERE p.branch_id = b.id AND p.full_name = 'Dr. Meera Shah');

-- Medical services assigned to doctors
INSERT INTO services (branch_id, professional_id, name, description, estimated_duration_minutes, base_fee, active)
SELECT b.id, p.id, 'Pediatric Consultation', 'Child health consultation', 20, 400.00, TRUE
FROM branches b JOIN professionals p ON p.branch_id = b.id AND p.full_name = 'Dr. Ananya Rao'
WHERE b.branch_code = 'QL-MED-001'
AND NOT EXISTS (SELECT 1 FROM services s WHERE s.branch_id = b.id AND s.name = 'Pediatric Consultation');

INSERT INTO services (branch_id, professional_id, name, description, estimated_duration_minutes, base_fee, active)
SELECT b.id, p.id, 'Cardiology Consultation', 'Heart health assessment', 30, 700.00, TRUE
FROM branches b JOIN professionals p ON p.branch_id = b.id AND p.full_name = 'Dr. Sandeep Reddy'
WHERE b.branch_code = 'QL-MED-001'
AND NOT EXISTS (SELECT 1 FROM services s WHERE s.branch_id = b.id AND s.name = 'Cardiology Consultation');

INSERT INTO services (branch_id, professional_id, name, description, estimated_duration_minutes, base_fee, active)
SELECT b.id, p.id, 'Womens Health Consultation', 'General women health consultation', 25, 500.00, TRUE
FROM branches b JOIN professionals p ON p.branch_id = b.id AND p.full_name = 'Dr. Meera Shah'
WHERE b.branch_code = 'QL-MED-001'
AND NOT EXISTS (SELECT 1 FROM services s WHERE s.branch_id = b.id AND s.name = 'Womens Health Consultation');

-- Banking services
INSERT INTO services (branch_id, name, description, estimated_duration_minutes, base_fee, active)
SELECT b.id, 'Cash Deposit', 'Deposit cash into a bank account', 10, 0.00, TRUE FROM branches b
WHERE b.branch_code = 'QL-BANK-001' AND NOT EXISTS (SELECT 1 FROM services s WHERE s.branch_id=b.id AND s.name='Cash Deposit');
INSERT INTO services (branch_id, name, description, estimated_duration_minutes, base_fee, active)
SELECT b.id, 'Cash Withdrawal', 'Withdraw cash from an account', 10, 0.00, TRUE FROM branches b
WHERE b.branch_code = 'QL-BANK-001' AND NOT EXISTS (SELECT 1 FROM services s WHERE s.branch_id=b.id AND s.name='Cash Withdrawal');
INSERT INTO services (branch_id, name, description, estimated_duration_minutes, base_fee, active)
SELECT b.id, 'Account e-KYC', 'Update or verify customer KYC details', 20, 0.00, TRUE FROM branches b
WHERE b.branch_code = 'QL-BANK-001' AND NOT EXISTS (SELECT 1 FROM services s WHERE s.branch_id=b.id AND s.name='Account e-KYC');
INSERT INTO services (branch_id, name, description, estimated_duration_minutes, base_fee, active)
SELECT b.id, 'Fund Transfer Support', 'Assistance with account transfers', 15, 0.00, TRUE FROM branches b
WHERE b.branch_code = 'QL-BANK-001' AND NOT EXISTS (SELECT 1 FROM services s WHERE s.branch_id=b.id AND s.name='Fund Transfer Support');

-- Government services
INSERT INTO services (branch_id, name, description, estimated_duration_minutes, base_fee, active)
SELECT b.id, 'Income Certificate', 'Apply for or collect an income certificate', 20, 0.00, TRUE FROM branches b
WHERE b.branch_code = 'QL-GOV-001' AND NOT EXISTS (SELECT 1 FROM services s WHERE s.branch_id=b.id AND s.name='Income Certificate');
INSERT INTO services (branch_id, name, description, estimated_duration_minutes, base_fee, active)
SELECT b.id, 'Residence Certificate', 'Residence certificate application support', 20, 0.00, TRUE FROM branches b
WHERE b.branch_code = 'QL-GOV-001' AND NOT EXISTS (SELECT 1 FROM services s WHERE s.branch_id=b.id AND s.name='Residence Certificate');
INSERT INTO services (branch_id, name, description, estimated_duration_minutes, base_fee, active)
SELECT b.id, 'Document Verification', 'Verification and submission of documents', 15, 0.00, TRUE FROM branches b
WHERE b.branch_code = 'QL-GOV-001' AND NOT EXISTS (SELECT 1 FROM services s WHERE s.branch_id=b.id AND s.name='Document Verification');

-- Education services
INSERT INTO services (branch_id, name, description, estimated_duration_minutes, base_fee, active)
SELECT b.id, 'Admissions Enquiry', 'Admissions and course information', 15, 0.00, TRUE FROM branches b
WHERE b.branch_code = 'QL-EDU-001' AND NOT EXISTS (SELECT 1 FROM services s WHERE s.branch_id=b.id AND s.name='Admissions Enquiry');
INSERT INTO services (branch_id, name, description, estimated_duration_minutes, base_fee, active)
SELECT b.id, 'Fee Payment Support', 'Assistance with student fee payments', 10, 0.00, TRUE FROM branches b
WHERE b.branch_code = 'QL-EDU-001' AND NOT EXISTS (SELECT 1 FROM services s WHERE s.branch_id=b.id AND s.name='Fee Payment Support');
INSERT INTO services (branch_id, name, description, estimated_duration_minutes, base_fee, active)
SELECT b.id, 'Certificates and Transcripts', 'Request academic certificates or transcripts', 20, 0.00, TRUE FROM branches b
WHERE b.branch_code = 'QL-EDU-001' AND NOT EXISTS (SELECT 1 FROM services s WHERE s.branch_id=b.id AND s.name='Certificates and Transcripts');

-- Other community services
INSERT INTO services (branch_id, name, description, estimated_duration_minutes, base_fee, active)
SELECT b.id, 'Utility Bill Assistance', 'Help with utility bill enquiries and payments', 10, 0.00, TRUE FROM branches b
WHERE b.branch_code = 'QL-OTHER-001' AND NOT EXISTS (SELECT 1 FROM services s WHERE s.branch_id=b.id AND s.name='Utility Bill Assistance');
INSERT INTO services (branch_id, name, description, estimated_duration_minutes, base_fee, active)
SELECT b.id, 'General Enquiry', 'General information and customer support', 10, 0.00, TRUE FROM branches b
WHERE b.branch_code = 'QL-OTHER-001' AND NOT EXISTS (SELECT 1 FROM services s WHERE s.branch_id=b.id AND s.name='General Enquiry');
INSERT INTO services (branch_id, name, description, estimated_duration_minutes, base_fee, active)
SELECT b.id, 'Appointment Assistance', 'Help with scheduling appointments', 10, 0.00, TRUE FROM branches b
WHERE b.branch_code = 'QL-OTHER-001' AND NOT EXISTS (SELECT 1 FROM services s WHERE s.branch_id=b.id AND s.name='Appointment Assistance');

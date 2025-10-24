SELECT name
FROM app_roles ar
         INNER JOIN user_role ur ON ar.role_id = ur.role_id
WHERE user_id = 1;


-- Sample data for app_user_profile
INSERT INTO app_user_profile (first_name, last_name, date_of_birth, place_of_birth, current_address, phone_number, gender, card_id, nationality, app_user_id)
VALUES
    ('John', 'Doe', '1990-05-15', 'New York', '123 Main St, New York, NY', '+1234567890', 'Male', 'ID123456', 'American', 9),
    ('Jane', 'Smith', '1985-08-20', 'Los Angeles', '456 Oak Ave, Los Angeles, CA', '+1987654321', 'Female', 'ID654321', 'American', 10);

-- Insert sample subjects
INSERT INTO subjects (subject_name, subject_description, group_level)
VALUES
    ('Mathematics', 'Study of numbers, quantities, shapes, and patterns', 'Basic'),
    ('Physics', 'Study of matter, energy, and the fundamental forces of nature', 'Advanced'),
    ('Chemistry', 'Study of substances and their properties', 'Intermediate'),
    ('Biology', 'Study of living organisms and life processes', 'Intermediate'),
    ('Computer Science', 'Study of computers and computational systems', 'Advanced'),
    ('English Literature', 'Study of literature written in the English language', 'Basic');

-- Insert sample teachers
INSERT INTO teachers (employee_code, hire_date, status, user_id)
VALUES
    ('EMP001', '2020-01-15', 'active', 9),
    ('EMP002', '2019-03-20', 'active', 10),
    ('EMP003', '2021-06-10', 'active', 11);
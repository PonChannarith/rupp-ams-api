    SELECT name
    FROM app_roles ar
             INNER JOIN user_role ur ON ar.role_id = ur.role_id
    WHERE user_id = 1;


    -- Sample data for app_user_profile
    INSERT INTO app_user_profile (first_name, last_name, date_of_birth, place_of_birth, current_address, phone_number, gender, card_id, nationality, app_user_id)
    VALUES
        ('John', 'Doe', '1990-05-15', 'New York', '123 Main St, New York, NY', '+1234567890', 'Male', 'ID123456', 'American', 6),
        ('Jane', 'Smith', '1985-08-20', 'Los Angeles', '456 Oak Ave, Los Angeles, CA', '+1987654321', 'Female', 'ID654321', 'American', 7);

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
        ('Teacher Reaksmey', '2020-01-15', 'active', 7),
        ('Teacher Samnang', '2021-06-10', 'active', 11);


    -- Insert sample data
    INSERT INTO classes (class_name, grade_level, academic_year) VALUES
                                                                     ('A1', 'Java', '2025-2026'),
                                                                     ('A2', 'Python', '2025-2026'),
                                                                     ('A5', 'MIS', '2025-2026'),
                                                                     ('E4', 'C Programming', '2025-2026'),
                                                                     ('E3', 'Software Engineering', '2025-2026');



-- Insert sample data
    INSERT INTO students (student_no, student_card_id, khmer_name, english_name, gender, phone_number, date_of_birth, user_id) VALUES
                                                                                                                                   ('STU001', 'CARD001', 'ស៊ុន ស៊ីណា', 'Sina Sun', 'Male', '+85512345678', '2005-03-15', 8),
                                                                                                                                   ('STU002', 'CARD002', 'ម៉ៅ សុផាត', 'Sophat Mao', 'Male', '+85512345679', '2005-07-22', 9),
                                                                                                                                   ('STU003', 'CARD003', 'ផល្គិន ស្រីណយ', 'Sreynoy Phalkin', 'Female', '+85512345680', '2005-11-08', 10);
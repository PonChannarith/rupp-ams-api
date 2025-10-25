package org.rupp.amsruppapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.rupp.amsruppapi.model.entity.Subject;
import org.rupp.amsruppapi.repository.SubjectRepository;
import org.rupp.amsruppapi.service.SubjectService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {
    private final SubjectRepository subjectRepository;

    @Override
    public List<Subject> findAll() {
        return subjectRepository.findAll();
    }

    @Override
    public Subject findById(Long id) {
        Subject subject = subjectRepository.findById(id);
        if (subject == null) {
            throw new RuntimeException("Subject not found with ID: " + id);
        }
        return subject;
    }

    @Override
    public Subject findBySubjectName(String subjectName) {
        Subject subject = subjectRepository.findBySubjectName(subjectName);
        if (subject == null) {
            throw new RuntimeException("Subject not found with name: " + subjectName);
        }
        return subject;
    }

    @Override
    public List<Subject> findByGroupLevel(String groupLevel) {
        return subjectRepository.findByGroupLevel(groupLevel);
    }

    @Override
    public Subject create(Subject subject) {
        // Check if subject name already exists
        if (subject.getSubjectName() != null &&
                subjectRepository.countBySubjectName(subject.getSubjectName()) > 0) {
            throw new IllegalArgumentException("Subject name already exists: " + subject.getSubjectName());
        }

        subjectRepository.insert(subject);
        return subjectRepository.findById(subject.getSubjectId());
    }

    @Override
    public Subject update(Long id, Subject subject) {
        // Fetch existing subject
        Subject existing = subjectRepository.findById(id);
        if (existing == null) {
            throw new RuntimeException("Subject not found with ID: " + id);
        }

        // Check if subject name is being changed and if it already exists
        if (subject.getSubjectName() != null &&
                !subject.getSubjectName().equals(existing.getSubjectName()) &&
                subjectRepository.countBySubjectNameExcludingId(subject.getSubjectName(), id) > 0) {
            throw new RuntimeException("Subject name already exists: " + subject.getSubjectName());
        }

        // Update fields
        existing.setSubjectName(subject.getSubjectName());
        existing.setSubjectDescription(subject.getSubjectDescription());
        existing.setGroupLevel(subject.getGroupLevel());

        int rows = subjectRepository.update(existing);
        if (rows == 0) {
            throw new RuntimeException("Failed to update subject with ID: " + id);
        }

        return subjectRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        Subject existing = subjectRepository.findById(id);
        if (existing == null) {
            throw new RuntimeException("Subject not found with ID: " + id);
        }
        subjectRepository.deleteById(id);
    }
}

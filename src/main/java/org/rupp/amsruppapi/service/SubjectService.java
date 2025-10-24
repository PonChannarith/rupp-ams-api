package org.rupp.amsruppapi.service;

import org.rupp.amsruppapi.model.entity.Subject;

import java.util.List;

public interface SubjectService {
    List<Subject> findAll();
    Subject findById(Long id);
    Subject findBySubjectName(String subjectName);
    List<Subject> findByGroupLevel(String groupLevel);
    Subject create(Subject subject);
    Subject update(Long id, Subject subject);
    void deleteById(Long id);
}

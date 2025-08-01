package dev.api.courses.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import dev.api.courses.model.CourseDocument;

@Repository
public interface CourseDocumentRepository extends ElasticsearchRepository<CourseDocument, String> {
}
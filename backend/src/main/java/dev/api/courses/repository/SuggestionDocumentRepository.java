package dev.api.courses.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import dev.api.courses.model.SuggestionDocument;

public interface SuggestionDocumentRepository extends ElasticsearchRepository<SuggestionDocument, String>{
    
}

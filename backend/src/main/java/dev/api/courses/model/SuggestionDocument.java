package dev.api.courses.model;



import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.core.suggest.Completion; // Important import

@Document(indexName = "general_suggestions") // Your new index
public class SuggestionDocument {


    @Id
    private String id;

    @Field(type = FieldType.Text) // The actual suggestion phrase
    private String phrase;

    public SuggestionDocument() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }
 

    
}
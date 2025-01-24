package ua.sevastianov.testtask;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {
    Map<String, Document> documents = new HashMap<>();
    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if (!documents.containsKey(document.getId()) || document.getId() == null) {
            String newId = UUID.randomUUID().toString();
            document.setId(newId);
            if (document.getCreated() == null) {
                document.setCreated(Instant.now());
            }
            documents.put(newId, document);
        }
        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search( SearchRequest request) {
        return documents.values().stream()
                .filter(d -> matchRequest(d, request))
                .collect(Collectors.toList());
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        return Optional.ofNullable(documents.get(id));
    }

    public boolean matchRequest(Document document, SearchRequest request) {
        if (request == null) return false;
        if(request.getTitlePrefixes() != null && document.getTitle() != null){
            for(String title : request.getTitlePrefixes()){
                if (document.getTitle().startsWith(title)) return true;
            }
        }
        if(request.getContainsContents() != null && document.getContent() != null){
            for(String content : request.getContainsContents()){
                if (document.getContent().startsWith(content)) return true;
            }
        }
        if (request.getAuthorIds() != null && document.getAuthor().getId() != null){
            for(String authorId : request.getAuthorIds()){
                if (document.getAuthor().getId().equals(authorId)) return true;
            }
        }
        if (request.getCreatedFrom() != null && document.getCreated() != null) {
            return document.getCreated().isAfter(request.getCreatedFrom());
        }
        if (request.getCreatedTo() != null && document.getCreated() != null) {
            return document.getCreated().isBefore(request.getCreatedTo());
        }
        return false;
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}
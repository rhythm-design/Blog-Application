package me.rhythmvarshney.blogapplication.dto;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import me.rhythmvarshney.blogapplication.entity.Tag;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

public interface MergeFilterDto<T,V> {


    /**
     * This method is used to set the value of field of a certain entity
     * to be used to perform search over collection.
     * This method used toString() method for comparision.
     *
     * @return override field value in toString() method of entity class
     * @return field name here for search
     *
     * Like in ManyToMany or OneToMany relationships, we usually have Collection<T> as field of entity
     * If we want to search over entire Collection then @return that collection field name here else pass
     * <code>null</code> for no search
     */
    String returnFieldNameForCollectionSearch();
    default Specification<T> authorEquals(String author) {
        // key value
        return (root, query, criteriaBuilder) -> {
            if (author == null || author.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("author"), author);
        };
    }


    default Specification<T> tagsContain2(Collection<V> tags, String fieldName) {
        return (root, query, criteriaBuilder) -> {
            if (tags == null || tags.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            List<Predicate> predicates = new ArrayList<>();
            for(Object object: tags){
                Join<V, T> tagJoin = root.join("tags");
                Predicate like = criteriaBuilder.like(criteriaBuilder.lower(tagJoin.get(returnFieldNameForCollectionSearch())), "%"+object.toString().toLowerCase()+"%");
                predicates.add(like);
            }
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    default Specification<T> tagsContain(Set<String> tags) {
        return (root, query, criteriaBuilder) -> {
            if (tags == null || tags.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            List<Predicate> predicates = new ArrayList<>();
            for(String tag: tags){
                Join<Tag, T> tagJoin = root.join("tags");
                Predicate like = criteriaBuilder.like(criteriaBuilder.lower(tagJoin.get("name")), "%"+tag.toLowerCase()+"%");
                predicates.add(like);
            }
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    default Specification<T> searchInAnyField(String searchText) {
        return (root, query, criteriaBuilder) -> {
            if (searchText == null || searchText.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String searchTextLower = searchText.toLowerCase();

            Predicate[] predicates = new Predicate[7];

            predicates[0] = criteriaBuilder.like(criteriaBuilder.lower(root.get("postTitle")), "%" + searchTextLower + "%");
            predicates[1] = criteriaBuilder.like(criteriaBuilder.lower(root.get("excerpt")), "%" + searchTextLower + "%");
            predicates[2] = criteriaBuilder.like(criteriaBuilder.lower(root.get("postContent")), "%" + searchTextLower + "%");
            predicates[3] = criteriaBuilder.like(criteriaBuilder.lower(root.get("author")), "%" + searchTextLower + "%");
            predicates[4] = criteriaBuilder.like(criteriaBuilder.lower(root.get("publishTime").as(String.class)), "%" + searchTextLower + "%");
            predicates[5] = criteriaBuilder.equal(root.get("isPublished"), true);
            predicates[6] = criteriaBuilder.like(criteriaBuilder.lower(root.get("postCreateTime").as(String.class)), "%" + searchTextLower + "%");

            return criteriaBuilder.and(Arrays.asList(predicates).toArray(new Predicate[0]));
        };
    }
}

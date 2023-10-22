package me.rhythmvarshney.blogapplication.service;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import me.rhythmvarshney.blogapplication.entity.Tag;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.util.*;

public interface MergeFilterService<T,V> {


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
     default Specification<T> searchByKeyValue(String key, String value) {
        // key value
        return (root, query, criteriaBuilder) -> {
            if (value == null || value.isEmpty() || key == null || key.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(key), value);
        };
    }


     default Specification<T> collectionContain(List<String> collectionList) {
        return (root, query, criteriaBuilder) -> {
            if (collectionList == null || collectionList.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();
            for(String tag: collectionList){
                // TODO: Hard coded modification
                Join<V, T> tagJoin = root.join("tags");
                Predicate like = criteriaBuilder.like(criteriaBuilder.lower(tagJoin.get(returnFieldNameForCollectionSearch())), "%"+tag.toLowerCase()+"%");
                predicates.add(like);
            }
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }


    default Specification<T> searchInStringFields(Field[] fields, String searchText){
        return (root, query, criteriaBuilder) -> {
            if (searchText == null || searchText.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            List<Predicate> predicates = new ArrayList<>();
            for(Field field: fields){
                if(!field.getType().equals(String.class)) continue;
                System.out.println(this + "Field Name: " + field.getName());
                Predicate like = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get(field.getName())),
                        "%" + searchText.toLowerCase() + "%"
                );
                predicates.add(like);
            }
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    default Specification<T> searchInAllFields(List<String> collectionList, Field[] fields, String searchText, String key, String value){
        Specification<T> tagsSpecification = collectionContain(collectionList);

        Specification<T> textSearchSpecification = searchInStringFields(fields,searchText);

        return Specification
                .where(tagsSpecification)
                .and(textSearchSpecification)
                .and(searchByKeyValue(key,value));
    }

}

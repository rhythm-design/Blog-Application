package me.rhythmvarshney.blogapplication.service;

import jakarta.persistence.criteria.*;
import me.rhythmvarshney.blogapplication.entity.Tag;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;


@Service
public class FilteringService<T>{

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

    private T t;

    public Specification<T> getSearchSpecification(String key, String value){
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get(key),value);
            }
        };
    }

    public Specification<T> getSearchSpecification2(Field[] fields, String searchText){
        return (root, query, criteriaBuilder) -> {
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

    public Specification<T> searchInAnyField(String searchText, Field[] fields) {

        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            String likePattern = "%" + searchText + "%";
            Predicate[] predicates = new Predicate[7]; // Number of fields to search

            predicates[0] = criteriaBuilder.like(root.get("postTitle"), likePattern);
            predicates[1] = criteriaBuilder.like(root.get("excerpt"), likePattern);
            predicates[2] = criteriaBuilder.like(root.get("postContent"), likePattern);
            predicates[3] = criteriaBuilder.like(root.get("author"), likePattern);
            predicates[4] = criteriaBuilder.like(root.get("publishTime").as(String.class), likePattern);
            predicates[5] = criteriaBuilder.equal(root.get("isPublished"), true);
            predicates[6] = criteriaBuilder.like(root.get("postCreateTime").as(String.class), likePattern);

            return criteriaBuilder.or(predicates[0]);
        };
    }

    public Specification<T> searchWithFiltersAndText(
            String author,
            Set<Tag> tags,
            String searchText
    ) {
        return (root, query, criteriaBuilder) -> {
            Predicate authorPredicate = (author == null || author.isEmpty())
                    ? criteriaBuilder.conjunction()
                    : criteriaBuilder.equal(root.get("author"), author);
            Predicate tagsPredicate = criteriaBuilder.conjunction();

//            Predicate tagsPredicate = (tags == null || tags.isEmpty())
//                    ? criteriaBuilder.conjunction()
//                    : root.join("tags").get("tagName").in(tags);

            System.out.println("getrerer " + searchText);
            Predicate textSearchPredicate = (searchText == null || searchText.isEmpty())
                    ? criteriaBuilder.conjunction()
                    : criteriaBuilder.or(
                    criteriaBuilder.like(root.get("postTitle"), "%" + searchText + "%"),
                    criteriaBuilder.like(root.get("excerpt"), "%" + searchText + "%"),
                    criteriaBuilder.like(root.get("postContent"), "%" + searchText + "%"),
                    criteriaBuilder.like(root.get("author"), "%" + searchText + "%"),
                    criteriaBuilder.like(root.get("publishTime").as(String.class), "%" + searchText + "%"),
                    criteriaBuilder.equal(root.get("isPublished"), true),
                    criteriaBuilder.like(root.get("postCreateTime").as(String.class), "%" + searchText + "%")
            );

            return criteriaBuilder.and(authorPredicate, tagsPredicate, textSearchPredicate);
        };
    }


    // gerererefrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr

    public Specification<T> allThis(String author, Set<String> tags, String text){
        Specification<T> authorSpecification = authorEquals(author);
        Specification<T> tagsSpecification = tagsContain(tags);
        Field[] fields = t.getClass().getDeclaredFields();

        Specification<T> textSearchSpecification = getSearchSpecification2(fields,text);

        Specification<T> finalSpecification = Specification.where(authorSpecification)
                .and(tagsSpecification)
                .and(textSearchSpecification);

        return finalSpecification;
    }

//    public Specification<T> searchALlFields(String text){
//
//    }

    public Specification<T> searchCollectionField(Collection<?> hashset, String fieldName){
        return (root, query, criteriaBuilder) -> {
//            if (tags == null || tags.isEmpty()) {
//                return criteriaBuilder.conjunction();
//            }
            List<Predicate> predicates = new ArrayList<>();
//            for(Object element: hashset){
//                Join<?, T> tagJoin = root.join(fieldName);
//                Predicate like = criteriaBuilder.like(criteriaBuilder.lower(tagJoin.get("name")), "%"+tag.toLowerCase()+"%");
//
//            }
//            for(String tag: tags){
//                Join<Tag, T> tagJoin = root.join("tags");
//                Predicate like = criteriaBuilder.like(criteriaBuilder.lower(tagJoin.get("name")), "%"+tag.toLowerCase()+"%");
//                predicates.add(like);
//            }
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };

    }



    public Specification<T> authorEquals(String author) {
        return (root, query, criteriaBuilder) -> {
            if (author == null || author.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("author"), author);
        };
    }

    public Specification<T> tagsContain(Set<String> tags) {
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

    public Specification<T> searchInAnyField(String searchText) {
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

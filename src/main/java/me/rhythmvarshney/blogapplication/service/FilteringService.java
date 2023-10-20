package me.rhythmvarshney.blogapplication.service;

import jakarta.persistence.criteria.*;
import me.rhythmvarshney.blogapplication.entity.Tag;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class FilteringService<T>{

    public Specification<T> getSearchSpecification(String key, String value){
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get(key),value);
            }
        };
    }

    public Specification<T> getSearchSpecification(Map<String,String> map){

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            for(Map.Entry<String,String> entry: map.entrySet()){
                if(entry.getKey().equals("tags")) continue;
                Predicate like = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get(entry.getKey())),
                        "%" + entry.getValue().toLowerCase() + "%"
                );
                predicates.add(like);
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }


    public Specification<T> searchByTagName(Map<String,String> map) {
        if(map.containsKey("tags")){
            String tagName = map.get("tags");
            String[] tagList = tagName.split(",");
            return (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                for(String tag: tagList){
                    Join<Tag, T> tagJoin = root.join("tags");
                    Predicate like = criteriaBuilder.like(criteriaBuilder.lower(tagJoin.get("name")), "%"+tag.toLowerCase()+"%");
                    predicates.add(like);
                }
                return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
            };
        }else{
            return getSearchSpecification(map);
        }
    }


    public Specification<T> searchByMultipleCriteria(Map<String, String> map) {
        return getSearchSpecification(map).and(searchByTagName(map));
    }
}

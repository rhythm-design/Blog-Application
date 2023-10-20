package me.rhythmvarshney.blogapplication.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
                Predicate equal = criteriaBuilder.equal(root.get(entry.getKey()), "%" + entry.getValue() + "%");
                predicates.add(equal);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

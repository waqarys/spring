package com.waqar.searchapp;

import com.waqar.searchapp.dbmodel.orm.Topic;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends CrudRepository<Topic, Long> {
    List<Topic> findByDescriptionLikeIgnoreCase(String searchString);
}

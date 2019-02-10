package com.waqar.searchapp;

import com.waqar.searchapp.dbmodel.orm.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Controller {

    @Autowired
    TopicRepository topicRepository;

    @RequestMapping("/")
    public List<Topic> index(){
        return topicRepository.findByDescriptionLikeIgnoreCase("%food%");
    }
}

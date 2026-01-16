package com.habitus.habitus.temp;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/temp")
@AllArgsConstructor
public class Controller {
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    public List<Map<String, Object>> list = new ArrayList<>();

    @GetMapping
    public List<Map<String, Object>> get() {
        return list;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getId(@PathVariable Integer id) {
        return getById(id);
    }

    @PostMapping
    public void add(@RequestBody Map<String, Object> obj) {
        logger.info(obj.toString());
        list.add(obj);
    }

    @PutMapping
    public void update(@RequestBody Map<String, Object> obj) {
        var map = getById((int) obj.get("id"));
        map.putAll(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        var map = getById(id);
        list.remove(map);
    }

    @ExceptionHandler
    public ResponseEntity<String> ex(Throwable throwable) {
        return ResponseEntity.badRequest().body(throwable.getMessage());
    }

    private Map<String, Object> getById(int id) {
        return list.stream().filter(it -> (int) it.get("id") == id).findFirst().orElseThrow();
    }
}

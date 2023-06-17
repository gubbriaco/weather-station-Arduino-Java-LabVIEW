package it.franco.arduinomisurazioni.controllers;

import it.franco.arduinomisurazioni.entities.Count;
import it.franco.arduinomisurazioni.repositories.CountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/count")
public class CountController {
    @Autowired
    private CountRepository countRepository;

    @PostMapping
    public ResponseEntity saveCount(@RequestParam String n){
        long id= 1;
        Count count = countRepository.getReferenceById(id);
        count.setN(Integer.parseInt(n));
        countRepository.save(count);
        return new ResponseEntity<>("Count server salvato correttamente", HttpStatus.OK);
    }
}

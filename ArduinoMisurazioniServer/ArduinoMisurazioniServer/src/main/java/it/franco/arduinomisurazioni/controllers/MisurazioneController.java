package it.franco.arduinomisurazioni.controllers;

import it.franco.arduinomisurazioni.entities.Count;
import it.franco.arduinomisurazioni.entities.Misurazione;
import it.franco.arduinomisurazioni.repositories.CountRepository;
import it.franco.arduinomisurazioni.services.MisurazioneService;
import it.franco.arduinomisurazioni.utils.ProjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
/*
Le classi annotate con @RestController, sono le classi che espongono gli endpoint del server, in questo caso,
di tipo REST. L'utilizzo della notazione @RequestMapping è necessario per definire una radice dell'URL
per contattare l'endpoint. L'uso di @Autowired è il medesimo visto nel Service. Ogni metodo all'interno di
questa classe rappresenta un endpoint, se e solo se, verrà annotato con una notazione di metodo HTTP.
I metodi HTTP (GET, POST, PUT ,DELETE e tanti altri), sono fondamentali nella richiesta per far capire al
server a quale endpoint agganciare la richiesta stessa. Molto importante è l'oggetto RESPONSE ENTITY,
che consente di convertire qualsiasi oggetto, in una response HTTP.*/

@RestController
@RequestMapping("/misurazione")
public class MisurazioneController {
    @Autowired
    private MisurazioneService misurazioneService;
    @Autowired
    private CountRepository countRepository;

    @GetMapping
    public ResponseEntity welcome(){
        return new ResponseEntity<>("Ciao effettua operazione!",HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity getAll(){
        return new ResponseEntity<>(misurazioneService.getAll(), HttpStatus.OK);
    }
    @GetMapping("/date")
    public ResponseEntity getByDate(@RequestParam String data) throws ParseException {
        SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
        Date d= sdf.parse(data);
        List<Misurazione> misurazioni= misurazioneService.getByDate(d);
        return new ResponseEntity<>(misurazioni,HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity createMisuration(@RequestParam String pars){
        long id=1;
        Count count;
        if( !countRepository.existsById(id) ){
            count = new Count(id, 1, 0);
        }
        else {
            count = countRepository.getReferenceById(id);
        }
        if(count.getCount()== count.getN()){
            StringTokenizer st = new StringTokenizer(pars,"-");
            String umidita= st.nextToken();
            String temperatura=st.nextToken();
            String temperaturaPercepita= st.nextToken();
            Misurazione misurazione= new Misurazione(umidita,temperatura,temperaturaPercepita, ProjectUtils.getDateWithoutTime(new Date()));
            misurazioneService.saveMisurazione(misurazione);
            count.setCount(0);
            countRepository.save(count);
            return new ResponseEntity<>("Misurazione salvata correttamente.",HttpStatus.OK);
        }
        else{
            int c= count.getCount();
            count.setCount(c+1);
            countRepository.save(count);
            return new ResponseEntity<>("Misurazione salvata correttamente.",HttpStatus.OK);
        }
    }
    @PostMapping("/count")
    public ResponseEntity setCount(@RequestParam int n){
        long id =1;
        System.out.println(n);
        if(countRepository.existsById(id)){
            Count c= countRepository.getReferenceById(id);
            c.setN(n);
            c.setCount(0);
            //System.out.println("ciao1");
            countRepository.save(c);
        }
        else{
            Count c= new Count(1,n,0);
            //System.out.println("ciao2");
            countRepository.save(c);
        }
        return new ResponseEntity<>("Count impostato correttamente",HttpStatus.OK);
    }
    @GetMapping("/temperatura/max")
    public ResponseEntity getMaxTempValByDate(@RequestParam String date) throws ParseException {
        SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
        Date d= sdf.parse(date);
        if(!misurazioneService.existsByData(d))
            return new ResponseEntity<>("There are no measurements on: "+date,HttpStatus.OK);
        List<Misurazione> misurazioni= misurazioneService.getByDate(d);
        Float max=Float.MIN_VALUE;
        for (Misurazione m: misurazioni) {
            if(max<Float.parseFloat(m.getTemperatura()))
                max=Float.parseFloat(m.getTemperatura());
        }
        return new ResponseEntity<>("On "+date+" the maximum temperature was "+max+" degree Celsius.",HttpStatus.OK);
    }
    @GetMapping("/temperatura/min")
    public ResponseEntity getMinTempValByDate(@RequestParam String date) throws ParseException {
        SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
        Date d= sdf.parse(date);
        if(!misurazioneService.existsByData(d))
            return new ResponseEntity<>("There are no measurements on: "+date,HttpStatus.OK);
        List<Misurazione> misurazioni= misurazioneService.getByDate(d);
        Float min=Float.MAX_VALUE;
        for (Misurazione m: misurazioni) {
            if(min>Float.parseFloat(m.getTemperatura()))
                min=Float.parseFloat(m.getTemperatura());
        }
        return new ResponseEntity<>("On "+date+" the minimum temperature was "+min+" degree Celsius.",HttpStatus.OK);
    }
    @GetMapping("/umidita/max")
    public ResponseEntity getMaxUmiValByDate(@RequestParam String date) throws ParseException {
        SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
        Date d= sdf.parse(date);
        if(!misurazioneService.existsByData(d))
            return new ResponseEntity<>("There are no measurements on: "+date,HttpStatus.OK);
        List<Misurazione> misurazioni= misurazioneService.getByDate(d);
        Float max=Float.MIN_VALUE;
        for (Misurazione m: misurazioni) {
            if(max<Float.parseFloat(m.getUmidita()))
                max=Float.parseFloat(m.getUmidita());
        }
        return new ResponseEntity<>("On "+date+" the maximum humidity was "+max+"%.",HttpStatus.OK);
    }
    @GetMapping("/umidita/min")
    public ResponseEntity getMinUmiValByDate(@RequestParam String date) throws ParseException{
        SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
        Date d= sdf.parse(date);
        if(!misurazioneService.existsByData(d))
            return new ResponseEntity<>("There are no measurements on: "+date,HttpStatus.OK);
        List<Misurazione> misurazioni= misurazioneService.getByDate(d);
        Float min=Float.MAX_VALUE;
        for (Misurazione m: misurazioni) {
            if(min>Float.parseFloat(m.getUmidita()))
                min=Float.parseFloat(m.getUmidita());
        }
        return new ResponseEntity<>("On "+date+" the minimum humidity was "+min+"%.",HttpStatus.OK);
    }
    @GetMapping("/umidita/media")
    public ResponseEntity getUmiditaMediaByDate(@RequestParam String date)throws ParseException{
        SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
        Date d= sdf.parse(date);
        if(!misurazioneService.existsByData(d))
            return new ResponseEntity<>("There are no measurements on: "+date,HttpStatus.OK);
        List<Misurazione> misurazioni= misurazioneService.getByDate(d);
        Float media=0.0f;
        for (Misurazione m: misurazioni) {
            media+=Float.parseFloat(m.getUmidita());
        }
        media=media/ misurazioni.size();
        media = (float) (Math.floor(media*100)/100);
        return new ResponseEntity<>("On "+date+" the average humidity was "+media+"%.",HttpStatus.OK);
    }
    @GetMapping("/temperatura/media")
    public ResponseEntity getTemperaturaMediaByDate(@RequestParam String date)throws ParseException{
        SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
        Date d= sdf.parse(date);
        if(!misurazioneService.existsByData(d))
            return new ResponseEntity<>("There are no measurements on: "+date,HttpStatus.OK);
        List<Misurazione> misurazioni= misurazioneService.getByDate(d);
        Float media=0.0f;
        for (Misurazione m: misurazioni) {
            media+=Float.parseFloat(m.getTemperatura());
        }
        media=media/ misurazioni.size();
        media = (float) (Math.floor(media*100)/100);
        return new ResponseEntity<>("On "+date+" the average temperature was "+media+" degree Celsius.",HttpStatus.OK);
    }
    @GetMapping("/resoconto")
    public ResponseEntity getResoconto(){
        return new ResponseEntity<>("Questo è un resoconto di prova",HttpStatus.OK);
    }
}

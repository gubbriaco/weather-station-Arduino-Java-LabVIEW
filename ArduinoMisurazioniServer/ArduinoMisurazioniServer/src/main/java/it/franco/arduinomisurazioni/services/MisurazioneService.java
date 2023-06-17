package it.franco.arduinomisurazioni.services;

import it.franco.arduinomisurazioni.entities.Misurazione;
import it.franco.arduinomisurazioni.repositories.MisurazioneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Date;
import java.util.List;
/*La classe service è la classe che permette al lato applicativo di interfacciarsi con il db,
contenendo al suo interno una serie di metodi che richiameranno le intestazioni dichiarate all'interno,
del repository. La notazione @Service serve a far capire al compilatore che questa è una delle classi che
utilizzerà come servizio, cioè come interfaccia all'interfaccia dei dati. Acquista un certo valore
nello sviluppo di microservizi. La notazione @Autowired ha una notevole importanza, in quanto permette
di istruire il compilatore ad inizializzare un oggetto automaticamente, iniettandolo come bean
all'interno del server. Morale della favola, così utilizzato creerà un oggetto disponibile a tutte le classi,
all'interno del serve. Questo genere di oggetti prende il nome di BEAN.
*/

@Service
public class MisurazioneService {
    @Autowired
    private MisurazioneRepository misurazioneRepository;
    /*La notazione @Transactional è una notazione che specifica a spring, se la transazione che ci
    * apprestiamo ad avviare all'interno del db è di sola lettura o meno, in modo da operare in modi differenti.*/
    @Transactional(readOnly = true)
    public List<Misurazione> getAll(){
        return misurazioneRepository.findAll();
    }
    @Transactional
    public long saveMisurazione(Misurazione misurazione){
        return misurazioneRepository.save(misurazione).getId();
    }
    @Transactional(readOnly = true)
    public List<Misurazione> getByDate(Date data){
        return misurazioneRepository.findByData(data);
    }
    @Transactional
    public long deleteMisurazione(Misurazione misurazione){
        misurazioneRepository.delete(misurazione);
        return misurazione.getId();
    }
    @Transactional(readOnly = true)
    public boolean existsByData(Date data){
        return misurazioneRepository.existsByData(data);
    }


}

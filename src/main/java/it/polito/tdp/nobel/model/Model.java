  package it.polito.tdp.nobel.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.polito.tdp.nobel.db.EsameDAO;

public class Model {
	
//	 definisco quelli che devo essere i parametri di input della procedura ricorsiva
	private List<Esame> esami; // inizializzo la lista in un costruttore di Model chiamando il DAO
	
//	 L'output che restituisco al chiamante sarà sempre una collection di esami, in questo caso mi basta un Set dato che
//	 l'ordine non conta. 
//	 Associato al Set<Esami> migliore, devo avere un parametro double che contenga la media della rispettiva soluzione in modo che possibile
//	 fare i confronti tra le soluzioni.
	
	private Set<Esame> migliore; // lo inzializzo tutte le volte che l'utente mi chiederà una soluzione
	private double mediaMigliore;
	
	public Model () {
		EsameDAO dao = new EsameDAO();
		this.esami = dao.getTuttiEsami();
		
	}

	public Set<Esame> calcolaSottoinsiemeEsami(int m) { // m = numero crediti da dover raggiungere
		
//		 faccio il ripristino di soluzione migliore
		migliore = new HashSet<Esame>();
		mediaMigliore = 0.0;
		
		Set<Esame> parziale = new HashSet<Esame>(); // creo la soluzione parziale da passare alla procedura ricorsiva
		
//		cerca(parziale,0,m);
		cercaIntelligente(parziale,0,m);
		
		return migliore;	
	}

	private void cercaIntelligente(Set<Esame> parziale, int l, int m) {
	
//		 controllo come prima cosa i casi terminali
//		 1) livello l = numero di esami N
//		 2) parziale.sommaCrediti > m, oppure parziale.sommaCrediti == m e la media è la migliore
				
		int sommaCrediti = sommaCrediti(parziale);
				
		if(sommaCrediti > m) {  // soluzione ovviamente non valida
			return;
		}
					
		if(sommaCrediti == m) {
//		 la soluzione potrebbe essere valida, devo fare il controllo sulla media dei voti (se è una soluzione migliorativa la tengo altrimento 
//		 la scarto e continuo.
					
				 double mediaVoti = calcolaMedia(parziale);
				 if(mediaVoti > mediaMigliore) {
					 migliore = new HashSet<Esame>(parziale);
					 mediaMigliore = mediaVoti;
				 }
				   
				 return;
		}
		
//		sicuramente qui ho crediti < m
		if(l==esami.size()) {  // soluzione che si verifica quando i crediti sono < m ma non ho più nulla da aggiungere, ho finito gli esami
					           // a mia disposizione da poter inserire
			return; 
		}
		
//		se arrivo qui posso avere due possibilità o aggiungo l'esame che sto analizzando (esami(l)) o non lo aggiungoo
		parziale.add(esami.get(l));
		cercaIntelligente(parziale,l+1,m);
		
//		qui decido di non aggiungere l'esame e andare avanti
		parziale.remove(esami.get(l));
		cercaIntelligente(parziale,l+1,m);
		
//	il livello della ricorsione coincide con l'esame da considerare:
//	potro avere {e1,e2,...} ma mai {e2,e1,..} perchè io li vado a considerare sempre in ordine quindi al posto di esplorare tutte le 
//	combinazioni (complessità n!) ne vado a considerare solo 2^n combinazioni, ovvero per ogni esame ho due possibilità: 
//		1- aggiungo e procedo
//		2- lo escludo e procedo
		
	}

	/*
	 * la complessità di questo algortimo è N!, esploro tutte le possibili saluzioni, la stragrande maggioranza sono
	 * soluzioni ripeture (stessi esami ma in posizioni diverse, ricordo che a me dell'ordine con cui vengono considerati 
	 * gli esami se uguali non mi interessa)
	 */
	  
	private void cerca(Set<Esame> parziale, int l, int m) {    //SOLUZIONE STUPIDA
		
//		 controllo come prima cosa i casi terminali
//		 1) livello l = numero di esami N
//		 2) parziale.sommaCrediti > m, oppure parziale.sommaCrediti == m e la media è la migliore
		
		int sommaCrediti = sommaCrediti(parziale);
		
		if(sommaCrediti > m) {  // soluzione ovviamente non valida
			return;
		}
			
		if(sommaCrediti == m) {
//		 la soluzione potrebbe essere valida, devo fare il controllo sulla media dei voti (se è una soluzione migliorativa la tengo altrimento 
//		 la scarto e continuo.
			
		   double mediaVoti = calcolaMedia(parziale);
		   if(mediaVoti > mediaMigliore) {
			   migliore = new HashSet<Esame>(parziale);
			   mediaMigliore = mediaVoti;
		   }
		   
		   return;
		}
		
		if(l==esami.size()) {  // soluzione che si verifica quando i crediti sono < m ma non ho più nulla da aggiungere, ho finito gli esami
			                   // a mia disposizione da poter inserire
			return; 
		}
		
//		 se arrivo qui posso generare dei sotto problemi (non sono ancora arrivato a m crediti ma ho ancora esami da poter inserire)
		for(Esame e: esami) {
			if(!parziale.contains(e)) {
				parziale.add(e);
				cerca(parziale, l+1, m);
				
				parziale.remove(e); // gli passo un oggetto perchè sono sicuro che lavorando con un Set tale elemento sarà unico e 
				                    // non ripetuto, lavorassi con una lista questo non funzionerebbe e il backtracking lo dovrei fare 
				                    // togliendo l'ultimo elemento inserito, ovvero quello presente in fondo alla lista (parziale.remove(parziale.size()-1))
			}
		}
	}

	public double calcolaMedia(Set<Esame> esami) {
		
		int crediti = 0;
		int somma = 0;
		
		for(Esame e : esami){
			crediti += e.getCrediti();
			somma += (e.getVoto() * e.getCrediti());
		}
		
		return somma/crediti;
	}
	
	public int sommaCrediti(Set<Esame> esami) {
		int somma = 0;
		
		for(Esame e : esami)
			somma += e.getCrediti();
		
		return somma;
	}

}

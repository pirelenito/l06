package Semantico;

import java.util.HashMap;
import java.util.Vector;

public class Declaracoes {
	
	private Vector escopo;
	
	private int indiceEscopoAtual;
	
	HashMap escopoAtual;
		
	public void sobeEscopo() {
		
		escopoAtual = new HashMap ();
				
		escopo.add(escopoAtual);
		
		indiceEscopoAtual++;
	}
	
	public void desceEscopo() {
		
		escopo.remove(indiceEscopoAtual);
		
		indiceEscopoAtual--;
	}
	
	public Declaracao pegaDeclaracao ( String identificador ) throws Exception {

		Declaracao retorno = (Declaracao)escopoAtual.get(identificador); 
		
		if ( retorno == null )
			throw new Exception ("Chamada a função não declarada" );
		
		return retorno; 
	}

	public void declara ( Declaracao declaracao ) throws Exception {
		
		if ( escopoAtual.containsKey(declaracao.identificador ) )
			throw new Exception ("Declarando com identificador duplicado" );
		
		escopoAtual.put(declaracao.identificador, declaracao);
	
	}

}

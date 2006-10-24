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

	
	public Tipo pegaTipo(String identificador) {
		
		return (Tipo)escopoAtual.get(identificador);
	}

	public TipoFuncao pegaFuncao(String identificador) {
		
		TipoFuncao retorno = (TipoFuncao)escopoAtual.get(identificador); 
		
		if ( retorno.nome.compareTo("func") != 0 )
			return null;
		
		return retorno;
	}

	public TipoArray pegaArray(String identificador) {
	
		TipoArray retorno = (TipoArray)escopoAtual.get(identificador); 
		
		if ( retorno.nome.compareTo("array") != 0 )
			return null;
		
		return retorno;
	}

	public TipoRecord pegaRecord(String identificador) {

		TipoRecord retorno = (TipoRecord)escopoAtual.get(identificador); 
		
		if ( retorno.nome.compareTo("registro") != 0 )
			return null;
		
		return retorno;
	}

	public Tipo pegaDeclaracao(String identificador) {

		return (Tipo)escopoAtual.get(identificador);
	}

	public boolean declaraRotulo(String valor) {
						
		return false;
	}

	public boolean declaraConstante(String identificador) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean declara(Tipo tipo, String identificador) {
		
		if ( escopoAtual.containsKey(identificador ) )
			return false;
		
		escopoAtual.put(identificador, tipo);
		return true;
		
	}

	public boolean declaraProcedimento(Tipo procedimento, String identificador) {
		
		return declara(procedimento, identificador);
		
	}

	public boolean declaraFuncao(Tipo funcao, String identificador) {
		
		return declara(funcao, identificador);
	}

	public boolean declaraTipo(Tipo tipo, String identificador) {
		
		return declara(tipo, identificador);
	}	

}

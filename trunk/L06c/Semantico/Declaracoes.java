package Semantico;

import Sintatico.ArvorePrograma;

public class Declaracoes {
	
	private int escopoAtual;
		
	public void sobeEscopo() {
		
		escopoAtual++;
	}
	
	public void desceEscopo() {
		
		escopoAtual--;
		//tem q limpar as trankeiras
	}

	public Tipo pegaTipoVariavel(String identificador) {
		// TODO Auto-generated method stub
		return null;
	}

	public TipoFuncao pegaFuncao(String identificador) {
		// TODO Auto-generated method stub
		return null;
	}

	public TipoArray pegaArray(String identificador) {
		// TODO Auto-generated method stub
		return null;
	}

	public TipoRecord pegaRecord(String identificador) {
		// TODO Auto-generated method stub
		return null;
	}

	public Tipo pegaDeclaracao(String identificador) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean declaraRotulo(String valor) {
		// TODO Auto-generated method stub
		return false;
	}

	public void declaraConstante(String identificador) {
		// TODO Auto-generated method stub
		
	}

	public boolean declara(Tipo tipo, String identificador) {
		// TODO Auto-generated method stub
		
	}

	public Tipo pegaTipo(String valor) {
		// TODO Auto-generated method stub
		
	}

	public boolean declaraProcedimento(Tipo procedimento, String valor) {
		// TODO Auto-generated method stub
		
	}

	public boolean declaraFuncao(Tipo funcao, String valor) {
		// TODO Auto-generated method stub
		return false;
	}	

}

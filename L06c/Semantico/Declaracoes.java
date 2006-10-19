package Semantico;

import Sintatico.ArvorePrograma;

public class Declaracoes {
	
	private int escopoAtual;
	
	public boolean declara(String valor, Tipo tipo, ArvorePrograma pai) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean estaDeclaradoNoEscopo(String valor) {
		// TODO Auto-generated method stub
		return false;
		
	}

	public void sobeEscopo() {
		
		escopoAtual++;
	}
	
	public void desceEscopo() {
		
		escopoAtual--;
		//tem q limpar as trankeiras
	}	

}

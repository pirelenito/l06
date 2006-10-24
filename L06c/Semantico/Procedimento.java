package Semantico;

import java.util.Vector;

public class Procedimento extends Declaracao {

	public Procedimento(String string) {
		super (string);
		
		parametros = new Vector ( );
	}
	
	Vector parametros;
	
	public Variavel pegaParametro(int i) {		
		
		return (Variavel)parametros.elementAt(i);
	}
	

	public void adicionaParametro( Variavel variavel ) throws Exception {
		
		for ( int i=0; i< parametros.size();i++ )
			if ( ( (Variavel)parametros.elementAt(i) ).identificador.compareTo(variavel.identificador) == 0 )
				throw new Exception ("Parametro com nome duplicado");
		
		parametros.add( variavel );
	}
	
}

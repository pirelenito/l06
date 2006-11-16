package Semantico;

import java.util.TreeMap;
import java.util.Vector;

public class Declaracoes {
	
	private Vector escopo;
		
	TreeMap escopoAtual;
	
	public Declaracoes()
	{
		escopo = new Vector ();
	}
		
	public void sobeEscopo() {
		
		escopoAtual = new TreeMap ();
				
		escopo.add(escopoAtual);
		
		adicionaDeclaracoesPadrao ( );
	}
	
	private void adicionaDeclaracoesPadrao() {
		
		try {
		
			Tipo tipo = new Tipo ("INTEGER");		
			declara( (Declaracao) tipo );
			
			tipo = new Tipo ("BOOL");		
			declara( (Declaracao) tipo );
			
			Variavel var = new Variavel ("TRUE", new Tipo ("BOOL"), true, false );
			declara( (Declaracao) var );
			
			var = new Variavel ("FALSE", new Tipo ("BOOL"), true, false );
			declara( (Declaracao) var );
			
			Procedimento procedimento = new Procedimento ( "print" );
			procedimento.adicionaParametro( new Variavel ( "parametro", Tipo.tipoInteiro(), false, false ) );			
			declara ( (Declaracao)procedimento );
			
		} catch (Exception e) {
			
		}
		
	}

	public void desceEscopo() {	
		
		escopo.remove(escopo.size() - 1);
	}
	
	public Declaracao pegaDeclaracao ( String identificador ) throws Exception {

		Declaracao retorno;
		
		for ( int i = escopo.size() - 1; i >= 0; i-- )			
		{
			retorno = (Declaracao) ( ( (TreeMap)(escopo.elementAt(i)) ).get(identificador));
			
			if ( retorno != null )
				return retorno;
		}
		
		throw new Exception ("Chamada a função não declarada" ); 
	}

	public void declara ( Declaracao declaracao ) throws Exception {
		
		if ( escopoAtual.containsKey(declaracao.identificador ) )
			throw new Exception ("Declarando com identificador duplicado" );
		
		escopoAtual.put(declaracao.identificador, declaracao);
	
	}

}

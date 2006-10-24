package Semantico;

public class Variavel extends Declaracao {

	public Tipo tipo;
	
	public boolean constante;
	
	public boolean referencia;
	
	/**
	 * Variavel sem nome
	 * 
	 * @param _tipo
	 * @param _constante
	 * @param _referencia
	 */
	public Variavel(Tipo _tipo, boolean _constante, boolean _referencia) {
		super ( "" );
		
		tipo = _tipo;
		constante = _constante;
		referencia = _referencia;
	}

	/**
	 * Variavel com nome 
	 * 
	 * @param identificador
	 * @param _tipo
	 * @param _constante
	 * @param _referencia
	 */
	public Variavel(String identificador, Tipo _tipo, boolean _constante, boolean _referencia) {
		super ( identificador );
		
		tipo = _tipo;
		constante = _constante;
		referencia = _referencia;
	}

	/**
	 * realiza uma operacao com outra variavel
	 * @param variavel
	 * @return
	 * @throws Exception
	 */
	public Variavel opera(Variavel variavel) throws Exception {

		if ( !tipo.verificaIgualdade(variavel.tipo) )
			throw new Exception ( "Relação invalida: Incompatibilidade de tipos" );

		if ( constante && variavel.constante )
			return new Variavel ( tipo, true, false );	
		
		return new Variavel ( tipo, false, false );
	}

	/**
	 * Realiza uma atribuição
	 * @param segundaVariavel
	 * @throws Exception
	 */
	public void atribui(Variavel segundaVariavel) throws Exception {
		
		if ( constante )
			throw new Exception ( "Tentativa de setar variavel constante" );
		
		if ( !tipo.verificaIgualdade(segundaVariavel.tipo) )
			throw new Exception ("Incompatibilidade de tipos");
	}	
}

package Semantico;

import java.util.TreeMap;

public class TipoRecord extends Tipo {

	public TipoRecord( ) {
		super("Record");
		
		propriedades = new TreeMap ( );
	}

	TreeMap propriedades;

	public Tipo pegaTipoPropriedade(String identificador) throws Exception {
		
		Tipo retorno = (Tipo)propriedades.get( identificador );

		if ( retorno == null )
			throw new Exception ("Propriedade invalida para o record");
		
		
		return retorno;
	}

	public void adicionaPropriedade(Tipo tipoPropriedades, String identificador) throws Exception {
		
		if ( propriedades.containsKey( identificador ) )
			throw new Exception ("Propriedade com nome duplicado!" );
		
		propriedades.put( identificador, tipoPropriedades );
		
	}
	
}

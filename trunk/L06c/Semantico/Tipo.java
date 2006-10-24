package Semantico;

public class Tipo extends Declaracao{

	public Tipo(String _identificador) {
		super(_identificador);
	}

	public Tipo pegaTipoMore ( ) 
	{
		return this;
	}
	
	public static Tipo tipoInteiro ( )
	{
		return new Tipo ( "INTEGER" );
	}
	
	public static Tipo tipoBooleano ( )
	{
		return new Tipo ( "BOOL" );
	}	
	

	public boolean verificaIgualdade ( Tipo tipo ) {
		
		if ( pegaTipoMore().identificador.compareTo( tipo.pegaTipoMore().identificador ) == 0 )
			return true;		
		
		return false;
	}

	public boolean verificaIgualdade ( String tipo ) {
		
		if ( pegaTipoMore().identificador.compareTo( tipo ) == 0 )
			return true;
		
		return false;
		
	} 
}

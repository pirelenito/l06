package Semantico;

public class Tipo extends Declaracao{

	Tipo tipoPai;
	
	public Tipo ( Tipo pai, String string ) {
		super ( string );
		tipoPai = pai;
	}
	
	public static Tipo tipoInteiro ( )
	{
		return new Tipo ( "INTEGER", null );
	}
	
	public static Tipo tipoBooleano ( )
	{
		return new Tipo ( "BOOL", null );
	}
	
	public Tipo pegaTipoMore ( )
	{
		Tipo temp = this;
		
		while ( temp.tipoPai != null )
			temp = temp.tipoPai;
		
		return temp;
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

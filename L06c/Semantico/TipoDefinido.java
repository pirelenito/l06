package Semantico;

public class TipoDefinido extends Tipo {

	Tipo tipoPai;
	
	public TipoDefinido ( Tipo pai, String string ) {
		super ( string );
		tipoPai = pai;
	}
	
	public Tipo pegaTipoMore ( )
	{
		Tipo temp = this;
		
		while ( temp.pegaComportamento().compareTo("TipoDefinido") == 0 )
			temp = ((TipoDefinido)temp).tipoPai;
		
		return temp;
	}
	
}

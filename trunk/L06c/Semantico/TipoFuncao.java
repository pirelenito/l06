package Semantico;

import java.util.Vector;

public class TipoFuncao extends TipoProcedimento {
	
	public TipoFuncao(Tipo tipoRetorno) {
		super ("func");
		
		retorno = tipoRetorno;
	}

	Tipo retorno;
	

}

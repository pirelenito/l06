package Semantico;

import java.util.Vector;

public class TipoProcedimento extends Tipo {

	public TipoProcedimento(String string) {
		super (string);
	}
	
	public TipoProcedimento() {
		super("proc");
	}

	Vector parametros;
	
	public String pegaTipoParametro(int i) {
		// TODO Auto-generated method stub
		return null;
	}
	

	public boolean adicionaParametro(Tipo tipoParametros, String valor, boolean referencia) {
		// TODO Auto-generated method stub
		return false;
	}


}

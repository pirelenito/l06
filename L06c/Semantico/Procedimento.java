package Semantico;

import java.util.Vector;

public class Procedimento extends Declaracao {

	public Procedimento(String string) {
		super (string);
	}
	
	public Procedimento() {
		super("proc");
	}

	Vector parametros;
	
	public String pegaTipoParametro(int i) {
		// TODO Auto-generated method stub
		return null;
	}
	

	public void adicionaParametro(Tipo tipoParametros, String valor, boolean referencia) throws Exception {
		
		throw new Exception ("Parametro com nome duplicado");
		
	}


}

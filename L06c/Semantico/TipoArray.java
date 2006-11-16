package Semantico;

public class TipoArray extends Tipo {

	int numeroDimensoes;
	Tipo tipoDados;
	
	public TipoArray(Tipo _tipoDados) {
		super ( "Array");
		
		tipoDados = _tipoDados;
	}

	public int pegaNumeroDimensoes() {
		
		return numeroDimensoes;
	}

	public void adicionaIndice(String inicioRange, String fimRange) {

		numeroDimensoes++;
		
	}

	public Tipo pegaTipoDados() {
		return tipoDados;
	}
	
}

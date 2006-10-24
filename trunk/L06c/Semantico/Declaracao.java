package Semantico;

public class Declaracao {

	public String identificador;
	
	public Declaracao(String _identificador) {
		identificador = _identificador;
	}

	public String pegaComportamento ( )
	{
		//Semantico.
		return this.getClass().getSimpleName();
	}
	
}

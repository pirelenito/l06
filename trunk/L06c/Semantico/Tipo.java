package Semantico;

public class Tipo {

	public Tipo(String _nome) {
		nome = _nome;
	}

	public String nome;

	public boolean verificaIgualdade(Tipo tipo) {
		
		if ( nome.compareTo( tipo.nome ) == 0)
			return true;
		
		return false;
	}

	public boolean verificaIgualdade(String string) {
		
		if ( nome.compareTo( string ) == 0 )
			return true;
		
		return false;
		
	} 
		
}
